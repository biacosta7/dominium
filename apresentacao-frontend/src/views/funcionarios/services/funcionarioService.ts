import type { CadastrarFuncionarioRequest, EditarFuncionarioRequest, Funcionario } from '../types/funcionario';

const BASE_URL = '/funcionarios';
const SINDICO_ID = '2';

async function erroDe(res: Response, fallback: string): Promise<Error> {
  try {
    const body = await res.json();
    return new Error(body.message || fallback);
  } catch {
    return new Error(fallback);
  }
}

export const funcionarioService = {
  async listar(): Promise<Funcionario[]> {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw await erroDe(res, 'Erro ao listar funcionários');
    return res.json();
  },

  async cadastrar(data: CadastrarFuncionarioRequest): Promise<Funcionario> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'X-Sindico-Id': SINDICO_ID },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao cadastrar funcionário');
    return res.json();
  },

  async editar(id: string, data: EditarFuncionarioRequest): Promise<Funcionario> {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'X-Sindico-Id': SINDICO_ID },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw await erroDe(res, 'Erro ao editar funcionário');
    return res.json();
  },
};
