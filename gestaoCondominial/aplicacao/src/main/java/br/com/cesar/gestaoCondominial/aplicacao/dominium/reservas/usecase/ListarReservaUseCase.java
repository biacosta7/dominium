package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.UsuarioId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarReservaUseCase {
    private final ReservaRepository repository;

    public ListarReservaUseCase(ReservaRepository repository) {
        this.repository = repository;
    }

    public List<Reserva> listarPorUsuario(UsuarioId usuarioId) {
        return repository.buscarPorUsuario(usuarioId);
    }
}
