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
