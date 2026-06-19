const BASE_URL = 'http://localhost:8080';

export async function listarOcorrencias() {
  const res = await fetch(`${BASE_URL}/ocorrencias`);
  if (!res.ok) throw new Error(`Erro ${res.status} ao buscar ocorrências`);
  return res.json();
}

export async function criarOcorrencia(dados: any) {
  const res = await fetch(`${BASE_URL}/ocorrencias`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  if (!res.ok) throw new Error(`Erro ${res.status} ao criar ocorrência`);
  return res.json();
}

export async function editarOcorrencia(id: number | string, dados: any) {
  const res = await fetch(`${BASE_URL}/ocorrencias/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  if (!res.ok) throw new Error(`Erro ${res.status} ao editar ocorrência`);
  return res.json();
}

export async function deletarOcorrencia(id: number | string) {
  const res = await fetch(`${BASE_URL}/ocorrencias/${id}`, {
    method: 'DELETE',
  });
  if (!res.ok) throw new Error(`Erro ${res.status} ao deletar ocorrência`);
}

export async function atualizarStatusOcorrencia(id: number | string, status: string) {
  const res = await fetch(`${BASE_URL}/ocorrencias/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status }),
  });
  if (!res.ok) throw new Error(`Erro ${res.status} ao atualizar status`);
  return res.json();
}

export async function encerrarOcorrencia(id: number | string, dados: any) {
  const res = await fetch(`${BASE_URL}/ocorrencias/${id}/encerrar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  if (!res.ok) throw new Error(`Erro ${res.status} ao encerrar ocorrência`);
  return res.json();
}