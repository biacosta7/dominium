Feature: Gestão de Notificações

  Scenario Outline: Registro automático de "notificação" para "eventos" relevantes
    Given que ocorre um "evento" do tipo <tipo_evento>
    When o sistema processa o "evento"
    Then o sistema deve realizar o registro de uma "notificação" vinculada ao "morador"
    And a "notificação" deve possuir o "status" inicial "não lida"

    Examples:
      | tipo_evento                   |
      | "nova assembleia"             |
      | "cancelamento de reserva"     |
      | "aplicação de multa"          |
      | "promoção na lista de espera" |
      | "geração de taxa"             |

  Scenario: Marcar "notificação" como lida pelo "morador"
    Given existe uma "notificação" com "status" "não lida" para um "morador"
    When o "morador" solicita marcar a "notificação" como "lida"
    Then o sistema deve atualizar o "status" da "notificação" para "lida"
    And o "morador" deve continuar visualizando a "notificação" em seu histórico

  Scenario: Impedir a exclusão de "notificação" pelo "morador"
    Given existe uma "notificação" vinculada a um "morador"
    When o "morador" tenta realizar a exclusão da "notificação"
    Then o sistema deve rejeitar a solicitação de exclusão
    And a "notificação" deve ser mantida no "histórico do sistema" para fins de auditoria