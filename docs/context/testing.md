# Testing Context

## Regra principal

O projeto deve ser desenvolvido com TDD.

Fluxo esperado:

```text
teste primeiro -> implementacao -> teste passando -> review -> commit
```

## Tipos de teste

Usar:

- testes unitarios;
- testes de integracao;
- testes E2E quando houver consulta externa ou frontend.

## O que testar

Para cada funcionalidade relevante, cobrir:

- cenario de sucesso;
- cenarios de erro;
- validacao de entrada;
- regras de permissao;
- estados esperados;
- comportamento quando dependencias falham.

## Testes unitarios

Devem focar em:

- regras de negocio;
- services/use cases;
- factories;
- validacoes;
- conversao de estados;
- regras de limite por usuario.

## Testes de integracao

Devem focar em:

- repositories;
- endpoints principais;
- seguranca;
- fluxo de autenticacao;
- upload e persistencia de metadados;
- criacao e mudanca de status de jobs.

## Testes E2E

Usar quando houver:

- frontend;
- consulta externa;
- integracao com servico externo;
- fluxo completo que precise validar experiencia real.

Exemplos futuros:

- login no frontend;
- upload de PDF pela interface;
- acompanhamento do status do job;
- download do DXF.
