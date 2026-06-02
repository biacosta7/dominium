export const buscarReservas = async (usuarioId: number) => {
  const res = await fetch(`/reservas/usuario/${usuarioId}`);
  if (!res.ok) throw new Error("Erro ao buscar reservas");
  const data = await res.json();
  return { data };
};

export const cancelarReserva = async (id: string) => {
  const res = await fetch(`/reservas/${id}/cancelar`, {
    method: "PUT"
  });
  if (!res.ok) throw new Error("Erro ao cancelar reserva");
  return { status: res.status };
};

export const criarReserva = async (dados: any) => {
  const res = await fetch("/reservas", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(dados)
  });
  if (!res.ok) throw new Error("Erro ao criar reserva");
  const data = await res.json();
  return { data };
};

export const atualizarReserva = async (id: string, data: string, horaInicio: string, horaFim: string) => {
  const params = new URLSearchParams({
    data,
    horaInicio: horaInicio.includes(":") && horaInicio.split(":").length === 2 ? `${horaInicio}:00` : horaInicio,
    horaFim: horaFim.includes(":") && horaFim.split(":").length === 2 ? `${horaFim}:00` : horaFim
  });
  const res = await fetch(`/reservas/${id}?${params.toString()}`, {
    method: "PUT"
  });
  if (!res.ok) throw new Error("Erro ao atualizar reserva");
  const result = await res.json();
  return { data: result };
};