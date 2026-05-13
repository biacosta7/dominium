import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

export interface Reserva {
  reservaId: string;
  data: string;
  horaInicio: string;
  horaFim: string;
  status: string;
  areaComumId: number;
}

interface DiaCalendario {
  dia: number;
  mesAtual: boolean;
  ehHoje: boolean;
  reservas: Reserva[];
}

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reservas.html',
  styleUrl: './reservas.css',
})
export class Reservas implements OnInit, OnChanges {

  @Input() usuarioId!: number;

  readonly MESES = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ];
  readonly DIAS_SEMANA = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SÁB'];
 
  hoje = new Date();
  anoAtual = this.hoje.getFullYear();
  mesAtual = this.hoje.getMonth(); // 0-indexed
 
  semanas: DiaCalendario[][] = [];
  reservasDoPeriodo: Reserva[] = [];
  diaSelecionado: DiaCalendario | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.carregar();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['usuarioId'] && !changes['usuarioId'].firstChange) {
      this.carregar();
    }
  }
 
  get tituloMes(): string {
    return `${this.MESES[this.mesAtual]} ${this.anoAtual}`;
  }
 
  mesAnterior(): void {
    if (this.mesAtual === 0) {
      this.mesAtual = 11;
      this.anoAtual--;
    } else {
      this.mesAtual--;
    }
    this.diaSelecionado = null;
    this.carregar();
  }
 
  proximoMes(): void {
    if (this.mesAtual === 11) {
      this.mesAtual = 0;
      this.anoAtual++;
    } else {
      this.mesAtual++;
    }
    this.diaSelecionado = null;
    this.carregar();
  }
 
  selecionarDia(dia: DiaCalendario): void {
    if (!dia.mesAtual || dia.reservas.length === 0) return;
    this.diaSelecionado = this.diaSelecionado?.dia === dia.dia ? null : dia;
  }

  private carregar(): void {
    this.http
      .get<Reserva[]>(
        `/reservas/usuario/${this.usuarioId}`   // endpoint que já existe
      )
      .subscribe({
        next: (reservas) => {
          // filtra por mês/ano aqui no front mesmo
          this.reservasDoPeriodo = reservas.filter(r => {
            const d = new Date(r.data);
            return d.getFullYear() === this.anoAtual &&
                   d.getMonth() === this.mesAtual;
          });
          this.construirGrade();
        },
        error: () => {
          this.reservasDoPeriodo = [];
          this.construirGrade();
        }
      });
  }

  private construirGrade(): void {
    const primeiroDia = new Date(this.anoAtual, this.mesAtual, 1).getDay();
    const diasNoMes = new Date(this.anoAtual, this.mesAtual + 1, 0).getDate();
    const diasMesAnterior = new Date(this.anoAtual, this.mesAtual, 0).getDate();
 
    const reservaMap = new Map<string, Reserva[]>();
    for (const r of this.reservasDoPeriodo) {
      const chave = r.data; // 'YYYY-MM-DD'
      if (!reservaMap.has(chave)) reservaMap.set(chave, []);
      reservaMap.get(chave)!.push(r);
    }
 
    const dias: DiaCalendario[] = [];
 
    // dias do mês anterior
    for (let i = primeiroDia - 1; i >= 0; i--) {
      dias.push({ dia: diasMesAnterior - i, mesAtual: false, ehHoje: false, reservas: [] });
    }
 
    // dias do mês atual
    for (let d = 1; d <= diasNoMes; d++) {
      const mm = String(this.mesAtual + 1).padStart(2, '0');
      const dd = String(d).padStart(2, '0');
      const chave = `${this.anoAtual}-${mm}-${dd}`;
 
      const ehHoje =
        d === this.hoje.getDate() &&
        this.mesAtual === this.hoje.getMonth() &&
        this.anoAtual === this.hoje.getFullYear();
 
      dias.push({
        dia: d,
        mesAtual: true,
        ehHoje,
        reservas: reservaMap.get(chave) ?? []
      });
    }
 
    // completar última semana
    const restante = dias.length % 7 === 0 ? 0 : 7 - (dias.length % 7);
    for (let d = 1; d <= restante; d++) {
      dias.push({ dia: d, mesAtual: false, ehHoje: false, reservas: [] });
    }
 
    // agrupar em semanas
    this.semanas = [];
    for (let i = 0; i < dias.length; i += 7) {
      this.semanas.push(dias.slice(i, i + 7));
    }
  }
}
