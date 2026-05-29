Feature: Gestão de Multas

  Scenario: Aplicação de multa progressiva
    Given a "unidade" "possui" histórico de reincidência para a infração
     When o sistema gera uma "multa automática"
     Then o sistema aplica o valor da "multa progressiva"
      And o sistema integra o valor com a taxa mensal

  Scenario: Criar multa manual
    Given o síndico informa os dados da "multa"
     When o síndico solicita a criação da "multa"
     Then o sistema aplica a "multa" na "unidade"

  Scenario: Registrar pagamento de multa
    Given a "multa" "está" pendente
     When o sistema registra o "pagamento" da "multa"
     Then o sistema atualiza o status da "multa" para paga

  Scenario: Criação de multa atualiza saldo devedor da unidade
    Given a unidade possui saldo devedor zero
     When uma multa de 200 reais é aplicada à unidade
     Then o saldo devedor da unidade é atualizado para 200 reais
      And o status de adimplência da unidade muda para INADIMPLENTE

  Scenario: Pagamento de multa zera o saldo devedor da unidade
    Given a unidade possui uma multa aberta no valor de 200 reais
     When o pagamento integral da multa é registrado
     Then o saldo devedor da unidade é zerado
      And o status de adimplência da unidade muda para ADIMPLENTE
