import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import Reserva from './espacos-condominio/Reserva';
import { buscarReservas } from './espacos-condominio/services/reservaService';
import AssembleiasMorador from './governanca/AssembleiasMorador';
import FinanceiroMorador from './financeiro/FinanceiroMorador';
import OcorrenciasMorador from './ocorrencias/OcorrenciasMorador';
import MoradoresMorador from './moradores/MoradoresMorador';
import { pautaService } from './governanca/services/pautaService';
import type { Pauta } from './governanca/types/pauta';
import type { Taxa } from './financeiro/types/financeiro';
import {
  Calendar, DollarSign, Users, AlertTriangle,
  MessageSquare, LogOut, Check, ArrowRight, X
} from 'lucide-react';

const MESES_ABREV = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ'];

interface DashboardMoradorProps {
  userEmail: string;
  onLogout: () => void;
}

interface AgendaItem {
  id: string;
  day: string;
  month: string;
  title: string;
  time: string;
  status: string;
  statusType: 'success' | 'primary';
  color: string;
}

export const DashboardMorador: React.FC<DashboardMoradorProps> = ({ userEmail, onLogout }) => {
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [activeTab, setActiveTab] = useState('Início');

  // Modals state
  const [modalType, setModalType] = useState<'ocorrencia' | 'financeiro' | null>(null);
  const [paginaAtual, setPaginaAtual] = useState('dashboard');
  const [pautasAbertas, setPautasAbertas] = useState<Pauta[]>([]);

  // Custom states for resolved user/vinculo
  const [currentUser, setCurrentUser] = useState<any>(null);
  const [isTitular, setIsTitular] = useState(false);

  const [ocorrenciaTitulo, setOcorrenciaTitulo] = useState('');
  const [ocorrenciaDesc, setOcorrenciaDesc] = useState('');
  const [ocorrenciaSuccess, setOcorrenciaSuccess] = useState(false);

  const [agenda, _setAgenda] = useState<AgendaItem[]>([]);

  const [unidadeMorador, setUnidadeMorador] = useState<any>(null);
  const [taxasMorador, setTaxasMorador] = useState<Taxa[]>([]);
  const [proximaReserva, setProximaReserva] = useState<string | null>(null);

  const irParaAssembleias = () => {
    setActiveTab('Assembleias');
    setModalType(null);
    setPaginaAtual('assembleias');
  };

  const irParaInicio = () => {
    setActiveTab('Início');
    setModalType(null);
    setPaginaAtual('dashboard');
  };

  const pautaParaAgenda = (pauta: Pauta): AgendaItem => {
    const hoje = new Date();
    return {
      id: `pauta-${pauta.id}`,
      day: hoje.getDate().toString().padStart(2, '0'),
      month: MESES_ABREV[hoje.getMonth()] ?? 'JUN',
      title: `Votação — ${pauta.titulo}`,
      time: pauta.descricao
        ? `${pauta.descricao.slice(0, 60)}${pauta.descricao.length > 60 ? '…' : ''}`
        : 'Participação aberta · Toque para votar',
      status: 'Aberta',
      statusType: 'primary',
      color: '#8b5cf6',
    };
  };

  const getTodayFormatted = () => {
    const today = new Date();
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    };
    const formatted = today.toLocaleDateString('pt-BR', options);
    return formatted.charAt(0).toUpperCase() + formatted.slice(1);
  };

  useEffect(() => {
    const carregarDashboard = async () => {
      let abertas: Pauta[] = [];
      let itensAgenda: AgendaItem[] = [];

      let loggedUser: any = null;
      let userUnit: any = null;
      let userVinculo: any = null;

      try {
        const resUser = await fetch('/usuarios');
        if (resUser.ok) {
          const allUsers = await resUser.json();
          loggedUser = allUsers.find((u: any) => u.email === userEmail);
          setCurrentUser(loggedUser);
        }

        if (loggedUser) {
          const resUnits = await fetch('/unidades');
          if (resUnits.ok) {
            const allUnits = await resUnits.json();
            for (const unit of allUnits) {
              const resV = await fetch(`/api/unidades/${unit.id}/moradores`);
              if (resV.ok) {
                const list = await resV.json();
                const found = list.find((v: any) => v.usuario.id === loggedUser.id && v.status === 'ATIVO');
                if (found) {
                  userUnit = unit;
                  userVinculo = found;
                  break;
                }
              }
            }
          }
        }

        if (userUnit) {
          setUnidadeMorador(userUnit);
        }
        if (userVinculo) {
          setIsTitular(userVinculo.tipo === 'TITULAR');
        }
      } catch (error) {
        console.error('Erro ao resolver usuário/unidade do morador', error);
      }

      const activeUserId = loggedUser ? loggedUser.id : 1;
      const activeUnitId = userUnit ? userUnit.id : 1;

      try {
        const pautas = await pautaService.listar();
        abertas = pautas.filter((p) => p.status === 'ABERTA');
        setPautasAbertas(abertas);
        itensAgenda = abertas.map(pautaParaAgenda);
      } catch (error) {
        console.error('Erro ao carregar pautas para o dashboard', error);
        setPautasAbertas([]);
      }

      try {
        const response = await buscarReservas(activeUserId);
        const reservasBackend = response.data;
        const hojeStr = new Date().toISOString().split('T')[0];

        const futuras = reservasBackend
          .filter((res: any) => res.status !== 'CANCELADA' && res.data >= hojeStr)
          .sort((a: any, b: any) => a.data.localeCompare(b.data));

        if (futuras.length > 0) {
          const next = futuras[0];
          const dataObj = new Date(next.data + 'T00:00:00');
          const dayStr = dataObj.getDate().toString().padStart(2, '0');
          const monthStr = (dataObj.getMonth() + 1).toString().padStart(2, '0');
          setProximaReserva(`${dayStr}/${monthStr} · ${next.nomeArea}`);
        } else {
          setProximaReserva(null);
        }

        const mappedReservas: AgendaItem[] = reservasBackend
          .filter((res: any) => res.status !== 'CANCELADA')
          .map((res: any) => {
            const dataObj = new Date(res.data + 'T00:00:00');
            const dayStr = dataObj.getDate().toString().padStart(2, '0');
            const monthName = MESES_ABREV[dataObj.getMonth()] || 'JUN';

            return {
              id: 'res-' + res.id,
              day: dayStr,
              month: monthName,
              title: `Sua reserva — ${res.nomeArea}`,
              time: `${res.horaInicio.substring(0, 5)} às ${res.horaFim.substring(0, 5)} · Confirmada ✓`,
              status: res.status === 'ATIVA' ? 'Conf.' : res.status,
              statusType: 'success' as const,
              color: '#3b82f6',
            };
          });

        itensAgenda = [...itensAgenda, ...mappedReservas];
      } catch (error) {
        console.error('Erro ao carregar reservas para o dashboard', error);
      }

      let taxasMoradorData: Taxa[] = [];
      try {
        const resTaxas = await fetch(`/api/taxas/unidade/${activeUnitId}`);
        if (resTaxas.ok) {
          taxasMoradorData = await resTaxas.json();
          taxasMoradorData.sort((a, b) => new Date(b.dataVencimento).getTime() - new Date(a.dataVencimento).getTime());
          setTaxasMorador(taxasMoradorData);
        }
      } catch (error) {
        console.error('Erro ao carregar taxas para o dashboard', error);
      }

      const dynamicTaxaItems: AgendaItem[] = taxasMoradorData
        .filter(t => t.status !== 'PAGO')
        .map(t => {
          const dateObj = new Date(t.dataVencimento + 'T00:00:00');
          const dayStr = dateObj.getDate().toString().padStart(2, '0');
          const monthName = MESES_ABREV[dateObj.getMonth()] || 'JUN';

          return {
            id: `taxa-${t.id}`,
            day: dayStr,
            month: monthName,
            title: `Taxa Condominial - Ref. ${monthName}`,
            time: `Valor: ${new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(t.valorTotal)} · Vencimento: ${dayStr}/${(dateObj.getMonth() + 1).toString().padStart(2, '0')}`,
            status: t.status === 'PENDENTE' ? 'Pendente' : 'Atrasada',
            statusType: 'primary' as const,
            color: '#14b8a6',
          };
        });

      _setAgenda([...itensAgenda, ...dynamicTaxaItems]);
    };

    carregarDashboard();
  }, [paginaAtual]);


  const handleCreateOcorrencia = (e: React.FormEvent) => {
    e.preventDefault();
    setOcorrenciaSuccess(true);
    setTimeout(() => {
      setOcorrenciaSuccess(false);
      setModalType(null);
      setOcorrenciaTitulo('');
      setOcorrenciaDesc('');
    }, 2000);
  };

  return (
    <div style={{ backgroundColor: 'var(--gray-50)', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <header className="dash-header">
        <div className="dash-logo" onClick={irParaInicio}>
          <div className="logo-badge">D</div>
          <span className="logo-text">Dominium</span>
        </div>

        <nav>
          <ul className="dash-nav">
            <li
              className={`dash-nav-item ${activeTab === 'Início' ? 'active' : ''}`}
              onClick={irParaInicio}
            >
              Início
            </li>
            <li
              className={`dash-nav-item ${activeTab === 'Reservas' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('Reservas');
                setModalType(null);
                setPaginaAtual('reserva');
              }}
            >
              Reservas
            </li>
            <li
              className={`dash-nav-item ${activeTab === 'Financeiro' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('Financeiro');
                setPaginaAtual('financeiro');
                setModalType(null);
              }}
            >
              Financeiro
            </li>
            <li
              className={`dash-nav-item ${activeTab === 'Assembleias' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('Assembleias');
                setModalType(null);
                setPaginaAtual('assembleias');
              }}
            >
              Assembleias
            </li>
            <li
              className={`dash-nav-item ${activeTab === 'Ocorrências' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('Ocorrências');
                setPaginaAtual('ocorrencias');
              }}
            >
              Ocorrências
            </li>
            {isTitular && (
              <li
                className={`dash-nav-item ${activeTab === 'Moradores' ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab('Moradores');
                  setPaginaAtual('moradores');
                  setModalType(null);
                }}
              >
                Moradores
              </li>
            )}
            <li className="dash-nav-item">
              Avisos <span className="badge">4</span>
            </li>
          </ul>
        </nav>

        <div style={{ position: 'relative' }}>
          <div className="dash-profile" onClick={() => setShowProfileMenu(!showProfileMenu)}>
            <div className="avatar-circle">
              {currentUser?.nome ? currentUser.nome.split(' ').map((n: string) => n[0]).join('').substring(0, 2).toUpperCase() : 'AL'}
            </div>
            <div className="profile-info">
              <span className="profile-name">{currentUser?.nome || 'Ana Lima'}</span>
              <span className="profile-sub">
                {unidadeMorador ? `Apto ${unidadeMorador.numero} - Bloco ${unidadeMorador.bloco || ''}` : 'Apto 102 - Bloco A'}
              </span>
            </div>
          </div>

          {showProfileMenu && (
            <div
              style={{
                position: 'absolute',
                top: '50px',
                right: '0',
                backgroundColor: 'white',
                border: '1px solid var(--gray-200)',
                borderRadius: 'var(--radius-md)',
                boxShadow: 'var(--shadow-lg)',
                padding: '8px 0',
                width: '160px',
                zIndex: 10
              }}
            >
              <div
                style={{
                  padding: '8px 16px',
                  fontSize: '12px',
                  color: 'var(--gray-400)',
                  borderBottom: '1px solid var(--gray-100)'
                }}
              >
                {userEmail}
              </div>
              <button
                onClick={onLogout}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  width: '100%',
                  padding: '10px 16px',
                  background: 'none',
                  border: 'none',
                  textAlign: 'left',
                  fontSize: '14px',
                  color: 'var(--danger)',
                  cursor: 'pointer'
                }}
              >
                <LogOut size={16} /> Sair
              </button>
            </div>
          )}
        </div>
      </header>

      <main className="dash-container animate-fade-in">
        {paginaAtual === 'reserva' ? (
          <Reserva
            onVoltar={irParaInicio}
          />
        ) : paginaAtual === 'assembleias' ? (
          <AssembleiasMorador />
        ) : paginaAtual === 'financeiro' ? (
          <FinanceiroMorador />
        ) : paginaAtual === 'ocorrencias' ? (
          <OcorrenciasMorador />
        ) : paginaAtual === 'moradores' ? (
          <MoradoresMorador
            unidadeId={unidadeMorador?.id}
            requesterId={currentUser?.id}
            unidadeNumero={unidadeMorador?.numero}
            unidadeBloco={unidadeMorador?.bloco}
          />
        ) : (
          <>
            <section className="welcome-banner">
              <div className="welcome-text">
                <h1>Olá, {currentUser?.nome ? currentUser.nome.split(' ')[0] : 'Ana'}! 👋</h1>
                <p>{getTodayFormatted()} · Residencial Parque Verde · Apto {unidadeMorador?.numero || '102'}, Bloco {unidadeMorador?.bloco || 'A'}</p>
              </div>

              <div className="banner-stats">
                {proximaReserva ? (
                  <div className="stat-badge" onClick={() => { setActiveTab('Reservas'); setPaginaAtual('reserva'); }} style={{ cursor: 'pointer' }}>
                    <Calendar size={16} />
                    <span>Próxima reserva: <strong>{proximaReserva}</strong></span>
                  </div>
                ) : (
                  <div className="stat-badge" onClick={() => { setActiveTab('Reservas'); setPaginaAtual('reserva'); }} style={{ cursor: 'pointer' }}>
                    <Calendar size={16} />
                    <span>Nenhuma reserva futura</span>
                  </div>
                )}
                <div className="stat-badge" onClick={() => { setActiveTab('Financeiro'); setPaginaAtual('financeiro'); }} style={{ cursor: 'pointer' }}>
                  <DollarSign size={16} />
                  <span>{(() => {
                    const pending = taxasMorador.filter(t => t.status !== 'PAGO');
                    if (pending.length > 0) {
                      const sortedUnpaid = [...pending].sort((a, b) => new Date(a.dataVencimento).getTime() - new Date(b.dataVencimento).getTime());
                      const oldestUnpaid = sortedUnpaid[0];
                      const dateObj = new Date(oldestUnpaid.dataVencimento + 'T00:00:00');
                      const mesName = MESES_ABREV[dateObj.getMonth()] || 'Mês';
                      const formattedVal = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(oldestUnpaid.valorTotal);
                      return `Próxima Cota (${mesName}): ${formattedVal} · ${oldestUnpaid.status === 'PENDENTE' ? 'Pendente' : 'Atrasada'}`;
                    }
                    if (taxasMorador.length > 0) {
                      const latestPaid = taxasMorador.find(t => t.status === 'PAGO');
                      if (latestPaid) {
                        const dateObj = new Date(latestPaid.dataVencimento + 'T00:00:00');
                        const mesName = MESES_ABREV[dateObj.getMonth()] || 'Mês';
                        return `Cota de ${mesName.toLowerCase()}: Em dia ✓`;
                      }
                    }
                    return 'Financeiro: Em dia ✓';
                  })()}</span>
                </div>
                {pautasAbertas.length > 0 && (
                  <div
                    className="stat-badge"
                    style={{ cursor: 'pointer' }}
                    onClick={irParaAssembleias}
                  >
                    <Users size={16} />
                    <span>
                      Votação aberta:{' '}
                      <strong>
                        {pautasAbertas.length === 1
                          ? pautasAbertas[0].titulo
                          : `${pautasAbertas.length} pautas`}
                      </strong>
                    </span>
                  </div>
                )}
              </div>
            </section>

            <section>
              <h2 className="section-title">Ações Rápidas</h2>
              <div className="actions-grid">
                <div className="action-card" onClick={() => setPaginaAtual('reserva')}>
                  <div className="action-icon" style={{ backgroundColor: 'var(--primary-light)', color: 'var(--primary)' }}>
                    <Calendar size={22} />
                  </div>
                  <span>Fazer Reserva</span>
                </div>

                <div className="action-card" onClick={() => {
                  setActiveTab('Financeiro');
                  setPaginaAtual('financeiro');
                  setModalType(null);
                }}>
                  <div className="action-icon" style={{ backgroundColor: 'var(--success-light)', color: 'var(--success)' }}>
                    <DollarSign size={22} />
                  </div>
                  <span>Ver Financeiro</span>
                </div>

                <div className="action-card" onClick={irParaAssembleias}>
                  <div className="action-icon" style={{ backgroundColor: 'rgba(139, 92, 246, 0.1)', color: '#8b5cf6' }}>
                    <Users size={22} />
                  </div>
                  <span>Votar Agora</span>
                </div>

                <div className="action-card" onClick={() => setModalType('ocorrencia')}>
                  <div className="action-icon" style={{ backgroundColor: 'var(--danger-light)', color: 'var(--danger)' }}>
                    <AlertTriangle size={22} />
                  </div>
                  <span>Registrar Ocorrência</span>
                </div>
              </div>
            </section>

            <div className="dash-content-grid">
              <section className="attention-column">
                <h2 className="section-title">Atenção</h2>

                {pautasAbertas.map((pauta) => (
                  <div className="alert-card info" key={pauta.id}>
                    <div className="alert-icon">
                      <Users size={20} />
                    </div>
                    <div className="alert-text">
                      <h4>Votação disponível</h4>
                      <p>
                        A pauta <strong>{pauta.titulo}</strong> está aberta para votação.
                        {pauta.descricao ? ` ${pauta.descricao}` : ' Seu voto é importante para o condomínio.'}
                      </p>
                      <span className="alert-link" onClick={irParaAssembleias}>
                        Votar Agora <ArrowRight size={14} />
                      </span>
                    </div>
                  </div>
                ))}
              </section>

              {/* Agenda */}
              <section className="agenda-column">
                <h2 className="section-title">Agenda</h2>

                {agenda.map((item) => (
                  <div
                    className="agenda-card"
                    key={item.id}
                    style={item.id.startsWith('pauta-') ? { cursor: 'pointer' } : undefined}
                    onClick={item.id.startsWith('pauta-') ? irParaAssembleias : undefined}
                  >
                    <div className="agenda-left">
                      <div className="agenda-date" style={{ backgroundColor: item.color }}>
                        <span className="day">{item.day}</span>
                        <span className="month">{item.month}</span>
                      </div>
                      <div className="agenda-details">
                        <h4>{item.title}</h4>
                        <p>{item.time}</p>
                      </div>
                    </div>
                    <span className={`status-badge ${item.statusType}`}>
                      {item.status}
                    </span>
                  </div>
                ))}
              </section>
            </div>

            <section>
              <h2 className="section-title">Último Comunicado</h2>
              <div className="announcement-card">
                <div className="announcement-icon">
                  <MessageSquare size={24} />
                </div>
                <div className="announcement-body">
                  <h3>Manutenção do elevador Bloco A</h3>
                  <p>
                    O elevador do Bloco A passará por manutenção preventiva no dia <strong>09/03 (segunda-feira)</strong>, das 8h às 12h.
                    Durante esse período, apenas a escada estará disponível para circulação. Pedimos desculpas por eventuais transtornos.
                  </p>
                  <div className="announcement-footer">
                    Síndico Marco Ribeiro · 03/03/2026
                  </div>
                </div>
              </div>
            </section>
          </>
        )}
      </main>

      {modalType === 'ocorrencia' && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h3>Registrar Ocorrência</h3>
              <button className="close-modal-btn" onClick={() => setModalType(null)}><X size={20} /></button>
            </div>
            {ocorrenciaSuccess ? (
              <div style={{ textAlign: 'center', padding: '20px 0', color: 'var(--success)' }}>
                <Check size={48} style={{ margin: '0 auto 12px' }} />
                <h4>Ocorrência Registrada!</h4>
                <p style={{ color: 'var(--gray-500)', fontSize: '13px', marginTop: '6px' }}>
                  A administração foi notificada e analisará o ocorrido.
                </p>
              </div>
            ) : (
              <form onSubmit={handleCreateOcorrencia} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                <div className="form-group">
                  <label>Título / Assunto</label>
                  <input
                    type="text"
                    placeholder="Ex: Barulho excessivo no apto 304"
                    value={ocorrenciaTitulo}
                    onChange={(e) => setOcorrenciaTitulo(e.target.value)}
                    style={{ width: '100%', padding: '10px', border: '1px solid var(--gray-200)', borderRadius: 'var(--radius-md)' }}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Descrição Detalhada</label>
                  <textarea
                    placeholder="Descreva o ocorrido informando data, hora e detalhes..."
                    value={ocorrenciaDesc}
                    onChange={(e) => setOcorrenciaDesc(e.target.value)}
                    rows={4}
                    style={{ width: '100%', padding: '10px', border: '1px solid var(--gray-200)', borderRadius: 'var(--radius-md)', resize: 'none' }}
                    required
                  />
                </div>

                <button type="submit" className="submit-btn" style={{ marginTop: '10px' }}>
                  Enviar Ocorrência
                </button>
              </form>
            )}
          </div>
        </div>
      )}



    </div>
  );
};
