import React, { useState, useEffect } from 'react';
import './ModalOcorrencia.css';

const tiposInfracao = [
  'Barulho Excessivo',
  'Descarte Irregular',
  'Vandalismo',
  'Limpeza',
  'Uso Indevido de Área Comum',
  'Estacionamento Irregular',
  'Animais sem Guia',
  'Outros',
];

interface ModalOcorrenciaProps {
  ocorrencia?: any;
  onSalvar: (dados: any) => void;
  onFechar: () => void;
}

export const ModalOcorrencia: React.FC<ModalOcorrenciaProps> = ({ ocorrencia, onSalvar, onFechar }) => {
  const editando = !!ocorrencia;

  const [unidade, setUnidade] = useState('');
  const [tipo, setTipo] = useState('');
  const [data, setData] = useState('');
  const [hora, setHora] = useState('');
  const [descricao, setDescricao] = useState('');
  const [acaoInicial, setAcaoInicial] = useState('Aberto para Análise');

  useEffect(() => {
    if (ocorrencia) {
      setUnidade(ocorrencia.unidadeId?.toString() || ocorrencia.unidade || '');
      setTipo(ocorrencia.tipo || '');
      
      // Converte dd/mm/yyyy → yyyy-mm-dd para o input date se for o caso
      if (ocorrencia.data) {
        const partes = ocorrencia.data.split('/');
        if (partes.length === 3) {
          setData(`${partes[2]}-${partes[1]}-${partes[0]}`);
        } else {
          setData(ocorrencia.data);
        }
      } else if (ocorrencia.dataRegistro) {
        setData(ocorrencia.dataRegistro.split('T')[0]);
      }
      setDescricao(ocorrencia.descricao || '');
    }
  }, [ocorrencia]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSalvar({
      unidadeId: parseInt(unidade) || undefined,
      tipo,
      descricao,
    });
  };

  return (
    <div className="modal-overlay" onClick={onFechar} id="modal-overlay-ocorrencia">
      <div
        className="modal-container"
        onClick={(e) => e.stopPropagation()}
        id={editando ? 'modal-editar-ocorrencia' : 'modal-registrar-ocorrencia'}
      >
        {/* Header */}
        <div className="modal-header">
          <h2>{editando ? 'Editar Ocorrência' : 'Registrar Ocorrência'}</h2>
          <button className="modal-close" onClick={onFechar} id="btn-fechar-modal" title="Fechar">
            ✕
          </button>
        </div>

        {/* Warning Banner */}
        <div className="modal-warning">
          <div className="warning-icon">⚠️</div>
          <p>
            <strong>Regra {editando ? 'ILMR3' : 'ILMR5'}:</strong> Ocorrências ficam visíveis ao histórico do
            morador. Elas podem ser usadas como base para advertências ou
            multas.
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="modal-form">
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
                {tiposInfracao.map((t) => (
                  <option key={t} value={t}>{t}</option>
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

          <div className="form-group full">
            <label htmlFor="campo-acao">AÇÃO INICIAL (STATUS)</label>
            <select
              id="campo-acao"
              value={acaoInicial}
              onChange={(e) => setAcaoInicial(e.target.value)}
            >
              <option value="Aberto para Análise">Apenas Registrar (Aberto para Análise)</option>
              <option value="Pendente">Aplicar e-ter (Aberto para Análise)</option>
            </select>
          </div>

          {/* Footer Buttons */}
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
