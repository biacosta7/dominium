Feature: Gestão de Funcionários e Prestadores de Serviço

  Scenario: Cadastrar funcionário CLT com vínculo válido
    Given o síndico informa os dados do "funcionário" do tipo "CLT"
     When o síndico solicita o cadastro do "funcionário"
     Then o sistema cadastra o "funcionário" com status "ATIVO"

  Scenario: Cadastrar prestador eventual sem ordem de serviço
    Given o síndico informa os dados do "funcionário" do tipo "EVENTUAL"
     When o síndico solicita o cadastro do "funcionário"
     Then o sistema cadastra o "funcionário" com status "INATIVO"

  Scenario: Vincular prestador eventual a uma ordem de serviço ativa
    Given o "funcionário" do tipo "EVENTUAL" "está" cadastrado
     When o síndico cria uma "ordem de serviço" para o "funcionário"
     Then o sistema vincula o "funcionário" à "ordem de serviço"
      And o "funcionário" passa a ter status "ATIVO"

  Scenario: Bloquear ordem de serviço para funcionário CLT
    Given o "funcionário" do tipo "CLT" "está" cadastrado
     When o síndico tenta criar uma "ordem de serviço" para o "funcionário"
     Then o sistema bloqueia a criação informando que ordens são exclusivas para prestadores eventuais

  Scenario: Encerrar ordem de serviço inativa prestador eventual
    Given o "funcionário" do tipo "EVENTUAL" possui uma "ordem de serviço" ativa
     When o síndico conclui a "ordem de serviço"
     Then o sistema marca a "ordem de serviço" como "CONCLUIDA"
      And o "funcionário" passa a ter status "INATIVO"

  Scenario: Renovar contrato sem avaliações negativas
    Given o "funcionário" "não possui" avaliações negativas recentes
     When o síndico solicita a renovação do contrato do "funcionário"
     Then o sistema renova o contrato do "funcionário"

  Scenario: Bloquear renovação automática por avaliações negativas recorrentes
    Given o "funcionário" "possui" 3 avaliações negativas recentes
     When o síndico solicita a renovação do contrato do "funcionário"
     Then o sistema bloqueia a renovação informando avaliações negativas

  Scenario: Gerar despesa mensal automática para funcionário ativo
    Given existe um "orçamento" cadastrado para o ano corrente
      And o "funcionário" "está" ativo com contrato vigente
     When o sistema executa a geração de despesas mensais
     Then o sistema registra uma despesa de pessoal com o valor mensal do "funcionário"

  Scenario: Não gerar despesa para funcionário com contrato vencido
    Given existe um "orçamento" cadastrado para o ano corrente
      And o "funcionário" "possui" contrato vencido
     When o sistema executa a geração de despesas mensais
     Then o sistema não registra despesa para o "funcionário"

  Scenario: Bloquear ativação de funcionário com contrato vencido
    Given o "funcionário" do tipo "EVENTUAL" "possui" contrato vencido
     When o síndico tenta criar uma "ordem de serviço" para o "funcionário"
     Then o sistema bloqueia a ativação informando que o contrato está vencido
