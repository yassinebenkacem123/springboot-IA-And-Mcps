package com.mcp.mcpServer.Config;

import java.util.List;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

@Configuration
public class McpFeaturesConfig {

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> invoiceResources(
            @Value("classpath:facture.pdf") Resource pdf) {

        McpSchema.Annotations annotations = new McpSchema.Annotations(List.of(McpSchema.Role.USER), 1.0);

        var invoiceTextResource = new McpSchema.Resource(
                "invoice://facture/fulltext",
                "Facture PDF (full text)",
                "Full extracted text from facture.pdf (page concatenation).",
                "text/plain",
                annotations);

        var invoiceTextResourceSpec = new McpServerFeatures.SyncResourceSpecification(invoiceTextResource,
                (exchange, request) -> {
                    String body;
                    try {
                        if (!pdf.exists()) {
                            body = "facture.pdf was not found on the classpath. "
                                    + "Add it to mcpServer/src/main/resources/facture.pdf";
                        } else {
                            var reader = new PagePdfDocumentReader(pdf, PdfDocumentReaderConfig.defaultConfig());
                            body = reader.get().stream().map(d -> d.getText()).reduce("", (a, b) -> a + "\n" + b);
                        }
                    } catch (Exception e) {
                        body = "Failed to read facture.pdf from classpath: " + e.getMessage();
                    }

                    return new McpSchema.ReadResourceResult(
                            List.of(new McpSchema.TextResourceContents(
                                    request.uri(),
                                    "text/plain",
                                    body)));
                });

        return List.of(invoiceTextResourceSpec);
    }
}
