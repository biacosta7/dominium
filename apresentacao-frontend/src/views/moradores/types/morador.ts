export interface Usuario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  cpf: string;
  tipo: 'MORADOR' | 'SINDICO' | 'PRESTADOR';
}

export interface Vinculo {
  id: number;
  unidadeId: number;
  usuario: Usuario;
  tipo: 'TITULAR' | 'DEPENDENTE';
  status: 'ATIVO' | 'INATIVO';
}

export interface Unidade {
  id: number;
  numero: string;
  bloco: string;
  proprietarioId?: number;
  inquilinoId?: number;
  status: 'ADIMPLENTE' | 'INADIMPLENTE' | 'EM_NEGOCIACAO';
  saldoDevedor: number;
}
