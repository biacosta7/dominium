import type { Orcamento, Despesa, Taxa, Unidade } from '../types/financeiro';

export const financeiroService = {
  async fetchOrcamentos(): Promise<Orcamento[]> {
    const res = await fetch('/financeiro/orcamentos');
    if (!res.ok) throw new Error('Erro ao buscar orçamentos');
    return res.json();
  },

  async createOrcamento(data: { ano: number; valorTotal: number }): Promise<Orcamento> {
    const res = await fetch('/financeiro/orcamentos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao cadastrar orçamento');
    }
    return res.json();
  },

  async fetchOrcamentoPorAno(ano: number): Promise<Orcamento> {
    const res = await fetch(`/financeiro/orcamentos/${ano}`);
    if (!res.ok) throw new Error(`Erro ao buscar orçamento do ano ${ano}`);
    return res.json();
  },

  async fetchSaldoOrcamento(ano: number): Promise<number> {
    const res = await fetch(`/financeiro/orcamentos/${ano}/saldo`);
    if (!res.ok) throw new Error(`Erro ao buscar saldo do orçamento de ${ano}`);
    return res.json(); // returns BigDecimal (number)
  },

  async fetchDespesasPorOrcamento(ano: number, categoria?: string): Promise<Despesa[]> {
    let url = `/financeiro/orcamentos/${ano}/despesas`;
    if (categoria && categoria !== 'TODAS') {
      url += `?categoria=${categoria}`;
    }
    const res = await fetch(url);
    if (!res.ok) throw new Error('Erro ao buscar despesas do orçamento');
    return res.json();
  },

  async createDespesa(data: {
    descricao: string;
    valor: number;
    data: string; // YYYY-MM-DD
    categoria: string;
    tipo: string;
  }): Promise<Despesa> {
    const res = await fetch('/financeiro/despesas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao registrar despesa');
    }
    return res.json();
  },

  async aprovarDespesa(id: number): Promise<Despesa> {
    const res = await fetch(`/financeiro/despesas/${id}/aprovar`, {
      method: 'PUT',
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao aprovar despesa extraordinária');
    }
    return res.json();
  },

  async fetchTaxas(): Promise<Taxa[]> {
    const res = await fetch('/api/taxas');
    if (!res.ok) throw new Error('Erro ao buscar taxas condominiais');
    const taxas = await res.json();
    return taxas.map((taxa: Omit<Taxa, 'status'> & { status: string }) => ({
      ...taxa,
      status: (taxa.status === 'PAGA' ? 'PAGO' : taxa.status) as Taxa['status'],
    })) as Taxa[];
  },

  async gerarTaxa(data: {
    unidadeId: number;
    valorBase: number;
    valorMultas: number;
    dataVencimento: string; // YYYY-MM-DD
  }): Promise<Taxa> {
    const res = await fetch('/api/taxas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao gerar taxa condominial');
    }
    return res.json();
  },

  async registrarPagamentoTaxa(id: number): Promise<Taxa> {
    const res = await fetch(`/api/taxas/${id}/pagamento`, {
      method: 'PUT',
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao registrar pagamento de taxa');
    }
    return res.json();
  },

  async atualizarTaxa(id: number, novoValorBase: number, novasMultas: number): Promise<Taxa> {
    const res = await fetch(`/api/taxas/${id}/valor`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ novoValorBase, novasMultas }),
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || 'Erro ao atualizar taxa condominial');
    }
    return res.json();
  },

  async fetchUnits(): Promise<Unidade[]> {
    const res = await fetch('/unidades');
    if (!res.ok) throw new Error('Erro ao buscar unidades');
    return res.json();
  },

  async fetchMoradoresDaUnidade(unidadeId: number) {
    const res = await fetch(`/api/unidades/${unidadeId}/moradores`);
    if (!res.ok) throw new Error('Erro ao buscar moradores da unidade');
    return res.json();
  }
};
