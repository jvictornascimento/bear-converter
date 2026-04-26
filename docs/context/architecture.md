# Architecture Context

## Decisao inicial

Comecar como **monolito modular em Spring Boot**.

Nao iniciar com microservicos.

## Motivo

Microservicos adicionam complexidade antes de existir demanda real:

- comunicacao entre servicos;
- versionamento de APIs internas;
- observabilidade distribuida;
- deploy de varios servicos;
- rastreamento de erros entre servicos;
- consistencia de dados;
- filas e eventos mais complexos;
- custo maior de infraestrutura;
- mais dificuldade para desenvolver sozinho.

Para a V1, o monolito modular entrega mais valor com menos risco.

## Modulos conceituais

```text
auth
users
conversions
files
shared
```

Modulos futuros possiveis:

```text
billing
plans
projects
notifications
```

## Separacao interna recomendada

Separar por contexto de negocio e responsabilidade.

Usar separacao em camadas. Nao usar DDD pesado na V1, porque a complexidade nao compensa neste momento.

Exemplo conceitual:

```text
br.com.bearflow.bear_converter
  auth
    api
    application
    domain
    infrastructure
  users
    api
    application
    domain
    infrastructure
  conversions
    api
    application
    domain
    infrastructure
  files
    application
    domain
    infrastructure
  shared
```

## Regras de arquitetura

- Controllers nao devem acessar repositories diretamente.
- Regras de negocio devem ficar em services/use cases.
- Usar DTOs para entrada e saida da API.
- Usar records quando fizer sentido para dados imutaveis.
- Usar enums para estados, tipos e categorias controladas.
- Nao usar mappers automaticos.
- Usar factories quando a criacao de objetos tiver regra.
- Usar StringBuilder para montagem textual complexa.
- Isolar integracoes externas.
- Isolar o modulo de conversao.
- Evitar dependencias circulares entre modulos.
- Manter contratos claros entre dominios.

## Worker de conversao

O primeiro candidato natural para separacao futura e o worker de conversao.

Evolucao esperada:

```text
V1: Spring Boot API + worker interno simples
V2: API + fila + worker no mesmo deploy ou processo separado
V3: API + fila + worker dedicado
```

Essa separacao futura nao significa comecar com microservicos completos.
