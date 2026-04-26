# Deploy Context

## Decisao inicial

Na V1, priorizar deploy simples e barato.

Arquitetura recomendada:

```text
Frontend
  |
  v
Spring Boot API
  |
  +--> PostgreSQL
  |
  +--> Storage local ou volume persistente
  |
  +--> Worker interno
```

## Opcoes para V1

Opcoes possiveis:

- VPS com Docker Compose;
- Render;
- Railway;
- Fly.io;
- Google Cloud Run;
- AWS Elastic Beanstalk;
- AWS Lightsail.

Escolha mais equilibrada no inicio:

> Desenvolver com Docker Compose localmente e preparar deploy V1 em VPS ou Render/Railway.

## Evolucao

V2:

```text
Spring Boot API
PostgreSQL
Redis ou RabbitMQ
Storage S3/MinIO
Worker de conversao
```

V3:

- worker dedicado para OCR/conversao pesada;
- container separado com ferramentas externas, se necessario;
- logs centralizados;
- metricas de tempo de conversao e falhas.

V4:

- storage S3 ou compativel obrigatorio;
- controle de quota por usuario;
- politicas de retencao;
- backups;
- possivel CDN para download.

## Nao usar no inicio

- Kubernetes;
- microservicos completos;
- infraestrutura distribuida complexa.

