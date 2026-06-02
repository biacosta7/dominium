import { useState } from 'react';
import type { CriarPautaRequest, TipoMaioria, TipoQuorum } from '../types/pauta';
import { pautaService } from '../services/pautaService';

interface Props {
  assembleiaId: number;
  onClose: () => void;
  onCriada: () => void;
}

export default function ModalCriarPauta({ assembleiaId, onClose, onCriada }: Props) {
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [maioria, setMaioria] = useState<TipoMaioria>('SIMPLES');
  const [quorum, setQuorum] = useState<TipoQuorum>('SIMPLES');
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState('');

  async function handleSubmit() {
    if (!titulo.trim()) {
      setErro('Título é obrigatório');
      return;
    }
    setLoading(true);
    setErro('');
    try {
      const payload: CriarPautaRequest = {
        assembleiaId,
        titulo,
        descricao,
        quorum,
        maioria,
      };
      await pautaService.criar(payload);
      onCriada();
      onClose();
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Criar Nova Pauta de Votação</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          <label className="field-label">TÍTULO DA PAUTA</label>
          <input
            className="field-input"
            placeholder="Ex: Substituição do mobiliário da piscina"
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
          />

          <div className="field-row">
            <div className="field-col">
              <label className="field-label">TIPO DE MAIORIA</label>
              <select
                className="field-input"
                value={maioria}
                onChange={(e) => setMaioria(e.target.value as TipoMaioria)}
              >
                <option value="SIMPLES">Maioria Simples (50% + 1 dos votos)</option>
                <option value="ABSOLUTA">Maioria Absoluta ({'>'} 50% do total)</option>
                <option value="QUALIFICADA">Maioria Qualificada (2/3 dos votos)</option>
              </select>
            </div>
            <div className="field-col">
              <label className="field-label">TIPO DE QUÓRUM</label>
              <select
                className="field-input"
                value={quorum}
                onChange={(e) => setQuorum(e.target.value as TipoQuorum)}
              >
                <option value="SIMPLES">Simples</option>
                <option value="QUALIFICADO">Qualificado</option>
              </select>
            </div>
          </div>

          <label className="field-label">DESCRIÇÃO E CONTEXTO</label>
          <textarea
            className="field-input field-textarea"
            placeholder="Explique os detalhes para os moradores..."
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
          />

          {erro && <p className="field-erro">{erro}</p>}
        </div>

        <div className="modal-footer">
          <button className="btn-secundario" onClick={onClose} disabled={loading}>
            Cancelar
          </button>
          <button className="btn-primario" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Abrindo...' : 'Abrir Votação'}
          </button>
        </div>
      </div>
    </div>
  );
}