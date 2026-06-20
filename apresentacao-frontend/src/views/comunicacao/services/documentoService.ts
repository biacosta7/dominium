import type { Documento, VersaoDocumento } from '../types/documento';

const BASE_URL = '/documentos';
const SINDICO_ID = '2';
const MOCK_KEY = 'dominium_documentos_mock';

// ─── Helpers ─────────────────────────────────────────────────────────────────

async function handleError(res: Response, fallback: string): Promise<Error> {
  if (res.status === 413) {
    return new Error('Arquivo muito grande. O limite é 50 MB.');
  }
  try {
    const body = await res.json();
    return new Error(body.message || body.error || fallback);
  } catch {
    return new Error(fallback);
  }
}

function isNetworkError(e: unknown, status?: number): boolean {
  // Vite proxy retorna 502/503/504 quando o backend não está rodando
  if (status !== undefined && (status === 502 || status === 503 || status === 504)) return true;
  // 413 é erro real do servidor (arquivo grande demais), não falha de rede
  if (status === 413) return false;
  // Sem proxy: TypeError de fetch ou timeout do AbortSignal
  return (
    e instanceof TypeError && e.message.includes('fetch') ||
    e instanceof DOMException
  );
}

async function fetchComMock<T>(
  fetchFn: () => Promise<Response>,
  mockFn: () => T
): Promise<{ data: T | null; isMock: boolean; error: Error | null }> {
  try {
    const res = await fetchFn();
    if (isNetworkError(null, res.status)) {
      return { data: mockFn(), isMock: true, error: null };
    }
    if (!res.ok) {
      return { data: null, isMock: false, error: await handleError(res, 'Erro inesperado') };
    }
    return { data: await res.json() as T, isMock: false, error: null };
  } catch (e) {
    if (isNetworkError(e)) {
      return { data: mockFn(), isMock: true, error: null };
    }
    return { data: null, isMock: false, error: e instanceof Error ? e : new Error(String(e)) };
  }
}

// ─── Mock com localStorage (apenas metadados — sem conteúdo do arquivo) ──────

const mock = {
  listar(): Documento[] {
    try {
      return JSON.parse(localStorage.getItem(MOCK_KEY) ?? '[]');
    } catch {
      return [];
    }
  },

  salvar(docs: Documento[]): void {
    localStorage.setItem(MOCK_KEY, JSON.stringify(docs));
  },

  cadastrar(
    nome: string,
    categoria: string,
    dataValidade: string | null,
    arquivo: File
  ): Documento {
    const id = `mock-${Date.now()}`;
    const novo: Documento = {
      id,
      nome,
      categoria,
      status: 'ATIVO',
      dataValidade: dataValidade ?? null,
      dataCriacao: new Date().toISOString(),
      versaoAtual: 1,
      nomeArquivoAtual: arquivo.name,
    };
    mock.salvar([...mock.listar(), novo]);
    return novo;
  },

  atualizarArquivo(id: string, arquivo: File): VersaoDocumento {
    const docs = mock.listar();
    const idx = docs.findIndex(d => d.id === id);
    if (idx === -1) throw new Error('Documento não encontrado');
    const novaVersao = (docs[idx].versaoAtual ?? 1) + 1;
    docs[idx] = { ...docs[idx], versaoAtual: novaVersao, nomeArquivoAtual: arquivo.name };
    mock.salvar(docs);
    return {
      id: `v-${Date.now()}`,
      documentoId: id,
      versao: novaVersao,
      nomeArquivo: arquivo.name,
      caminho: `mock/${id}/v${novaVersao}/${arquivo.name}`,
      sindicoId: Number(SINDICO_ID),
      criadaEm: new Date().toISOString(),
    };
  },

  inativar(id: string): Documento {
    const docs = mock.listar();
    const idx = docs.findIndex(d => d.id === id);
    if (idx === -1) throw new Error('Documento não encontrado');
    docs[idx] = { ...docs[idx], status: 'INATIVO' };
    mock.salvar(docs);
    return docs[idx];
  },
};

// ─── Serviço público (tenta backend → cai em mock automaticamente) ────────────

export const documentoService = {
  isMock: false, // atualizado em runtime para a UI exibir aviso

  async listar(incluirInativos = false): Promise<Documento[]> {
    const url = incluirInativos ? `${BASE_URL}?incluirInativos=true` : BASE_URL;
    const result = await fetchComMock(
      () => fetch(url, { signal: AbortSignal.timeout(3000) }),
      () => {
        const todos = mock.listar();
        return incluirInativos ? todos : todos.filter(d => d.status === 'ATIVO');
      }
    );
    if (result.error) throw result.error;
    documentoService.isMock = result.isMock;
    return result.data!;
  },

  async cadastrar(
    nome: string,
    categoria: string,
    dataValidade: string | null,
    arquivo: File
  ): Promise<Documento> {
    const form = new FormData();
    form.append('nome', nome);
    form.append('categoria', categoria);
    if (dataValidade) form.append('dataValidade', dataValidade);
    form.append('arquivo', arquivo);

    const result = await fetchComMock(
      () => fetch(BASE_URL, {
        method: 'POST',
        headers: { 'X-Sindico-Id': SINDICO_ID },
        body: form,
        signal: AbortSignal.timeout(5000),
      }),
      () => mock.cadastrar(nome, categoria, dataValidade, arquivo)
    );
    if (result.error) throw result.error;
    const data = result.data as any;
    return data?.documento ?? data;
  },

  async atualizarArquivo(id: string, arquivo: File): Promise<VersaoDocumento> {
    const form = new FormData();
    form.append('arquivo', arquivo);

    const result = await fetchComMock(
      () => fetch(`${BASE_URL}/${id}`, {
        method: 'PUT',
        headers: { 'X-Sindico-Id': SINDICO_ID },
        body: form,
        signal: AbortSignal.timeout(5000),
      }),
      () => mock.atualizarArquivo(id, arquivo)
    );
    if (result.error) throw result.error;
    return result.data!;
  },

  async inativar(id: string): Promise<Documento> {
    const result = await fetchComMock(
      () => fetch(`${BASE_URL}/${id}/inativar`, {
        method: 'PUT',
        headers: { 'X-Sindico-Id': SINDICO_ID },
        signal: AbortSignal.timeout(3000),
      }),
      () => mock.inativar(id)
    );
    if (result.error) throw result.error;
    const data = result.data as any;
    return data?.documento ?? data;
  },

  async historico(id: string): Promise<VersaoDocumento[]> {
    const result = await fetchComMock(
      () => fetch(`${BASE_URL}/${id}/historico`, { signal: AbortSignal.timeout(3000) }),
      () => [] as VersaoDocumento[]
    );
    if (result.error) throw result.error;
    return result.data!;
  },

  downloadUrl(id: string): string {
    return id.startsWith('mock-') ? '#' : `${BASE_URL}/${id}/download`;
  },
};
