import type { Reserva } from "../types/reserva";

interface Props {
  reserva: Reserva;
  onCancelar: (id: string) => void;
  onEditar?: (reserva: Reserva) => void;
  onConfirmar?: (id: string) => void;
}

export default function ReservaCard({
  reserva,
  onCancelar,
  onEditar,
  onConfirmar,
}: Props) {
  return (
    <div className="res-card">

      <div className="res-color-bar" />

      <div className="res-info">
        <div style={{ display: "flex", alignItems: "center", gap: "8px", flexWrap: "wrap", marginBottom: "3px" }}>
          <div className="res-title" style={{ marginBottom: 0 }}>
            {reserva.nomeArea}
          </div>
          <span className={`reserva-status status-${reserva.status.toLowerCase()}`}>
            {reserva.status.replace("_", " ")}
          </span>
        </div>

        <div className="res-meta">
          {reserva.data} · {reserva.horaInicio.substring(0, 5)} às {reserva.horaFim.substring(0, 5)}
        </div>
      </div>

      <div className="res-actions">
        {reserva.status === "AGUARDANDO_CONFIRMACAO" && onConfirmar && (
          <button
            className="btn btn-primary btn-sm"
            onClick={() => onConfirmar(reserva.reservaId)}
          >
            Confirmar
          </button>
        )}
        <button
          className="btn btn-outline btn-sm"
          onClick={() => onCancelar(reserva.reservaId)}
        >
          Cancelar
        </button>
        {onEditar && reserva.status !== "AGUARDANDO_CONFIRMACAO" && (
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