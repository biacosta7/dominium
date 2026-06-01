export interface Reserva {
  reservaId: string;
  data: string;
  horaInicio: string;
  horaFim: string;
  status: string;
  areaComumId: number;
  nomeArea: string;
}

export interface AreaCatalogo {
  id: number;
  nome: string;
  icone: string;
  horarioLivre: string;
}

export interface DisponibilidadeArea {
  area: AreaCatalogo;
  ocupada: boolean;
  detalhe: string;
  badgeClass: string;
  badgeLabel: string;
}

export interface DiaCalendario {
  dia: number;
  mesAtual: boolean;
  ehHoje: boolean;
  reservas: Reserva[];
}