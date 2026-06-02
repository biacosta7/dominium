import { useEffect, useState } from 'react';
import type { Pauta } from '../types/pauta';
import type { ResumoVotos } from '../types/voto';
import { votoService } from '../services/votoService';
import { pautaService } from '../services/pautaService';

interface Props {
  pauta: Pauta;
  onAtualizar: () => void;
}

export default function PautaCardAdmin({ pauta, onAtualizar }: Props) {
  const [resumo, setResumo] = useState<ResumoVotos | null>(null);
  const [encerrando, setEncerrando] = useState(false);

  useEffect(() => {
    votoService.listarPorPauta(pauta.id).then((votos) => {
      setResumo(votoService.resumir(votos));
    });
  }, [pauta.id]);

  async function handleEncerrar() {
    if (!confirm(`Encerrar a pauta "${pauta.titulo}"?`)) return;
    setEncerrando(true);
    try {
      await pautaService.encerrar(pauta.id);
      onAtualizar();
    } catch (e: any) {
      alert(e.message);
    } finally {
      setEncerrando(false);
    }
  }

  const pctFavor = resumo && resumo.total > 0
    ? Math.round((resumo.favor / resumo.total) * 100)
    : 0;

  const statusLabel = pauta.status === 'ABERTA' ? 'Votação em Curso' : 'Encerrada';
  const statusClass = pauta.status === 'ABERTA' ? 'badge-aberta' : 'badge-encerrada';

  return (
    <div className="pauta-card">
      <div className="pauta-card-header">
        <div>
          <h3 className="pauta-titulo">Gestão de Votação Ativa</h3>
          <p className="pauta-nome">{pauta.titulo}</p>
          {pauta.descricao && <p className="pauta-desc">{pauta.descricao}</p>}
          {resumo && (
            <p className="pauta-meta">
              Quórum atual: {resumo.total} voto(s)
            </p>
          )}
        </div>
        <span className={`badge ${statusClass}`}>{statusLabel}</span>
      </div>

      {resumo && pauta.status === 'ABERTA' && (
        <div className="pauta-resultado-parcial">
          <div className="resultado-linha">
            <span>Aprovação (Sim)</span>
            <span className="resultado-pct favor">{pctFavor}%</span>
          </div>
          <div className="barra-bg">
            <div className="barra-fill favor" style={{ width: `${pctFavor}%` }} />
          </div>
        </div>
      )}

      {pauta.status === 'ENCERRADA' && pauta.resultado && (
        <div className={`resultado-final resultado-${pauta.resultado.toLowerCase()}`}>
          Resultado: <strong>{pauta.resultado}</strong>
        </div>
      )}

      {pauta.status === 'ABERTA' && (
        <div className="pauta-card-footer">
          <button className="btn-encerrar" onClick={handleEncerrar} disabled={encerrando}>
            {encerrando ? 'Encerrando...' : 'Encerrar Votação'}
          </button>
        </div>
      )}
    </div>
  );
}