import React, { useState, useEffect } from 'react';
import './ModalOcorrencia.css';
import { listarTiposOcorrencia } from '../services/ocorrenciaService';

interface TipoOcorrencia {
  id: number;
  nome: string;
  valorBaseMulta: number;
}

interface ModalOcorrenciaProps {
  ocorrencia?: any;
  tipoInicial?: string;
  onSalvar: (dados: any) => void;
  onEncerrar?: (id: number, dados: any) => void;
  onFechar: () => void;
}

const formatarMoeda = (valor: number) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);

const statusLabels: Record<string, string> = {
  ABERTA: 'Aberto',
  EM_ANALISE: 'Em Análise',
  ENCERRADA: 'Encerrado',
};

export const ModalOcorrencia: React.FC<ModalOcorrenciaProps> = ({
  ocorrencia,
  tipoInicial,
  onSalvar,
  onEncerrar,
  onFechar,
}) => {
  const editando = !!ocorrencia;
  const jaEncerrada = ocorrencia?.status === 'ENCERRADA';

  // ─── campos de criação ───────────────────────────────────────
  const [unidade, setUnidade] = useState('');
  const [tipo, setTipo] = useState(tipoInicial || '');
  const [data, setData] = useState('');
  const [hora, setHora] = useState('');
  const [descricao, setDescricao] = useState('');

  // ─── campos de encerramento ──────────────────────────────────
  const [penalidade, setPenalidade] = useState('NENHUMA');
  const [observacao, setObservacao] = useState('');

  // ─── tipos carregados do backend ─────────────────────────────
  const [tiposOcorrencia, setTiposOcorrencia] = useState<TipoOcorrencia[]>([]);

  useEffect(() => {
    listarTiposOcorrencia()
      .then(setTiposOcorrencia)
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (ocorrencia) {
      setUnidade(ocorrencia.unidadeId?.toString() || '');
      setTipo(ocorrencia.tipo || '');
      if (ocorrencia.dataRegistro) {
        setData(ocorrencia.dataRegistro.split('T')[0]);
      }
      setDescricao(ocorrencia.descricao || '');
    }
  }, [ocorrencia]);

  const tipoSelecionado = tiposOcorrencia.find((t) => t.nome === (editando ? ocorrencia?.tipo : tipo));

  const handleCriar = (e: React.FormEvent) => {
    e.preventDefault();
    onSalvar({ unidadeId: parseInt(unidade) || undefined, tipo, descricao });
  };

  const handleEncerrar = (e: React.FormEvent) => {
    e.preventDefault();
    onEncerrar(ocorrencia.id, { penalidade, observacao });
  };

  // ─── modo visualização de ocorrência encerrada ───────────────
  if (editando && jaEncerrada) {
    return (
      <div className="modal-overlay" onClick={onFechar} id="modal-overlay-ocorrencia">
        <div className="modal-container" onClick={(e) => e.stopPropagation()} id="modal-editar-ocorrencia">
          <div className="modal-header">
            <h2>Ocorrência Encerrada</h2>
            <button className="modal-close" onClick={onFechar} title="Fechar">✕</button>
          </div>
          <div className="modal-info-grid">
            <div className="info-item"><span className="info-label">Unidade</span><span>{ocorrencia.unidadeId}</span></div>
            <div className="info-item"><span className="info-label">Tipo</span><span>{ocorrencia.tipo || '—'}</span></div>
            <div className="info-item"><span className="info-label">Status</span><span>{statusLabels[ocorrencia.status] || ocorrencia.status}</span></div>
            <div className="info-item">
              <span className="info-label">Penalidade</span>
              <span>
                {ocorrencia.penalidade === 'MULTA' && ocorrencia.valorMulta
                  ? formatarMoeda(ocorrencia.valorMulta)
                  : ocorrencia.penalidade === 'ADVERTENCIA'
                  ? 'Advertência'
                  : 'Nenhuma'}
              </span>
            </div>
            {ocorrencia.observacaoSindico && (
              <div className="info-item full"><span className="info-label">Observação</span><span>{ocorrencia.observacaoSindico}</span></div>
            )}
            <div className="info-item full"><span className="info-label">Descrição</span><span>{ocorrencia.descricao}</span></div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn-cancelar" onClick={onFechar}>Fechar</button>
          </div>
        </div>
      </div>
    );
  }

  // ─── modo encerramento (editar ocorrência aberta) ────────────
  if (editando) {
    return (
      <div className="modal-overlay" onClick={onFechar} id="modal-overlay-ocorrencia">
        <div className="modal-container" onClick={(e) => e.stopPropagation()} id="modal-editar-ocorrencia">
          <div className="modal-header">
            <h2>Encerrar Ocorrência #{String(ocorrencia.id).padStart(4, '0')}</h2>
            <button className="modal-close" onClick={onFechar} title="Fechar">✕</button>
          </div>

          <div className="modal-info-grid">
            <div className="info-item"><span className="info-label">Unidade</span><span>{ocorrencia.unidadeId}</span></div>
            <div className="info-item"><span className="info-label">Tipo</span><span>{ocorrencia.tipo || '—'}</span></div>
            <div className="info-item"><span className="info-label">Status Atual</span><span>{statusLabels[ocorrencia.status] || ocorrencia.status}</span></div>
            <div className="info-item full"><span className="info-label">Descrição</span><span>{ocorrencia.descricao}</span></div>
          </div>

          <form onSubmit={handleEncerrar} className="modal-form">
            <div className="form-group full">
              <label htmlFor="campo-penalidade">PENALIDADE</label>
              <select
                id="campo-penalidade"
                value={penalidade}
                onChange={(e) => setPenalidade(e.target.value)}
              >
                <option value="NENHUMA">Sem penalidade</option>
                <option value="ADVERTENCIA">Advertência</option>
                <option value="MULTA">Multa</option>
              </select>
            </div>

            {penalidade === 'MULTA' && tipoSelecionado && (
              <div className="multa-preview">
                <span className="multa-preview-label">Valor base para "{tipoSelecionado.nome}":</span>
                <span className="multa-preview-valor">{formatarMoeda(tipoSelecionado.valorBaseMulta)}</span>
                <span className="multa-preview-nota">Reincidências adicionam 10% por ocorrência anterior da mesma unidade.</span>
              </div>
            )}

            {penalidade === 'MULTA' && !tipoSelecionado && (
              <div className="multa-preview">
                <span className="multa-preview-nota">Valor padrão aplicado: R$ 150,00. Reincidências adicionam 10%.</span>
              </div>
            )}

            <div className="form-group full">
              <label htmlFor="campo-observacao">OBSERVAÇÃO DO SÍNDICO</label>
              <textarea
                id="campo-observacao"
                rows={3}
                placeholder="Descreva a decisão tomada..."
                value={observacao}
                onChange={(e) => setObservacao(e.target.value)}
              />
            </div>

            <div className="modal-footer">
              <button type="button" className="btn-cancelar" onClick={onFechar}>Cancelar</button>
              <button type="submit" className="btn-encerrar" id="btn-encerrar-modal">
                Encerrar Ocorrência
              </button>
            </div>
          </form>
        </div>
      </div>
    );
  }

  // ─── modo criação ─────────────────────────────────────────────
  return (
    <div className="modal-overlay" onClick={onFechar} id="modal-overlay-ocorrencia">
      <div className="modal-container" onClick={(e) => e.stopPropagation()} id="modal-registrar-ocorrencia">
        <div className="modal-header">
          <h2>Registrar Ocorrência</h2>
          <button className="modal-close" onClick={onFechar} id="btn-fechar-modal" title="Fechar">✕</button>
        </div>

        <div className="modal-warning">
          <div className="warning-icon">⚠️</div>
          <p>
            <strong>Regra ILMR5:</strong> Ocorrências ficam visíveis ao histórico do morador. Elas podem ser
            usadas como base para advertências ou multas.
          </p>
        </div>

        <form onSubmit={handleCriar} className="modal-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="campo-unidade">UN. BASE INFRATORA/MULTA</label>
              <div className="input-with-prefix">
                <span className="input-prefix">Ex.</span>
                <input
                  type="text"
                  id="campo-unidade"
                  placeholder="501"
                  value={unidade}
                  onChange={(e) => setUnidade(e.target.value)}
                  required
                />
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="campo-tipo">TIPO DE INFRAÇÃO</label>
              <select
                id="campo-tipo"
                value={tipo}
                onChange={(e) => setTipo(e.target.value)}
                required
              >
                <option value="">Selecione...</option>
                {tiposOcorrencia.map((t) => (
                  <option key={t.id} value={t.nome}>
                    {t.nome} — {formatarMoeda(t.valorBaseMulta)}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="campo-data">DATA DO OCORRIDO</label>
              <input
                type="date"
                id="campo-data"
                value={data}
                onChange={(e) => setData(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="campo-hora">HORA APROXIMADA</label>
              <input
                type="time"
                id="campo-hora"
                value={hora}
                onChange={(e) => setHora(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group full">
            <label htmlFor="campo-descricao">DESCRIÇÃO DETALHADA</label>
            <textarea
              id="campo-descricao"
              rows={4}
              placeholder="Descreva os fatos ocorridos com o máximo de detalhes..."
              value={descricao}
              onChange={(e) => setDescricao(e.target.value)}
              required
            />
          </div>

          <div className="modal-footer">
            <button type="button" className="btn-cancelar" onClick={onFechar} id="btn-cancelar-modal">
              Cancelar
            </button>
            <button type="submit" className="btn-salvar" id="btn-salvar-modal">
              Salvar Registro
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
