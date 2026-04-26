# Stack Context

## Backend principal

- Java 21;
- Spring Boot;
- Spring Web MVC;
- Spring Validation;
- Spring Security;
- JWT para autenticacao;
- Spring Data JPA;
- PostgreSQL.

## Processamento de arquivos

Na V1:

- Apache PDFBox para leitura e analise inicial do PDF;
- geracao de DXF por biblioteca Java ou gerador proprio simples;
- processamento assincrono inicial com `@Async` ou fila simples baseada no banco.

Futuro:

- Redis ou RabbitMQ para fila;
- worker separado;
- Tesseract OCR para PDFs escaneados;
- ferramenta externa para DXF -> DWG, se houver opcao viavel.

## Storage

Na V1:

- storage local no desenvolvimento;
- estrutura preparada para trocar depois por MinIO ou S3;
- arquivos vinculados ao usuario e ao job de conversao.

Futuro:

- MinIO em desenvolvimento mais avancado;
- S3, Cloudflare R2 ou storage compativel em producao;
- separacao entre arquivos temporarios e permanentes;
- storage premium na V4.

## Infraestrutura

Na V1:

- Docker;
- Docker Compose;
- container para PostgreSQL;
- variaveis de ambiente para configuracoes sensiveis.

