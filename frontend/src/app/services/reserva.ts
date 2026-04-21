import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva } from '../models/reserva';

@Injectable({
  providedIn: 'root'
})

export class ReservaService {
    private readonly API = 'http://localhost:8080/reservas'; //mudar se colocar no banco
    constructor(private http: HttpClient) {}

    criar(reserva: Reserva): Observable<Reserva> {
    return this.http.post<Reserva>(this.API, reserva);
    }

    listarPorUnidade(unidadeId: number): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.API}/unidade/${unidadeId}`);
    }

    atualizar(id: number, reserva: Reserva): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.API}/${id}`, reserva);
    }

    cancelar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
    }
}