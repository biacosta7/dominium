export type TipoVinculo = 'CLT' | 'TERCEIRIZADO' | 'EVENTUAL';
export type StatusFuncionario = 'ATIVO' | 'INATIVO' | 'BLOQUEADO';

export interface Funcionario {
  id: string;
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  tipoVinculo: TipoVinculo;
  status: StatusFuncionario;
  contratoInicio: string;
  contratoFim: string;
  valorMensal: number | null;
}

export interface CadastrarFuncionarioRequest {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  tipoVinculo: TipoVinculo;
  contratoInicio: string;
  contratoFim: string;
  valorMensal: number | null;
}

export type EditarFuncionarioRequest = CadastrarFuncionarioRequest;
