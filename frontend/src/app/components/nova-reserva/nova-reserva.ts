import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaService } from '../../services/reserva';

@Component({
  selector: 'app-nova-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nova-reserva.html',
  styleUrls: ['./nova-reserva.css']
})
export class NovaReservaComponent {
  @Output() fechado = new EventEmitter<boolean>();
  
  data: string = '';
  horario: string = '08:00 às 12:00';

  constructor(private service: ReservaService) {}

  salvar() {
    const [hInicio, hFim] = this.horario.split(' às ').map(h => h + ':00');
    this.service.criar({
      unidadeId: 1, pessoaId: 1,
      dataReserva: this.data,
      horaInicio: hInicio, horaFim: hFim
    }).subscribe(() => this.fechado.emit(true));
  }
}