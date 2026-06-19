import type { Pauta, CriarPautaRequest } from '../types/pauta';

const BASE_URL = '/pautas';

async function erroDe(res: Response, fallback: string): Promise<Error> {
  try {
    const body = await res.json();
    return new Error(body.message || fallback);
  } catch {
    return new Error(fallback);
  }
}

export const pautaService = {
  async listar(): Promise<Pauta[]> {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw await erroDe(res, 'Erro ao listar pautas');
    return res.json();
  },

  async criar(data: CriarPautaRequest): Promise<Pauta> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao criar pauta');
    return res.json();
  },

  async encerrar(id: number): Promise<Pauta> {
    const res = await fetch(`${BASE_URL}/${id}/encerrar`, {
      method: 'PATCH',
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao encerrar pauta');
    return res.json();
  },
};