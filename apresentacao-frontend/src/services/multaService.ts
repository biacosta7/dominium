const BASE_URL = 'http://localhost:8080';

export async function listarMultasPorUnidade(unidadeId: number) {
  const res = await fetch(`${BASE_URL}/multas/unidade/${unidadeId}`);
  if (!res.ok) throw new Error(`Erro ${res.status} ao buscar multas da unidade`);
  return res.json();
}

export interface RecursoMulta {
  id: string;
  multaId: number;
  moradorId: number;
  motivo: string;
  status: 'PENDENTE' | 'DEFERIDO' | 'INDEFERIDO';
  justificativaSindico?: string;
  dataSolicitacao: string;
}

export async function abrirRecurso(multaId: number, usuarioId: number, motivo: string) {
  const res = await fetch(`${BASE_URL}/api/recursos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ multaId, usuarioId, motivo }),
  });
  if (!res.ok) throw new Error((await res.text()) || `Erro ${res.status} ao abrir recurso`);
  return res.json();
}

export async function listarRecursos(): Promise<RecursoMulta[]> {
  const res = await fetch(`${BASE_URL}/api/recursos`);
  if (!res.ok) throw new Error(`Erro ${res.status} ao buscar recursos`);
  return res.json();
}

export async function julgarRecurso(
  id: string,
  status: 'DEFERIDO' | 'INDEFERIDO',
  justificativa: string,
) {
  const res = await fetch(`${BASE_URL}/api/recursos/${id}/julgar`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status, justificativa, cancelarMulta: status === 'DEFERIDO' }),
  });
  if (!res.ok) throw new Error((await res.text()) || `Erro ${res.status} ao julgar recurso`);
}
