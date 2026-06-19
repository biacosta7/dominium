export type StatusAssembleia = 'AGENDADA' | 'CANCELADA' | 'ENCERRADA';
export type TipoAssembleia = 'ORDINARIA' | 'EXTRAORDINARIA';

export interface Assembleia {
  id: number;
  titulo: string;
  dataHora: string;
  local: string;
  pauta: string[];
  status: StatusAssembleia;
  tipo: TipoAssembleia;
  sindicoId: number;
  dataCriacao: string;
}

export interface CriarAssembleiaRequest {
  titulo: string;
  dataHora: string;
  local: string;
  pauta: string[];
  tipo: TipoAssembleia;
}

export type EditarAssembleiaRequest = CriarAssembleiaRequest;
