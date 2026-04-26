# language: pt
Funcionalidade: US08 - Gestão de Assembleias
  Como síndico
  Quero gerenciar assembleias
  Para organizar decisões coletivas

  Contexto:
    Dado que existe um usuário síndico com id "1" no sistema
    E que existe um usuário comum com id "2" no sistema

  # =====================================================================
  # CRIAR ASSEMBLEIA
  # =====================================================================

  Cenário: [POS] Criar assembleia com antecedência mínima respeitada
    # Por quê: fluxo principal — síndico agenda com pelo menos 5 dias de
    # antecedência garantindo tempo hábil para notificação dos moradores
    Quando o síndico "1" cria uma assembleia "AGO 2025" para daqui a "10" dias com pauta "Aprovação de obras"
    Então a assembleia deve ser criada com status "AGENDADA"
    E os moradores devem ter sido notificados

  Cenário: [NEG] Criar assembleia sem antecedência mínima de 5 dias
    # Por quê: assembleias de última hora impedem participação dos moradores —
    # regra garante o direito de acesso à informação prévia
    Quando o síndico "1" tenta criar uma assembleia para daqui a "3" dias
    Então o sistema deve rejeitar com a mensagem "A assembleia deve ser agendada com no mínimo 5 dias de antecedência"

  Cenário: [NEG] Usuário sem perfil síndico não pode criar assembleia
    # Por quê: criação de assembleia é exclusiva do síndico —
    # evita convocações não autorizadas que gerariam confusão
    Quando o usuário comum "2" tenta criar uma assembleia "Reunião" para daqui a "10" dias
    Então o sistema deve rejeitar com a mensagem "Apenas o síndico pode criar assembleias"

  # =====================================================================
  # ENCERRAR ASSEMBLEIA
  # =====================================================================

  Cenário: [POS] Encerrar assembleia com pauta preenchida
    # Por quê: assembleia com pauta documentada pode ser formalmente encerrada,
    # registrando as decisões tomadas
    Dado que existe uma assembleia "ASS1" com status "AGENDADA" e pauta "Eleição do síndico"
    Quando o síndico encerra a assembleia "ASS1"
    Então a assembleia "ASS1" deve ter status "ENCERRADA"

  Cenário: [NEG] Encerrar assembleia sem pauta
    # Por quê: assembleia sem pauta não formaliza nenhuma decisão —
    # regra evita registros vagos e sem valor jurídico
    Dado que existe uma assembleia "ASS2" com status "AGENDADA" e sem pauta
    Quando o síndico tenta encerrar a assembleia "ASS2"
    Então o sistema deve rejeitar com a mensagem "Não é possível encerrar uma assembleia sem pauta"

  Cenário: [NEG] Cancelar assembleia já encerrada
    # Por quê: assembleia encerrada representa uma decisão formal concluída —
    # não faz sentido cancelar o que já foi deliberado
    Dado que existe uma assembleia "ASS3" com status "ENCERRADA"
    Quando o síndico tenta cancelar a assembleia "ASS3"
    Então o sistema deve rejeitar com a mensagem "Apenas assembleias agendadas podem ser canceladas"

---

Funcionalidade: US09 - Gestão de Pautas e Votações
  Como síndico
  Quero gerenciar pautas e votações
  Para formalizar decisões

  Contexto:
    Dado que existe uma assembleia "ASS4" com status "AGENDADA"
    E que existe uma unidade adimplente "U1" com titular "200"
    E que existe uma unidade adimplente "U2" com titular "201"
    E que existe uma unidade inadimplente "U3" com titular "202"

  # =====================================================================
  # VOTAÇÃO
  # =====================================================================

  Cenário: [POS] Unidade adimplente vota em pauta aberta
    # Por quê: fluxo principal — titular de unidade adimplente registra seu voto
    Dado que existe uma pauta "P1" na assembleia "ASS4" com status "EM_VOTACAO"
    Quando o titular "200" da unidade "U1" vota "A_FAVOR" na pauta "P1"
    Então o voto deve ser registrado com sucesso

  Cenário: [NEG] Unidade inadimplente não pode votar
    # Por quê: inadimplentes perdem direito a voto — regra prevista em
    # convenção condominial para proteger a legitimidade das decisões
    Dado que existe uma pauta "P1" na assembleia "ASS4" com status "EM_VOTACAO"
    Quando o titular "202" da unidade inadimplente "U3" tenta votar "A_FAVOR" na pauta "P1"
    Então o sistema deve rejeitar o voto com mensagem de inadimplência

  Cenário: [NEG] Mesma unidade não pode votar duas vezes
    # Por quê: um voto por unidade é regra fundamental de governança —
    # votos duplicados comprometeriam a legitimidade da assembleia
    Dado que existe uma pauta "P2" na assembleia "ASS4" com status "EM_VOTACAO"
    E que a unidade "U1" já votou na pauta "P2"
    Quando o titular "200" da unidade "U1" tenta votar novamente na pauta "P2"
    Então o sistema deve rejeitar com mensagem de voto duplicado
