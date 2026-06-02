export type OpcaoVoto = 'FAVOR' | 'CONTRA' | 'ABSTENCAO';

export interface VotarRequest {
  pautaId: number;
  usuarioId: number;
  unidadeId: number;
  opcao: OpcaoVoto;
}

export interface VotoResponse {
  id: number;
  pautaId: number;
  unidadeId: number;
  usuarioId: number;
  opcao: string;
}

export interface ResumoVotos {
  favor: number;
  contra: number;
  abstencao: number;
  total: number;
}