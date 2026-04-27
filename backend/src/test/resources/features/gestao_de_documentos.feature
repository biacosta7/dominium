Feature: Gestão de Documentos

  Cenário: Síndico cadastra um novo documento com sucesso
    Given o usuário logado é um "SINDICO"
    When o síndico cadastra o documento "Ata de Assembleia Geral" na categoria "ATA"
    Then o documento é salvo com status "ATIVO"
    And o documento está vinculado à categoria "ATA"

  Cenário: Morador tenta cadastrar um documento
    Given o usuário logado é um "MORADOR"
    When o morador tenta cadastrar o documento "Meu Contrato" na categoria "CONTRATO"
    Then o sistema nega o acesso informando que apenas o síndico pode realizar esta ação

  Cenário: Atualização de documento gera nova versão
    Given existe um documento "Regulamento Interno" cadastrado
    And o usuário logado é um "SINDICO"
    When o síndico atualiza o arquivo do documento "Regulamento Interno"
    Then o sistema incrementa o número da versão do documento
    And o histórico mantém a versão anterior armazenada

  Cenário: Listagem de documentos ativos para morador
    Given existem os documentos:
      | Nome                  | Categoria    | Status  |
      | Ata Jan/2026          | ATA          | ATIVO   |
      | Contrato Limpeza      | CONTRATO     | ATIVO   |
      | Regulamento Antigo    | REGULAMENTO  | INATIVO |
    And o usuário logado é um "MORADOR"
    When o morador solicita a listagem de documentos
    Then a lista deve conter apenas os documentos "ATIVOS"
    And o documento "Regulamento Antigo" não deve aparecer na lista

  Cenário: Inativação de documento pelo síndico
    Given existe um documento "Apólice de Seguro 2025" cadastrado com status "ATIVO"
    And o usuário logado é um "SINDICO"
    When o síndico solicita a inativação do documento "Apólice de Seguro 2025"
    Then o status do documento muda para "INATIVO"
    And o documento não aparece na listagem padrão

  Cenário: Consulta de documentos inativos no histórico
    Given existe um documento "Ata 2024" com status "INATIVO"
    And o usuário logado é um "SINDICO"
    When o síndico solicita a consulta do histórico de documentos incluindo inativos
    Then o documento "Ata 2024" deve ser exibido no histórico

  Cenário: Notificação de documento próximo ao vencimento
    Given existe um documento "Alvará de Incêndio" com validade para "daqui a 30 dias"
    When o sistema verifica documentos com prazo de validade próximo
    Then uma notificação deve ser enviada para o síndico sobre o vencimento do "Alvará de Incêndio"
