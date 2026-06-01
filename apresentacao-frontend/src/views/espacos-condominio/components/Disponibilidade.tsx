import type { DisponibilidadeArea } from "../types/reserva";

interface Props {
  itens: DisponibilidadeArea[];
  onReservar: (id: number) => void;
}

export default function Disponibilidade({ itens, onReservar }: Props) {
  return (
    <div className="espacos-list">
      {itens.map((item) => {
        const itemClass = item.ocupada ? "espaco-item res-indisponivel" : "espaco-item";
        
        return (
          <div
            key={item.area.id}
            className={itemClass}
            onClick={() => !item.ocupada && onReservar(item.area.id)}
          >
            <span className="espaco-icon">{item.area.icone}</span>
            
            <div className="espaco-texto">
              <div className="espaco-name">{item.area.nome}</div>
              <div className={`espaco-cap ${item.ocupada ? "ocupada" : ""}`}>
                Horário: {item.area.horarioLivre} · {item.detalhe}
              </div>
            </div>

            <span className={`badge ${item.badgeClass}`}>
              {item.badgeLabel}
            </span>
          </div>
        );
      })}
    </div>
  );
}