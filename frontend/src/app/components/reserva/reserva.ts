import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservaService } from '../../services/reserva';
import { Reserva } from '../../models/reserva';
import { NovaReservaComponent } from '../nova-reserva/nova-reserva';

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, NovaReservaComponent],
  templateUrl: './reserva.html',
  styleUrls: ['./reserva.css']
})
export class ReservaComponent implements OnInit {
  
  // Lista que armazenará as reservas vindas do Back-end
  minhasReservas = signal<Reserva[]>([]);
  
  // Controle de visibilidade do Modal (Imagem 3)
  exibirModal = false;

  // Controle do Feedback de sucesso (Imagem 4)
  mostrarFeedback = false;
  mensagemFeedback = '';

  constructor(private reservaService: ReservaService) {}

  ngOnInit(): void {
    this.carregarReservas();
  }

  carregarReservas() {
    // Usando o ID 1 como exemplo (no futuro, viria do usuário logado)
    this.reservaService.listarPorUnidade(1).subscribe({
      next: (dados) => {
        this.minhasReservas.set(dados);
      },
      error: (err) => {
        console.error('Erro ao buscar reservas:', err);
      }
    });
  }

  abrirModal() {
    this.exibirModal = true;
  }

  fecharModal(atualizar: boolean) {
    this.exibirModal = false;
    if (atualizar) {
      this.carregarReservas();
      this.dispararFeedback('Reserva criada com sucesso!');
    }
  }

  cancelarReserva(id: number) {
    if (confirm('Deseja realmente cancelar esta reserva?')) {
      this.reservaService.cancelar(id).subscribe({
        next: () => {
          this.carregarReservas();
          this.dispararFeedback('Reserva cancelada');
        },
        error: (err) => alert('Erro ao cancelar')
      });
    }
  }

  private dispararFeedback(mensagem: string) {
    this.mensagemFeedback = mensagem;
    this.mostrarFeedback = true;
    // Esconde o feedback após 3 segundos (como um Toast)
    setTimeout(() => this.mostrarFeedback = false, 3000);
  }
}