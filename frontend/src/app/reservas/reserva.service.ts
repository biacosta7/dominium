import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface Reserva {
  id?: number;
  unidadeId: number;
  pessoaId: number;
  dataReserva: string;
  horaInicio: string;
  horaFim: string;
  espacoReservado: string;
  status?: string;
}


@Injectable({
  providedIn: 'root'
})
export class ReservaService {


  private API = 'http://localhost:8080/reservas';


  constructor(private http: HttpClient) {}


  criar(reserva: Reserva): Observable<Reserva> {
    return this.http.post<Reserva>(this.API, reserva);
  }


  listarPorUnidade(id: number): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.API}/unidade/${id}`);
  }


  atualizar(id: number, reserva: Reserva): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.API}/${id}`, reserva);
  }


  cancelar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
