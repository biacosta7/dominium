import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-reservation-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-modal.component.html',
  styleUrls: ['./reservation-modal.component.css']
})
export class ReservationModalComponent {
  @Input() isOpen = false;
  @Input() data = '';
  @Input() espaco = '';
  @Output() close = new EventEmitter();
  @Output() confirm = new EventEmitter<any>();

  nomeResponsavel = '';
  startTime = '09:00';
  endTime = '12:00';
  observacoes = '';

  confirmar() {
    if(!this.nomeResponsavel) {
      alert("Por favor, digite o nome do responsável.");
      return;
    }
    this.confirm.emit({
      startTime: this.startTime,
      endTime: this.endTime,
      nome: this.nomeResponsavel,
      obs: this.observacoes
    });
  }
}