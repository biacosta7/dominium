import type { Assembleia, CriarAssembleiaRequest, EditarAssembleiaRequest } from '../types/assembleia';

const BASE_URL = '/assembleias';

// TODO: substituir pelo síndico real vindo da sessão/contexto
const SINDICO_ID = '2';

async function erroDe(res: Response, fallback: string): Promise<Error> {
  try {
    const body = await res.json();
    return new Error(body.message || fallback);
  } catch {
    return new Error(fallback);
  }
}

export const assembleiaService = {
  async listar(): Promise<Assembleia[]> {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw await erroDe(res, 'Erro ao listar assembleias');
    return res.json();
  },

  async criar(data: CriarAssembleiaRequest): Promise<Assembleia> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'X-Sindico-Id': SINDICO_ID },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao criar assembleia');
    return res.json();
  },

  async editar(id: number, data: EditarAssembleiaRequest): Promise<Assembleia> {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'X-Sindico-Id': SINDICO_ID },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao editar assembleia');
    return res.json();
  },

  async cancelar(id: number): Promise<Assembleia> {
    const res = await fetch(`${BASE_URL}/${id}/cancelar`, {
      method: 'PUT',
      headers: { 'X-Sindico-Id': SINDICO_ID },
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao cancelar assembleia');
    return res.json();
  },

  async encerrar(id: number): Promise<Assembleia> {
    const res = await fetch(`${BASE_URL}/${id}/encerrar`, {
      method: 'PUT',
      headers: { 'X-Sindico-Id': SINDICO_ID },
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao encerrar assembleia');
    return res.json();
  },
};
