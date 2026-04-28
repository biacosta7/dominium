Feature: Gestão de Assembleias

  Scenario: Criar assembleia com antecedência
    Given a data da "assembleia" "respeita" a antecedência mínima
     When o síndico solicita a criação da "assembleia"
     Then o sistema cria a "assembleia"
      And o sistema notifica os "moradores"

  Scenario: Criar assembleia sem antecedência mínima
    Given a data da "assembleia" "não respeita" a antecedência mínima
     When o síndico solicita a criação da "assembleia"
     Then o sistema bloqueia a criação informando a antecedência mínima obrigatória

  Scenario: Encerrar assembleia com pauta
    Given a "assembleia" "possui" pautas cadastradas
     When o síndico solicita o encerramento da "assembleia"
     Then o sistema encerra a "assembleia"

  Scenario: Encerrar assembleia sem pauta
    Given a "assembleia" "não possui" pautas cadastradas
     When o síndico solicita o encerramento da "assembleia"
     Then o sistema bloqueia o encerramento informando que não há pauta
