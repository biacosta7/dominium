import type { Reserva } from "../types/reserva";

interface Props {
  reserva: Reserva;
  onCancelar: (id: string) => void;
  onEditar?: (reserva: Reserva) => void;
}

export default function ReservaCard({
  reserva,
  onCancelar,
  onEditar,
}: Props) {
  return (
    <div className="res-card">

      <div className="res-color-bar" />

      <div className="res-info">
        <div className="res-title">
          {reserva.nomeArea}
        </div>

        <div className="res-meta">
          {reserva.data} · {reserva.horaInicio.substring(0, 5)} às {reserva.horaFim.substring(0, 5)}
        </div>
      </div>

      <div className="res-actions">
        <button
          className="btn btn-outline btn-sm"
          onClick={() => onCancelar(reserva.reservaId)}
        >
          Cancelar
        </button>
        {onEditar && (
          <button
            className="btn btn-primary btn-sm"
            onClick={() => onEditar(reserva)}
          >
            Editar
          </button>
        )}
      </div>

    </div>
  );
}