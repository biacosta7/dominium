# Dominium

**Dominium** é um sistema web para **gestão inteligente de condomínios**, desenvolvido como projeto da disciplina **Requisitos, Projeto de Software e Validação**.

A plataforma centraliza funcionalidades administrativas e operacionais de condomínios residenciais, permitindo que síndicos e moradores gerenciem reservas de áreas comuns, ocorrências, assembleias, votações e obrigações financeiras de forma organizada e transparente.

---

# Objetivo do Projeto

O objetivo do Dominium é facilitar a administração condominial através de uma plataforma digital que:

- organize processos administrativos
- centralize comunicação entre moradores e gestão
- automatize regras de negócio do condomínio
- aumente transparência em decisões coletivas

---

# Arquitetura

O sistema segue princípios de:

- **Domain-Driven Design (DDD)**
- **Clean Architecture**
- **BDD (Behavior Driven Development)**

Arquitetura geral do sistema:

```

[ Angular Frontend ]
|
| REST API
v
[ Spring Boot Backend ]
|
| JPA / Hibernate
v
[ PostgreSQL Database ]

```

O frontend consome a API REST exposta pelo backend, que implementa as regras de negócio e persiste os dados em banco relacional.

---

# Tecnologias Utilizadas

## Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Maven

## Frontend
- Angular
- TypeScript
- CSS

## Banco de Dados
- PostgreSQL

## Testes
- Cucumber
- BDD (Behavior Driven Development)

## Ferramentas
- Git / GitHub
- Context Mapper

---

# Estrutura do Repositório

```

dominium
│
├── dominium-backend
│   └── API Spring Boot
│
├── dominium-frontend
│   └── Aplicação Angular
│
└── docs
├── arquitetura
├── bdd
├── prototipos
└── context-mapper

```

---

# Funcionalidades do Sistema

O sistema inclui funcionalidades como:

- Gestão de moradores e unidades
- Reserva de áreas comuns
- Registro e acompanhamento de ocorrências
- Gestão de multas
- Controle de pagamentos condominiais
- Gestão de assembleias
- Sistema de votações
- Notificações para moradores
- Registro de visitantes
- Controle de manutenção

Cada funcionalidade possui regras de negócio específicas e não se limita apenas a operações CRUD.

---

# Executando o Projeto

## Backend (Spring Boot)

Entrar na pasta:

```

cd dominium-backend

```

Executar a aplicação:

```

./mvnw spring-boot:run

```

A API ficará disponível em:

[http://localhost:8080](http://localhost:8080)


---

## Frontend (Angular)

Entrar na pasta:

```

cd dominium-frontend

```

Instalar dependências:

```

npm install

```

Executar o projeto:

```

ng serve

```

A aplicação ficará disponível em:

[http://localhost:4200](http://localhost:4200)

---

# Metodologia de Desenvolvimento

O projeto segue uma abordagem baseada em **Domain-Driven Design**, incluindo:

- Linguagem Ubíqua
- Modelagem de Domínio
- Context Mapping
- Arquitetura em camadas

Além disso, utiliza **BDD (Behavior Driven Development)** para especificação e validação de funcionalidades através de cenários automatizados.

---

# Padrões de Projeto Utilizados

O sistema utiliza diversos **design patterns**, incluindo:

- Strategy
- Observer
- Decorator
- Proxy
- Iterator
- Template Method

Esses padrões são aplicados em componentes específicos da lógica de negócio.

---

# Documentação

A documentação do projeto está localizada na pasta:

```

/docs

```

Incluindo:

- arquitetura do sistema
- cenários BDD
- modelos de domínio
- arquivos Context Mapper (CML)
- protótipos de interface

---

# Equipe

- Beatriz Costa
- Gabrielle Mastellari
- George Neto
- Maria Fernanda Oliveira
- Nina França
- Sofia Tenório

Projeto desenvolvido para a disciplina:

**Requisitos, Projeto de Software e Validação**

_Ciência da Computação, Cesar School._

---

# Licença

Este projeto foi desenvolvido para fins acadêmicos.
