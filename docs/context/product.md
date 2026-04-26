# Product Context

## Nome

**bear-converter**

## Resumo

Bear Converter e um sistema backend-first para conversao de arquivos tecnicos.

O foco inicial e converter PDFs tecnicos vetoriais exportados de softwares CAD em arquivos DXF editaveis.

O projeto nasce como portfolio, mas deve ser pensado com possibilidade real de evoluir para SaaS.

## Problema

Em projetos eletricos, arquitetonicos e complementares, e comum receber apenas a prancha em PDF, mesmo quando o desenho original foi criado em AutoCAD ou ferramenta CAD parecida.

Conversores gratuitos geralmente perdem informacoes importantes:

- escala correta;
- layers;
- qualidade das linhas;
- textos;
- blocos ou simbolos;
- organizacao do desenho;
- relacao confiavel entre medida do PDF e medida real.

## Publico inicial

- projetistas eletricos;
- profissionais que recebem bases em PDF;
- pessoas que precisam reaproveitar desenhos tecnicos;
- pequenos escritorios tecnicos.

## Proposta da V1

Converter PDF CAD vetorial para DXF sem calibracao de escala.

A V1 deve validar o fluxo principal:

```text
usuario -> upload PDF -> job de conversao -> processamento -> DXF -> download
```

## Interface grafica

Quando finalizada, a V1 tera uma interface grafica ainda a ser desenvolvida.

URL prevista:

```text
https://www.bear-converter.bearflow.com.br
```

## Diferencial futuro

O diferencial mais forte do produto sera a conversao com escala assistida, prevista para a V2.

Na V4, o produto pode evoluir para uma biblioteca de projetos com storage premium.

