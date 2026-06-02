import type { VotarRequest, VotoResponse, ResumoVotos } from '../types/voto';

const BASE_URL = '/votos';

function mensagemErroApi(body: string, fallback: string): string {
  try {
    const json = JSON.parse(body) as { message?: string };
    if (json.message) return json.message;
  } catch {
    // corpo não é JSON
  }
  return body.trim() || fallback;
}

export const votoService = {
  async votar(data: VotarRequest): Promise<void> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const erro = await res.text();
      throw new Error(mensagemErroApi(erro, 'Erro ao registrar voto'));
    }
  },

  async listarPorPauta(pautaId: number): Promise<VotoResponse[]> {
    const res = await fetch(`${BASE_URL}/pauta/${pautaId}`);
    if (!res.ok) throw new Error('Erro ao buscar votos');
    return res.json();
  },

  resumir(votos: VotoResponse[]): ResumoVotos {
    const favor = votos.filter((v) => v.opcao === 'FAVOR').length;
    const contra = votos.filter((v) => v.opcao === 'CONTRA').length;
    const abstencao = votos.filter((v) => v.opcao === 'ABSTENCAO').length;
    return { favor, contra, abstencao, total: votos.length };
  },
};