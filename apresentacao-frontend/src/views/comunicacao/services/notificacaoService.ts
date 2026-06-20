import type { Notificacao } from '../types/notificacao';

const BASE_URL = '/notificacoes';
const SINDICO_ID = 2;

async function handleError(res: Response, fallback: string): Promise<Error> {
  try {
    const body = await res.json();
    return new Error(body.message || fallback);
  } catch {
    return new Error(fallback);
  }
}

function wrapFetchError(e: unknown, fallback: string): Error {
  if (e instanceof TypeError && e.message.includes('fetch')) {
    return new Error('Servidor indisponível. Verifique se o backend está rodando na porta 8080.');
  }
  return e instanceof Error ? e : new Error(fallback);
}

export const notificacaoService = {
  async listar(): Promise<Notificacao[]> {
    try {
      const res = await fetch(BASE_URL);
      if (!res.ok) throw await handleError(res, 'Erro ao listar notificações');
      return res.json();
    } catch (e) {
      throw wrapFetchError(e, 'Erro ao listar notificações');
    }
  },

  async marcarComoLida(id: number, usuarioId: number = SINDICO_ID): Promise<Notificacao> {
    try {
      const res = await fetch(`${BASE_URL}/${id}/lida`, {
        method: 'PATCH',
        headers: { 'X-Usuario-Id': String(usuarioId) },
      });
      if (!res.ok) throw await handleError(res, 'Erro ao marcar como lida');
      return res.json();
    } catch (e) {
      throw wrapFetchError(e, 'Erro ao marcar como lida');
    }
  },
};
