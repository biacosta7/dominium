import type { Unidade, Vinculo } from '../types/morador';

export const moradorService = {
  async fetchUnits(): Promise<Unidade[]> {
    const res = await fetch('/unidades');
    if (!res.ok) throw new Error('Erro ao buscar unidades');
    return res.json();
  },

  async fetchMoradoresDaUnidade(unidadeId: number): Promise<Vinculo[]> {
    const res = await fetch(`/api/unidades/${unidadeId}/moradores`);
    if (!res.ok) throw new Error('Erro ao buscar moradores da unidade');
    return res.json();
  },

  async addMorador(unidadeId: number, data: {
    nome: string;
    email: string;
    telefone: string;
    cpf: string;
    tipoViculo: 'TITULAR' | 'DEPENDENTE';
  }, requesterId?: number): Promise<Vinculo> {
    const payload = {
      novoUsuario: {
        nome: data.nome,
        email: data.email,
        senha: '123456', // default password
        telefone: data.telefone,
        cpf: data.cpf,
        tipo: 'MORADOR'
      },
      tipo: data.tipoViculo
    };

    const res = await fetch(`/api/unidades/${unidadeId}/moradores`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'X-Requester-Id': requesterId ? String(requesterId) : '2'
      },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao adicionar morador');
    }
    return res.json();
  },

  async updateMorador(
    vinculoId: number,
    usuarioId: number,
    data: {
      nome: string;
      email: string;
      telefone: string;
      cpf: string;
      tipoViculo: 'TITULAR' | 'DEPENDENTE';
    },
    requesterId?: number
  ): Promise<void> {
    // 1. Update Vínculo Type
    const vinculoPayload = {
      tipo: data.tipoViculo
    };
    const vinculoRes = await fetch(`/api/moradores/${vinculoId}`, {
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json',
        'X-Requester-Id': requesterId ? String(requesterId) : '2'
      },
      body: JSON.stringify(vinculoPayload)
    });
    if (!vinculoRes.ok) {
      const errText = await vinculoRes.text();
      throw new Error(errText || 'Erro ao atualizar vínculo do morador');
    }

    // 2. Update User details
    const usuarioPayload = {
      nome: data.nome,
      email: data.email,
      senha: '', // blank/null is allowed since we updated the backend to allow blank passwords on update
      telefone: data.telefone,
      cpf: data.cpf,
      tipo: 'MORADOR'
    };
    const usuarioRes = await fetch(`/usuarios/${usuarioId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(usuarioPayload)
    });
    if (!usuarioRes.ok) {
      const errText = await usuarioRes.text();
      throw new Error(errText || 'Erro ao atualizar dados cadastrais do morador');
    }
  },

  async removeMorador(vinculoId: number, requesterId?: number): Promise<void> {
    const res = await fetch(`/api/moradores/${vinculoId}`, {
      method: 'DELETE',
      headers: {
        'X-Requester-Id': requesterId ? String(requesterId) : '2'
      }
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao remover morador');
    }
  }
};
