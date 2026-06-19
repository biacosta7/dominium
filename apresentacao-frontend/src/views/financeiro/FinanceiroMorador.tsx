import { useEffect, useState } from 'react';
import { 
  DollarSign, CheckCircle, Info, Eye, Loader2, AlertTriangle, X
} from 'lucide-react';
import { financeiroService } from './services/financeiroService';
import type { Taxa, Orcamento, Despesa, Unidade } from './types/financeiro';
import './FinanceiroMorador.css';

export default function FinanceiroMorador() {
  const [taxas, setTaxas] = useState<Taxa[]>([]);
  const [orcamento, setOrcamento] = useState<Orcamento | null>(null);
  const [despesas, setDespesas] = useState<Despesa[]>([]);
  const [unidade, setUnidade] = useState<Unidade | null>(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Modal State
  const [selectedTaxa, setSelectedTaxa] = useState<Taxa | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  async function loadResidentFinanceData() {
    setLoading(true);
    setError(null);
    try {
      const units = await financeiroService.fetchUnits();
      const usuariosResponse = await fetch('/usuarios');
      if (!usuariosResponse.ok) throw new Error('Erro ao identificar o morador.');
      const usuarios: Array<{ id: number; email: string }> = await usuariosResponse.json();
      const emailAtual = localStorage.getItem('dominium_userEmail');
      const usuarioAtual = usuarios.find(u => u.email === emailAtual);
      const myUnit = units.find(u =>
        u.proprietarioId === usuarioAtual?.id || u.inquilinoId === usuarioAtual?.id
      );
      
      if (!myUnit) {
        throw new Error('Nenhuma unidade vinculada ao morador conectado.');
      }
      setUnidade(myUnit);

      // 2. Fetch fees for my unit from /api/taxas/unidade/{id}
      const resTaxas = await fetch(`/api/taxas/unidade/${myUnit.id}`);
      if (!resTaxas.ok) throw new Error('Erro ao carregar taxas condominiais.');
      const taxasBackend = await resTaxas.json();
      const dataTaxas: Taxa[] = taxasBackend.map((taxa: Omit<Taxa, 'status'> & { status: string }) => ({
        ...taxa,
        status: (taxa.status === 'PAGA' ? 'PAGO' : taxa.status) as Taxa['status'],
      }));
      
      // Sort taxas by due date descending
      dataTaxas.sort((a, b) => new Date(b.dataVencimento).getTime() - new Date(a.dataVencimento).getTime());
      setTaxas(dataTaxas);

      // 3. Fetch global budget for 2026 transparency
      const budgets = await financeiroService.fetchOrcamentos();
      const budget2026 = budgets.find(b => b.ano === 2026);
      if (budget2026) {
        setOrcamento(budget2026);
        // Fetch approved expenses under this budget
        const budgetDespesas = await financeiroService.fetchDespesasPorOrcamento(2026);
        // Only show approved expenses
        const approvedDespesas = budgetDespesas.filter(d => d.status === 'APROVADA');
        setDespesas(approvedDespesas);
      }
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar informações financeiras.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadResidentFinanceData();
  }, []);

  const handlePayTaxa = async (taxaId: number) => {
    if (!window.confirm('Confirmar o pagamento desta cota condominial?')) return;
    try {
      await financeiroService.registrarPagamentoTaxa(taxaId);
      loadResidentFinanceData();
    } catch (err: any) {
      alert(err.message || 'Erro ao registrar pagamento.');
    }
  };

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

  // Calculations
  const pendingTaxas = taxas.filter(t => t.status !== 'PAGO');
  const nextFee = pendingTaxas.length > 0 ? pendingTaxas[pendingTaxas.length - 1] : null;

  return (
    <div className="financeiro-morador-container">
      {/* Header */}
      <div className="financeiro-morador-header">
        <div>
          <h1>Seu Financeiro</h1>
          <p>Consulte suas cotas condominiais, pagamentos e a transparência do orçamento do condomínio</p>
        </div>
      </div>

      {error && (
        <div className="error-banner">
          <AlertTriangle size={20} />
          <span>{error}</span>
          <button onClick={loadResidentFinanceData} className="retry-btn">
            Tentar Novamente
          </button>
        </div>
      )}

      {loading ? (
        <div className="loading-spinner-container" style={{ padding: '60px 0' }}>
          <Loader2 size={40} className="spinner" />
          <p style={{ marginTop: '12px' }}>Carregando histórico financeiro...</p>
        </div>
      ) : (
        <>
          {/* Personal Metrics cards */}
          <section className="morador-metrics-row">
            {nextFee ? (
              <div className="morador-metric-card status-pending">
                <div className="morador-metric-info">
                  <h3>Próxima Cota Condominial</h3>
                  <h2>{formatarMoeda(nextFee.valorTotal)}</h2>
                  <p>Vencimento em {formatarDataPtBr(nextFee.dataVencimento)}</p>
                </div>
                <span className="morador-metric-badge">Aguardando pagamento</span>
              </div>
            ) : (
              <div className="morador-metric-card status-ok">
                <div className="morador-metric-info">
                  <h3>Status Geral Financeiro</h3>
                  <h2>Tudo em dia!</h2>
                  <p>Parabéns! Todas as suas cotas condominiais estão quitadas.</p>
                </div>
                <span className="morador-metric-badge">Adimplente ✓</span>
              </div>
            )}

            <div className="morador-metric-card status-ok">
              <div className="morador-metric-info">
                <h3>Sua Unidade</h3>
                <h2>Apto {unidade?.numero}</h2>
                <p>Bloco {unidade?.bloco} · Residencial Parque Verde</p>
              </div>
              <span className="morador-metric-badge" style={{ backgroundColor: 'var(--primary-light)', color: 'var(--primary)' }}>
                Titular
              </span>
            </div>
          </section>

          {/* Grid Layout: Left personal charges, Right budget transparency */}
          <div className="morador-panel-grid">
            
            {/* LEFT: Charges History */}
            <div className="morador-panel-card">
              <h3>Histórico de Cotas Condominiais</h3>

              {taxas.length === 0 ? (
                <div className="empty-data-box">
                  <DollarSign size={40} />
                  <h4>Nenhuma taxa encontrada</h4>
                  <p>Não há cotas condominiais geradas para sua unidade no momento.</p>
                </div>
              ) : (
                <div className="finance-table-wrapper">
                  <table className="finance-table">
                    <thead>
                      <tr>
                        <th>REFERÊNCIA</th>
                        <th>COTA BASE</th>
                        <th>MULTA/JUROS</th>
                        <th>TOTAL</th>
                        <th>VENCIMENTO</th>
                        <th>STATUS</th>
                        <th style={{ width: '120px', textAlign: 'center' }}>AÇÕES</th>
                      </tr>
                    </thead>
                    <tbody>
                      {taxas.map((taxa) => (
                        <tr key={taxa.id}>
                          <td className="val-bold">{formatarDataDDMM(taxa.dataVencimento)}</td>
                          <td>{formatarMoeda(taxa.valorBase)}</td>
                          <td>{taxa.valorMultas > 0 ? formatarMoeda(taxa.valorMultas) : '—'}</td>
                          <td className="val-bold">{formatarMoeda(taxa.valorTotal)}</td>
                          <td>{formatarDataDDMM(taxa.dataVencimento)}</td>
                          <td>
                            <span className={`badge-status ${taxa.status.toLowerCase()}`}>
                              {taxa.status === 'PAGO' ? 'Pago' : taxa.status === 'PENDENTE' ? 'Pendente' : 'Atrasada'}
                            </span>
                          </td>
                          <td>
                            <div className="actions-cell" style={{ justifyContent: 'center' }}>
                              <button 
                                className="action-btn-circle"
                                title="Ver detalhamento da taxa"
                                onClick={() => {
                                  setSelectedTaxa(taxa);
                                  setModalAberto(true);
                                }}
                              >
                                <Eye size={14} />
                              </button>
                              {taxa.status !== 'PAGO' && (
                                <button 
                                  className="btn-pay"
                                  onClick={() => handlePayTaxa(taxa.id)}
                                >
                                  Pagar
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

            {/* RIGHT: Condominium Budget Transparency */}
            <div className="morador-panel-card">
              <h3>Transparência: Orçamento Condomínio</h3>

              {orcamento ? (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div className="transparency-bar-wrapper">
                    <div className="transparency-bar-meta">
                      <span>Orçamento Global {orcamento.ano}</span>
                      <span>{((orcamento.valorGasto / orcamento.valorTotal) * 100).toFixed(0)}% Utilizado</span>
                    </div>
                    <div className="transparency-track">
                      <div 
                        className="transparency-fill" 
                        style={{ 
                          width: `${Math.min((orcamento.valorGasto / orcamento.valorTotal) * 100, 100)}%`,
                          backgroundColor: (orcamento.valorGasto / orcamento.valorTotal) > 0.9 ? 'var(--danger)' : (orcamento.valorGasto / orcamento.valorTotal) > 0.7 ? 'var(--warning)' : 'var(--success)'
                        }}
                      ></div>
                    </div>
                    <p style={{ fontSize: '11.5px', color: 'var(--gray-400)', marginTop: '4px' }}>
                      Do total orçado de {formatarMoeda(orcamento.valorTotal)}, foram gastos {formatarMoeda(orcamento.valorGasto)} em melhorias e despesas ordinárias.
                    </p>
                  </div>

                  <div>
                    <h4 style={{ fontSize: '13px', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '10px', letterSpacing: '0.5px' }}>Despesas Recentes Aprovadas</h4>
                    {despesas.length === 0 ? (
                      <p style={{ fontSize: '12.5px', color: 'var(--gray-400)' }}>Nenhuma despesa lançada.</p>
                    ) : (
                      <div className="mini-expense-list">
                        {despesas.slice(0, 3).map((d) => (
                          <div className="mini-expense-item" key={d.id}>
                            <div className="mini-expense-info">
                              <h4>{d.descricao}</h4>
                              <p>{formatarDataDDMM(d.data)} · {d.categoria.toLowerCase()}</p>
                            </div>
                            <span className="mini-expense-val">{formatarMoeda(d.valor)}</span>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <div style={{ padding: '20px 0', textAlign: 'center', color: 'var(--gray-400)', fontSize: '13px' }}>
                  Nenhum orçamento ativo configurado pela administração.
                </div>
              )}
            </div>

          </div>
        </>
      )}

      {/* COMPOSIÇÃO TAXA DETAILS MODAL */}
      {modalAberto && selectedTaxa && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h3>Composição da Taxa</h3>
              <button className="close-btn" onClick={() => { setModalAberto(false); setSelectedTaxa(null); }}>
                <X size={20} />
              </button>
            </div>
            
            <div className="read-only-modal-info">
              <div className="info-banner-gray">
                <Info size={16} style={{ display: 'inline', marginRight: '6px', verticalAlign: 'middle' }} />
                Detalhamento dos custos referentes à taxa condominial com vencimento em {formatarDataPtBr(selectedTaxa.dataVencimento)}.
              </div>

              <div className="comp-list">
                <div className="comp-item">
                  <span>Cota Condominial Base</span>
                  <span>{formatarMoeda(selectedTaxa.valorBase)}</span>
                </div>
                {selectedTaxa.valorMultas > 0 && (
                  <div className="comp-item" style={{ color: 'var(--danger)' }}>
                    <span>Multas e Juros por Atraso</span>
                    <span>{formatarMoeda(selectedTaxa.valorMultas)}</span>
                  </div>
                )}
                <div className="comp-total-row">
                  <span>Valor Total</span>
                  <span>{formatarMoeda(selectedTaxa.valorTotal)}</span>
                </div>
              </div>

              {selectedTaxa.status === 'PAGO' ? (
                <div style={{ display: 'flex', gap: '6px', alignItems: 'center', color: 'var(--success)', fontSize: '12.5px', marginTop: '8px' }}>
                  <CheckCircle size={16} />
                  <span>Pago em {selectedTaxa.dataPagamento ? formatarDataPtBr(selectedTaxa.dataPagamento.substring(0, 10)) : '—'}</span>
                </div>
              ) : (
                <div style={{ display: 'flex', gap: '6px', alignItems: 'center', color: 'var(--warning)', fontSize: '12.5px', marginTop: '8px' }}>
                  <Info size={16} />
                  <span>Aguardando pagamento</span>
                </div>
              )}
            </div>

            <div className="modal-footer">
              <button type="button" className="cancel-btn" onClick={() => { setModalAberto(false); setSelectedTaxa(null); }}>
                Fechar
              </button>
              {selectedTaxa.status !== 'PAGO' && (
                <button 
                  type="button" 
                  className="save-btn"
                  onClick={() => {
                    handlePayTaxa(selectedTaxa.id);
                    setModalAberto(false);
                    setSelectedTaxa(null);
                  }}
                >
                  Confirmar Pagamento
                </button>
              )}
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
