# language: pt
Funcionalidade: US06 - Gestão de Multas
  Como sistema
  Quero aplicar e gerenciar multas
  Para penalizar infrações e inadimplência

  Contexto:
    Dado que existe uma unidade com id "10" cadastrada no sistema

  # =====================================================================
  # CRIAR MULTA MANUAL
  # =====================================================================

  Cenário: [POS] Criar multa manual para primeira infração
    # Por quê: primeira ocorrência não deve ter acréscimo progressivo —
    # o valor informado é aplicado integralmente
    Quando o síndico cria uma multa manual de "200.00" para a unidade "10" com descrição "Barulho excessivo"
    Então a multa deve ser criada com valor "200.00" e status "ABERTA"
    E o índice de reincidência deve ser "0"

  Cenário: [POS] Criar multa com progressividade por reincidência
    # Por quê: reincidência aumenta 10% por ocorrência — mecanismo dissuasório
    # fundamental para manter disciplina no condomínio
    Dado que a unidade "10" já possui "2" multas com descrição "Barulho excessivo"
    Quando o síndico cria uma multa manual de "200.00" para a unidade "10" com descrição "Barulho excessivo"
    Então a multa deve ser criada com valor "240.00" e status "ABERTA"
    E o índice de reincidência deve ser "2"

  Cenário: [NEG] Criar multa para unidade inexistente
    # Por quê: multa sem unidade válida não pode ser registrada — violaria
    # a integridade referencial e impossibilitaria a cobrança
    Quando o síndico tenta criar uma multa para a unidade inexistente "999"
    Então o sistema deve rejeitar com a mensagem "Unidade não encontrada."

  # =====================================================================
  # REGISTRAR PAGAMENTO
  # =====================================================================

  Cenário: [POS] Registrar pagamento de multa aberta
    # Por quê: quitação da multa deve atualizar status e registrar data/valor
    Dado que existe uma multa com id "M1" no status "ABERTA" para a unidade "10"
    Quando o pagamento de "200.00" é registrado para a multa "M1"
    Então a multa "M1" deve ter status "PAGA"
    E a data de pagamento deve ter sido registrada

---

Funcionalidade: US07 - Gestão de Recursos Contra Multas
  Como morador
  Quero abrir recurso contra multa
  Para solicitar revisão da penalidade

  # =====================================================================
  # ABRIR RECURSO
  # =====================================================================

  Cenário: [POS] Abrir recurso dentro do prazo de 15 dias
    # Por quê: morador tem direito de contestar; fluxo deve funcionar
    # quando a multa foi aplicada recentemente
    Dado que existe uma multa com id "M2" criada há "5" dias
    Quando o morador "100" abre um recurso contra a multa "M2" com motivo "A festa era autorizada pelo síndico"
    Então o recurso deve ser criado com status "PENDENTE"
    E a multa "M2" deve registrar a data e motivo da contestação

  Cenário: [NEG] Abrir recurso fora do prazo de 15 dias
    # Por quê: prazo é uma regra de negócio para garantir celeridade processual —
    # recursos tardios não devem ser aceitos
    Dado que existe uma multa com id "M3" criada há "20" dias
    Quando o morador "100" tenta abrir um recurso contra a multa "M3"
    Então o sistema deve rejeitar com a mensagem "Prazo máximo de 15 dias para recurso expirado."

  Cenário: [NEG] Abrir recurso para multa inexistente
    # Por quê: recurso sem multa associada é inválido
    Quando o morador "100" tenta abrir um recurso contra a multa inexistente "M999"
    Então o sistema deve rejeitar com a mensagem "Multa não encontrada."

  # =====================================================================
  # JULGAR RECURSO
  # =====================================================================

  Cenário: [POS] Síndico defere recurso e cancela multa
    # Por quê: quando deferido com cancelamento, o valor da multa deve ser zerado,
    # representando o cancelamento efetivo da penalidade
    Dado que existe um recurso "REC1" pendente referente à multa "M4" com valor "300.00"
    Quando o síndico julga o recurso "REC1" como "DEFERIDO" com cancelamento da multa
    Então a multa "M4" deve ter valor "0.00"
    E o recurso "REC1" deve ter status "DEFERIDO"

  Cenário: [POS] Síndico defere recurso e reduz valor da multa
    # Por quê: síndico pode reconhecer infração parcial e ajustar o valor —
    # flexibilidade necessária para casos com circunstâncias atenuantes
    Dado que existe um recurso "REC2" pendente referente à multa "M5" com valor "300.00"
    Quando o síndico julga o recurso "REC2" como "DEFERIDO" com novo valor "150.00"
    Então a multa "M5" deve ter valor "150.00"
    E o recurso "REC2" deve ter status "DEFERIDO"

  Cenário: [POS] Síndico indefere recurso mantendo multa original
    # Por quê: recurso indeferido não altera a multa — punição é mantida integralmente
    Dado que existe um recurso "REC3" pendente referente à multa "M6" com valor "200.00"
    Quando o síndico julga o recurso "REC3" como "INDEFERIDO" com justificativa "Infração comprovada em câmeras"
    Então a multa "M6" deve manter o valor "200.00"
    E o recurso "REC3" deve ter status "INDEFERIDO"

  Cenário: [NEG] Julgar recurso inexistente
    # Por quê: o id do recurso deve ser válido para garantir rastreabilidade
    Quando o síndico tenta julgar o recurso inexistente "REC999"
    Então o sistema deve rejeitar com a mensagem "Recurso não encontrado."
