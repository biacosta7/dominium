export interface Documento {
  id: string;
  nome: string;
  categoria: string;
  status: string;
  dataValidade: string | null;
  dataCriacao: string;
  versaoAtual: number | null;
  nomeArquivoAtual: string | null;
}

export interface VersaoDocumento {
  id: string;
  documentoId: string;
  versao: number;
  nomeArquivo: string;
  caminho: string;
  sindicoId: number;
  criadaEm: string;
}

export type CategoriaDocumento =
  | 'ATA'
  | 'CONTRATO'
  | 'REGULAMENTO'
  | 'APOLICE'
  | 'ALVARA'
  | 'OUTRO';

export const CATEGORIAS: Record<CategoriaDocumento, string> = {
  ATA: 'Ata',
  CONTRATO: 'Contrato',
  REGULAMENTO: 'Regulamento',
  APOLICE: 'Apólice',
  ALVARA: 'Alvará',
  OUTRO: 'Outro',
};
