package com.dominium.backend.application.reservas.usecase;

import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;
import com.dominium.backend.domain.usuario.UsuarioId;
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
