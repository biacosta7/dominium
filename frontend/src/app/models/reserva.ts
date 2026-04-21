export interface Reserva {
  id?: number;
  unidadeId: number;
  pessoaId: number;
  dataReserva: string; 
  horaInicio: string;  
  horaFim: string;     
  status?: string;     
}