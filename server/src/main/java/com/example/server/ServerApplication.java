package com.example.server;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootApplication
public class ServerApplication {


	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
	
	@Bean
	CommandLineRunner commandLineRunner(
		VectorStore vectorStore,
		JdbcTemplate jdbcTemplate,
		@Value("classpath:facture.pdf") Resource pdf
	){

		return args->{
			textEmbedding(vectorStore,jdbcTemplate, pdf);
		};
	}

	public static void textEmbedding(VectorStore vectorStore, JdbcTemplate jdbcTemplate, Resource pdf) {
		jdbcTemplate.update("delete from vector_store");
		PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();
		PagePdfDocumentReader documentReader = new PagePdfDocumentReader(pdf, config);
		List<Document> documents  = documentReader.get();
		String content = documents.stream()
			.map(cnt -> cnt.getText()).collect(Collectors.joining("\n"));
		TokenTextSplitter splitter = new TokenTextSplitter();

		List<Document> chunksDocs = splitter.split(documents);
		vectorStore.accept(chunksDocs);	
	}

}
