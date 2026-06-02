export type TipoQuorum = 'SIMPLES' | 'QUALIFICADO';
export type TipoMaioria = 'SIMPLES' | 'ABSOLUTA' | 'QUALIFICADA';
export type StatusPauta = 'ABERTA' | 'ENCERRADA';
export type ResultadoPauta = 'APROVADO' | 'REJEITADO' | 'ADIADO';

export interface Pauta {
  id: number;
  assembleiaId: number;
  titulo: string;
  descricao: string;
  status: StatusPauta;
  resultado: ResultadoPauta | null;
  tipoQuorum?: TipoQuorum;
  tipoMaioria?: TipoMaioria;
}

export interface CriarPautaRequest {
  assembleiaId: number;
  titulo: string;
  descricao: string;
  quorum: TipoQuorum;
  maioria: TipoMaioria;
}