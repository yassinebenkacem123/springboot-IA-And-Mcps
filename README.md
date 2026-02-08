# spring-AI-mcps

Multi-module Spring Boot workspace that demonstrates:

- **`server/`**: ingesting a PDF (`facture.pdf`), splitting it into chunks, and storing embeddings in a **VectorStore** (backed by a SQL table).
- **`mcpServer/`**: exposing **MCP (Model Context Protocol)** features (Prompts/Resources/Tools depending on your config) so an agent (or MCP Inspector) can query resources and run server-side capabilities.

> Windows-first instructions (this repo was developed on Windows).

---

## Modules

### 1) `server` (RAG / Embedding pipeline)
`server` boots a Spring Boot app that loads a PDF from the classpath and writes chunk embeddings into the configured VectorStore.

Key behavior (see `ServerApplication.java`):
- Reads `classpath:facture.pdf`
- Splits the PDF into chunks (`TokenTextSplitter`)
- Clears existing vectors (`delete from vector_store`)
- Stores new vectors (`vectorStore.accept(chunksDocs)`)

### 2) `mcpServer` (MCP Server)
`mcpServer` runs a Spring Boot app that exposes MCP endpoints/features. Typically this is used by:
- **MCP Inspector** (to list prompts/resources and execute them)
- An **agent/client** that connects to MCP and requests prompts/resources/tools.

---

## Prerequisites

- **Java 21**
- **PostgreSQL** (or whatever DB your VectorStore is configured for)
- **Maven** (or use the Maven wrapper)
- Your PDF file: `facture.pdf`

---

## Project structure (high level)

- `server/src/main/resources/facture.pdf`
- `mcpServer/src/main/resources/facture.pdf` *(recommended — see note below)*
- `server/...` Spring AI + VectorStore indexing
- `mcpServer/...` MCP features configuration (prompts/resources)

---

## Important note about `facture.pdf`

Each module has its **own classpath**.

If `mcpServer` loads `@Value("classpath:facture.pdf")`, then `facture.pdf` must exist in:

- `mcpServer/src/main/resources/facture.pdf`

Likewise, if `server` loads it from the classpath, it must exist in:

- `server/src/main/resources/facture.pdf`

If the PDF is missing in a module, you may see:
`FileNotFoundException: class path resource [facture.pdf] cannot be opened because it does not exist`

---

## Configure the database / VectorStore

This repo expects a working DB connection (example seen in logs):
- JDBC URL like: `jdbc:postgresql://localhost:5433/mcpTest`

You must ensure:
- The database exists
- Credentials match your `application.properties` / `application.yml`
- The **`vector_store`** table exists (or your VectorStore auto-creates it, depending on the implementation)

> The `server` app runs `delete from vector_store` on startup, so the table must exist and the user must have permission.

---

## Run (Windows)

### Option A: Run each module from VS Code
Open each module and run the Spring Boot main class:
- `server`: `com.example.server.ServerApplication`
- `mcpServer`: `com.mcp.mcpServer.McpServerApplication`

### Option B: Maven commands (from repo root)

Build everything:
```bat
mvn -DskipTests clean package
```

Run `server`:
```bat
mvn -pl server spring-boot:run
```

Run `mcpServer`:
```bat
mvn -pl mcpServer spring-boot:run
```

If you use the Maven wrapper on Windows:
```bat
server\mvnw.cmd -DskipTests spring-boot:run
mcpServer\mvnw.cmd -DskipTests spring-boot:run
```

---

## Using MCP Inspector

1. Start `mcpServer`
2. Open MCP Inspector
3. Connect to the MCP Server (host/port depends on your config; logs may show Tomcat port, e.g. `8989`)
4. Use:
   - **Prompts** tab → `List Prompts` → execute a prompt with arguments
   - **Resources** tab → `List Resources` → read a resource (e.g., PDF full-text resource)

---

## Troubleshooting

### 1) “The invoice/context does not contain information about user ‘yassine’…”
That response usually means **the retrieved context (PDF text/resource) does not contain that information**.  
It’s not a server crash; it’s the model answering “not found”.

Fix/verify:
- Confirm the PDF actually contains the information you’re asking for.
- Confirm the agent is actually using MCP resources/tools and/or RAG retrieval results.
- In MCP Inspector, read the PDF resource and verify the expected text is present.

### 2) `FileNotFoundException: class path resource [facture.pdf] ... does not exist`
- Put `facture.pdf` in the correct module under `src/main/resources/`
- Rebuild/restart

### 3) Vector store table errors
If startup fails on `delete from vector_store`:
- Create the table (or enable auto-schema creation if supported)
- Verify DB URL/credentials
- Verify permissions


## License

Add your license here (MIT/Apache-2.0/etc).
