# Dominium - Sistema de Gestão Condominial

Este repositório contém o projeto **Dominium**, um sistema robusto para gestão de condomínios, desenvolvido seguindo princípios de **DDD (Domain-Driven Design)**, **Clean Architecture** e **SOLID**.

### Descrição do Domínio e Linguagem Onipresente
A documentação detalhada do domínio, incluindo o dicionário de termos da linguagem onipresente, pode ser acessada no link abaixo:
* [Documentação do Domínio (Google Docs)](https://docs.google.com/document/d/1aWK6FDDeQgYQenl1TC0TvFRtFSu0mYm-PGoDvy8YANo/edit?usp=sharing)

### Mapa de Histórias do Usuário (User Story Map)
O mapeamento das jornadas dos usuários e a estruturação das funcionalidades em épicos e histórias estão disponíveis no seguinte arquivo:
* [UserStoryMap.pdf](./UserStoryMap.pdf)

### Protótipos de Alta Fidelidade
O design da interface foi projetado no Figma:
* [Protótipo Dominium no Figma](https://www.figma.com/design/Yl9MbXfSx6m3IA85iIvU1P/Dominium-Prot%C3%B3tipo?node-id=0-1&t=zsSIWvJwQfBRxVfy-1)

### Context Mapper
O modelo tático e estratégico desenvolvido com a ferramenta Context Mapper (arquivo CML) descreve os Bounded Contexts e seus relacionamentos:
* [Dominium.cml](./Dominium.cml)

### Cenários de Teste BDD
Os cenários de aceitação foram escritos utilizando a sintaxe Gherkin para garantir que o comportamento do sistema atenda aos requisitos de negócio. Os arquivos `.feature` encontram-se em:
* `apresentacao-backend/src/test/resources/features/`

#### Funcionalidades Mapeadas:
- Gestão de Assembleias
- Gestão de Pautas e Votações
- Gestão de Comunicados
- Gestão de Recursos
- Gestão de Funcionários
- Gestão de Reservas
- Gestão de Lista de Espera
- Gestão de Taxa Condominial
- Gestão de Moradores
- Gestão de Unidades
- Gestão de Multas
- Gestão Financeira

### Automação dos Cenários BDD (Cucumber)
Os cenários BDD estão automatizados utilizando Cucumber integrado ao JUnit no backend.
- **Step Definitions**: `apresentacao-backend/src/test/java/br/com/cesar/gestaoCondominial/apresentacao/bdd/`
- **Runner**: `RunCucumberTest.java`

## Requisitos
- Java 17
- Maven (ou usar o `./mvnw` incluso)
- Docker e Docker Compose (opcional)

## Tecnologias Utilizadas

- **Backend**: Java 17, Spring Boot 3, JDBC (Sem ORM), H2 Database.
- **Frontend**: Angular, CSS Vanilla.
- **Qualidade**: Cucumber, JUnit, Vitest.
- **Design**: Figma.

## Como Executar o Projeto

### 1. Via Docker (Recomendado)
Utilize o Docker Compose na raiz do projeto:
```bash
docker-compose up --build
```

### 2. Execução Tradicional (Local)

#### Backend
Para compilar e rodar o servidor:
```bash
./mvnw install -DskipTests
./mvnw spring-boot:run -pl apresentacao-backend
```

#### Testes
Para rodar os testes de aceitação (Cucumber):
```bash
./mvnw test -pl apresentacao-backend -Dtest=RunCucumberTest
```

## Documentação da API (Swagger)
Com o backend em execução, você pode acessar a interface do Swagger para explorar e testar os endpoints:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
