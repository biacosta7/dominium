import React, { useState, useEffect, useCallback } from 'react';
import { ModalOcorrencia } from '../../components/ModalOcorrencia';
import { listarOcorrenciasPorUnidade, criarOcorrencia } from '../../services/ocorrenciaService';
import { listarMultasPorUnidade, contestarMulta } from '../../services/multaService';
import './OcorrenciasMorador.css';

const UNIDADE_ID = 1;

const statusLabels: Record<string, string> = {
  ABERTA: 'Aberto',
  EM_ANALISE: 'Em análise',
  ENCERRADA: 'Encerrado',
};

const statusColors: Record<string, string> = {
  ABERTA: '#f59e0b',
  EM_ANALISE: '#3b82f6',
  ENCERRADA: '#6b7280',
};

const multaStatusLabels: Record<string, string> = {
  ABERTA: 'Pendente',
  PAGA: 'Paga',
  CONTESTADA: 'Em recurso',
  CANCELADA: 'Multa Cancelada',
};

const multaStatusColors: Record<string, string> = {
  ABERTA: '#ef4444',
  PAGA: '#22c55e',
  CONTESTADA: '#f59e0b',
  CANCELADA: '#22c55e',
};

function formatarData(dataStr: string) {
  if (!dataStr) return '—';
  try {
    return new Date(dataStr).toLocaleDateString('pt-BR');
  } catch {
    return dataStr;
  }
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
}

function prazoRecurso(dataCriacao: string) {
  if (!dataCriacao) return null;
  const d = new Date(dataCriacao);
  d.setDate(d.getDate() + 30);
  return d.toLocaleDateString('pt-BR');
}

const categorias = [
  { icon: '🔊', titulo: 'Barulho', desc: 'Som alto, festas, animais', tipo: 'Barulho Excessivo' },
  { icon: '🏗️', titulo: 'Infraestrutura', desc: 'Elevadores, iluminação, vazamentos', tipo: 'Limpeza' },
  { icon: '📋', titulo: 'Outro', desc: 'Segurança, limpeza, outros', tipo: 'Outros' },
];

const OcorrenciasMorador: React.FC = () => {
  const [ocorrencias, setOcorrencias] = useState<any[]>([]);
  const [multas, setMultas] = useState<any[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [tipoInicial, setTipoInicial] = useState<string | undefined>();

  // recurso modal state
  const [recursoMultaId, setRecursoMultaId] = useState<number | null>(null);
  const [justificativa, setJustificativa] = useState('');
  const [enviandoRecurso, setEnviandoRecurso] = useState(false);

  // detail modal state
  const [ocorrenciaDetalhe, setOcorrenciaDetalhe] = useState<any | null>(null);

  const carregar = useCallback(async () => {
    setCarregando(true);
    try {
      const [ocs, ms] = await Promise.all([
        listarOcorrenciasPorUnidade(UNIDADE_ID),
        listarMultasPorUnidade(UNIDADE_ID),
      ]);
      setOcorrencias(ocs);
      setMultas(ms);
    } catch (e) {
      console.error(e);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  const abrirModal = (tipo?: string) => {
    setTipoInicial(tipo);
    setModalAberto(true);
  };

  const salvar = async (dados: any) => {
    try {
      await criarOcorrencia(dados);
      setModalAberto(false);
      carregar();
    } catch (e: any) {
      alert('Erro ao registrar ocorrência: ' + e.message);
    }
  };

  const abrirRecurso = (multaId: number) => {
    setRecursoMultaId(multaId);
    setJustificativa('');
  };

  const enviarRecurso = async () => {
    if (!recursoMultaId || !justificativa.trim()) return;
    setEnviandoRecurso(true);
    try {
      await contestarMulta(recursoMultaId, justificativa);
      setRecursoMultaId(null);
      carregar();
    } catch (e: any) {
      alert('Erro ao enviar recurso: ' + e.message);
    } finally {
      setEnviandoRecurso(false);
    }
  };

  return (
    <div className="ocm-page">
      {/* Header */}
      <div className="ocm-header">
        <div>
          <h1 className="ocm-title">Ocorrências</h1>
          <p className="ocm-subtitle">Registre e acompanhe problemas no condomínio</p>
        </div>
        <button className="ocm-btn-registrar" onClick={() => abrirModal()}>
          + Registrar Ocorrência
        </button>
      </div>

      {carregando ? (
        <div className="ocm-loading">Carregando...</div>
      ) : (
        <>
          {/* MINHAS OCORRÊNCIAS */}
          <section className="ocm-section">
            <h2 className="ocm-section-label">MINHAS OCORRÊNCIAS</h2>
            {ocorrencias.length === 0 ? (
              <div className="ocm-empty">Nenhuma ocorrência registrada para sua unidade.</div>
            ) : (
              <div className="ocm-cards">
                {ocorrencias.map((oc) => (
                  <div key={oc.id} className="ocm-card ocm-card--clickable" onClick={() => setOcorrenciaDetalhe(oc)}>
                    <div className="ocm-card-icon">
                      <span>≡</span>
                    </div>
                    <div className="ocm-card-body">
                      <div className="ocm-card-title">
                        {oc.tipo || 'Ocorrência'} — Apto {oc.unidadeId}
                      </div>
                      <div className="ocm-card-desc">{oc.descricao}</div>
                      <div className="ocm-card-date">Registrada em {formatarData(oc.dataRegistro)}</div>
                      {oc.valorMulta != null && (
                        <div className="ocm-multa-valor">Multa prevista: {formatarMoeda(oc.valorMulta)}</div>
                      )}
                    </div>
                    <div
                      className="ocm-status-dot"
                      style={{ color: statusColors[oc.status] || '#6b7280' }}
                    >
                      ● {statusLabels[oc.status] || oc.status}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>

          {/* MULTAS E RECURSOS */}
          <section className="ocm-section">
            <h2 className="ocm-section-label">MULTAS E RECURSOS</h2>
            {multas.length === 0 ? (
              <div className="ocm-empty">Nenhuma multa aplicada à sua unidade.</div>
            ) : (
              <div className="ocm-cards">
                {multas.map((m) => (
                  <div
                    key={m.id}
                    className={`ocm-card ocm-card--multa ${m.status === 'ABERTA' ? 'ocm-card--multa-aberta' : ''}`}
                  >
                    <div className={`ocm-card-icon ${m.status === 'ABERTA' ? 'ocm-card-icon--red' : ''}`}>
                      <span>≡</span>
                    </div>
                    <div className="ocm-card-body">
                      {m.status === 'CONTESTADA' || m.status === 'CANCELADA' ? (
                        <>
                          <div className="ocm-card-title">
                            Recurso Julgado — Multa #{String(m.id).padStart(4, '0')} ({m.descricao?.replace('Multa: ', '')})
                          </div>
                          {m.justificativaContestacao && (
                            <div className="ocm-card-desc">
                              Sua defesa: "{m.justificativaContestacao}"
                            </div>
                          )}
                          {m.status === 'CANCELADA' && (
                            <div className="ocm-decision-box">
                              <strong>Decisão do Síndico:</strong> Recurso aceito. A multa foi cancelada e não constará no próximo boleto.
                            </div>
                          )}
                          {m.dataContestacao && (
                            <div className="ocm-card-date">Julgado em {formatarData(m.dataContestacao)}</div>
                          )}
                        </>
                      ) : (
                        <>
                          <div className="ocm-card-title">
                            Multa Aplicada — {m.descricao?.replace('Multa: ', '') || 'Infração'}
                          </div>
                          {m.ocorrenciaId && (
                            <div className="ocm-card-desc">
                              Referente à Ocorrência #{String(m.ocorrenciaId).padStart(4, '0')}.
                            </div>
                          )}
                          <div className="ocm-multa-valor">Valor: {formatarMoeda(m.valor)}</div>
                          {m.status === 'ABERTA' && m.dataCriacao && (
                            <div className="ocm-multa-prazo">
                              Prazo limite para recurso: até {prazoRecurso(m.dataCriacao)}
                            </div>
                          )}
                        </>
                      )}
                    </div>
                    <div className="ocm-card-side">
                      <span
                        className="ocm-status-dot"
                        style={{ color: multaStatusColors[m.status] || '#6b7280' }}
                      >
                        ● {multaStatusLabels[m.status] || m.status}
                      </span>
                      {m.status === 'ABERTA' && (
                        <button
                          className="ocm-btn-recurso"
                          onClick={() => abrirRecurso(m.id)}
                        >
                          Abrir Recurso ⚖️
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>

          {/* COMO REGISTRAR */}
          <section className="ocm-section">
            <h2 className="ocm-section-label">COMO REGISTRAR</h2>
            <div className="ocm-categorias">
              {categorias.map((cat) => (
                <button
                  key={cat.titulo}
                  className="ocm-categoria-card"
                  onClick={() => abrirModal(cat.tipo)}
                >
                  <span className="ocm-categoria-icon">{cat.icon}</span>
                  <span className="ocm-categoria-titulo">{cat.titulo}</span>
                  <span className="ocm-categoria-desc">{cat.desc}</span>
                </button>
              ))}
            </div>
          </section>
        </>
      )}

      {/* Registrar Ocorrência Modal */}
      {modalAberto && (
        <ModalOcorrencia
          tipoInicial={tipoInicial}
          onSalvar={salvar}
          onFechar={() => setModalAberto(false)}
        />
      )}

      {/* Occurrence Detail Modal */}
      {ocorrenciaDetalhe && (
        <div className="ocm-overlay" onClick={() => setOcorrenciaDetalhe(null)}>
          <div className="ocm-detalhe-modal" onClick={(e) => e.stopPropagation()}>
            <div className="ocm-detalhe-header">
              <div>
                <span className="ocm-detalhe-id">#{String(ocorrenciaDetalhe.id).padStart(4, '0')}</span>
                <h3 className="ocm-detalhe-titulo">{ocorrenciaDetalhe.tipo || 'Ocorrência'}</h3>
              </div>
              <button className="ocm-detalhe-close" onClick={() => setOcorrenciaDetalhe(null)}>✕</button>
            </div>

            <div className="ocm-detalhe-grid">
              <div className="ocm-detalhe-item">
                <span className="ocm-detalhe-label">Unidade</span>
                <span>Apto {ocorrenciaDetalhe.unidadeId}</span>
              </div>
              <div className="ocm-detalhe-item">
                <span className="ocm-detalhe-label">Status</span>
                <span
                  className="ocm-detalhe-status"
                  style={{ color: statusColors[ocorrenciaDetalhe.status] || '#6b7280' }}
                >
                  ● {statusLabels[ocorrenciaDetalhe.status] || ocorrenciaDetalhe.status}
                </span>
              </div>
              <div className="ocm-detalhe-item">
                <span className="ocm-detalhe-label">Data de Registro</span>
                <span>{formatarData(ocorrenciaDetalhe.dataRegistro)}</span>
              </div>
              {ocorrenciaDetalhe.relatorNome && (
                <div className="ocm-detalhe-item">
                  <span className="ocm-detalhe-label">Registrado por</span>
                  <span>{ocorrenciaDetalhe.relatorNome}</span>
                </div>
              )}
              {ocorrenciaDetalhe.penalidade && ocorrenciaDetalhe.penalidade !== 'NENHUMA' && (
                <div className="ocm-detalhe-item">
                  <span className="ocm-detalhe-label">Penalidade</span>
                  <span>
                    {ocorrenciaDetalhe.penalidade === 'MULTA' && ocorrenciaDetalhe.valorMulta
                      ? formatarMoeda(ocorrenciaDetalhe.valorMulta)
                      : 'Advertência'}
                  </span>
                </div>
              )}
              {ocorrenciaDetalhe.penalidade !== 'MULTA' && ocorrenciaDetalhe.valorMulta != null && (
                <div className="ocm-detalhe-item">
                  <span className="ocm-detalhe-label">Multa prevista</span>
                  <span>{formatarMoeda(ocorrenciaDetalhe.valorMulta)}</span>
                </div>
              )}
            </div>

            <div className="ocm-detalhe-descricao">
              <span className="ocm-detalhe-label">Descrição</span>
              <p>{ocorrenciaDetalhe.descricao}</p>
            </div>

            {ocorrenciaDetalhe.observacaoSindico && (
              <div className="ocm-detalhe-observacao">
                <span className="ocm-detalhe-label">Observação do Síndico</span>
                <p>{ocorrenciaDetalhe.observacaoSindico}</p>
              </div>
            )}

            <div className="ocm-detalhe-footer">
              <button className="ocm-btn-cancelar" onClick={() => setOcorrenciaDetalhe(null)}>Fechar</button>
            </div>
          </div>
        </div>
      )}

      {/* Recurso Modal */}
      {recursoMultaId !== null && (
        <div className="ocm-overlay" onClick={() => setRecursoMultaId(null)}>
          <div className="ocm-recurso-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Abrir Recurso</h3>
            <p>Descreva sua defesa para contestar esta multa:</p>
            <textarea
              rows={4}
              placeholder="Explique o motivo da contestação..."
              value={justificativa}
              onChange={(e) => setJustificativa(e.target.value)}
            />
            <div className="ocm-recurso-footer">
              <button className="ocm-btn-cancelar" onClick={() => setRecursoMultaId(null)}>
                Cancelar
              </button>
              <button
                className="ocm-btn-enviar"
                onClick={enviarRecurso}
                disabled={enviandoRecurso || !justificativa.trim()}
              >
                {enviandoRecurso ? 'Enviando...' : 'Enviar Recurso'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OcorrenciasMorador;
