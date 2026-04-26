# XP Workflow Context

## Como vamos trabalhar

O desenvolvimento sera feito em formato de pair programming com IA.

Na pratica:

- o usuario define prioridade e direcao do produto;
- a IA ajuda a desenhar a solucao tecnica;
- a IA implementa em passos pequenos;
- cada mudanca deve ter motivo claro;
- commits devem ser curtos e frequentes;
- todo desenvolvimento de funcionalidade deve seguir TDD;
- depois de cada commit, o usuario faz code review;
- so seguir para o proximo passo depois da aprovacao do review.

## Ciclo de trabalho

1. Definir uma tarefa pequena.
2. Confirmar o comportamento esperado.
3. Escrever primeiro os testes.
4. Cobrir cenario de sucesso e cenarios de erro.
5. Implementar a menor mudanca coerente.
6. Rodar teste/build quando possivel.
7. Revisar o resultado.
8. Fazer commit curto.
9. Aguardar code review do usuario.

## Padrao de branch

Usar nomes com contexto:

```text
docs/context-files
feat/auth-users
feat/conversion-jobs
feat/file-upload
chore/docker-postgres
test/auth-flow
```

Toda branch deve especificar o escopo da tarefa.

## Padrao de commit

Commits devem seguir Conventional Commits.

Referencia unica para consulta:

```text
https://www.conventionalcommits.org/en/v1.0.0/
```

Regras do projeto:

- idioma: ingles simples, nivel B1;
- titulo curto;
- corpo com explicacao detalhada do que foi feito;
- usar escopo quando ajudar a entender a area alterada;
- preferir commits pequenos;
- nao misturar assuntos diferentes no mesmo commit.

Formato:

```text
<type>(<scope>): <short description>

<detailed explanation in simple English>
```

Exemplos:

```text
docs(context): add ai workflow

Add project context files for AI agents.
They explain the product, roadmap, architecture, and work rules.

feat(auth): add user login

Add the first login flow for users.
The endpoint receives user credentials and returns a JWT when they are valid.

test(auth): cover invalid login

Add tests for login errors.
The tests check wrong password and missing email cases.
```

## Preferencias tecnicas

- evitar grandes refactors sem necessidade;
- preferir clareza a abstracao prematura;
- registrar decisoes relevantes;
- usar separacao em camadas;
- nao construir microservicos na V1;
- manter o projeto executavel localmente;
- nao usar DDD pesado na V1;
- usar DTOs para entrada e saida de API;
- usar records quando fizer sentido;
- usar enums para estados e tipos controlados;
- nao usar mappers automaticos;
- usar factories quando houver criacao com regra;
- usar StringBuilder para montagem textual complexa.

## Testes

Tipos de teste esperados:

- testes unitarios;
- testes de integracao;
- testes E2E quando houver consulta externa ou frontend.

Todo comportamento relevante deve ter:

- cenario de sucesso;
- cenario de erro;
- validacoes de entrada quando aplicavel;
- regra de permissao quando aplicavel.

## Preferencias ainda pendentes

Este arquivo ainda deve receber preferencias pessoais do usuario sobre:

- padrao de tratamento de erros;
- nomes finais dos pacotes;
- ferramenta para testes E2E;
- estrategia de banco para testes de integracao;
- ferramenta de code review/PR.
