import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaService, Reserva } from './reserva.service';

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reserva.component.html',
  styleUrls: ['./reserva.component.css']
})
export class ReservaComponent implements OnInit {

  // 🔹 estado principal
  reservas: Reserva[] = [];

  selectedDate: string = new Date().toISOString().split('T')[0];
  selectedSpace: string = 'piscina';

  unidadeId = 1; 
  pessoaId = 1;
  //this.unidadeId = Number(localStorage.getItem('unidadeId'));
  //this.pessoaId = Number(localStorage.getItem('pessoaId'));

  // 🔹 controle de UI
  loading = false;
  erro = '';

  // 🔹 espaços disponíveis
  spaces = [
    'piscina',
    'churrasqueira',
    'academia',
    'salao',
    'coworking'
  ];

  constructor(private service: ReservaService) {}

  ngOnInit(): void {
    this.carregarReservas();
  }

  proximaHora(hora: string): string {
  const [h, m] = hora.split(':').map(Number);
  return `${(h + 1).toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
    }

  // =============================
  // 📥 CARREGAR
  // =============================
  carregarReservas() {
    this.loading = true;

    this.service.listarPorUnidade(this.unidadeId).subscribe({
      next: (res) => {
        this.reservas = res;
        this.loading = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar reservas';
        this.loading = false;
      }
    });
  }

  // =============================
  // ➕ CRIAR
  // =============================
  criarReserva(horaInicio: string, horaFim: string) {

    const nova: Reserva = {
      unidadeId: this.unidadeId,
      pessoaId: this.pessoaId,
      dataReserva: this.selectedDate,
      horaInicio,
      horaFim,
      espacoReservado: this.selectedSpace
    };

    this.service.criar(nova).subscribe({
      next: () => {
        this.carregarReservas();
        this.erro = '';
      },
      error: (err) => {
        this.erro = err.error?.message || 'Erro ao criar reserva';
      }
    });
  }

  // =============================
  // ❌ CANCELAR
  // =============================
  cancelarReserva(id: number) {
    this.service.cancelar(id).subscribe(() => {
      this.carregarReservas();
    });
  }

  // =============================
  // 🔍 FILTROS
  // =============================

  reservasDoDia(): Reserva[] {
    return this.reservas.filter(r => r.dataReserva === this.selectedDate);
  }

  reservasDoEspaco(): Reserva[] {
    return this.reservasDoDia().filter(
      r => r.espacoReservado === this.selectedSpace
    );
  }

  // =============================
  // ⛔ BLOQUEIO DE HORÁRIO
  // =============================
  isHorarioOcupado(hora: string): boolean {
    return this.reservasDoEspaco().some(r => {
      return hora >= r.horaInicio && hora < r.horaFim;
    });
  }

}