export interface Notificacao {
  id: number;
  usuarioId: number;
  mensagem: string;
  tipo: string;
  lida: boolean;
  criadaEm: string;
}

export const TIPOS_NOTIFICACAO: Record<string, string> = {
  GERAL: 'Aviso Geral',
  NOVA_ASSEMBLEIA: 'Nova Assembleia',
  CANCELAMENTO_RESERVA: 'Cancelamento de Reserva',
  APLICACAO_MULTA: 'Aplicação de Multa',
  PROMOCAO_LISTA_ESPERA: 'Promoção na Lista de Espera',
  GERACAO_TAXA: 'Geração de Taxa',
  VENCIMENTO_DOCUMENTO: 'Vencimento de Documento',
};

export const TIPOS_PUBLICO: Record<string, string> = {
  TODOS: 'Todos os Moradores (Broadcast)',
  SINDICO: 'Somente Síndico',
  INDIVIDUAL: 'Morador específico',
};
