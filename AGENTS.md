# AGENTS.md

Contexto para agentes de IA trabalhando no projeto **bear-converter**.

## Papel da IA

A IA atua como pair programmer do projeto.

O trabalho deve seguir uma postura pragmatica inspirada em XP:

- discutir intencao antes de mudancas grandes;
- quebrar o desenvolvimento em passos pequenos;
- manter commits curtos e com contexto claro;
- validar com testes ou build sempre que possivel;
- explicar decisoes tecnicas relevantes;
- evitar complexidade antes da necessidade real;
- manter o usuario como dono das decisoes de produto.

## Fonte de contexto

Antes de implementar funcionalidades importantes, ler os arquivos em:

```text
docs/context
```

Esses arquivos registram o contexto atual do produto, arquitetura, roadmap e deploy.

## Regras de trabalho

- Criar branches com nome relacionado ao contexto da tarefa.
- Nao trabalhar direto na `main`.
- Fazer commits pequenos.
- Escrever commits em ingles simples, nivel B1.
- Seguir Conventional Commits.
- Nao misturar mudancas sem relacao no mesmo commit.
- Manter o README alinhado com o estado real do projeto.
- Registrar decisoes relevantes em `docs/context/decisions.md`.
- Nao implementar recursos fora da V1 sem decisao explicita.
- Preferir simplicidade operacional na V1.
- Usar TDD: primeiro testes, depois funcionalidade.
- Cobrir caminhos de sucesso e erro.
- Aguardar code review do usuario antes de seguir para o proximo passo apos um commit.

## Arquitetura da V1

A decisao inicial e usar **monolito modular em Spring Boot**.

Nao iniciar com microservicos.

Separacao conceitual esperada:

```text
br.com.bearflow.bear_converter
  auth
  users
  conversions
  files
  shared
```

O primeiro ponto preparado para separacao futura deve ser o worker de conversao.

Usar separacao em camadas. Nao usar DDD pesado na V1.

Preferencias de modelagem:

- usar DTOs quando houver entrada ou saida de API;
- usar records quando fizer sentido para dados imutaveis;
- usar enums para estados, tipos e categorias controladas;
- nao introduzir mappers automaticos;
- usar factories quando a criacao de objetos tiver regra;
- usar StringBuilder quando houver montagem textual mais complexa, como geracao de conteudo DXF.

## Escopo da V1

A V1 deve focar em:

- cadastro e login de usuarios;
- autenticacao com JWT;
- upload de PDF;
- deteccao se o PDF e vetorial ou escaneado;
- extracao inicial de linhas, curvas, textos e possiveis layers;
- criacao de jobs de conversao;
- conversao de PDF CAD vetorial para DXF sem escala;
- historico de conversoes por usuario;
- status da conversao;
- download do DXF gerado;
- limite de conversoes vinculado ao usuario.

Fora da V1:

- calibracao de escala;
- usuarios premium;
- pagamentos;
- DWG direto;
- CDR;
- OCR completo;
- conversao em lote;
- storage premium de projetos.
