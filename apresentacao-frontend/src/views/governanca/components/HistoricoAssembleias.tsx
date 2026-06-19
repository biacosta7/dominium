import type { Assembleia } from '../types/assembleia';

interface Props {
  assembleias: Assembleia[];
}

function formatarData(dataHora: string) {
  const data = new Date(dataHora);
  return `${String(data.getDate()).padStart(2, '0')}/${String(data.getMonth() + 1).padStart(2, '0')}/${data.getFullYear()}`;
}

const TIPO_LABEL: Record<string, string> = {
  ORDINARIA: 'Ordinária',
  EXTRAORDINARIA: 'Extraordinária',
};

function statusExibido(a: Assembleia): { label: string; classe: string } {
  if (a.status === 'ENCERRADA') return { label: 'Concluída', classe: 'badge-concluida' };
  if (a.status === 'CANCELADA') return { label: 'Cancelada', classe: 'badge-cancelada' };
  // AGENDADA só é exibida como "Agendada" se a data ainda não passou
  const jaPassou = new Date(a.dataHora) < new Date();
  return jaPassou
    ? { label: 'Expirada', classe: 'badge-expirada' }
    : { label: 'Agendada', classe: 'badge-agendada' };
}

export default function HistoricoAssembleias({ assembleias }: Props) {
  if (assembleias.length === 0) {
    return (
      <div className="card-box-empty">
        <p>Nenhuma assembleia no histórico ainda.</p>
      </div>
    );
  }

  return (
    <table className="historico-table">
      <thead>
        <tr>
          <th>DATA</th>
          <th>TIPO</th>
          <th>LOCAL</th>
          <th>PAUTAS</th>
          <th>STATUS</th>
        </tr>
      </thead>
      <tbody>
        {assembleias.map((a) => {
          const { label, classe } = statusExibido(a);
          return (
            <tr key={a.id}>
              <td>{formatarData(a.dataHora)}</td>
              <td>{TIPO_LABEL[a.tipo]}</td>
              <td>{a.local}</td>
              <td>{a.pauta.length} pauta{a.pauta.length !== 1 ? 's' : ''}</td>
              <td>
                <span className={`badge ${classe}`}>{label}</span>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
