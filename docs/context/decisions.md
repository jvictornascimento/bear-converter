# Decisions

Registro de decisoes tecnicas e de produto do Bear Converter.

## 2026-04-26 - Comecar com monolito modular

Decisao:

> O projeto comeca como monolito modular em Spring Boot.

Motivo:

- menor complexidade operacional;
- mais adequado para desenvolvimento solo;
- suficiente para a V1;
- facilita deploy inicial;
- ainda permite separar o worker de conversao no futuro.

## 2026-04-26 - Comecar por DXF antes de DWG

Decisao:

> A primeira versao deve mirar PDF CAD vetorial para DXF, nao DWG direto.

Motivo:

- DWG e proprietario;
- DXF e mais simples de gerar programaticamente;
- softwares CAD conseguem abrir DXF;
- reduz risco tecnico da V1;
- permite validar a dor principal antes de investir em DWG.

## 2026-04-26 - V1 sem calibracao de escala

Decisao:

> A V1 gera DXF sem calibracao de escala.

Motivo:

- reduz escopo inicial;
- acelera entrega;
- permite validar usuarios, upload, jobs, historico e download;
- deixa escala como diferencial forte da V2.

## 2026-04-26 - Limite por usuario

Decisao:

> O limite de conversoes deve ficar associado ao usuario, nao apenas ao tipo de plano.

Motivo:

- permite plano gratuito;
- permite premium;
- permite compra avulsa de conversoes extras;
- evita prender a regra de negocio a um tipo fixo de plano.

## 2026-04-26 - Storage premium fica para V4

Decisao:

> Biblioteca de projetos e storage premium entram como V4.

Motivo:

- evita aumentar a V1/V2 antes da hora;
- permite entregar o conversor primeiro;
- transforma o produto em plataforma de trabalho quando a base ja estiver madura.

## 2026-04-26 - Commits em ingles simples com Conventional Commits

Decisao:

> Todos os commits devem ser escritos em ingles simples, nivel B1, seguindo Conventional Commits.

Motivo:

- cria historico padronizado;
- facilita code review;
- melhora leitura do projeto por recrutadores;
- mantem commits pequenos e claros.

Referencia unica:

```text
https://www.conventionalcommits.org/en/v1.0.0/
```

## 2026-04-26 - Desenvolvimento baseado em TDD

Decisao:

> O projeto deve ser desenvolvido com TDD: primeiro testes, depois implementacao.

Motivo:

- forca clareza sobre comportamento esperado;
- cobre cenarios de sucesso e erro;
- reduz regressao;
- combina com pair programming e code review frequente.

## 2026-04-26 - Usar camadas, nao DDD pesado

Decisao:

> Usar separacao em camadas e evitar DDD pesado na V1.

Motivo:

- DDD completo adicionaria complexidade desnecessaria;
- camadas sao suficientes para organizar o monolito modular;
- deixa a arquitetura mais simples de manter no inicio.

## 2026-04-26 - Revisao obrigatoria antes do proximo passo

Decisao:

> Apos cada commit, o usuario fara code review. O proximo passo so deve comecar depois da aprovacao.

Motivo:

- mantem o usuario no controle das decisoes;
- reduz retrabalho;
- reforca o ciclo curto de pair programming.

## 2026-04-26 - Autenticacao com JWT RS256 e refresh token persistido

Decisao:

> O login da V1 deve usar access token JWT assinado com RS256 e refresh token persistido no banco.

Motivo:

- RS256 separa chave privada e publica;
- access token curto reduz impacto em caso de vazamento;
- refresh token persistido permite logout real;
- refresh token pode ser rotacionado a cada uso;
- tokens expirados podem ser revogados por rotina agendada;
- prepara a base para login com Google no futuro.

## 2026-04-27 - Usar PDFBox para leitura inicial de PDFs

Decisao:

> A leitura inicial dos PDFs da V1 deve usar Apache PDFBox 3.0.7.

Motivo:

- biblioteca Java madura para leitura e inspecao de PDF;
- encaixa bem no backend Spring Boot;
- permite detectar texto, imagens e comandos graficos vetoriais;
- ajuda a separar PDFs vetoriais de PDFs baseados em imagem;
- cria base para alimentar o modelo intermediario antes da escrita DXF.

Referencia unica para uso nesta decisao:

```text
https://javadoc.io/doc/org.apache.pdfbox/pdfbox/3.0.7/index.html
```

## 2026-05-03 - Validar upload de PDF antes do parser

Decisao:

> O upload de PDF deve ser validado antes de chamar o PDFBox.

Motivo:

- reduz superficie de ataque no endpoint de upload;
- evita chamar o parser com arquivos claramente invalidos;
- limita consumo de memoria com tamanho maximo de 10MB;
- bloqueia nomes de arquivo com path traversal;
- valida extensao, Content-Type e assinatura `%PDF-`;
- retorna erros controlados para payloads invalidos.
