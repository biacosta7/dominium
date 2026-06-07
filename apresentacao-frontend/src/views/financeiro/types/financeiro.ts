export interface Orcamento {
  id: number;
  ano: number;
  valorTotal: number;
  valorGasto: number;
  saldoDisponivel: number;
}

export type CategoriaDespesa = 'MANUTENCAO' | 'UTILIDADES' | 'PESSOAL' | 'SEGURANCA' | 'OUTROS';
export type TipoDespesa = 'ORDINARIA' | 'EXTRAORDINARIA';
export type StatusDespesa = 'PENDENTE' | 'APROVADA' | 'REJEITADA';

export interface Despesa {
  id: number;
  descricao: string;
  valor: number;
  data: string; // YYYY-MM-DD
  categoria: CategoriaDespesa;
  tipo: TipoDespesa;
  status: StatusDespesa;
}

export type StatusTaxa = 'PAGO' | 'PENDENTE' | 'ATRASADA';

export interface Taxa {
  id: number;
  unidadeId: number;
  valorBase: number;
  valorMultas: number;
  valorTotal: number;
  dataVencimento: string; // YYYY-MM-DD
  dataPagamento: string | null;
  status: StatusTaxa;
}

export interface Unidade {
  id: number;
  numero: string;
  bloco: string;
  proprietarioId: number | null;
  inquilinoId: number | null;
  status: 'ADIMPLENTE' | 'INADIMPLENTE';
  saldoDevedor: number;
}
