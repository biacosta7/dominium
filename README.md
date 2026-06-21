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
Nas histórias de Gestão de Assembleias e Gestão de Funcionários, havia a necessidade de adicionar comportamentos transversais (auditoria e validação de regras de negócio) na hora de acessar os repositórios, sem alterar a implementação JDBC já existente nem os use cases que dependem dela. O padrão Proxy foi escolhido para resolver isso: um objeto Proxy implementa a mesma interface do repositório real e se posiciona entre o use case e a implementação JDBC, interceptando as chamadas para executar lógica adicional antes e/ou depois de delegar a chamada real, de forma totalmente transparente para quem consome o repositório.

**Como foi implementado:**
Em ambos os casos, a implementação JDBC concreta (`AssembleiaRepositoryImpl` / `FuncionarioRepositoryImpl`) passou a ser registrada com um `@Qualifier` específico (`assembleiaRepositoryJdbc` / `funcionarioRepositoryJdbc`), deixando de ser o bean injetado por padrão. No lugar dela, uma classe Proxy (`AssembleiaRepositoryAuditProxy` / `FuncionarioRepositoryValidacaoProxy`) implementa a mesma interface do repositório, recebe a implementação JDBC injetada via construtor (usando o `@Qualifier` correspondente) e é marcada com `@Primary`, garantindo que seja ela, e não a implementação real, a injetada em todos os use cases que dependem do repositório.

Na **Gestão de Assembleias**, o `AssembleiaRepositoryAuditProxy` adiciona auditoria: a cada `save()`, registra em log se a operação é uma criação ou atualização (com título, status e síndico responsável) antes de delegar para o repositório real, e registra a conclusão (com o id gerado) depois; em `findById()` e `findAll()`, registra logs de busca e indica se a assembleia foi encontrada. Nenhuma dessas operações de log altera o resultado retornado, o Proxy apenas observa e repassa.

Na **Gestão de Funcionários**, o `FuncionarioRepositoryValidacaoProxy` adiciona uma regra de validação automática: em `findById()`, `findAll()` e `findByStatus()`, cada `Funcionario` retornado pela implementação real passa pelo método `aplicarRegraVencimento()`, que verifica se o contrato do funcionário está vencido enquanto ele ainda está marcado como `ATIVO`. Se estiver, o Proxy bloqueia o funcionário (`funcionario.bloquear()`) e persiste essa mudança através do próprio delegate, antes de devolver o objeto já atualizado para quem chamou, garantindo que nenhum funcionário com contrato vencido seja exibido como ativo, sem que os use cases precisem conhecer essa regra.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-governanca/.../dominio/assembleia/repository/AssembleiaRepository.java` | Interface comum implementada pelo Proxy e pelo objeto real (*Subject*) |
| `subdominio-governanca/.../infraestrutura/assembleia/AssembleiaRepositoryImpl.java` | Implementação JDBC real (*RealSubject*), qualificada como `assembleiaRepositoryJdbc` |
| `subdominio-governanca/.../aplicacao/assembleia/proxy/AssembleiaRepositoryAuditProxy.java` | Proxy de auditoria: loga criação/atualização/busca de assembleias antes de delegar |
| `subdominio-operacional/.../dominio/funcionario/repository/FuncionarioRepository.java` | Interface comum implementada pelo Proxy e pelo objeto real (*Subject*) |
| `subdominio-operacional/.../infraestrutura/funcionario/FuncionarioRepositoryImpl.java` | Implementação JDBC real (*RealSubject*), qualificada como `funcionarioRepositoryJdbc` |
| `subdominio-operacional/.../aplicacao/funcionario/proxy/FuncionarioRepositoryValidacaoProxy.java` | Proxy de validação: bloqueia automaticamente funcionários com contrato vencido ao serem buscados |

### Strategy

**Implementado por:** Beatriz Costa

**Motivação:**
Na gestão financeira de um condomínio, o fluxo de aprovação e autorização de despesas pode variar de forma dinâmica dependendo das regras e da rigidez administrativa do próprio condomínio. Em vez de acoplar o processo de registro de despesas a regras rígidas codificadas diretamente nas classes de serviço ou utilizar desvios condicionais baseados em tipos de despesas ordinárias/extraordinárias, emprega-se o padrão **Strategy**. Isso permite desacoplar a definição da política financeira (`PoliticaFinanceiraStrategy`) da lógica de registro de despesas (`RegistrarDespesaUseCase`), facilitando a substituição dinâmica de políticas (ex: Flexível, Conservadora, Rígida) e permitindo a extensão para novos tipos de políticas sem a necessidade de modificar o caso de uso existente (respeitando o Princípio Aberto/Fechado - OCP).

**Como foi implementado:**
A implementação define uma interface `PoliticaFinanceiraStrategy` contendo o método `determinarStatusInicial`, responsável por avaliar uma despesa associada a um determinado orçamento e retornar o seu status inicial (`StatusDespesa`).

Três classes concretas de estratégia implementam esse comportamento:
1. `PoliticaFinanceiraFlexivelStrategy`: Qualquer despesa com saldo orçamentário disponível é aprovada imediatamente.
2. `PoliticaFinanceiraConservadoraStrategy` (Estratégia `@Primary` padrão): Segue as regras padrão do condomínio, onde despesas extraordinárias que superam um limite de R$ 5.000,00 ficam pendentes para votação em assembleia.
3. `PoliticaFinanceiraRigidaStrategy`: Exige que todas as despesas extraordinárias (independente de valor) e quaisquer despesas ordinárias que excedam R$ 10.000,00 fiquem pendentes de aprovação/auditoria manual pelo síndico.

O caso de uso `RegistrarDespesaUseCase` recebe a interface `PoliticaFinanceiraStrategy` por injeção de dependência e delega a ela a responsabilidade de decidir se a despesa deve nascer aprovada ou pendente. A anotação `@Primary` do Spring garante que a política conservadora padrão seja selecionada por padrão, preservando toda a compatibilidade com a base de código e testes existentes.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-financeiro/.../dominio/financeiro/strategy/PoliticaFinanceiraStrategy.java` | Interface da estratégia (*Strategy*) |
| `subdominio-financeiro/.../dominio/financeiro/strategy/PoliticaFinanceiraFlexivelStrategy.java` | Estratégia concreta: Política flexível |
| `subdominio-financeiro/.../dominio/financeiro/strategy/PoliticaFinanceiraConservadoraStrategy.java` | Estratégia concreta: Política conservadora (Padrão) |
| `subdominio-financeiro/.../dominio/financeiro/strategy/PoliticaFinanceiraRigidaStrategy.java` | Estratégia concreta: Política rígida |
| `subdominio-financeiro/.../aplicacao/financeiro/usecase/RegistrarDespesaUseCase.java` | Contexto que utiliza a estratégia (*Context*) |

### Decorator

**Implementado por:** George Neto

**Motivação:**
Nas histórias de Gestão de Recursos Contra Multas e Gestão de Taxa Condominial, era necessário acrescentar regras ao comportamento principal sem concentrar todas as validações e cálculos nos casos de uso ou nas entidades. O padrão **Decorator** foi escolhido para envolver a operação-base com comportamentos adicionais, permitindo compor validações e parcelas do cálculo de maneira desacoplada e facilitando futuras extensões sem modificar o núcleo das funcionalidades, em conformidade com o Princípio Aberto/Fechado (OCP).

**Como foi implementado:**
Na **Gestão de Recursos Contra Multas**, a classe `RecursoDecorator` define a interface funcional `Abertura`, que representa o componente do padrão. A implementação `Validacao` atua como Decorator concreto: recebe outra `Abertura`, verifica se a multa está aberta e se o prazo máximo de 15 dias ainda não expirou e, somente após essas validações, delega a execução para a operação decorada. O `AbrirRecursoUseCase` cria a operação-base responsável por registrar a contestação e persistir o recurso, envolvendo-a com o Decorator antes da execução.

Na **Gestão de Taxa Condominial**, a classe `TaxaDecorator` define a interface funcional `Calculo`. A implementação `Multas` envolve o cálculo-base e adiciona o valor de multas e juros. A entidade `TaxaCondominial` compõe esse cálculo tanto na geração quanto na atualização da taxa, mantendo o valor-base independente dos acréscimos e obtendo o valor total pela cadeia decorada.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-financeiro/.../aplicacao/recurso/decorator/RecursoDecorator.java` | Define o componente `Abertura` e o Decorator concreto de validação do recurso |
| `subdominio-financeiro/.../aplicacao/recurso/usecase/AbrirRecursoUseCase.java` | Cria a operação-base e aplica o Decorator antes de abrir o recurso (*Client*) |
| `subdominio-financeiro/.../dominio/taxa/decorator/TaxaDecorator.java` | Define o componente `Calculo` e o Decorator concreto que adiciona multas e juros |
| `subdominio-financeiro/.../dominio/taxa/TaxaCondominial.java` | Compõe e utiliza o cálculo decorado na geração e atualização da taxa (*Client*) |


### Iterator

**Implementado por:** Sofia Gomes Tenório

**Motivação:**
Na Gestão de Reservas de Espaços Comuns, a classe `PoliticaReserva` precisa validar a criação ou atualização de novas reservas. Para isso, ela deve iterar sobre as reservas existentes e ativas no mesmo período para verificar disponibilidade, conflito de horários ou capacidade máxima excedida. O padrão **Iterator** foi escolhido para desacoplar a lógica de validação de regras de negócio de como a coleção de reservas existentes é estruturada internamente (por exemplo, encapsulando se é mantida como `List`, `Set`, `Map` ou outra coleção na memória), permitindo percorrer os dados de forma uniforme e simplificada.

**Como foi implementado:**
A implementação segue a estrutura clássica do padrão Iterator:
1. A interface `ReservaIterator` define os métodos clássicos `hasNext()` e `next()` para navegar pela coleção de reservas de forma abstrata.
2. A classe `ReservaCollection` atua como a estrutura agregadora, encapsulando a lista bruta (`List<Reserva> reservas`) e fornecendo o método `iterator()`.
3. A classe `ReservaListIterator` é a implementação concreta do iterador, mantendo o controle do cursor de iteração (`position`) sobre a lista interna de reservas.
4. O método `validarNovaReserva` em `PoliticaReserva` foi projetado para receber o iterador (`ReservaIterator`) em vez da lista concreta, consumindo os elementos de maneira abstrata.
5. Nos casos de uso `CriarReservaUseCase` e `AtualizarReservaUseCase`, após buscar as reservas existentes no banco de dados, cria-se a `ReservaCollection` e o iterador é gerado e passado para o método de validação da política de reserva.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-espacos-condominio/.../dominio/reservas/iterator/ReservaIterator.java` | Interface do iterador (*Iterator*) |
| `subdominio-espacos-condominio/.../dominio/reservas/iterator/ReservaCollection.java` | Agregador de dados (*Aggregate*) |
| `subdominio-espacos-condominio/.../dominio/reservas/iterator/ReservaListIterator.java` | Implementador concreto do iterador (*ConcreteIterator*) |
| `subdominio-espacos-condominio/.../aplicacao/reservas/service/PoliticaReserva.java` | Cliente que consome o iterador para validar políticas de agendamento |
| `subdominio-espacos-condominio/.../aplicacao/reservas/usecase/CriarReservaUseCase.java` | Instancia e passa o iterador durante a criação de uma reserva |
| `subdominio-espacos-condominio/.../aplicacao/reservas/usecase/AtualizarReservaUseCase.java` | Instancia e passa o iterador durante a atualização de uma reserva |

### Template Method

**Implementado por:** Felipe Santos Marques Filho

**Motivação:**
Nas funcionalidades de Gestão de Notificações e Gestão de Documentos, havia dois algoritmos com estrutura fixa mas com etapas variáveis por implementação concreta. No envio de notificações, todo canal de entrega (banco de dados, console, e-mail, push) segue sempre a mesma sequência: formatar a mensagem, criar a entidade de domínio, persistir e despachar — mas cada canal define de forma diferente como persiste e como despacha. Nos alertas de documentos, o fluxo de execução é sempre o mesmo: buscar documentos, filtrar os elegíveis, identificar o destinatário, construir a mensagem e disparar a notificação — mas cada tipo de alerta define seus próprios critérios e textos. O padrão **Template Method** foi escolhido para fixar o esqueleto desses algoritmos na classe base, garantindo que a ordem das etapas nunca seja alterada, e delegar apenas as variações específicas às subclasses via métodos abstratos e hooks.

**Como foi implementado:**
O padrão foi aplicado em dois templates independentes dentro do bounded context de Comunicação:

No **envio de notificações**, a classe abstrata `NotificacaoServiceTemplate` implementa a interface `NotificacaoService` e define o template method `enviar()` como `final`, impedindo que subclasses alterem a sequência. O algoritmo chama: `formatarMensagem()` (hook — padrão retorna a mensagem sem alteração), `Notificacao.criar()` (etapa fixa de domínio), `persistir()` (hook — padrão não persiste, adequado para console e testes) e `despachar()` (método abstract — cada canal define sua forma de entrega). A subclasse `NotificacaoServiceImpl` sobrescreve `persistir()` para salvar no banco e implementa `despachar()` sem envio externo. A subclasse `ConsoleNotificacaoService` não sobrescreve `persistir()` e implementa `despachar()` imprimindo no console, sendo útil para ambiente de desenvolvimento e testes.

Nos **alertas de documentos**, a classe abstrata `AlertaDocumentoTemplate` define o template method `executar()` como `final`, com o algoritmo: `obterDocumentos()` (hook — padrão retorna todos os ativos do repositório), iteração com `deveAlertar()` (abstract — critério de elegibilidade), `obterDestinatario()` (hook — padrão usa o síndico do documento), `construirMensagem()` (abstract — texto personalizado) e `obterTipoNotificacao()` (abstract — tipo do enum de notificação). A subclasse concreta `NotificarDocumentosVencendoUseCase` implementa os três métodos abstratos: alerta documentos cuja data de validade está a 30 dias ou menos, constrói a mensagem com o nome e a data de vencimento do documento, e associa o tipo `DOCUMENTO_VENCENDO`. A arquitetura abre espaço para novas classes — como `AlertarDocumentosExpirados` ou `AlertarDocumentosSemVersao` — sem modificar o template existente, respeitando o Princípio Aberto/Fechado.

**Arquivos envolvidos:**

| Arquivo | Papel no padrão |
|---|---|
| `subdominio-comunicacao/.../infraestrutura/notification/NotificacaoServiceTemplate.java` | Template Method de notificação: define o esqueleto `enviar()` com hooks e método abstract `despachar()` |
| `subdominio-comunicacao/.../infraestrutura/notification/NotificacaoServiceImpl.java` | Subclasse concreta: persiste no banco e implementa `despachar()` sem envio externo |
| `subdominio-comunicacao/.../infraestrutura/notification/ConsoleNotificacaoService.java` | Subclasse concreta: ignora persistência e implementa `despachar()` via console |
| `subdominio-comunicacao/.../aplicacao/documento/usecase/AlertaDocumentoTemplate.java` | Template Method de alertas: define o esqueleto `executar()` com hooks e métodos abstratos de critério, mensagem e tipo |
| `subdominio-comunicacao/.../aplicacao/documento/usecase/NotificarDocumentosVencendoUseCase.java` | Subclasse concreta: alerta documentos com validade em até 30 dias |
