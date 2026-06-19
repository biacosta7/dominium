import { useState } from 'react';
import type { Assembleia, TipoAssembleia } from '../types/assembleia';
import { assembleiaService } from '../services/assembleiaService';

interface Props {
  assembleiaEmEdicao?: Assembleia | null;
  onClose: () => void;
  onSalva: () => void;
}

function paraDataInput(dataHora: string) {
  return dataHora ? dataHora.substring(0, 10) : '';
}

function paraHoraInput(dataHora: string) {
  return dataHora ? dataHora.substring(11, 16) : '';
}

function dataMinima() {
  const min = new Date();
  min.setDate(min.getDate() + 5);
  return min.toISOString().substring(0, 10);
}

export default function ModalAssembleia({ assembleiaEmEdicao, onClose, onSalva }: Props) {
  const editando = !!assembleiaEmEdicao;

  const [titulo, setTitulo] = useState(assembleiaEmEdicao?.titulo ?? '');
  const [data, setData] = useState(paraDataInput(assembleiaEmEdicao?.dataHora ?? ''));
  const [hora, setHora] = useState(paraHoraInput(assembleiaEmEdicao?.dataHora ?? ''));
  const [local, setLocal] = useState(assembleiaEmEdicao?.local ?? '');
  const [tipo, setTipo] = useState<TipoAssembleia>(assembleiaEmEdicao?.tipo ?? 'ORDINARIA');
  const [pautaTexto, setPautaTexto] = useState((assembleiaEmEdicao?.pauta ?? []).join('\n'));
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState('');

  async function handleSubmit() {
    if (!titulo.trim() || !data || !hora || !local.trim()) {
      setErro('Preencha todos os campos obrigatórios');
      return;
    }

    const pauta = pautaTexto
      .split('\n')
      .map((linha) => linha.replace(/^\s*\d+[.)]\s*/, '').trim())
      .filter((linha) => linha.length > 0);

    setLoading(true);
    setErro('');
    try {
      const payload = { titulo, dataHora: `${data}T${hora}:00`, local, pauta, tipo };
      if (editando) {
        await assembleiaService.editar(assembleiaEmEdicao!.id, payload);
      } else {
        await assembleiaService.criar(payload);
      }
      onSalva();
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
          <h2>{editando ? 'Editar Assembleia' : 'Criar Assembleia'}</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          <label className="field-label">TÍTULO DA ASSEMBLEIA</label>
          <input
            className="field-input"
            placeholder="Ex: Assembleia Ordinária – Março"
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
          />

          <div className="field-row">
            <div className="field-col">
              <label className="field-label">DATA</label>
              <input
                type="date"
                className="field-input"
                min={dataMinima()}
                value={data}
                onChange={(e) => setData(e.target.value)}
              />
            </div>
            <div className="field-col">
              <label className="field-label">HORA</label>
              <input
                type="time"
                className="field-input"
                value={hora}
                onChange={(e) => setHora(e.target.value)}
              />
            </div>
          </div>

          <div className="field-row">
            <div className="field-col">
              <label className="field-label">LOCAL</label>
              <input
                className="field-input"
                placeholder="Ex: Salão de Festas"
                value={local}
                onChange={(e) => setLocal(e.target.value)}
              />
            </div>
            <div className="field-col">
              <label className="field-label">TIPO</label>
              <select
                className="field-input"
                value={tipo}
                onChange={(e) => setTipo(e.target.value as TipoAssembleia)}
              >
                <option value="ORDINARIA">Ordinária</option>
                <option value="EXTRAORDINARIA">Extraordinária</option>
              </select>
            </div>
          </div>

          <label className="field-label">PAUTAS (UMA POR LINHA)</label>
          <textarea
            className="field-input field-textarea"
            placeholder={'1. Aprovação do orçamento 2026...'}
            value={pautaTexto}
            onChange={(e) => setPautaTexto(e.target.value)}
          />

          {erro && <p className="field-erro">{erro}</p>}
        </div>

        <div className="modal-footer">
          <button className="btn-secundario" onClick={onClose} disabled={loading}>
            Cancelar
          </button>
          <button className="btn-primario" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Salvando...' : editando ? 'Salvar Assembleia' : 'Agendar Assembleia'}
          </button>
        </div>
      </div>
    </div>
  );
}
