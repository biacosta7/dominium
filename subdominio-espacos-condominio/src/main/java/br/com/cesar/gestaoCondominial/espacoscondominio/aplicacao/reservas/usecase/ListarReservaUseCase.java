package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
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
