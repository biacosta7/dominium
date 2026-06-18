import React, { useState, useEffect, useCallback } from 'react';
import { ModalOcorrencia } from '../components/ModalOcorrencia';
import { listarOcorrencias, criarOcorrencia, encerrarOcorrencia } from '../services/ocorrenciaService';
import './Ocorrencias.css';

const statusClasses: Record<string, string> = {
  ABERTA: 'status-aberto',
  EM_ANALISE: 'status-pendente',
  ENCERRADA: 'status-resolvido',
};

const statusLabels: Record<string, string> = {
  ABERTA: 'Aberto',
  EM_ANALISE: 'Em Análise',
  ENCERRADA: 'Encerrado',
};

function formatarData(dataStr: string) {
  if (!dataStr) return '—';
  try {
    const d = new Date(dataStr);
    return d.toLocaleDateString('pt-BR');
  } catch {
    return dataStr;
  }
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
}

export const Ocorrencias: React.FC = () => {
  const [ocorrencias, setOcorrencias] = useState<any[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [modalAberto, setModalAberto] = useState(false);
  const [ocorrenciaEdicao, setOcorrenciaEdicao] = useState<any | null>(null);
  const [filtroStatus, setFiltroStatus] = useState('Todos');

  const carregar = useCallback(async () => {
    try {
      setCarregando(true);
      setErro(null);
      const dados = await listarOcorrencias();
      setOcorrencias(dados);
    } catch (e: any) {
      setErro('Não foi possível conectar ao servidor. Verifique se o backend está rodando.');
      console.error(e);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const abrirCriar = () => {
    setOcorrenciaEdicao(null);
    setModalAberto(true);
  };

  const abrirEditar = (oc: any) => {
    setOcorrenciaEdicao(oc);
    setModalAberto(true);
  };

  const fecharModal = () => {
    setModalAberto(false);
    setOcorrenciaEdicao(null);
  };

  const salvar = async (dados: any) => {
    try {
      await criarOcorrencia(dados);
      fecharModal();
      carregar();
    } catch (e: any) {
      alert('Erro ao salvar ocorrência: ' + e.message);
    }
  };

  const encerrar = async (id: number, dados: any) => {
    try {
      await encerrarOcorrencia(id, dados);
      fecharModal();
      carregar();
    } catch (e: any) {
      alert('Erro ao encerrar ocorrência: ' + e.message);
    }
  };

  const ocorrenciasFiltradas = ocorrencias.filter((o) => {
    if (filtroStatus !== 'Todos' && o.status !== filtroStatus) return false;
    return true;
  });

  const statusUnicos = ['Todos', ...Array.from(new Set(ocorrencias.map((o) => o.status).filter(Boolean)))];

  return (
    <div className="admin-content ocorrencias-content" style={{ padding: '32px' }}>
      {/* Header Row */}
      <div className="content-header">
        <div>
          <h1>Ocorrências e Multas</h1>
          <p className="subtitle">Registro e acompanhamento de infrações</p>
        </div>
        <button className="btn-registrar" id="btn-registrar-ocorrencia" onClick={abrirCriar}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path d="M12 5v14M5 12h14"/>
          </svg>
          Registrar Ocorrência
        </button>
      </div>

      {/* Filters */}
      <div className="filters-row" id="filters-row">
        <select
          className="filter-select"
          id="filter-status"
          value={filtroStatus}
          onChange={(e) => setFiltroStatus(e.target.value)}
        >
          {statusUnicos.map((s) => (
            <option key={s as string} value={s as string}>
              {s === 'Todos' ? 'Todos os Status' : statusLabels[s as string] || s}
            </option>
          ))}
        </select>
        <input type="date" className="filter-date" id="filter-date" />

        <button className="btn-atualizar" onClick={carregar} title="Recarregar lista">
          🔄
        </button>
      </div>

      {/* Error Banner */}
      {erro && (
        <div className="error-banner" id="error-banner">
          ⚠️ {erro}
        </div>
      )}

      {/* Table */}
      <div className="table-wrapper" id="table-ocorrencias">
        {carregando ? (
          <div className="loading-state">Carregando ocorrências...</div>
        ) : (
          <table className="ocorrencias-table">
            <thead>
              <tr>
                <th>#</th>
                <th>UNIDADE</th>
                <th>RELATOR</th>
                <th>TIPO</th>
                <th>DESCRIÇÃO</th>
                <th>DATA REGISTRO</th>
                <th>PENALIDADE</th>
                <th>STATUS</th>
                <th>AÇÕES</th>
              </tr>
            </thead>
            <tbody>
              {ocorrenciasFiltradas.map((oc) => (
                <tr key={oc.id}>
                  <td className="td-id">#{String(oc.id).padStart(4, '0')}</td>
                  <td className="td-unidade">{oc.unidadeId || '—'}</td>
                  <td>{oc.relatorNome || '—'}</td>
                  <td className="td-tipo">{oc.tipo || '—'}</td>
                  <td className="td-descricao">{oc.descricao}</td>
                  <td>{formatarData(oc.dataRegistro)}</td>
                  <td>
                    {oc.valorMulta != null ? (
                      <span className="multa-badge multa-valor">{formatarMoeda(oc.valorMulta)}</span>
                    ) : oc.penalidade === 'ADVERTENCIA' ? (
                      <span className="multa-badge advertencia-badge">Advertência</span>
                    ) : (
                      <span className="multa-sem">—</span>
                    )}
                  </td>
                  <td>
                    <span className={`status-badge ${statusClasses[oc.status] || ''}`}>
                      {statusLabels[oc.status] || oc.status}
                    </span>
                  </td>
                  <td className="td-acoes">
                    <button
                      className="btn-acao btn-editar"
                      title={oc.status === 'ENCERRADA' ? 'Ver detalhes' : 'Encerrar ocorrência'}
                      id={`btn-editar-${oc.id}`}
                      onClick={() => abrirEditar(oc)}
                    >
                      {oc.status === 'ENCERRADA' ? '👁️' : '✏️'}
                    </button>
                  </td>
                </tr>
              ))}
              {!carregando && ocorrenciasFiltradas.length === 0 && (
                <tr>
                  <td colSpan={9} className="td-vazio">
                    {erro ? 'Erro ao carregar.' : 'Nenhuma ocorrência encontrada.'}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal */}
      {modalAberto && (
        <ModalOcorrencia
          ocorrencia={ocorrenciaEdicao}
          onSalvar={salvar}
          onEncerrar={encerrar}
          onFechar={fecharModal}
        />
      )}
    </div>
  );
};
