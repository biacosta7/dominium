Feature: Gestão de Ocorrências e Comunicados Internos

  Scenario: Criar ocorrência vinculada a unidade
    Given o síndico informa os dados da "ocorrência"
      And a "ocorrência" "está vinculada" a uma "unidade"
     When o síndico solicita a criação da "ocorrência"
     Then o sistema registra a "ocorrência" com sucesso

  Scenario: Criar ocorrência sem vínculo
    Given o síndico informa os dados da "ocorrência"
      And a "ocorrência" "não está vinculada" a nenhuma "unidade"
     When o síndico solicita a criação da "ocorrência"
     Then o sistema informa que a ocorrência deve estar vinculada a uma unidade

  Scenario: Encerrar ocorrência gerando advertência
    Given a "ocorrência" "está" aberta
     When o síndico encerra a "ocorrência" aplicando "advertência"
     Then o sistema encerra a "ocorrência"
      And o sistema gera a "advertência" para a "unidade"

  Scenario: Tentativa de apagar histórico de ocorrência
    Given a "ocorrência" já está registrada no sistema
     When um usuário tenta apagar a "ocorrência"
     Then o sistema bloqueia a ação garantindo que o histórico não pode ser apagado
