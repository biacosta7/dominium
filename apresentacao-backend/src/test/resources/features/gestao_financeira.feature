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

  Scenario: Registrar despesa ordinária de alto valor
    Given o "orçamento" "possui" saldo disponível
      And a despesa "é" "ordinária"
      And o valor "é" de alto valor
     When o síndico registra a despesa
     Then o sistema registra a despesa
      And desconta o valor do saldo disponível do orçamento
      And classifica a despesa por categoria

  Scenario: Registrar despesa extraordinária
    Given a despesa "é" "extraordinária"
      And o valor "está" acima do limite
     When o síndico registra a despesa
     Then o sistema exige aprovação em assembleia para a despesa
      And a despesa aguarda rateio automático após aprovada

  Scenario: Registrar despesa extraordinária de alto valor com política flexível
    Given que o condomínio adota a política financeira "flexível"
      And o "orçamento" "possui" saldo disponível
      And a despesa "é" "extraordinária"
      And o valor "está" acima do limite
     When o síndico registra a despesa
     Then o sistema registra a despesa
      And desconta o valor do saldo disponível do orçamento
      And a despesa registrada fica com status "APROVADA"

  Scenario: Registrar despesa extraordinária de alto valor com política rígida
    Given que o condomínio adota a política financeira "rígida"
      And o "orçamento" "possui" saldo disponível
      And a despesa "é" "extraordinária"
      And o valor "está" acima do limite
     When o síndico registra a despesa
     Then o sistema registra a despesa
      And a despesa registrada fica com status "PENDENTE"

  Scenario: Registrar despesa ordinária de alto valor com política rígida
    Given que o condomínio adota a política financeira "rígida"
      And o "orçamento" "possui" saldo disponível
      And a despesa "é" "ordinária"
      And o valor "é" de alto valor
     When o síndico registra a despesa
     Then o sistema registra a despesa
      And a despesa registrada fica com status "PENDENTE"

  Scenario: Aprovar despesa extraordinária pendente
    Given o "orçamento" "possui" saldo disponível
      And a despesa "é" "extraordinária"
      And o valor "está" acima do limite
     When o síndico registra a despesa
     Then o sistema registra a despesa
      And a despesa registrada fica com status "PENDENTE"
     When o síndico aprova a despesa extraordinária
     Then a despesa registrada fica com status "APROVADA"
      And desconta o valor do saldo disponível do orçamento

