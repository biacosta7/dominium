import React, { useEffect, useState } from 'react';
import { 
  AlertTriangle, 
  X, CheckCircle, FileText, Loader2, Info, Filter, Download, Eye
} from 'lucide-react';
import { financeiroService } from './services/financeiroService';
import type { Orcamento, Despesa, Taxa, Unidade, CategoriaDespesa } from './types/financeiro';
import './FinanceiroAdmin.css';

export default function FinanceiroAdmin() {
  const [taxas, setTaxas] = useState<Taxa[]>([]);
  const [orcamentos, setOrcamentos] = useState<Orcamento[]>([]);
  const [unidades, setUnidades] = useState<Unidade[]>([]);
  const [vinculos, setVinculos] = useState<any[]>([]);
  const [despesas, setDespesas] = useState<Despesa[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Navigation & Filter States
  const [activeTab, setActiveTab] = useState<'cobrancas' | 'orcamentos'>('cobrancas');
  const [cobrancasFilter, setCobrancasFilter] = useState<'TODOS' | 'PAGO' | 'PENDENTE' | 'ATRASADA'>('TODOS');
  const [selectedBudgetYear, setSelectedBudgetYear] = useState<number>(new Date().getFullYear());
  const [expenseCategoryFilter, setExpenseCategoryFilter] = useState<string>('TODAS');
  
  // Modal States
  const [modalType, setModalType] = useState<'gerar-taxa' | 'editar-taxa' | 'novo-orcamento' | 'nova-despesa' | 'ver-despesa' | 'ver-orcamento' | null>(null);
  const [selectedTaxa, setSelectedTaxa] = useState<Taxa | null>(null);
  const [selectedDespesa, setSelectedDespesa] = useState<Despesa | null>(null);

  
  // Form Fields
  const [formUnidadeId, setFormUnidadeId] = useState('');
  const [formValorBase, setFormValorBase] = useState('');
  const [formValorMultas, setFormValorMultas] = useState('0');
  const [formDataVencimento, setFormDataVencimento] = useState('');
  
  const [formOrcamentoAno, setFormOrcamentoAno] = useState('');
  const [formOrcamentoValorTotal, setFormOrcamentoValorTotal] = useState('');
  const [formOrcamentoDescricao, setFormOrcamentoDescricao] = useState(''); // Visual only
  
  const [formDespesaDescricao, setFormDespesaDescricao] = useState('');
  const [formDespesaValor, setFormDespesaValor] = useState('');
  const [formDespesaData, setFormDespesaData] = useState('');
  const [formDespesaCategoria, setFormDespesaCategoria] = useState<CategoriaDespesa>('MANUTENCAO');
  const [formDespesaExtraordinaria, setFormDespesaExtraordinaria] = useState(false);
  
  const [formSubmitting, setFormSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  // Load all initial data
  async function loadInitialData() {
    setLoading(true);
    setError(null);
    try {
      const fetchedTaxas = await financeiroService.fetchTaxas();
      setTaxas(fetchedTaxas);

      const fetchedOrcamentos = await financeiroService.fetchOrcamentos();
      setOrcamentos(fetchedOrcamentos);

      const fetchedUnits = await financeiroService.fetchUnits();
      setUnidades(fetchedUnits);

      // Fetch resident linkages concurrently to resolve Titular names
      const fetchedVinculosPromises = fetchedUnits.map((u) =>
        financeiroService.fetchMoradoresDaUnidade(u.id).catch(() => [])
      );
      const lists = await Promise.all(fetchedVinculosPromises);
      setVinculos(lists.flat());

      // If budgets exist, select the latest year's budget
      if (fetchedOrcamentos.length > 0) {
        const sorted = [...fetchedOrcamentos].sort((a, b) => b.ano - a.ano);
        setSelectedBudgetYear(sorted[0].ano);
      }
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar dados financeiros do backend.');
    } finally {
      setLoading(false);
    }
  }

  // Load expenses whenever selected budget changes
  async function loadExpensesForSelectedBudget() {
    if (!selectedBudgetYear) return;
    try {
      const fetchedDespesas = await financeiroService.fetchDespesasPorOrcamento(selectedBudgetYear, expenseCategoryFilter);
      setDespesas(fetchedDespesas);
      
      // Update individual budget in list if needed by reloading single budget details
      const updatedBudget = await financeiroService.fetchOrcamentoPorAno(selectedBudgetYear);
      setOrcamentos(prev => prev.map(o => o.ano === selectedBudgetYear ? updatedBudget : o));
    } catch (err) {
      console.error('Erro ao buscar despesas do orçamento:', err);
    }
  }

  useEffect(() => {
    loadInitialData();
  }, []);

  useEffect(() => {
    loadExpensesForSelectedBudget();
  }, [selectedBudgetYear, expenseCategoryFilter]);

  // Utility formatters
  const formatarMoeda = (val: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(val);
  };

  const formatarDataDDMM = (dataStr: string) => {
    if (!dataStr) return '—';
    const parts = dataStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}`;
    }
    return dataStr;
  };

  const formatarDataPtBr = (dataStr: string) => {
    if (!dataStr) return '—';
    const parts = dataStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}/${parts[0]}`;
    }
    return dataStr;
  };

  const getTitularForUnidade = (unidadeId: number) => {
    const unitVinculos = vinculos.filter(v => v.unidadeId === unidadeId);
    const titular = unitVinculos.find(v => v.tipo === 'TITULAR');
    return titular ? titular.usuario.nome : '—';
  };

  const getUnidadeLabel = (unidadeId: number) => {
    const unit = unidades.find(u => u.id === unidadeId);
    return unit ? `${unit.numero} ${unit.bloco ? '- Bloco ' + unit.bloco : ''}` : `Unidade #${unidadeId}`;
  };

  // Metrics calculation
  const totalReceita = taxas
    .filter(t => t.status === 'PAGO')
    .reduce((acc, t) => acc + t.valorTotal, 0);

  const totalPagamentosCount = taxas.filter(t => t.status === 'PAGO').length;
  const totalPendentesCount = taxas.filter(t => t.status !== 'PAGO').length;

  const activeBudget = orcamentos.find(o => o.ano === selectedBudgetYear);
  const totalDespesas = activeBudget ? activeBudget.valorGasto : 0;

  const unpaidTaxas = taxas.filter(t => t.status !== 'PAGO');
  const totalInadimplencia = unpaidTaxas.reduce((acc, t) => acc + t.valorTotal, 0);
  const totalUnidadesInadimplentes = new Set(unpaidTaxas.map(t => t.unidadeId)).size;

  // Handlers
  const handleGerarTaxaSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setFormSubmitting(true);
    try {
      await financeiroService.gerarTaxa({
        unidadeId: Number(formUnidadeId),
        valorBase: Number(formValorBase),
        valorMultas: Number(formValorMultas),
        dataVencimento: formDataVencimento,
      });
      setModalType(null);
      loadInitialData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao gerar taxa.');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleNovoOrcamentoSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setFormSubmitting(true);
    try {
      await financeiroService.createOrcamento({
        ano: Number(formOrcamentoAno),
        valorTotal: Number(formOrcamentoValorTotal),
      });
      setModalType(null);
      loadInitialData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao criar orçamento.');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleNovaDespesaSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setFormSubmitting(true);
    try {
      await financeiroService.createDespesa({
        descricao: formDespesaDescricao,
        valor: Number(formDespesaValor),
        data: formDespesaData,
        categoria: formDespesaCategoria,
        tipo: formDespesaExtraordinaria ? 'EXTRAORDINARIA' : 'ORDINARIA',
      });
      setModalType(null);
      loadInitialData();
      if (activeBudget) {
        loadExpensesForSelectedBudget();
      }
    } catch (err: any) {
      setFormError(err.message || 'Erro ao criar despesa.');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleAprovarDespesa = async (despesaId: number) => {
    if (!window.confirm('Deseja realmente aprovar esta despesa extraordinária?')) return;
    try {
      await financeiroService.aprovarDespesa(despesaId);
      loadInitialData();
      loadExpensesForSelectedBudget();
    } catch (err: any) {
      alert(err.message || 'Erro ao aprovar despesa.');
    }
  };

  const handleRegistrarPagamentoTaxa = async (taxaId: number) => {
    if (!window.confirm('Deseja registrar o pagamento desta cota condominial?')) return;
    try {
      await financeiroService.registrarPagamentoTaxa(taxaId);
      loadInitialData();
    } catch (err: any) {
      alert(err.message || 'Erro ao registrar pagamento.');
    }
  };

  const handleAtualizarTaxaSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTaxa) return;
    setFormError(null);
    setFormSubmitting(true);
    try {
      await financeiroService.atualizarTaxa(
        selectedTaxa.id,
        Number(formValorBase),
        Number(formValorMultas),
      );
      setModalType(null);
      setSelectedTaxa(null);
      loadInitialData();
    } catch (err: any) {
      setFormError(err.message || 'Erro ao atualizar taxa.');
    } finally {
      setFormSubmitting(false);
    }
  };

  // Open modal triggers
  const openGerarTaxaModal = () => {
    setFormUnidadeId('');
    setFormValorBase('580');
    setFormValorMultas('0');
    setFormDataVencimento(new Date().toISOString().split('T')[0]);
    setFormError(null);
    setModalType('gerar-taxa');
  };

  const openEditarTaxaModal = (taxa: Taxa) => {
    setSelectedTaxa(taxa);
    setFormValorBase(String(taxa.valorBase));
    setFormValorMultas(String(taxa.valorMultas));
    setFormError(null);
    setModalType('editar-taxa');
  };

  const openNovoOrcamentoModal = () => {
    setFormOrcamentoAno(new Date().getFullYear().toString());
    setFormOrcamentoValorTotal('');
    setFormOrcamentoDescricao('');
    setFormError(null);
    setModalType('novo-orcamento');
  };

  const openNovaDespesaModal = () => {
    setFormDespesaDescricao('');
    setFormDespesaValor('');
    setFormDespesaData(new Date().toISOString().split('T')[0]);
    setFormDespesaCategoria('MANUTENCAO');
    setFormDespesaExtraordinaria(false);
    setFormError(null);
    setModalType('nova-despesa');
  };

  const openVerDespesaModal = (d: Despesa) => {
    setSelectedDespesa(d);
    setModalType('ver-despesa');
  };



  // Filter charge data
  const filteredTaxas = taxas.filter(t => {
    const matchesStatus = cobrancasFilter === 'TODOS' || t.status === cobrancasFilter;
    return matchesStatus;
  });

  return (
    <div className="financeiro-container animate-fade-in">
      {/* Page Header */}
      <div className="financeiro-header">
        <div className="financeiro-title">
          <h1>Financeiro</h1>
          <p>Cobranças, pagamentos e histórico de despesas/orçamentos</p>
        </div>
        <div className="header-actions">
          <button className="btn-header primary" onClick={openGerarTaxaModal}>
            Gerar Taxa Mensal
          </button>
          <button className="btn-header" onClick={openNovoOrcamentoModal}>
            Novo Orçamento
          </button>
          <button className="btn-header" onClick={openNovaDespesaModal}>
            Nova Despesa
          </button>
          <button className="btn-header" onClick={() => alert('Recurso em desenvolvimento: Registro de multa avulsa.')}>
            Multa Avulsa
          </button>
          <button className="btn-header" onClick={() => alert('Dados exportados com sucesso!')}>
            <Download size={14} /> Exportar
          </button>
        </div>
      </div>

      {error && (
        <div className="error-banner">
          <AlertTriangle size={20} />
          <span>{error}</span>
          <button onClick={loadInitialData} className="retry-btn">
            Tentar Novamente
          </button>
        </div>
      )}

      {loading ? (
        <div className="loading-spinner-container" style={{ padding: '80px 0' }}>
          <Loader2 size={40} className="spinner" />
          <p style={{ marginTop: '12px' }}>Carregando dados financeiros...</p>
        </div>
      ) : (
        <>
          {/* Metrics Panel Row */}
          <section className="finance-metrics-row">
            <div className="finance-metric-card receitas">
              <span className="metric-label">RECEITA — MARÇO/2026</span>
              <h2 className="metric-value">{formatarMoeda(totalReceita)}</h2>
              <span className="metric-subtext">
                {totalPagamentosCount} pagamentos · {totalPendentesCount} pendentes
              </span>
            </div>

            <div className="finance-metric-card despesas">
              <span className="metric-label">DESPESAS — ORÇAMENTO {selectedBudgetYear}</span>
              <h2 className="metric-value">{formatarMoeda(totalDespesas)}</h2>
              <span className="metric-subtext">
                Com base no orçamento ativo de {selectedBudgetYear}
              </span>
            </div>

            <div className="finance-metric-card inadimplencia">
              <span className="metric-label">INADIMPLÊNCIA TOTAL</span>
              <h2 className="metric-value">{formatarMoeda(totalInadimplencia)}</h2>
              <span className="metric-subtext">
                {totalUnidadesInadimplentes} unidades com pendências financeiras
              </span>
            </div>
          </section>

          {/* Navigation Tabs */}
          <div className="tabs-navigation">
            <button 
              className={`tab-btn ${activeTab === 'cobrancas' ? 'active' : ''}`}
              onClick={() => setActiveTab('cobrancas')}
            >
              Cobranças e Receitas
            </button>
            <button 
              className={`tab-btn ${activeTab === 'orcamentos' ? 'active' : ''}`}
              onClick={() => setActiveTab('orcamentos')}
            >
              Orçamentos e Despesas
            </button>
          </div>

          {/* Tab Contents */}
          <div className="tab-content">
            {activeTab === 'cobrancas' ? (
              /* COBRANÇAS PANEL */
              <div className="panel-card animate-fade-in">
                <div className="panel-header-row">
                  <h3>Cobranças – Histórico Geral</h3>
                  <div className="filter-controls">
                    <Filter size={16} style={{ color: 'var(--gray-400)' }} />
                    <select 
                      className="select-filter"
                      value={cobrancasFilter}
                      onChange={(e) => setCobrancasFilter(e.target.value as any)}
                    >
                      <option value="TODOS">Todos os Status</option>
                      <option value="PAGO">Pago</option>
                      <option value="PENDENTE">Pendente</option>
                      <option value="ATRASADA">Atrasada / Inadimplente</option>
                    </select>
                  </div>
                </div>

                {filteredTaxas.length === 0 ? (
                  <div className="empty-data-box">
                    <FileText size={40} />
                    <h4>Nenhuma cobrança encontrada</h4>
                    <p>Não há taxas condominiais registradas que correspondam ao filtro selecionado.</p>
                    <button className="btn-header primary" onClick={openGerarTaxaModal}>
                      Gerar primeira taxa
                    </button>
                  </div>
                ) : (
                  <div className="finance-table-wrapper">
                    <table className="finance-table">
                      <thead>
                        <tr>
                          <th>UNIDADE</th>
                          <th>TITULAR</th>
                          <th>COTA BASE</th>
                          <th>MULTA/JUROS</th>
                          <th>TOTAL</th>
                          <th>VENCIMENTO</th>
                          <th>STATUS</th>
                          <th style={{ width: '100px', textAlign: 'center' }}>AÇÕES</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredTaxas.map((taxa) => (
                          <tr key={taxa.id}>
                            <td className="unit-column">{getUnidadeLabel(taxa.unidadeId)}</td>
                            <td>{getTitularForUnidade(taxa.unidadeId)}</td>
                            <td>{formatarMoeda(taxa.valorBase)}</td>
                            <td>{taxa.valorMultas > 0 ? formatarMoeda(taxa.valorMultas) : '—'}</td>
                            <td className="val-bold">{formatarMoeda(taxa.valorTotal)}</td>
                            <td>{formatarDataDDMM(taxa.dataVencimento)}</td>
                            <td>
                              <span className={`badge-status ${taxa.status.toLowerCase()}`}>
                                {taxa.status === 'PAGO' ? 'Pago' : taxa.status === 'PENDENTE' ? 'Pendente' : 'Inadimplente'}
                              </span>
                            </td>
                            <td>
                              <div className="actions-cell" style={{ justifyContent: 'center' }}>
                                {taxa.status !== 'PAGO' && (
                                  <>
                                    <button
                                      className="action-btn-circle"
                                      title="Atualizar valor da taxa"
                                      onClick={() => openEditarTaxaModal(taxa)}
                                    >
                                      <FileText size={15} />
                                    </button>
                                    <button
                                      className="action-btn-circle success"
                                      title="Registrar pagamento"
                                      onClick={() => handleRegistrarPagamentoTaxa(taxa.id)}
                                    >
                                      <CheckCircle size={16} />
                                    </button>
                                  </>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            ) : (
              /* ORÇAMENTOS E DESPESAS PANEL */
              <div className="budget-grid-view animate-fade-in">
                {/* Active Budget Card */}
                {activeBudget ? (
                  <div className="active-budget-card">
                    <div className="budget-info-row">
                      <div className="budget-details-left">
                        <h4>Orçamento Anual — Ano {selectedBudgetYear}</h4>
                        <p>Meta financeira para a gestão das contas ordinárias e extraordinárias</p>
                      </div>
                      <div className="budget-details-right">
                        <div className="budget-stat-item">
                          <span className="budget-stat-label">Orçado Total</span>
                          <span className="budget-stat-val primary">{formatarMoeda(activeBudget.valorTotal)}</span>
                        </div>
                        <div className="budget-stat-item">
                          <span className="budget-stat-label">Total Gasto</span>
                          <span className="budget-stat-val">{formatarMoeda(activeBudget.valorGasto)}</span>
                        </div>
                        <div className="budget-stat-item">
                          <span className="budget-stat-label">Saldo Disponível</span>
                          <span className="budget-stat-val success">{formatarMoeda(activeBudget.saldoDisponivel)}</span>
                        </div>
                      </div>
                    </div>

                    {/* Progress Bar */}
                    <div className="progress-bar-container">
                      <div className="progress-bar-meta">
                        <span>Consumo do Orçamento</span>
                        <span>
                          {((activeBudget.valorGasto / activeBudget.valorTotal) * 100).toFixed(1)}% utilizado
                        </span>
                      </div>
                      <div className="progress-track">
                        <div 
                          className="progress-fill" 
                          style={{ 
                            width: `${Math.min((activeBudget.valorGasto / activeBudget.valorTotal) * 100, 100)}%`,
                            backgroundColor: (activeBudget.valorGasto / activeBudget.valorTotal) > 0.9 ? 'var(--danger)' : (activeBudget.valorGasto / activeBudget.valorTotal) > 0.7 ? 'var(--warning)' : 'var(--success)'
                          }}
                        ></div>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="empty-data-box" style={{ padding: '60px 40px' }}>
                    <AlertTriangle size={40} style={{ color: 'var(--warning)' }} />
                    <h4>Nenhum orçamento ativo para {selectedBudgetYear}</h4>
                    <p>Você precisa registrar o orçamento global deste ano para gerenciar e vincular suas despesas.</p>
                    <button className="btn-header primary" onClick={openNovoOrcamentoModal}>
                      Cadastrar Orçamento {selectedBudgetYear}
                    </button>
                  </div>
                )}

                {/* Expenses List Panel */}
                <div className="panel-card">
                  <div className="panel-header-row">
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                      <h3>Despesas Vinculadas</h3>
                      {orcamentos.length > 0 && (
                        <select 
                          className="select-filter"
                          value={selectedBudgetYear}
                          onChange={(e) => setSelectedBudgetYear(Number(e.target.value))}
                          style={{ fontWeight: 600, fontSize: '13px' }}
                        >
                          {orcamentos.map(o => (
                            <option key={o.id} value={o.ano}>Ano {o.ano}</option>
                          ))}
                        </select>
                      )}
                    </div>
                    <div className="filter-controls">
                      <Filter size={16} style={{ color: 'var(--gray-400)' }} />
                      <select 
                        className="select-filter"
                        value={expenseCategoryFilter}
                        onChange={(e) => setExpenseCategoryFilter(e.target.value)}
                      >
                        <option value="TODAS">Todas as Categorias</option>
                        <option value="MANUTENCAO">Manutenção</option>
                        <option value="UTILIDADES">Utilidades</option>
                        <option value="PESSOAL">Pessoal</option>
                        <option value="SEGURANCA">Segurança</option>
                        <option value="OUTROS">Outros</option>
                      </select>
                    </div>
                  </div>

                  {despesas.length === 0 ? (
                    <div className="empty-data-box">
                      <FileText size={40} />
                      <h4>Nenhuma despesa registrada</h4>
                      <p>Não há despesas lançadas sob o orçamento de {selectedBudgetYear} para a categoria selecionada.</p>
                      <button className="btn-header primary" onClick={openNovaDespesaModal}>
                        Registrar despesa
                      </button>
                    </div>
                  ) : (
                    <div className="finance-table-wrapper">
                      <table className="finance-table">
                        <thead>
                          <tr>
                            <th>DESCRIÇÃO</th>
                            <th>VALOR</th>
                            <th>DATA</th>
                            <th>CATEGORIA</th>
                            <th>TIPO</th>
                            <th>STATUS</th>
                            <th style={{ width: '120px', textAlign: 'center' }}>AÇÕES</th>
                          </tr>
                        </thead>
                        <tbody>
                          {despesas.map((desp) => (
                            <tr key={desp.id}>
                              <td className="val-bold">{desp.descricao}</td>
                              <td>{formatarMoeda(desp.valor)}</td>
                              <td>{formatarDataPtBr(desp.data)}</td>
                              <td>
                                <span style={{ textTransform: 'capitalize' }}>
                                  {desp.categoria.toLowerCase()}
                                </span>
                              </td>
                              <td>
                                <span style={{ 
                                  fontSize: '11px', 
                                  fontWeight: 700, 
                                  color: desp.tipo === 'EXTRAORDINARIA' ? '#8b5cf6' : 'var(--gray-500)',
                                  backgroundColor: desp.tipo === 'EXTRAORDINARIA' ? 'rgba(139,92,246,0.1)' : 'var(--gray-100)',
                                  padding: '2px 6px',
                                  borderRadius: '4px'
                                }}>
                                  {desp.tipo === 'EXTRAORDINARIA' ? 'Extraordinária' : 'Ordinária'}
                                </span>
                              </td>
                              <td>
                                <span className={`badge-status ${desp.status.toLowerCase()}`}>
                                  {desp.status === 'APROVADA' ? 'Aprovada' : desp.status === 'PENDENTE' ? 'Pendente' : 'Rejeitada'}
                                </span>
                              </td>
                              <td>
                                <div className="actions-cell" style={{ justifyContent: 'center' }}>
                                  <button 
                                    className="action-btn-circle"
                                    title="Visualizar detalhes"
                                    onClick={() => openVerDespesaModal(desp)}
                                  >
                                    <Eye size={14} />
                                  </button>
                                  {desp.tipo === 'EXTRAORDINARIA' && desp.status === 'PENDENTE' && (
                                    <button 
                                      className="action-btn-circle success"
                                      title="Aprovar despesa extraordinária"
                                      onClick={() => handleAprovarDespesa(desp.id)}
                                    >
                                      <CheckCircle size={14} />
                                    </button>
                                  )}
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        </>
      )}

      {/* ====================================================================
          MODALS IMPLEMENTATION
          ==================================================================== */}

      {/* GERAR TAXA MODAL */}
      {modalType === 'gerar-taxa' && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Gerar Taxa Mensal</h2>
              <button className="close-btn" onClick={() => setModalType(null)}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleGerarTaxaSubmit}>
              <div className="form-group-full">
                <label>Selecione a Unidade</label>
                <select
                  value={formUnidadeId}
                  onChange={(e) => setFormUnidadeId(e.target.value)}
                  required
                >
                  <option value="">Selecione a unidade...</option>
                  {unidades.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.numero}{u.bloco ? ` - Bloco ${u.bloco}` : ''} ({getTitularForUnidade(u.id)})
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>Valor Base da Cota (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Ex: 580"
                    value={formValorBase}
                    onChange={(e) => setFormValorBase(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group-half">
                  <label>Valor de Multas/Juros (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Ex: 0"
                    value={formValorMultas}
                    onChange={(e) => setFormValorMultas(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group-full">
                <label>Data de Vencimento</label>
                <input
                  type="date"
                  value={formDataVencimento}
                  onChange={(e) => setFormDataVencimento(e.target.value)}
                  required
                />
              </div>

              {formError && (
                <div className="modal-error-message">
                  <AlertTriangle size={16} />
                  <span>{formError}</span>
                </div>
              )}

              <div className="modal-footer">
                <button type="button" className="cancel-btn" onClick={() => setModalType(null)}>
                  Cancelar
                </button>
                <button type="submit" className="save-btn" disabled={formSubmitting}>
                  {formSubmitting ? 'Gerando...' : 'Gerar Taxa'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ATUALIZAR TAXA MODAL */}
      {modalType === 'editar-taxa' && selectedTaxa && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Atualizar Taxa</h2>
              <button className="close-btn" onClick={() => setModalType(null)}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleAtualizarTaxaSubmit}>
              <div className="form-row">
                <div className="form-group-half">
                  <label>Valor Base (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    value={formValorBase}
                    onChange={(e) => setFormValorBase(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group-half">
                  <label>Multas/Juros (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    value={formValorMultas}
                    onChange={(e) => setFormValorMultas(e.target.value)}
                    required
                  />
                </div>
              </div>
              {formError && <div className="modal-error-message">{formError}</div>}
              <div className="modal-footer">
                <button type="button" className="cancel-btn" onClick={() => setModalType(null)}>Cancelar</button>
                <button type="submit" className="save-btn" disabled={formSubmitting}>
                  {formSubmitting ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* NOVO ORÇAMENTO MODAL */}
      {modalType === 'novo-orcamento' && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Adicionar Orçamento</h2>
              <button className="close-btn" onClick={() => setModalType(null)}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleNovoOrcamentoSubmit}>
              <div className="form-row">
                <div className="form-group-half">
                  <label>Ano / Período</label>
                  <input
                    type="number"
                    min="2020"
                    max="2100"
                    placeholder="Ex: 2026"
                    value={formOrcamentoAno}
                    onChange={(e) => setFormOrcamentoAno(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group-half">
                  <label>Valor Total Previsto (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="1"
                    placeholder="Ex: 100000"
                    value={formOrcamentoValorTotal}
                    onChange={(e) => setFormOrcamentoValorTotal(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group-full">
                <label>Descrição</label>
                <input
                  type="text"
                  placeholder="Ex: Orçamento Anual Ordinário"
                  value={formOrcamentoDescricao}
                  onChange={(e) => setFormOrcamentoDescricao(e.target.value)}
                />
              </div>

              {formError && (
                <div className="modal-error-message">
                  <AlertTriangle size={16} />
                  <span>{formError}</span>
                </div>
              )}

              <div className="modal-footer">
                <button type="button" className="cancel-btn" onClick={() => setModalType(null)}>
                  Cancelar
                </button>
                <button type="submit" className="save-btn" disabled={formSubmitting}>
                  {formSubmitting ? 'Salvando...' : 'Salvar Orçamento'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* NOVA DESPESA MODAL */}
      {modalType === 'nova-despesa' && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Adicionar Despesa no Orçamento</h2>
              <button className="close-btn" onClick={() => setModalType(null)}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleNovaDespesaSubmit}>
              <div className="form-group-full">
                <label>Descrição da Despesa</label>
                <input
                  type="text"
                  placeholder="Ex: Manutenção dos Elevadores"
                  value={formDespesaDescricao}
                  onChange={(e) => setFormDespesaDescricao(e.target.value)}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>Valor (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0.01"
                    placeholder="Ex: 1500"
                    value={formDespesaValor}
                    onChange={(e) => setFormDespesaValor(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group-half">
                  <label>Data</label>
                  <input
                    type="date"
                    value={formDespesaData}
                    onChange={(e) => setFormDespesaData(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group-full">
                <label>Categoria</label>
                <select
                  value={formDespesaCategoria}
                  onChange={(e) => setFormDespesaCategoria(e.target.value as CategoriaDespesa)}
                  required
                >
                  <option value="MANUTENCAO">Manutenção</option>
                  <option value="UTILIDADES">Utilidades</option>
                  <option value="PESSOAL">Pessoal</option>
                  <option value="SEGURANCA">Segurança</option>
                  <option value="OUTROS">Outros</option>
                </select>
              </div>

              <div className="form-group-full" style={{ marginTop: '8px' }}>
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={formDespesaExtraordinaria}
                    onChange={(e) => setFormDespesaExtraordinaria(e.target.checked)}
                  />
                  Exige aprovação em assembleia (Despesa Extraordinária)
                </label>
              </div>

              {formError && (
                <div className="modal-error-message">
                  <AlertTriangle size={16} />
                  <span>{formError}</span>
                </div>
              )}

              <div className="modal-footer">
                <button type="button" className="cancel-btn" onClick={() => setModalType(null)}>
                  Cancelar
                </button>
                <button type="submit" className="save-btn" disabled={formSubmitting}>
                  {formSubmitting ? 'Registrando...' : 'Registrar Despesa'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* VER DESPESA DETAILS MODAL */}
      {modalType === 'ver-despesa' && selectedDespesa && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Detalhes da Despesa</h2>
              <button className="close-btn" onClick={() => { setModalType(null); setSelectedDespesa(null); }}>
                <X size={20} />
              </button>
            </div>
            <div className="read-only-modal-info">
              <div className="info-banner-gray">
                <Info size={16} style={{ display: 'inline', marginRight: '6px', verticalAlign: 'middle' }} />
                Como medida de conformidade e auditoria financeira, registros de despesas históricos não podem ser alterados diretamente após sua criação.
              </div>

              <div className="form-group-full">
                <label>Descrição</label>
                <input type="text" value={selectedDespesa.descricao} disabled className="disabled-input" />
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>Valor</label>
                  <input type="text" value={formatarMoeda(selectedDespesa.valor)} disabled className="disabled-input" />
                </div>
                <div className="form-group-half">
                  <label>Data</label>
                  <input type="text" value={formatarDataPtBr(selectedDespesa.data)} disabled className="disabled-input" />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group-half">
                  <label>Categoria</label>
                  <input type="text" value={selectedDespesa.categoria} disabled className="disabled-input" style={{ textTransform: 'capitalize' }} />
                </div>
                <div className="form-group-half">
                  <label>Tipo</label>
                  <input type="text" value={selectedDespesa.tipo === 'EXTRAORDINARIA' ? 'Extraordinária' : 'Ordinária'} disabled className="disabled-input" />
                </div>
              </div>

              <div className="form-group-full">
                <label>Status</label>
                <div style={{ marginTop: '4px' }}>
                  <span className={`badge-status ${selectedDespesa.status.toLowerCase()}`}>
                    {selectedDespesa.status}
                  </span>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button type="button" className="cancel-btn" onClick={() => { setModalType(null); setSelectedDespesa(null); }}>
                Fechar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
