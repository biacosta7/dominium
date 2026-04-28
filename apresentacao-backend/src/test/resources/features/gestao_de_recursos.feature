Feature: Gestão de Recursos Contra Multas

  Scenario: Abrir recurso dentro do prazo
    Given a "multa" "está" no prazo máximo para recurso
     When o "morador" solicita a abertura de "recurso"
     Then o sistema abre o "recurso" contra a "multa"

  Scenario: Abrir recurso fora do prazo
    Given a "multa" "não está" no prazo máximo para recurso
     When o "morador" solicita a abertura de "recurso"
     Then o sistema bloqueia a abertura do "recurso" informando que o prazo expirou

  Scenario: Síndico julga recurso cancelando multa
    Given o "recurso" "está" aberto
     When o síndico "julga" o "recurso" cancelando a multa
     Then o sistema exige que o síndico "justifique" a decisão
      And o sistema cancela a "multa"
      And o "histórico" da decisão é armazenado
