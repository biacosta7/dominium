Feature: Gestão de Moradores

  Scenario: Adicionar morador dentro do limite
    Given a "unidade" "não atingiu" o limite máximo de moradores
      And o "morador" "não pertence" a outra unidade
     When o síndico ou titular solicita adicionar o "morador"
     Then o sistema adiciona o "morador" à "unidade" com sucesso

  Scenario: Adicionar morador excedendo o limite
    Given a "unidade" "atingiu" o limite máximo de moradores
     When o síndico ou titular solicita adicionar o "morador"
     Then o sistema bloqueia a adição por exceder o limite máximo configurável

  Scenario: Adicionar morador já vinculado a outra unidade
    Given a "unidade" "não atingiu" o limite máximo de moradores
      And o "morador" "pertence" a outra unidade
     When o síndico ou titular solicita adicionar o "morador"
     Then o sistema informa que o "morador" pertence a apenas uma unidade

  Scenario: Titular remove dependente
    Given o usuário logado "é" "titular" da unidade
     When o usuário solicita a remoção de um "dependente"
     Then o sistema remove o "morador" com sucesso

  Scenario: Dependente tenta remover morador
    Given o usuário logado "não é" "titular" da unidade
     When o usuário solicita a remoção de um "morador"
     Then o sistema bloqueia a ação informando que apenas titulares podem remover dependentes
