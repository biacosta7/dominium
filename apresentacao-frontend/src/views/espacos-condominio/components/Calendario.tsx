import type { DiaCalendario } from "../types/reserva";

interface Props {
  semanas: DiaCalendario[][];
  onSelecionar: (dia: DiaCalendario) => void;
}

export default function Calendario({ semanas, onSelecionar }: Props) {
  return (
    <div className="cal-grid">
      {semanas.flat().map((dia, idx) => {
        const classes = ["dia-cell"];
        
        if (!dia.mesAtual) {
          classes.push("outro-mes");
        } else {
          classes.push("clicavel");
          if (dia.ehHoje) {
            classes.push("hoje");
          } else if (dia.reservas.length > 0) {
            classes.push("tem-reserva");
          }
        }

        return (
          <div
            key={`${dia.dia}-${dia.mesAtual}-${idx}`}
            className={classes.join(" ")}
            onClick={() => dia.mesAtual && onSelecionar(dia)}
          >
            <span className="dia-numero">{dia.dia}</span>
            {dia.mesAtual && dia.reservas.length > 0 && (
              <span className="dia-dot" />
            )}
          </div>
        );
      })}
    </div>
  );
}