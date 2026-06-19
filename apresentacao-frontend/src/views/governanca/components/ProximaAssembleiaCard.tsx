import type { Assembleia } from '../types/assembleia';

interface Props {
  assembleia: Assembleia;
  onEditar: () => void;
  onCancelar: () => void;
  onEncerrar: () => void;
  processando: boolean;
}

function formatarDataHora(dataHora: string) {
  const data = new Date(dataHora);
  const dia = String(data.getDate()).padStart(2, '0');
  const mes = String(data.getMonth() + 1).padStart(2, '0');
  const ano = data.getFullYear();
  const hora = String(data.getHours()).padStart(2, '0');
  const minuto = String(data.getMinutes()).padStart(2, '0');
  return `${dia}/${mes}/${ano} · ${hora}h${minuto !== '00' ? minuto : ''}`;
}

const TIPO_LABEL: Record<string, string> = {
  ORDINARIA: 'Ordinária',
  EXTRAORDINARIA: 'Extraordinária',
};

export default function ProximaAssembleiaCard({
  assembleia, onEditar, onCancelar, onEncerrar, processando,
}: Props) {
  return (
    <div className="pauta-card proxima-assembleia-card">
      <span className="badge badge-proxima">Próxima · {formatarDataHora(assembleia.dataHora)}</span>

      <h3 className="pauta-nome" style={{ marginTop: 10 }}>{assembleia.titulo}</h3>
      <p className="pauta-desc">{TIPO_LABEL[assembleia.tipo]} · {assembleia.local}</p>

      {assembleia.pauta.length > 0 && (
        <div className="assembleia-pautas-lista">
          <p className="section-label" style={{ marginBottom: 6 }}>PAUTAS</p>
          <ol>
            {assembleia.pauta.map((item, idx) => (
              <li key={idx}>{item}</li>
            ))}
          </ol>
        </div>
      )}

      <div className="pauta-card-footer" style={{ justifyContent: 'flex-start', gap: 10 }}>
        <button className="btn-primario" onClick={onEditar} disabled={processando}>
          Editar Assembleia
        </button>
        <button className="btn-secundario" onClick={onEncerrar} disabled={processando}>
          Encerrar
        </button>
        <button className="btn-encerrar" onClick={onCancelar} disabled={processando}>
          Cancelar
        </button>
      </div>
    </div>
  );
}
