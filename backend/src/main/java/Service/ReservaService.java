package Service;

import Entity.Reserva;
import Repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repository;

    public Reserva criarReserva(Reserva reserva) {

        // 🔒 Regra: não pode ter conflito de horário
        List<Reserva> conflitos = repository.verificarConflito(
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim()
        );

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Já existe uma reserva nesse horário.");
        }

        reserva.setStatus("ATIVA");
        return repository.save(reserva);
    }

    public List<Reserva> listarPorUnidade(Long unidadeId) {
        return repository.findByUnidadeId(unidadeId);
    }

    public Reserva atualizarReserva(Long id, Reserva novaReserva) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.setDataReserva(novaReserva.getDataReserva());
        reserva.setHoraInicio(novaReserva.getHoraInicio());
        reserva.setHoraFim(novaReserva.getHoraFim());

        return repository.save(reserva);
    }

    public void cancelarReserva(Long id) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.setStatus("CANCELADA");
        repository.save(reserva);
    }
}
