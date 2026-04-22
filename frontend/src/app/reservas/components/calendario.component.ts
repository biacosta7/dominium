import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  CalendarModule,
  CalendarView,
  DateAdapter
} from 'angular-calendar';

import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';

@Component({
  selector: 'app-calendario',
  standalone: true,
  imports: [CommonModule, CalendarModule],
  providers: [
    {
      provide: DateAdapter,
      useFactory: adapterFactory
    }
  ],
  templateUrl: './calendario.component.html',
  styleUrls: ['./calendario.component.css']
})
export class CalendarioComponent {

  view: CalendarView = CalendarView.Month;
  viewDate: Date = new Date();

  CalendarView = CalendarView;

  @Output() dataSelecionada = new EventEmitter<string>();

  diaClicado(event: any) {
    const data = event.day.date;
    const formatada = data.toISOString().split('T')[0];
    this.dataSelecionada.emit(formatada);
  }
}