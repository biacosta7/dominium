const BASE_URL = 'http://localhost:8080';

export async function listarMultasPorUnidade(unidadeId: number) {
  const res = await fetch(`${BASE_URL}/multas/unidade/${unidadeId}`);
  if (!res.ok) throw new Error(`Erro ${res.status} ao buscar multas da unidade`);
  return res.json();
}

export async function contestarMulta(id: number, justificativa: string) {
  const res = await fetch(`${BASE_URL}/multas/${id}/contestar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ justificativa }),
  });
  if (!res.ok) throw new Error(`Erro ${res.status} ao contestar multa`);
  return res.json();
}
