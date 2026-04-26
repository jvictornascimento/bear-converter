# Bear Converter

Bear Converter e um backend para conversao de arquivos tecnicos, com foco inicial em converter PDFs vetoriais exportados de softwares CAD para arquivos DXF editaveis.

O projeto nasce como um portfolio backend-first em Java com Spring Boot, mas com uma arquitetura pensada para evoluir para um produto SaaS. A primeira versao prioriza uma base solida: usuarios, autenticacao, upload de arquivos, historico e estrutura inicial de jobs de conversao.

## Problema

Em projetos eletricos, arquitetonicos e complementares, e comum receber apenas a prancha em PDF, mesmo quando o desenho original foi criado em AutoCAD ou outro software CAD.

Conversores gratuitos normalmente perdem informacoes importantes ou geram arquivos dificeis de reaproveitar. O Bear Converter busca atacar esse problema de forma progressiva, comecando por uma conversao simples para DXF e evoluindo depois para escala, planos pagos, processamento em lote e storage de projetos.

## Objetivo da V1

A V1 tem como objetivo entregar uma primeira versao funcional e bem organizada do backend, sem tentar resolver todos os problemas de conversao CAD de uma vez.

Quando finalizada, a V1 tambem tera uma interface grafica para uso do sistema. Essa interface ainda sera desenvolvida e a aplicacao ficara disponivel em:

```text
https://www.bear-converter.bearflow.com.br
```

Funcionalidades planejadas para a V1:

- cadastro de usuarios;
- login com autenticacao via JWT;
- controle de acesso com Spring Security;
- upload de arquivos PDF;
- criacao de jobs de conversao;
- conversao inicial de PDF CAD vetorial para DXF sem calibracao de escala;
- historico de conversoes por usuario;
- download do arquivo gerado;
- limite basico de conversoes vinculado ao usuario;
- persistencia em PostgreSQL;
- estrutura preparada para evoluir para fila, planos e storage externo.

## Fora do escopo da V1

- calibracao de escala;
- usuarios premium;
- pagamentos;
- conversao direta para DWG;
- OCR para PDFs escaneados;
- conversao em lote;
- biblioteca permanente de projetos;
- suporte generico a qualquer tipo de PDF.

## Stack inicial

- Java 21;
- Spring Boot 4;
- Spring Web MVC;
- Spring Security;
- Spring Validation;
- Spring Data JPA;
- PostgreSQL;
- Java JWT;
- Maven;
- JUnit e testes do Spring Boot.

## Arquitetura inicial

A decisao inicial e comecar como um monolito modular.

Isso significa uma unica aplicacao Spring Boot, mas com separacao interna clara entre os contextos do sistema.

Estrutura conceitual esperada:

```text
br.com.bearflow.bear_converter
  auth
  users
  conversions
  files
  shared
```

Motivos para comecar assim:

- menor complexidade operacional;
- desenvolvimento mais rapido;
- deploy mais simples;
- bom encaixe para portfolio;
- facilidade para evoluir futuramente;
- possibilidade de separar o worker de conversao quando houver necessidade real.

## Fluxo principal da V1

```text
Usuario faz login
  |
  v
Envia um PDF
  |
  v
API cria um job de conversao
  |
  v
Sistema processa o arquivo
  |
  v
Job muda para COMPLETED ou FAILED
  |
  v
Usuario baixa o DXF gerado
```

## Roadmap

### V1 - Base funcional

- usuarios;
- autenticacao JWT;
- upload de PDF;
- conversao PDF para DXF sem escala;
- historico;
- download;
- limite de conversoes por usuario.

### V2 - Planos, creditos e escala

- usuarios free e premium;
- saldo de conversoes por usuario;
- compra de conversoes extras;
- calibracao de escala;
- fila de jobs;
- prioridade para usuarios premium.

### V3 - Conversao avancada

- exportacao para DWG;
- conversao em lote;
- OCR para PDFs escaneados;
- melhorias na leitura de textos, layers e geometria;
- recursos voltados a projetos eletricos.

### V4 - Storage premium

- biblioteca de projetos;
- storage persistente para usuarios premium;
- organizacao por cliente, obra ou disciplina;
- historico completo por projeto;
- controle de espaco usado.

## Como executar

Requisitos:

- Java 21;
- Maven Wrapper incluido no projeto;
- PostgreSQL configurado para as proximas etapas de desenvolvimento.

Executar a aplicacao:

```bash
./mvnw spring-boot:run
```

Executar os testes:

```bash
./mvnw test
```

## Status

Projeto em fase inicial de desenvolvimento.

No momento, a estrutura base do Spring Boot esta criada e as dependencias principais da V1 ja estao configuradas.
