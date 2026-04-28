Feature: Gestão de Lista de Espera

  Scenario: Promoção automática ao cancelar reserva
    Given existe uma "reserva" cancelada
      And a "fila" "possui" moradores aguardando
     When o sistema processa o cancelamento
     Then o sistema promove o próximo "morador" da "fila" seguindo a ordem cronológica
      And o sistema envia uma "notificação obrigatória"

  Scenario: Prazo para confirmação da vaga
    Given o "morador" foi promovido da "fila"
     When o "prazo para confirmação" expirar sem resposta
     Then o sistema cancela a pré-reserva
      And o sistema promove o próximo "morador" da "fila"
