import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaService, Reserva } from './reserva.service';
import { ReservationModalComponent } from './reservation-modal/reservation-modal.component';

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule, ReservationModalComponent],
  templateUrl: './reserva.component.html',
  styleUrls: ['./reserva.component.css']
})

export class ReservaComponent implements OnInit {
  // Configurações de Data (Padrão: hoje)
  selectedDate: string = new Date().toISOString().split('T')[0];
  
  // Espaços conforme as imagens
  espacos = [
    { id: 'churrasqueira', nome: 'Churrasqueira', icon: 'filter_frames', limite: 20, func: '8h às 22h' },
    { id: 'piscina', nome: 'Área da Piscina', icon: 'pool', limite: 30, func: '7h às 20h' },
    { id: 'academia', nome: 'Academia', icon: 'fitness_center', limite: 10, func: '6h às 23h' },
    { id: 'salao', nome: 'Salão de Festas', icon: 'celebration', limite: 50, func: '10h às 23h' },
    { id: 'coworking', nome: 'Espaço Coworking', icon: 'business_center', limite: 15, func: '8h às 20h' }
  ];

  selectedSpace: any = this.espacos[0]; // Começa com churrasqueira selecionada
  reservasDoDia: Reserva[] = [];
  horariosDisponiveis: string[] = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00'];
  
  isModalOpen = false;

  constructor(private service: ReservaService) {}

  ngOnInit() {
    this.carregarReservas();
  }

  carregarReservas() {
    // Busca no back-end as reservas da unidade e filtra pela data/espaço selecionados
    this.service.listarPorUnidade(1).subscribe({
      next: (res) => {
        this.reservasDoDia = res.filter(r => r.dataReserva === this.selectedDate);
      },
      error: (err) => console.error('Erro ao carregar:', err)
    });
  }

  selecionarEspaco(espaco: any) {
    this.selectedSpace = espaco;
    this.carregarReservas();
  }

  // Verifica se o horário está dentro de uma reserva existente para mudar a cor no front
  isHorarioOcupado(hora: string): boolean {
    return this.reservasDoDia.some(r => hora >= r.horaInicio && hora < r.horaFim);
  }

  abrirModal() {
    this.isModalOpen = true;
  }

  confirmarReserva(dados: any) {
    const nova: Reserva = {
      unidadeId: 1, // Exemplo: vindo de um login
      pessoaId: 1,
      dataReserva: this.selectedDate,
      horaInicio: dados.horaInicio,
      horaFim: dados.horaFim,
      espaco: this.selectedSpace.nome // Adicione este campo na sua interface se necessário
    };

    this.service.criar(nova).subscribe(() => {
      this.carregarReservas();
      this.isModalOpen = false;
    });
  }
}