Feature: Gestão de Reservas de Áreas Comuns

  Scenario: Criar reserva com sucesso
    Given que o "morador" "está" adimplente
      And a "área comum" "não possui" conflito de horário
      And a "unidade" "não atingiu" o limite mensal de reservas
     When o "morador" solicita a criação de uma reserva
     Then o sistema cria a "reserva" com sucesso

  Scenario: Criar reserva com unidade inadimplente
    Given que o "morador" "não está" adimplente
     When o "morador" solicita a criação de uma reserva
     Then o sistema informa que unidade inadimplente não pode reservar

  Scenario: Criar reserva com conflito de horário
    Given que o "morador" "está" adimplente
      And a "área comum" "possui" conflito de horário
     When o "morador" solicita a criação de uma reserva
     Then o sistema informa que não pode haver conflito de horário
      And o sistema permite ativar a "lista de espera" automaticamente

  Scenario: Criar reserva excedendo o limite mensal
    Given que o "morador" "está" adimplente
      And a "unidade" "atingiu" o limite mensal de reservas
     When o "morador" solicita a criação de uma reserva
     Then o sistema informa que o limite mensal de reservas foi atingido

  Scenario: Cancelamento tardio de reserva
    Given o "morador" tem uma "reserva" confirmada
     When o "morador" cancela a "reserva" tardiamente
     Then o sistema cancela a "reserva"
      And o sistema gera uma "multa automática" pelo cancelamento tardio
