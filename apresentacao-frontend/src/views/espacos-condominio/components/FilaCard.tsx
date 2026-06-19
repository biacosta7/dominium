import type { FilaEspera } from "../types/reserva";

interface FilaCardProps {
  item: FilaEspera;
}

export default function FilaCard({ item }: FilaCardProps) {
  return (
    <div className="res-card fila-card">
      <div className="fila-posicao">{item.posicao}º</div>

      <div className="res-color-bar fila-color-bar" />

      <div className="res-info">
        <div className="res-title">{item.nomeArea}</div>
        <div className="res-meta">
          {item.dataDesejada} · {item.horaInicio.substring(0, 5)} às {item.horaFim.substring(0, 5)}
        </div>
        <div className="fila-status">Aguardando vaga · {item.posicao}º na fila</div>
      </div>
    </div>
  );
}
