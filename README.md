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
- Gestão de Comunicados Internos
- Gestão de Recursos
- Gestão de Funcionários
- Gestão de Reservas
- Gestão de Lista de Espera
- Gestão de Taxa Condominial
- Gestão de Moradores
- Gestão de Unidades
- Gestão de Multas
- Gestão Financeira
- Gestão de Notificação
- Gestão de Documentos

### Automação dos Cenários BDD (Cucumber)
Os cenários BDD estão automatizados utilizando Cucumber integrado ao JUnit no backend.
- **Step Definitions**: `apresentacao-backend/src/test/java/br/com/cesar/gestaoCondominial/apresentacao/bdd/`
- **Runner**: `RunCucumberTest.java`

## Requisitos
- Java 17
- Maven (ou usar o `./mvnw` incluso)
- Node.js (versão 18+ ou recente LTS) e npm
- Docker e Docker Compose (opcional)

## Tecnologias Utilizadas

- **Backend**: Java 17, Spring Boot 3, JDBC (Sem ORM), H2 Database.
- **Frontend**: React, TypeScript, CSS Vanilla.
- **Qualidade**: Cucumber, JUnit, Vitest.
- **Design**: Figma.

## Como Executar o Projeto

### 1. Via Docker (Recomendado para o Backend)
Utilize o Docker Compose na raiz do projeto para subir o contêiner do backend:
```bash
docker-compose up --build
```

### 2. Execução Tradicional (Local)

#### Backend
Para compilar e rodar o servidor do backend localmente:
```bash
./mvnw install -DskipTests
./mvnw spring-boot:run -pl apresentacao-backend
```

#### Frontend
Para rodar o servidor de desenvolvimento do frontend:
```bash
cd apresentacao-frontend
npm install
npm run dev
```
O frontend estará acessível em `http://localhost:5173`.

#### Testes
Para rodar os testes de aceitação (Cucumber):
```bash
./mvnw test -pl apresentacao-backend -Dtest=RunCucumberTest
```

## Documentação da API (Swagger)
Com o backend em execução, você pode acessar a interface do Swagger para explorar e testar os endpoints:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## Padrões de Projeto Implementados

### Observer

**Implementado por:** Nina Henrique França

**Motivação:**
Quando uma multa é criada, paga ou cancelada no sistema, outras partes do domínio precisam reagir a esse evento, especialmente a unidade condominial, cujo saldo devedor e status de adimplência devem ser atualizados automaticamente. O padrão Observer foi escolhido para desacoplar a lógica de criação de multas da lógica de atualização da unidade: o use case que cria a multa não precisa saber quais efeitos colaterais existem, apenas que um evento ocorreu.

**Como foi implementado:**
A implementação segue o padrão clássico com interfaces separadas para o publicador e para o ouvinte. A interface `MultaEventPublisher` define os métodos de publicação de eventos (`publicarMultaCriada`, `publicarMultaPaga`, `publicarMultaCancelada`) e os métodos de registro e remoção de ouvintes. A interface `MultaEventListener` define os callbacks que cada ouvinte deve implementar (`onMultaCriada`, `onMultaPaga`, `onMultaCancelada`).

`MultaEventPublisherImpl` é a implementação concreta do publicador: mantém uma lista de ouvintes registrados e, ao receber um evento, itera sobre todos eles chamando o callback correspondente. O Spring injeta automaticamente todos os beans que implementam `MultaEventListener` na lista do publicador via injeção por construtor.

O único ouvinte registrado atualmente é `UnidadeAdimplenciaListener`, que reage aos três eventos: ao criar uma multa, soma o valor ao saldo devedor da unidade e marca-a como `INADIMPLENTE`; ao registrar um pagamento, subtrai o valor pago e, se o saldo zerar, retorna o status para `ADIMPLENTE`; ao cancelar uma multa, reverte o valor adicionado ao saldo.

O `CreateMultaManualUseCase` atua como gatilho: após persistir a multa, chama `eventPublisher.publicarMultaCriada(salva)`, sem qualquer conhecimento de quem irá reagir ao evento.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-financeiro/.../dominio/multa/MultaEventPublisher.java` | Interface do publicador (*Subject*) |
| `subdominio-financeiro/.../dominio/multa/MultaEventListener.java` | Interface do ouvinte (*Observer*) |
| `subdominio-financeiro/.../aplicacao/multa/observer/MultaEventPublisherImpl.java` | Implementação concreta do publicador |
| `subdominio-financeiro/.../aplicacao/multa/observer/UnidadeAdimplenciaListener.java` | Ouvinte concreto — atualiza saldo devedor e adimplência da unidade |
| `subdominio-financeiro/.../aplicacao/multa/usecase/CreateMultaManualUseCase.java` | Dispara o evento após persistir a multa |
| `subdominio-financeiro/.../aplicacao/multa/usecase/RegistrarPagamentoMultaUseCase.java` | Dispara o evento de pagamento |
| `subdominio-financeiro/.../aplicacao/multa/usecase/UpdateMultaStatusUseCase.java` | Dispara o evento de cancelamento |

### Proxy

**Implementado por:** Gabrielle Mastellari

**Motivação:**
Nas histórias de Gestão de Assembleias e Gestão de Funcionários, havia a necessidade de adicionar comportamentos transversais (auditoria e validação de regras de negócio) na hora de acessar os repositórios, sem alterar a implementação JDBC já existente nem os use cases que dependem dela. O padrão Proxy foi escolhido para resolver isso: um objeto Proxy implementa a mesma interface do repositório real e se posiciona entre o use case e a implementação JDBC, interceptando as chamadas para executar lógica adicional antes e/ou depois de delegar a chamada real — de forma totalmente transparente para quem consome o repositório.

**Como foi implementado:**
Em ambos os casos, a implementação JDBC concreta (`AssembleiaRepositoryImpl` / `FuncionarioRepositoryImpl`) passou a ser registrada com um `@Qualifier` específico (`assembleiaRepositoryJdbc` / `funcionarioRepositoryJdbc`), deixando de ser o bean injetado por padrão. No lugar dela, uma classe Proxy (`AssembleiaRepositoryAuditProxy` / `FuncionarioRepositoryValidacaoProxy`) implementa a mesma interface do repositório, recebe a implementação JDBC injetada via construtor (usando o `@Qualifier` correspondente) e é marcada com `@Primary`, garantindo que seja ela — e não a implementação real — a injetada em todos os use cases que dependem do repositório.

Na **Gestão de Assembleias**, o `AssembleiaRepositoryAuditProxy` adiciona auditoria: a cada `save()`, registra em log se a operação é uma criação ou atualização (com título, status e síndico responsável) antes de delegar para o repositório real, e registra a conclusão (com o id gerado) depois; em `findById()` e `findAll()`, registra logs de busca e indica se a assembleia foi encontrada. Nenhuma dessas operações de log altera o resultado retornado — o Proxy apenas observa e repassa.

Na **Gestão de Funcionários**, o `FuncionarioRepositoryValidacaoProxy` adiciona uma regra de validação automática: em `findById()`, `findAll()` e `findByStatus()`, cada `Funcionario` retornado pela implementação real passa pelo método `aplicarRegraVencimento()`, que verifica se o contrato do funcionário está vencido enquanto ele ainda está marcado como `ATIVO`. Se estiver, o Proxy bloqueia o funcionário (`funcionario.bloquear()`) e persiste essa mudança através do próprio delegate, antes de devolver o objeto já atualizado para quem chamou — garantindo que nenhum funcionário com contrato vencido seja exibido como ativo, sem que os use cases precisem conhecer essa regra.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-governanca/.../dominio/assembleia/repository/AssembleiaRepository.java` | Interface comum implementada pelo Proxy e pelo objeto real (*Subject*) |
| `subdominio-governanca/.../infraestrutura/assembleia/AssembleiaRepositoryImpl.java` | Implementação JDBC real (*RealSubject*), qualificada como `assembleiaRepositoryJdbc` |
| `subdominio-governanca/.../aplicacao/assembleia/proxy/AssembleiaRepositoryAuditProxy.java` | Proxy de auditoria — loga criação/atualização/busca de assembleias antes de delegar |
| `subdominio-operacional/.../dominio/funcionario/repository/FuncionarioRepository.java` | Interface comum implementada pelo Proxy e pelo objeto real (*Subject*) |
| `subdominio-operacional/.../infraestrutura/funcionario/FuncionarioRepositoryImpl.java` | Implementação JDBC real (*RealSubject*), qualificada como `funcionarioRepositoryJdbc` |
| `subdominio-operacional/.../aplicacao/funcionario/proxy/FuncionarioRepositoryValidacaoProxy.java` | Proxy de validação — bloqueia automaticamente funcionários com contrato vencido ao serem buscados |
