import { useState, useEffect } from "react";
import type { Reserva } from "../types/reserva";

interface Props {
  aberto: boolean;
  onClose: () => void;
  onSalvar: (dados: any) => void;
  reservaEmEdicao?: Reserva | null;
  erro?: string | null;
}

export default function ModalReserva({
  aberto,
  onClose,
  onSalvar,
  reservaEmEdicao,
  erro,
}: Props) {

  const [form, setForm] = useState({
    areaComumId: 1,
    data: "",
    horaInicio: "09:00",
    horaFim: "12:00",
  });

  useEffect(() => {
    if (aberto) {
      if (reservaEmEdicao) {
        setForm({
          areaComumId: reservaEmEdicao.areaComumId,
          data: reservaEmEdicao.data,
          horaInicio: reservaEmEdicao.horaInicio.substring(0, 5),
          horaFim: reservaEmEdicao.horaFim.substring(0, 5),
        });
      } else {
        const hojeStr = new Date().toISOString().substring(0, 10);
        setForm({
          areaComumId: 1,
          data: hojeStr,
          horaInicio: "09:00",
          horaFim: "12:00",
        });
      }
    }
  }, [aberto, reservaEmEdicao]);

  if (!aberto) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal-titulo">{reservaEmEdicao ? "Editar Agendamento" : "Nova Reserva"}</h2>

        {erro && (
          <div className="error-message-box" style={{ 
            backgroundColor: "rgba(185, 28, 28, 0.08)", 
            border: "1px solid var(--text-danger, #b91c1c)", 
            borderRadius: "var(--r-sm, 8px)", 
            padding: "10px 12px", 
            color: "var(--text-danger, #b91c1c)", 
            marginBottom: "14px",
            fontSize: "13.5px",
            fontWeight: "500",
            display: "flex",
            alignItems: "center",
            gap: "8px"
          }}>
            <span>⚠️</span>
            <span>{erro}</span>
          </div>
        )}

        <div className="modal-form">
          <label className="form-label">
            Área Comum
            <select
              className="form-input"
              value={form.areaComumId}
              onChange={(e) =>
                setForm({
                  ...form,
                  areaComumId: Number(e.target.value),
                })
              }
              disabled={!!reservaEmEdicao}
            >
              <option value={1}>Churrasqueira 1</option>
              <option value={2}>Churrasqueira 2</option>
              <option value={3}>Salão de Festas</option>
              <option value={4}>Piscina (Espaço Gourmet)</option>
            </select>
          </label>

          <label className="form-label">
            Data
            <input
              type="date"
              className="form-input"
              value={form.data}
              onChange={(e) =>
                setForm({
                  ...form,
                  data: e.target.value,
                })
              }
              required
            />
          </label>

          <div className="form-row">
            <label className="form-label">
              Hora de Início
              <input
                type="time"
                className="form-input"
                value={form.horaInicio}
                onChange={(e) =>
                  setForm({
                    ...form,
                    horaInicio: e.target.value,
                  })
                }
                required
              />
            </label>

            <label className="form-label">
              Hora de Fim
              <input
                type="time"
                className="form-input"
                value={form.horaFim}
                onChange={(e) =>
                  setForm({
                    ...form,
                    horaFim: e.target.value,
                  })
                }
                required
              />
            </label>
          </div>

          <div className="modal-acoes">
            <button className="btn btn-outline" onClick={onClose}>
              Cancelar
            </button>
            <button className="btn btn-primary" onClick={() => onSalvar(form)}>
              Confirmar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}