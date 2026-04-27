Feature: Gestão de Unidades

  Scenario: Criar unidade com sucesso
    Given que os dados da "unidade" são "válidos" e o número "não está" em uso
     When o síndico solicita a criação da "unidade"
     Then o sistema cria a "unidade" com sucesso

  Scenario: Criar unidade com número duplicado
    Given que os dados da "unidade" são "válidos" mas o número "está" em uso
     When o síndico solicita a criação da "unidade"
     Then o sistema informa que o número da "unidade" deve ser único

  Scenario: Transferência de titularidade sem débitos
    Given uma "unidade" "não possui" débitos ativos
     When o síndico solicita a transferência de titularidade da "unidade"
     Then o sistema realiza a transferência com sucesso
      And o "histórico" de titularidade é atualizado

  Scenario: Transferência de titularidade bloqueada por débitos
    Given uma "unidade" "possui" débitos ativos
     When o síndico solicita a transferência de titularidade da "unidade"
     Then o sistema bloqueia a transferência da "unidade" informando os débitos

  Scenario: Inativar unidade sem débitos
    Given uma "unidade" "não possui" débitos ativos
     When o síndico solicita a inativação da "unidade"
     Then o sistema inativa a "unidade" com sucesso

  Scenario: Inativar unidade bloqueada por débitos
    Given uma "unidade" "possui" débitos ativos
     When o síndico solicita a inativação da "unidade"
     Then o sistema informa que a "unidade" não pode ser removida se possuir débitos ativos
