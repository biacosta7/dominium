Feature: Gestão de Taxa Condominial

  Scenario: Gerar taxa mensal
    Given uma "unidade" existe no sistema para taxa
    When o sistema gera a "taxa" mensal com valor base de "500.00" e vencimento para "2026-05-10"
    Then a "taxa" é criada com status "PENDENTE"

  Scenario: Atualizar valor da taxa com multas
    Given uma "taxa" "PENDENTE" existe para a "unidade" com valor base de "500.00"
    When o síndico atualiza o valor base para "500.00" e multas para "50.00"
    Then o valor total da "taxa" deve ser "550.00"

  Scenario: Registrar pagamento de taxa
    Given uma "taxa" para pagamento "PENDENTE" existe para a "unidade" com valor base de "500.00"
    When o sistema registra o "pagamento" da "taxa"
    Then o sistema atualiza o status da "taxa" para "PAGA"

  Scenario: Consultar histórico de taxas
    Given a "unidade" possui "2" taxas cadastradas
    When o morador consulta o histórico de taxas da "unidade"
    Then o sistema retorna uma lista contendo "2" taxas

  Scenario: Verificar inadimplência para bloqueio de reserva
    Given uma "taxa" está com status "ATRASADA" para a "unidade"
    When o sistema verifica se existe taxa atrasada para a "unidade"
    Then o sistema deve retornar que existe pendência