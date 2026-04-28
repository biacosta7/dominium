Feature: Gestão de Orçamentos e Despesas do Condomínio

  Scenario: Registrar despesa ordinária
    Given o "orçamento" "possui" saldo disponível
      And a despesa "é" "ordinária"
     When o síndico registra a despesa
     Then o sistema registra a despesa
      And desconta o valor do saldo disponível do orçamento
      And classifica a despesa por categoria

  Scenario: Registrar despesa estourando o orçamento
    Given o "orçamento" "não possui" saldo disponível suficiente
     When o síndico tenta registrar a despesa
     Then o sistema bloqueia a despesa para impedir o estouro do orçamento

  Scenario: Registrar despesa extraordinária
    Given a despesa "é" "extraordinária"
      And o valor "está" acima do limite
     When o síndico registra a despesa
     Then o sistema exige aprovação em assembleia para a despesa
      And a despesa aguarda rateio automático após aprovada
