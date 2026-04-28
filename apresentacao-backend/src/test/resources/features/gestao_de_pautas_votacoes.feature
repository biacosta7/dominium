Feature: Gestão de Pautas e Votações

  Scenario: Morador adimplente vota
    Given o "morador" "está" adimplente
      And a "unidade" "não registrou" voto nesta pauta
     When o "morador" registra o seu "voto"
     Then o sistema contabiliza o "voto" para a "unidade"

  Scenario: Morador inadimplente tenta votar
    Given o "morador" "não está" adimplente
     When o "morador" tenta registrar o seu "voto"
     Then o sistema bloqueia o "voto" informando a inadimplência

  Scenario: Tentativa de duplo voto pela mesma unidade
    Given a "unidade" "já registrou" voto nesta pauta
     When o "morador" tenta registrar o seu "voto"
     Then o sistema bloqueia o "voto" garantindo apenas um voto por unidade

  Scenario: Votação encerra validando quórum
    Given a "pauta" "atingiu" o quórum mínimo exigido
     When o síndico encerra a "votação"
     Then o sistema finaliza a "votação" e aplica as regras de aprovação
