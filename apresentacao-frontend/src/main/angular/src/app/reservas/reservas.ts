import { Component, Input, OnInit, OnChanges, SimpleChanges, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

export interface Reserva {
  reservaId: string;
  data: string;
  horaInicio: string;
  horaFim: string;
  status: string;
  areaComumId: number;
  nomeArea: string;
}

interface ReservaApi {
  id: string;
  status: string;
  areaComumId: number;
  data: string;
  horaInicio: string;
  horaFim: string;
}

interface DiaCalendario {
  dia: number;
  mesAtual: boolean;
  ehHoje: boolean;
  reservas: Reserva[];
}

export interface AreaCatalogo {
  id: number;
  nome: string;
  icone: string;
  horarioLivre: string;
}

export interface DisponibilidadeArea {
  area: AreaCatalogo;
  ocupada: boolean;
  detalhe: string;
  badgeClass: string;
  badgeLabel: string;
}

interface NovaReservaForm {
  areaComumId: number;
  data: string;
  horaInicio: string;
  horaFim: string;
}

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservas.html',
  styleUrl: './reservas.css',
})
export class Reservas implements OnInit, OnChanges {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);

  @Input() usuarioId = 1;
  @Input() unidadeId = 1;

  readonly MESES = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro',
  ];
  readonly DIAS_SEMANA = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SÁB'];

  readonly AREAS: AreaCatalogo[] = [
    { id: 1, nome: 'Churrasqueira 1', icone: '🍖', horarioLivre: '09:00 - 22:00' },
    { id: 2, nome: 'Churrasqueira 2', icone: '🍖', horarioLivre: '09:00 - 22:00' },
    { id: 3, nome: 'Salão de Festas', icone: '🎉', horarioLivre: '10:00 - 00:00' },
    { id: 4, nome: 'Piscina', icone: '🏊', horarioLivre: '08:00 - 20:00' },
  ];

  hoje = new Date();
  anoAtual = this.hoje.getFullYear();
  mesAtual = this.hoje.getMonth();

  semanas: DiaCalendario[][] = [];
  reservasDoPeriodo: Reserva[] = [];
  reservasUsuario: Reserva[] = [];
  disponibilidadeHoje: DisponibilidadeArea[] = [];
  diaSelecionado: DiaCalendario | null = null;

  modalAberto = false;
  salvando = false;
  mensagem: string | null = null;
  novaReserva: NovaReservaForm = this.formularioVazio();

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('usuarioId');
    if (param) {
      this.usuarioId = Number(param);
    }
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
    this.atualizarCalendario();
  }

  proximoMes(): void {
    if (this.mesAtual === 11) {
      this.mesAtual = 0;
      this.anoAtual++;
    } else {
      this.mesAtual++;
    }
    this.diaSelecionado = null;
    this.atualizarCalendario();
  }

  selecionarDia(dia: DiaCalendario): void {
    if (!dia.mesAtual || dia.reservas.length === 0) return;
    this.diaSelecionado = this.diaSelecionado?.dia === dia.dia ? null : dia;
  }

  abrirModalReserva(areaComumId?: number): void {
    this.novaReserva = {
      ...this.formularioVazio(),
      areaComumId: areaComumId ?? this.AREAS[0].id,
      data: this.hojeIso(),
    };
    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
  }

  criarReserva(): void {
    if (this.salvando) return;
    this.salvando = true;
    this.http
      .post<ReservaApi>('/reservas', {
        areaComumId: this.novaReserva.areaComumId,
        unidadeId: this.unidadeId,
        usuarioId: this.usuarioId,
        data: this.novaReserva.data,
        horaInicio: this.novaReserva.horaInicio,
        horaFim: this.novaReserva.horaFim,
      })
      .subscribe({
        next: () => {
          this.salvando = false;
          this.modalAberto = false;
          this.mostrarMensagem('Reserva criada com sucesso.');
          this.carregar();
        },
        error: () => {
          this.salvando = false;
          this.mostrarMensagem('Não foi possível criar a reserva.');
        },
      });
  }

  cancelar(reservaId: string): void {
    this.http.put(`/reservas/${reservaId}/cancelar`, null).subscribe({
      next: () => {
        this.mostrarMensagem('Reserva cancelada.');
        this.carregar();
      },
      error: () => this.mostrarMensagem('Não foi possível cancelar a reserva.'),
    });
  }

  podeCancelar(reserva: Reserva): boolean {
    return reserva.status !== 'CANCELADA' && reserva.status !== 'CONCLUIDA';
  }

  rotuloStatus(status: string): string {
    return status.replaceAll('_', ' ').toLowerCase();
  }

  classeStatus(status: string): string {
    return 'status-' + status.toLowerCase();
  }

  formatarData(data: string): string {
    const [ano, mes, dia] = data.split('-');
    const d = new Date(Number(ano), Number(mes) - 1, Number(dia));
    const diaSemana = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'][d.getDay()];
    return `${dia}/${mes}/${ano} · ${diaSemana}`;
  }

  formatarHorario(inicio: string, fim: string): string {
    return `${this.horaCurta(inicio)} às ${this.horaCurta(fim)}`;
  }

  private carregar(): void {
    this.http.get<ReservaApi[]>(`/reservas/usuario/${this.usuarioId}`).subscribe({
      next: (reservas) => {
        this.reservasUsuario = reservas
          .map((r) => this.mapear(r))
          .filter((r) => r.status !== 'CANCELADA')
          .sort((a, b) => a.data.localeCompare(b.data));
        this.reservasDoPeriodo = this.reservasUsuario.filter((r) => this.pertenceAoMes(r.data));
        this.atualizarDisponibilidade();
        this.construirGrade();
      },
      error: () => {
        this.reservasUsuario = [];
        this.reservasDoPeriodo = [];
        this.disponibilidadeHoje = this.AREAS.map((area) => this.disponivel(area));
        this.construirGrade();
      },
    });
  }

  private atualizarCalendario(): void {
    this.reservasDoPeriodo = this.reservasUsuario.filter((r) => this.pertenceAoMes(r.data));
    this.construirGrade();
  }

  private atualizarDisponibilidade(): void {
    const hoje = this.hojeIso();
    this.disponibilidadeHoje = this.AREAS.map((area) => {
      const ocupacao = this.reservasUsuario.find(
        (r) =>
          r.data === hoje &&
          r.areaComumId === area.id &&
          r.status !== 'CANCELADA' &&
          r.status !== 'CONCLUIDA',
      );
      if (!ocupacao) {
        return this.disponivel(area);
      }
      return {
        area,
        ocupada: true,
        detalhe: `Reservada: ${this.horaCurta(ocupacao.horaInicio)} às ${this.horaCurta(ocupacao.horaFim)}`,
        badgeClass: 'badge-yellow',
        badgeLabel: 'Ocupada',
      };
    });
  }

  private disponivel(area: AreaCatalogo): DisponibilidadeArea {
    return {
      area,
      ocupada: false,
      detalhe: `Livre: ${area.horarioLivre}`,
      badgeClass: 'badge-green',
      badgeLabel: area.id === 4 ? 'Livre' : 'Disponível',
    };
  }

  private mapear(r: ReservaApi): Reserva {
    const catalogo = this.AREAS.find((a) => a.id === r.areaComumId);
    return {
      reservaId: r.id,
      data: r.data,
      horaInicio: r.horaInicio,
      horaFim: r.horaFim,
      status: r.status,
      areaComumId: r.areaComumId,
      nomeArea: catalogo?.nome ?? `Área ${r.areaComumId}`,
    };
  }

  private pertenceAoMes(data: string): boolean {
    const [ano, mes] = data.split('-').map(Number);
    return ano === this.anoAtual && mes === this.mesAtual + 1;
  }

  private hojeIso(): string {
    const mm = String(this.hoje.getMonth() + 1).padStart(2, '0');
    const dd = String(this.hoje.getDate()).padStart(2, '0');
    return `${this.hoje.getFullYear()}-${mm}-${dd}`;
  }

  private horaCurta(hora: string): string {
    return hora?.slice(0, 5) ?? hora;
  }

  private formularioVazio(): NovaReservaForm {
    return {
      areaComumId: 1,
      data: '',
      horaInicio: '09:00',
      horaFim: '12:00',
    };
  }

  private mostrarMensagem(texto: string): void {
    this.mensagem = texto;
    setTimeout(() => {
      if (this.mensagem === texto) {
        this.mensagem = null;
      }
    }, 3500);
  }

  private construirGrade(): void {
    const primeiroDia = new Date(this.anoAtual, this.mesAtual, 1).getDay();
    const diasNoMes = new Date(this.anoAtual, this.mesAtual + 1, 0).getDate();
    const diasMesAnterior = new Date(this.anoAtual, this.mesAtual, 0).getDate();

    const reservaMap = new Map<string, Reserva[]>();
    for (const r of this.reservasDoPeriodo) {
      if (!reservaMap.has(r.data)) reservaMap.set(r.data, []);
      reservaMap.get(r.data)!.push(r);
    }

    const dias: DiaCalendario[] = [];

    for (let i = primeiroDia - 1; i >= 0; i--) {
      dias.push({ dia: diasMesAnterior - i, mesAtual: false, ehHoje: false, reservas: [] });
    }

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
        reservas: reservaMap.get(chave) ?? [],
      });
    }

    const restante = dias.length % 7 === 0 ? 0 : 7 - (dias.length % 7);
    for (let d = 1; d <= restante; d++) {
      dias.push({ dia: d, mesAtual: false, ehHoje: false, reservas: [] });
    }

    this.semanas = [];
    for (let i = 0; i < dias.length; i += 7) {
      this.semanas.push(dias.slice(i, i + 7));
    }
  }
}
