import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import Reserva from './espacos-condominio/Reserva';
import { buscarReservas } from './espacos-condominio/services/reservaService';
import AssembleiasMorador from './governanca/AssembleiasMorador';
import { pautaService } from './governanca/services/pautaService';
import type { Pauta } from './governanca/types/pauta';
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
  
  // Custom states for modals (commented out as they are handled in the dedicated ReservaView page now)
  // const [reservaArea, setReservaArea] = useState('Churrasqueira 2');
  // const [reservaDate, setReservaDate] = useState('2026-06-10');
  // const [reservaTime, setReservaTime] = useState('12h às 18h');
  
  const [ocorrenciaTitulo, setOcorrenciaTitulo] = useState('');
  const [ocorrenciaDesc, setOcorrenciaDesc] = useState('');
  const [ocorrenciaSuccess, setOcorrenciaSuccess] = useState(false);

  // Dynamic agenda items
  const [agenda, _setAgenda] = useState<AgendaItem[]>([]);

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
        const response = await buscarReservas(1); // User Ana Lima ID 1
        const reservasBackend = response.data;

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

      const staticItems: AgendaItem[] = [
        {
          id: 'cota-abril',
          day: '15',
          month: 'MAR',
          title: 'Vencimento cota abril',
          time: 'R$ 580,00 · Bloco A/102',
          status: 'Pendente',
          statusType: 'primary',
          color: '#14b8a6',
        },
      ];

      _setAgenda([...itensAgenda, ...staticItems]);
    };

    carregarDashboard();
  }, [paginaAtual]);

  // const handleCreateReserva = (e: React.FormEvent) => {
  //   e.preventDefault();
  //   const [, monthNum, dayStr] = reservaDate.split('-');
  //   const months = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ'];
  //   const monthName = months[parseInt(monthNum, 10) - 1] || 'JUN';
  // 
  //   const newItem: AgendaItem = {
  //     id: Date.now().toString(),
  //     day: dayStr,
  //     month: monthName,
  //     title: `Sua reserva — ${reservaArea}`,
  //     time: `${reservaTime} · Confirmada ✓`,
  //     status: 'Conf.',
  //     statusType: 'success',
  //     color: '#3b82f6'
  //   };
  // 
  //   setAgenda([newItem, ...agenda]);
  //   setModalType(null);
  // };

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

  // paginaAtual 'reserva' is now rendered inside the main container to preserve header/navbar

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
                setPaginaAtual('dashboard');
                setModalType('financeiro');
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
                setPaginaAtual('dashboard');
                setModalType('ocorrencia');
              }}
            >
              Ocorrências
            </li>
            <li className="dash-nav-item">
              Avisos <span className="badge">4</span>
            </li>
          </ul>
        </nav>

        <div style={{ position: 'relative' }}>
          <div className="dash-profile" onClick={() => setShowProfileMenu(!showProfileMenu)}>
            <div className="avatar-circle">AL</div>
            <div className="profile-info">
              <span className="profile-name">Ana Lima</span>
              <span className="profile-sub">Apto 102 - Bloco A</span>
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

      {/* Main Container */}
      <main className="dash-container animate-fade-in">
        {paginaAtual === 'reserva' ? (
          <Reserva
            onVoltar={irParaInicio}
          />
        ) : paginaAtual === 'assembleias' ? (
          <AssembleiasMorador />
        ) : (
          <>
            {/* Welcome Banner */}
            <section className="welcome-banner">
              <div className="welcome-text">
                <h1>Olá, Ana! 👋</h1>
                <p>{getTodayFormatted()} · Residencial Parque Verde · Apto 102, Bloco A</p>
              </div>

          {/*<div className="banner-stats">
            <div className="stat-badge">
              <Calendar size={16} />
              <span>Próxima reserva: <strong>07/03 · Churrasqueira</strong></span>
            </div>
            <div className="stat-badge">
              <DollarSign size={16} />
              <span>Cota de março: <strong>R$ 580 · Em dia ✓</strong></span>
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
          </div>*/}
        </section>

        {/* Quick Actions */}
        <section>
          <h2 className="section-title">Ações Rápidas</h2>
          <div className="actions-grid">
            <div className="action-card" onClick={() => setPaginaAtual('reserva')}>
              <div className="action-icon" style={{ backgroundColor: 'var(--primary-light)', color: 'var(--primary)' }}>
                <Calendar size={22} />
              </div>
              <span>Fazer Reserva</span>
            </div>

            <div className="action-card" onClick={() => setModalType('financeiro')}>
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

        {/* Attention & Agenda Column */}
        <div className="dash-content-grid">
          {/* Attention / Alerts */}
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

        {/* Latest Announcement */}
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

      {modalType === 'financeiro' && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h3>Detalhamento Financeiro</h3>
              <button className="close-modal-btn" onClick={() => setModalType(null)}><X size={20} /></button>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div style={{ background: 'var(--gray-50)', padding: '16px', borderRadius: 'var(--radius-md)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span style={{ fontSize: '13px', color: 'var(--gray-500)' }}>Cota Condominial (03/2026)</span>
                  <span style={{ fontSize: '14px', fontWeight: 700, color: 'var(--success)' }}>Pago</span>
                </div>
                <h2 style={{ fontSize: '28px', color: 'var(--gray-900)' }}>R$ 580,00</h2>
                <span style={{ fontSize: '11px', color: 'var(--gray-400)' }}>Pago em 01/03/2026 - ID Transação: #849302</span>
              </div>

              <div>
                <h4 style={{ fontSize: '14px', marginBottom: '10px' }}>Composição da Taxa</h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '13px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--gray-500)' }}>Fundo de Reserva ordinária</span>
                    <span>R$ 420,00</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--gray-500)' }}>Manutenção e Limpeza</span>
                    <span>R$ 100,00</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--gray-500)' }}>Taxa Extraordinária (Fachada)</span>
                    <span>R$ 60,00</span>
                  </div>
                  <hr style={{ border: 'none', borderTop: '1px solid var(--gray-200)' }} />
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 600 }}>
                    <span>Total</span>
                    <span>R$ 580,00</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};
