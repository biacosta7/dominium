import { useState, useEffect } from "react";
import Calendario from "./components/Calendario";
import Disponibilidade from "./components/Disponibilidade";
import ModalReserva from "./components/ModalReserva";
import ReservaCard from "./components/ReservaCard";
import { buscarReservas, cancelarReserva, criarReserva, atualizarReserva } from "./services/reservaService";
import type { Reserva, DiaCalendario, DisponibilidadeArea, AreaCatalogo } from "./types/reserva";
import "./Reserva.css";

interface ReservaProps {
  onVoltar: () => void;
}

const areasCatalogo: AreaCatalogo[] = [
  { id: 1, nome: "Churrasqueira 1", icone: "🍖", horarioLivre: "09h às 23h" },
  { id: 2, nome: "Churrasqueira 2", icone: "🥩", horarioLivre: "12h às 23h" },
  { id: 3, nome: "Salão de Festas", icone: "🎉", horarioLivre: "08h às 02h" },
  { id: 4, nome: "Piscina (Espaço Gourmet)", icone: "🏊", horarioLivre: "09h às 22h" },
];

export default function ReservaView({ onVoltar }: ReservaProps) {
  const [reservas, setReservas] = useState<Reserva[]>([]);
  const [modalAberto, setModalAberto] = useState(false);
  const [diaSelecionado, setDiaSelecionado] = useState<DiaCalendario | null>(null);
  const [reservaEmEdicao, setReservaEmEdicao] = useState<Reserva | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // States to control active calendar month/year dynamically
  const [anoAtivo, setAnoAtivo] = useState(new Date().getFullYear());
  const [mesAtivo, setMesAtivo] = useState(new Date().getMonth());

  const showToast = (msg: string) => {
    setToast(msg);
    setTimeout(() => setToast(null), 3000);
  };

  const carregarReservas = async () => {
    setLoading(true);
    try {
      const response = await buscarReservas(1); // User Ana Lima ID 1
      const mappedReservas: Reserva[] = response.data.map((res: any) => ({
        reservaId: res.id,
        data: res.data,
        horaInicio: res.horaInicio,
        horaFim: res.horaFim,
        status: res.status,
        areaComumId: res.areaComumId,
        nomeArea: res.nomeArea
      }));
      setReservas(mappedReservas);
    } catch (error) {
      console.error("Erro ao carregar reservas", error);
      showToast("Erro ao carregar reservas do servidor.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarReservas();
  }, []);

  const handleCancelar = async (id: string) => {
    try {
      await cancelarReserva(id);
      showToast("Reserva cancelada com sucesso!");
      carregarReservas();
      if (diaSelecionado) {
        // Reset selected day details to reflect changes
        setDiaSelecionado(null);
      }
    } catch (error) {
      console.error("Erro ao cancelar reserva", error);
      showToast("Erro ao solicitar cancelamento.");
    }
  };

  const handleEditar = (reserva: Reserva) => {
    setReservaEmEdicao(reserva);
    setModalAberto(true);
  };

  const handleCriarOuEditarReserva = async (form: any) => {
    try {
      if (reservaEmEdicao) {
        // UPDATE (U in CRUD)
        await atualizarReserva(
          reservaEmEdicao.reservaId,
          form.data,
          form.horaInicio,
          form.horaFim
        );
        showToast("Agendamento atualizado com sucesso!");
      } else {
        // CREATE (C in CRUD)
        const payload = {
          areaComumId: Number(form.areaComumId),
          unidadeId: 1, // unit 102
          usuarioId: 1, // Ana Lima
          data: form.data,
          horaInicio: form.horaInicio.includes(":") && form.horaInicio.split(":").length === 2 ? `${form.horaInicio}:00` : form.horaInicio,
          horaFim: form.horaFim.includes(":") && form.horaFim.split(":").length === 2 ? `${form.horaFim}:00` : form.horaFim
        };
        await criarReserva(payload);
        showToast("Nova reserva realizada!");
      }
      setModalAberto(false);
      setReservaEmEdicao(null);
      carregarReservas();
      setDiaSelecionado(null);
    } catch (error) {
      console.error("Erro ao salvar reserva", error);
      showToast("Erro ao salvar agendamento. Verifique se há conflito de horários.");
    }
  };

  const handleCloseModal = () => {
    setModalAberto(false);
    setReservaEmEdicao(null);
  };

  const mudarMes = (direcao: number) => {
    let novoMes = mesAtivo + direcao;
    let novoAno = anoAtivo;
    if (novoMes > 11) {
      novoMes = 0;
      novoAno += 1;
    } else if (novoMes < 0) {
      novoMes = 11;
      novoAno -= 1;
    }
    setMesAtivo(novoMes);
    setAnoAtivo(novoAno);
    setDiaSelecionado(null);
  };

  // 100% Dynamic calendar weeks generator for the active month and year
  const gerarSemanas = (reservasAtuais: Reserva[], ano: number, mes: number): DiaCalendario[][] => {
    const semanas: DiaCalendario[][] = [];
    const primeiroDiaMes = new Date(ano, mes, 1);
    const ultimoDiaMes = new Date(ano, mes + 1, 0);
    const totalDias = ultimoDiaMes.getDate();
    
    const diaInicioSemana = primeiroDiaMes.getDay(); // 0 is Sunday, 1 is Monday, etc.
    let diaAtual = 1;
    const hoje = new Date();
    
    for (let w = 0; w < 6; w++) {
      const semana: DiaCalendario[] = [];
      let temDiaMesAtual = false;
      
      for (let d = 0; d < 7; d++) {
        const indexDia = w * 7 + d;
        
        if (indexDia < diaInicioSemana) {
          const dataAnterior = new Date(ano, mes, 0);
          const diaAnterior = dataAnterior.getDate() - (diaInicioSemana - indexDia - 1);
          semana.push({
            dia: diaAnterior,
            mesAtual: false,
            ehHoje: false,
            reservas: [],
          });
        } else if (diaAtual <= totalDias) {
          temDiaMesAtual = true;
          const dataStr = `${ano}-${(mes + 1).toString().padStart(2, "0")}-${diaAtual.toString().padStart(2, "0")}`;
          const reservasDoDia = reservasAtuais.filter(
            (r) => r.data === dataStr && r.status !== "CANCELADA"
          );
          
          semana.push({
            dia: diaAtual,
            mesAtual: true,
            ehHoje: hoje.getDate() === diaAtual && hoje.getMonth() === mes && hoje.getFullYear() === ano,
            reservas: reservasDoDia,
          });
          diaAtual++;
        } else {
          semana.push({
            dia: diaAtual - totalDias,
            mesAtual: false,
            ehHoje: false,
            reservas: [],
          });
          diaAtual++;
        }
      }
      
      if (w < 5 || temDiaMesAtual) {
        semanas.push(semana);
      }
    }
    return semanas;
  };

  const obterDisponibilidadeHoje = (reservasAtuais: Reserva[]): DisponibilidadeArea[] => {
    // Dynamic today date matching real system time (e.g. 2026-06-01)
    const hojeStr = new Date().toISOString().substring(0, 10);
    return areasCatalogo.map((area) => {
      const ocupada = reservasAtuais.some(
        (r) => r.areaComumId === area.id && r.data === hojeStr && r.status !== "CANCELADA"
      );
      return {
        area,
        ocupada,
        detalhe: ocupada ? "Indisponível hoje" : "Disponível hoje",
        badgeClass: ocupada ? "badge-yellow" : "badge-green",
        badgeLabel: ocupada ? "Ocupado" : "Livre",
      };
    });
  };

  const semanas = gerarSemanas(reservas, anoAtivo, mesAtivo);
  const disponibilidadeHoje = obterDisponibilidadeHoje(reservas);

  return (
    <div className="reservas-container">
      {toast && <div className="toast">{toast}</div>}
      
      <div className="content">
        <div className="page-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <div>
            <h1 className="page-title">Reservas de Espaços Comuns</h1>
            <p className="page-sub">Agende áreas comuns e confira horários.</p>
          </div>
          <button className="btn btn-outline" onClick={onVoltar}>
            Voltar
          </button>
        </div>

        <div className="reservas-grid">
          {/* Calendario */}
          <div className="col-calendario card">
            <div className="cal-nav-row">
              <span className="cal-month-title">
                {new Date(anoAtivo, mesAtivo).toLocaleDateString("pt-BR", { month: "long", year: "numeric" }).toUpperCase()}
              </span>
              <div className="cal-nav-btns">
                <button className="btn btn-sm btn-outline" onClick={() => mudarMes(-1)}>◀</button>
                <button className="btn btn-sm btn-outline" onClick={() => mudarMes(1)}>▶</button>
              </div>
            </div>

            <div className="cal-grid" style={{ marginBottom: "8px" }}>
              {["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"].map((d) => (
                <div key={d} className="dia-rotulo">
                  {d}
                </div>
              ))}
            </div>

            <Calendario
              semanas={semanas}
              onSelecionar={(dia) => setDiaSelecionado(dia)}
            />

            <div className="legenda">
              <div className="legenda-item">
                <div className="legenda-cor hoje-cor" />
                <span>Hoje</span>
              </div>
              <div className="legenda-item">
                <div className="legenda-cor reserva-cor" />
                <span>Reservado</span>
              </div>
            </div>

            {diaSelecionado && diaSelecionado.mesAtual && (
              <div className="detalhe" style={{ marginTop: "16px" }}>
                <h4 className="detalhe-titulo">
                  Reservas no Dia {diaSelecionado.dia} de {new Date(anoAtivo, mesAtivo).toLocaleDateString("pt-BR", { month: "long" })}
                </h4>
                {diaSelecionado.reservas.length === 0 ? (
                  <p className="lista-vazia">Nenhum evento agendado para esta data.</p>
                ) : (
                  diaSelecionado.reservas.map((res) => (
                    <div key={res.reservaId} className="reserva-item">
                      <div className="reserva-item-info">
                        <span className="reserva-area">{res.nomeArea}</span>
                        <span className="reserva-hora">
                          {res.horaInicio.substring(0, 5)} - {res.horaFim.substring(0, 5)}
                        </span>
                      </div>
                      <span className={`reserva-status status-${res.status.toLowerCase()}`}>
                        {res.status}
                      </span>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>

          {/* Minhas Reservas */}
          <div className="col-lista card">
            <div className="card-title-row">
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <span className="card-title">Minhas Reservas Ativas</span>
                <span className="badge badge-green">
                  {reservas.filter((r) => r.status === "ATIVA" || r.status === "PENDENTE" || r.status === "AGUARDANDO_CONFIRMACAO").length}
                </span>
              </div>
              <button className="btn btn-sm btn-primary" onClick={() => setModalAberto(true)}>
                Nova Reserva
              </button>
            </div>

            {loading ? (
              <p className="lista-vazia">Carregando...</p>
            ) : reservas.filter((r) => r.status !== "CANCELADA").length === 0 ? (
              <p className="lista-vazia">Nenhuma reserva pendente ou ativa.</p>
            ) : (
              reservas
                .filter((r) => r.status !== "CANCELADA")
                .map((res) => (
                  <ReservaCard
                    key={res.reservaId}
                    reserva={res}
                    onCancelar={handleCancelar}
                    onEditar={handleEditar}
                  />
                ))
            )}
          </div>

          {/* Disponibilidade */}
          <div className="col-disponibilidade card">
            <div className="card-title-row">
              <span className="card-title">Áreas Comuns (Hoje)</span>
            </div>

            <Disponibilidade
              itens={disponibilidadeHoje}
              onReservar={() => {
                setModalAberto(true);
              }}
            />
          </div>
        </div>
      </div>

      <ModalReserva
        aberto={modalAberto}
        onClose={handleCloseModal}
        onSalvar={handleCriarOuEditarReserva}
        reservaEmEdicao={reservaEmEdicao}
      />
    </div>
  );
}

