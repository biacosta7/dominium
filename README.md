# Dominium - Sistema de Gestão Condominial

Este repositório contém o projeto **Dominium**, um sistema robusto para gestão de condomínios, desenvolvido seguindo princípios de **DDD (Domain-Driven Design)**, **Clean Architecture** e **SOLID**.

### Descrição do Domínio e Linguagem Onipresente
A documentação detalhada do domínio, incluindo o dicionário de termos da linguagem onipresente, pode ser acessada no link abaixo:
* [Documentação do Domínio (Google Docs/Wiki)](https://docs.google.com/document/d/1aWK6FDDeQgYQenl1TC0TvFRtFSu0mYm-PGoDvy8YANo/edit?usp=sharing)

### Mapa de Histórias do Usuário (User Story Map)
O mapeamento das jornadas dos usuários e a estruturação das funcionalidades em épicos e histórias estão disponíveis no seguinte arquivo:
* [UserStoryMap.pdf](./UserStoryMap.pdf)

### Protótipos de Alta Fidelidade
O design da interface e a experiência do usuário (UX) foram projetados no Figma:
* [Protótipo Dominium no Figma](https://www.figma.com/design/Yl9MbXfSx6m3IA85iIvU1P/Dominium-Prot%C3%B3tipo?node-id=0-1&t=zsSIWvJwQfBRxVfy-1)

### Modelo de Subdomínios (Context Mapper)
O modelo tático e estratégico desenvolvido com a ferramenta Context Mapper (arquivo CML) descreve os Bounded Contexts e seus relacionamentos:
* [Dominium.cml](./Dominium.cml)

### Cenários de Teste BDD
Os cenários de aceitação foram escritos utilizando a sintaxe Gherkin para garantir que o comportamento do sistema atenda aos requisitos de negócio. Os arquivos `.feature` encontram-se em:
* `backend/src/test/resources/features/`

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
- **Step Definitions**: `backend/src/test/java/com/dominium/backend/bdd/`
- **Runner**: `RunCucumberTest.java`

---

## Tecnologias Utilizadas

- **Backend**: Java 17+, Spring Boot 3, JDBC (Sem ORM), H2 Database.
- **Frontend**: Angular, CSS Vanilla.
- **Qualidade**: Cucumber, JUnit, Vitest.
- **Design**: Figma.

## Como Executar o Projeto

### Backend
1. Navegue até a pasta `backend`.
2. Execute o comando: `./mvnw spring-boot:run`.
3. Os testes podem ser executados com: `./mvnw test`.
