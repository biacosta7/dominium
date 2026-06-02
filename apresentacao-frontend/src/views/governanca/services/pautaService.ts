import type { Pauta, CriarPautaRequest } from '../types/pauta';

const BASE_URL = '/pautas';

export const pautaService = {
  async listar(): Promise<Pauta[]> {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw new Error('Erro ao listar pautas');
    return res.json();
  },

  async criar(data: CriarPautaRequest): Promise<Pauta> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Erro ao criar pauta');
    return res.json();
  },

  async encerrar(id: number): Promise<Pauta> {
    const res = await fetch(`${BASE_URL}/${id}/encerrar`, {
      method: 'PATCH',
    });
    if (!res.ok) throw new Error('Erro ao encerrar pauta');
    return res.json();
  },
};