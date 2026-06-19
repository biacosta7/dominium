import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import AssembleiasAdmin from './governanca/AssembleiasAdmin';
import MoradoresAdmin from './moradores/MoradoresAdmin';
import { Unidades } from './Unidades';
import FinanceiroAdmin from './financeiro/FinanceiroAdmin';
import FuncionariosAdmin from './funcionarios/FuncionariosAdmin';

import {
  Search, Bell, Plus, Users, DollarSign, AlertTriangle,
  FileText, Briefcase, Settings, ArrowRight, X, TrendingUp
} from 'lucide-react';
import { Ocorrencias } from './Ocorrencias';

interface DashboardAdminProps {
  userEmail: string;
  onLogout: () => void;
}

export const DashboardAdmin: React.FC<DashboardAdminProps> = ({ userEmail, onLogout }) => {
  const [activeMenu, setActiveMenu] = useState('Dashboard');
  const [chartMode, setChartMode] = useState<'receitas' | 'despesas'>('receitas');
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [modalType, setModalType] = useState<'novo-registro' | 'inadimplentes' | null>(null);

  // Dynamic backend states
  const [taxas, setTaxas] = useState<any[]>([]);
  const [unidades, setUnidades] = useState<any[]>([]);
  const [vinculos, setVinculos] = useState<any[]>([]);
  const [orcamentos, setOrcamentos] = useState<any[]>([]);

  useEffect(() => {
    async function loadDashboardData() {
      try {
        const resTaxas = await fetch('/api/taxas');
        if (resTaxas.ok) {
          const dataTaxas = await resTaxas.json();
          setTaxas(dataTaxas);
        }
        const resUnits = await fetch('/unidades');
        if (resUnits.ok) {
          const dataUnits = await resUnits.json();
          setUnidades(dataUnits);
          
          const fetchedPromises = dataUnits.map((u: any) =>
            fetch(`/api/unidades/${u.id}/moradores`).then(r => r.ok ? r.json() : []).catch(() => [])
          );
          const lists = await Promise.all(fetchedPromises);
          setVinculos(lists.flat());
        }
        const resBudget = await fetch('/financeiro/orcamentos');
        if (resBudget.ok) {
          const dataBudgets = await resBudget.json();
          setOrcamentos(dataBudgets);
        }
      } catch (err) {
        console.error('Erro ao buscar dados do dashboard:', err);
      }
    }
    loadDashboardData();
  }, [activeMenu]);

  const formatarDataDDMM = (dataStr: string) => {
    if (!dataStr) return '—';
    const parts = dataStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}`;
    }
    return dataStr;
  };

  const getTitular = (unidadeId: number) => {
    const unitVinculos = vinculos.filter(v => v.unidadeId === unidadeId);
    const titular = unitVinculos.find(v => v.tipo === 'TITULAR');
    return titular ? titular.usuario.nome : '—';
  };

  const getUnidadeNum = (unidadeId: number) => {
    const u = unidades.find(unit => unit.id === unidadeId);
    return u ? `${u.numero}${u.bloco ? ' - ' + u.bloco : ''}` : `Apto ${unidadeId}`;
  };

  const formatarMoeda = (val: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0
    }).format(val);
  };

  // Calculate unpaid taxes
  const unpaidList = taxas
    .filter(t => t.status !== 'PAGO')
    .map(t => {
      const due = new Date(t.dataVencimento);
      const diffTime = Math.max(0, new Date('2026-03-05').getTime() - due.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return {
        unitId: t.unidadeId,
        unitLabel: getUnidadeNum(t.unidadeId),
        name: getTitular(t.unidadeId),
        days: diffDays,
        amount: t.valorTotal,
        dateLabel: formatarDataDDMM(t.dataVencimento)
      };
    });

  const totalReceitaCalculada = taxas
    .filter(t => t.status === 'PAGO')
    .reduce((acc, t) => acc + t.valorTotal, 0);

  const uniqueUnpaidUnits = new Set(taxas.filter(t => t.status !== 'PAGO').map(t => t.unidadeId));
  const inadimplentesCount = uniqueUnpaidUnits.size;

  const budget2026 = orcamentos.find(o => o.ano === 2026);
  const totalDespesasCalculadas = budget2026 ? budget2026.valorGasto : 0;
  const saldoCalculado = totalReceitaCalculada - totalDespesasCalculadas;

  // Dynamic states for interactive demo
  const [activities, setActivities] = useState([
    { id: 1, text: 'Apto 304 realizou pagamento de R$ 580,00 — Cota março', time: 'há 12min', color: '#10b981' },
    { id: 2, text: 'Ocorrência registrada — Barulho excessivo Apto 508 às 23h', time: 'há 3h', color: '#ef4444' },
    { id: 3, text: 'Nova moradora adicionada — Carla Mendes, Apto 215', time: 'ontem', color: '#3b82f6' },
    { id: 4, text: 'Assembleia "Obras no Térreo" agendada para 13/03/2026', time: 'ontem', color: '#64748b' },
  ]);

  const [newRecordType, setNewRecordType] = useState('morador');
  const [newRecordName, setNewRecordName] = useState('');
  const [newRecordUnit, setNewRecordUnit] = useState('');

  const handleCreateRecord = (e: React.FormEvent) => {
    e.preventDefault();
    if (newRecordType === 'morador') {
      const newActivity = {
        id: Date.now(),
        text: `Novo(a) morador(a) adicionado(a) — ${newRecordName}, Apto ${newRecordUnit}`,
        time: 'agora mesmo',
        color: '#3b82f6'
      };
      setActivities([newActivity, ...activities]);
    } else if (newRecordType === 'ocorrencia') {
      const newActivity = {
        id: Date.now(),
        text: `Ocorrência registrada — ${newRecordName} na Unidade ${newRecordUnit}`,
        time: 'agora mesmo',
        color: '#ef4444'
      };
      setActivities([newActivity, ...activities]);
    }
    setModalType(null);
    setNewRecordName('');
    setNewRecordUnit('');
  };

  return (
    <div className="admin-layout animate-fade-in">
      {/* Sidebar */}
      <aside className="admin-sidebar">
        <div className="sidebar-top">
          <div className="sidebar-logo">
            <div className="logo-badge">D</div>
            <div>
              <h2>Dominium</h2>
              <span>Gestão Condominial</span>
            </div>
          </div>

          <div className="condo-selector">
            <h3>Residencial Parque Verde</h3>
            <p>120 unidades · Blocos A–D</p>
          </div>

          {/* Sidebar Menu */}
          <div className="sidebar-menu">
            <div>
              <div className="menu-group-title">Principal</div>
              <div className="sidebar-nav">
                <div
                  className={`sidebar-link ${activeMenu === 'Dashboard' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Dashboard')}
                >
                  <div className="inner"><TrendingUp size={16} /> Dashboard</div>
                </div>
                <div
                  className={`sidebar-link ${activeMenu === 'Financeiro' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Financeiro')}
                >
                  <div className="inner"><DollarSign size={16} /> Financeiro</div>
                  <span className="badge">3</span>
                </div>
                <div
                  className={`sidebar-link ${activeMenu === 'Notificações' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Notificações')}
                >
                  <div className="inner"><Bell size={16} /> Notificações</div>
                  <span className="badge danger">5</span>
                </div>
              </div>
            </div>

            <div>
              <div className="menu-group-title">Cadastros</div>
              <div className="sidebar-nav">
                <div
                  className={`sidebar-link ${activeMenu === 'Unidades' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Unidades')}
                >
                  <div className="inner"><FileText size={16} /> Unidades</div>
                </div>
                <div
                  className={`sidebar-link ${activeMenu === 'Moradores' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Moradores')}
                >
                  <div className="inner"><Users size={16} /> Moradores</div>
                </div>
                <div
                  className={`sidebar-link ${activeMenu === 'Funcionários' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Funcionários')}
                >
                  <div className="inner"><Briefcase size={16} /> Funcionários</div>
                </div>
              </div>
            </div>

            <div>
              <div className="menu-group-title">Operações</div>
              <div className="sidebar-nav">
                <div
                  className={`sidebar-link ${activeMenu === 'Ocorrências' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Ocorrências')}
                >
                  <div className="inner"><AlertTriangle size={16} /> Ocorrências</div>
                  <span className="badge danger">2</span>
                </div>
                <div
                  className={`sidebar-link ${activeMenu === 'Assembleias' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Assembleias')}
                >
                  <div className="inner"><Users size={16} /> Assembleias</div>
                </div>
                <div
                  className={`sidebar-link ${activeMenu === 'Documentos' ? 'active' : ''}`}
                  onClick={() => setActiveMenu('Documentos')}
                >
                  <div className="inner"><FileText size={16} /> Documentos</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar user footer */}
        <div className="sidebar-bottom" style={{ position: 'relative' }}>
          <div
            className="sidebar-user"
            style={{ cursor: 'pointer' }}
            onClick={() => setShowProfileMenu(!showProfileMenu)}
          >
            <div className="sidebar-user-avatar">MR</div>
            <div className="sidebar-user-info">
              <span className="sidebar-user-name">Marco Ribeiro</span>
              <span className="sidebar-user-role">Síndico</span>
            </div>
          </div>
          <button className="icon-button" onClick={() => setShowProfileMenu(!showProfileMenu)}>
            <Settings size={16} style={{ color: 'rgba(255,255,255,0.6)' }} />
          </button>

          {showProfileMenu && (
            <div
              style={{
                position: 'absolute',
                bottom: '50px',
                left: '12px',
                backgroundColor: '#1e293b',
                border: '1px solid rgba(255,255,255,0.1)',
                borderRadius: 'var(--radius-md)',
                boxShadow: 'var(--shadow-lg)',
                padding: '6px 0',
                width: '180px',
                zIndex: 10
              }}
            >
              <div
                style={{
                  padding: '6px 14px',
                  fontSize: '11px',
                  color: 'rgba(255,255,255,0.5)',
                  borderBottom: '1px solid rgba(255,255,255,0.1)'
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
                  padding: '8px 14px',
                  background: 'none',
                  border: 'none',
                  textAlign: 'left',
                  fontSize: '13px',
                  color: 'var(--danger)',
                  cursor: 'pointer'
                }}
              >
                Sair do Sistema
              </button>
            </div>
          )}
        </div>
      </aside>

      {/* Main Panel */}
      <main className="admin-main">
        {/* Topbar */}
        <header className="admin-topbar">
          <div className="admin-breadcrumb">
            Dominium › <strong>{activeMenu}</strong>
          </div>

          <div className="admin-topbar-actions">
            <div className="search-bar">
              <Search size={16} />
              <input type="text" placeholder="Buscar..." />
            </div>

            <button className="icon-button">
              <Bell size={18} />
            </button>

            <div className="sidebar-user-avatar" style={{ cursor: 'pointer', width: '32px', height: '32px', fontSize: '11px' }}>
              MR
            </div>
          </div>
        </header>

        {/* Content Container */}
        {activeMenu === 'Ocorrências' ? (
          <Ocorrencias />
        ) : (
        <div className="admin-content">
          {activeMenu === 'Assembleias' ? (
            <AssembleiasAdmin />
          ) : activeMenu === 'Moradores' ? (
            <MoradoresAdmin />
          ) : activeMenu === 'Unidades' ? (
            <Unidades />
          ) : activeMenu === 'Financeiro' ? (
            <FinanceiroAdmin />
          ) : activeMenu === 'Funcionários' ? (
            <FuncionariosAdmin />
          ) : (
            <>
              {/* Welcome Row */}
              <div className="admin-welcome-row">
                <div className="admin-welcome">
                  <h1>Bom dia, Marco 👋</h1>
                  <p>Quinta-feira, 05 de março de 2026 · Residencial Parque Verde</p>
                </div>
                <button className="new-record-btn" onClick={() => setModalType('novo-registro')}>
                  <Plus size={16} /> Novo Registro
                </button>
              </div>

              {/* Metrics row */}
              <section className="metrics-row">
                <div className="metric-card">
                  <div className="metric-card-header">
                    <div className="metric-icon-box" style={{ backgroundColor: 'var(--success-light)', color: 'var(--success)' }}>
                      <DollarSign size={18} />
                    </div>
                  </div>
                  <div className="metric-card-content">
                    <h2>{formatarMoeda(totalReceitaCalculada)}</h2>
                    <p>Receita mensal</p>
                  </div>
                  <div className="metric-card-footer" style={{ color: 'var(--success)' }}>
                    Faturamento atual
                  </div>
                </div>

                <div className="metric-card">
                  <div className="metric-card-header">
                    <div className="metric-icon-box" style={{ backgroundColor: 'var(--danger-light)', color: 'var(--danger)' }}>
                      <Users size={18} />
                    </div>
                  </div>
                  <div className="metric-card-content">
                    <h2>{inadimplentesCount}</h2>
                    <p>Inadimplentes</p>
                  </div>
                  <div className="metric-card-footer" style={{ color: 'var(--danger)' }}>
                    Cota em atraso ou pendente
                  </div>
                </div>

                <div className="metric-card">
                  <div className="metric-card-header">
                    <div className="metric-icon-box" style={{ backgroundColor: 'rgba(139, 92, 246, 0.1)', color: '#8b5cf6' }}>
                      <AlertTriangle size={18} />
                    </div>
                  </div>
                  <div className="metric-card-content">
                    <h2>2</h2>
                    <p>Ocorrências abertas</p>
                  </div>
                  <div className="metric-card-footer" style={{ color: 'var(--gray-400)' }}>
                    Aguardando análise
                  </div>
                </div>

                <div className="metric-card">
                  <div className="metric-card-header">
                    <div className="metric-icon-box" style={{ backgroundColor: 'rgba(20, 184, 166, 0.1)', color: '#14b8a6' }}>
                      <Users size={18} />
                    </div>
                  </div>
                  <div className="metric-card-content">
                    <h2>1</h2>
                    <p>Assembleia próxima</p>
                  </div>
                  <div className="metric-card-footer" style={{ color: 'var(--gray-400)' }}>
                    Em 8 dias
                  </div>
                </div>
              </section>

              {/* Content Layout */}
              <div className="admin-grid-layout">
                {/* Left Column */}
                <div className="admin-left-col">
                  {/* Financial Chart Card */}
                  <div className="card-box">
                    <div className="card-box-header">
                      <h3>Fluxo Financeiro — 2026</h3>
                      <div className="toggle-group">
                        <button
                          className={`toggle-btn ${chartMode === 'receitas' ? 'active' : ''}`}
                          onClick={() => setChartMode('receitas')}
                        >
                          Receitas
                        </button>
                        <button
                          className={`toggle-btn ${chartMode === 'despesas' ? 'active' : ''}`}
                          onClick={() => setChartMode('despesas')}
                        >
                          Despesas
                        </button>
                      </div>
                    </div>

                    <div className="chart-container">
                      <div className="bars-wrapper">
                        {/* Render dynamic columns depending on toggle */}
                        <div className="chart-bar-group">
                          <div className="chart-bar-fill" style={{ height: chartMode === 'receitas' ? '82px' : '65px', backgroundColor: 'var(--primary)' }}></div>
                          <span>Jan</span>
                        </div>
                        <div className="chart-bar-group">
                          <div className="chart-bar-fill" style={{ height: chartMode === 'receitas' ? '98px' : '55px', backgroundColor: 'var(--primary)' }}></div>
                          <span>Fev</span>
                        </div>
                        <div className="chart-bar-group">
                          <div className="chart-bar-fill" style={{ height: chartMode === 'receitas' ? '92px' : '70px', backgroundColor: 'var(--primary)' }}></div>
                          <span>Mar</span>
                        </div>
                        <div className="chart-bar-group">
                          <div className="chart-bar-fill" style={{ height: chartMode === 'receitas' ? '30px' : '20px', backgroundColor: 'var(--gray-300)' }}></div>
                          <span>Abr</span>
                        </div>
                        <div className="chart-bar-group">
                          <div className="chart-bar-fill" style={{ height: chartMode === 'receitas' ? '45px' : '32px', backgroundColor: 'var(--gray-300)' }}></div>
                          <span>Mai</span>
                        </div>
                        <div className="chart-bar-group">
                          <div className="chart-bar-fill" style={{ height: chartMode === 'receitas' ? '25px' : '15px', backgroundColor: 'var(--gray-300)' }}></div>
                          <span>Jun</span>
                        </div>
                      </div>

                      <div className="chart-stats">
                        <div className="chart-stat-item">
                          <span className="chart-stat-label">Receita</span>
                          <span className="chart-stat-value" style={{ color: 'var(--primary-hover)' }}>{formatarMoeda(totalReceitaCalculada)}</span>
                        </div>
                        <div className="chart-stat-item">
                          <span className="chart-stat-label">Despesas</span>
                          <span className="chart-stat-value" style={{ color: 'var(--gray-700)' }}>{formatarMoeda(totalDespesasCalculadas)}</span>
                        </div>
                        <div className="chart-stat-item">
                          <span className="chart-stat-label">Saldo</span>
                          <span className="chart-stat-value" style={{ color: 'var(--success)' }}>{formatarMoeda(saldoCalculado)}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Recent Activity Card */}
                  <div className="card-box">
                    <div className="card-box-header">
                      <h3>Atividade Recente</h3>
                    </div>
                    <div className="activity-list">
                      {activities.map((act) => (
                        <div className="activity-item" key={act.id}>
                          <div className="activity-content">
                            <div className="activity-dot" style={{ backgroundColor: act.color }}></div>
                            <span className="activity-text">{act.text}</span>
                          </div>
                          <span className="activity-time">{act.time}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Right Column */}
                <div className="admin-right-col">
                  {/* Upcoming Events Card */}
                  <div className="card-box">
                    <div className="card-box-header">
                      <h3>Próximos Eventos</h3>
                      <span style={{ fontSize: '12px', color: 'var(--primary)', cursor: 'pointer' }}>Calendário</span>
                    </div>
                    <div className="agenda-column" style={{ gap: '0' }}>
                      <div className="agenda-card">
                        <div className="agenda-left">
                          <div className="agenda-date" style={{ backgroundColor: '#8b5cf6' }}>
                            <span className="day">13</span>
                            <span className="month">MAR</span>
                          </div>
                          <div className="agenda-details">
                            <h4>Assembleia Ordinária</h4>
                            <p>19h · Salão de Festas · 3 pautas</p>
                          </div>
                        </div>
                      </div>

                      <div className="agenda-card" style={{ marginBottom: 0 }}>
                        <div className="agenda-left">
                          <div className="agenda-date" style={{ backgroundColor: '#14b8a6' }}>
                            <span className="day">15</span>
                            <span className="month">MAR</span>
                          </div>
                          <div className="agenda-details">
                            <h4>Vencimento Cotas</h4>
                            <p>120 unidades · R$ 580,00/unid</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Debtors List Card */}
                  <div className="card-box">
                    <div className="card-box-header">
                      <h3>Inadimplência</h3>
                      <span className="status-badge" style={{ backgroundColor: 'var(--danger-light)', color: 'var(--danger)', fontSize: '11px' }}>{inadimplentesCount} unidades</span>
                    </div>
                    {unpaidList.length === 0 ? (
                      <div style={{ padding: '20px 0', textAlign: 'center', color: 'var(--gray-400)', fontSize: '13px' }}>
                        Nenhuma pendência financeira pendente.
                      </div>
                    ) : (
                      <>
                        <div className="debt-list">
                          {unpaidList.slice(0, 3).map((item, idx) => (
                            <div className="debt-item" key={idx}>
                              <div className="debt-item-info">
                                <h4>{item.unitLabel} · {item.name}</h4>
                                <p>Venc. {item.dateLabel} · {item.days} dias em atraso</p>
                              </div>
                              <span className="debt-amount">{formatarMoeda(item.amount)}</span>
                            </div>
                          ))}
                        </div>

                        <span className="view-all-link" onClick={() => setModalType('inadimplentes')}>
                          Ver todos os {inadimplentesCount} <ArrowRight size={14} style={{ display: 'inline', marginLeft: '4px', verticalAlign: 'middle' }} />
                        </span>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
        )}
      </main>

      {/* ====================================
          MODALS IMPLEMENTATION
          ==================================== */}
      {modalType === 'novo-registro' && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h3>Adicionar Novo Registro</h3>
              <button className="close-modal-btn" onClick={() => setModalType(null)}><X size={20} /></button>
            </div>
            <form onSubmit={handleCreateRecord} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div className="form-group">
                <label>Tipo de Registro</label>
                <select
                  value={newRecordType}
                  onChange={(e) => setNewRecordType(e.target.value)}
                  style={{ width: '100%', padding: '10px', border: '1px solid var(--gray-200)', borderRadius: 'var(--radius-md)' }}
                >
                  <option value="morador">Cadastrar Morador</option>
                  <option value="ocorrencia">Registrar Ocorrência</option>
                </select>
              </div>

              <div className="form-group">
                <label>{newRecordType === 'morador' ? 'Nome Completo' : 'Título da Ocorrência'}</label>
                <input
                  type="text"
                  value={newRecordName}
                  onChange={(e) => setNewRecordName(e.target.value)}
                  placeholder={newRecordType === 'morador' ? 'Ex: Carlos Souza' : 'Ex: Vazamento de água no subsolo'}
                  style={{ width: '100%', padding: '10px', border: '1px solid var(--gray-200)', borderRadius: 'var(--radius-md)' }}
                  required
                />
              </div>

              <div className="form-group">
                <label>Unidade (Apto e Bloco)</label>
                <input
                  type="text"
                  value={newRecordUnit}
                  onChange={(e) => setNewRecordUnit(e.target.value)}
                  placeholder="Ex: 302 - Bloco B"
                  style={{ width: '100%', padding: '10px', border: '1px solid var(--gray-200)', borderRadius: 'var(--radius-md)' }}
                  required
                />
              </div>

              <button type="submit" className="submit-btn" style={{ marginTop: '10px' }}>
                Salvar Registro
              </button>
            </form>
          </div>
        </div>
      )}

      {modalType === 'inadimplentes' && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ width: '600px' }}>
            <div className="modal-header">
              <h3>Todos os Inadimplentes ({inadimplentesCount})</h3>
              <button className="close-modal-btn" onClick={() => setModalType(null)}><X size={20} /></button>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxHeight: '400px', overflowY: 'auto', paddingRight: '6px' }}>
              {unpaidList.map((debtor, index) => (
                <div className="debt-item" key={index}>
                  <div className="debt-item-info">
                    <h4>{debtor.unitLabel} · {debtor.name}</h4>
                    <p>Venc. {debtor.dateLabel} · {debtor.days} dias em atraso</p>
                  </div>
                  <span className="debt-amount">{formatarMoeda(debtor.amount)}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
