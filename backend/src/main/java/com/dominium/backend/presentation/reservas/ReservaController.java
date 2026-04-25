package com.dominium.backend.presentation.reservas;

import com.dominium.backend.application.reservas.dto.CriarReservaRequest;
import com.dominium.backend.application.reservas.dto.ReservaResponse;
import com.dominium.backend.application.reservas.usecase.ListarReservaUseCase;

import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.reservas.*;
import com.dominium.backend.domain.usuario.UsuarioId;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.application.reservas.usecase.AtualizarReservaUseCase;
import com.dominium.backend.application.reservas.usecase.CancelarReservaUseCase;
import com.dominium.backend.application.reservas.usecase.CriarReservaUseCase;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final AtualizarReservaUseCase atualizarReservaUseCase;
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final CriarReservaUseCase criarReservaUseCase;
    private final ListarReservaUseCase listarReservaUseCase;

    public ReservaController(
            AtualizarReservaUseCase atualizarReservaUseCase,
            CancelarReservaUseCase cancelarReservaUseCase,
            CriarReservaUseCase criarReservaUseCase,
            ListarReservaUseCase listarReservaUseCase
    ) {
        this.atualizarReservaUseCase = atualizarReservaUseCase;
        this.cancelarReservaUseCase = cancelarReservaUseCase;
        this.criarReservaUseCase = criarReservaUseCase;
        this.listarReservaUseCase = listarReservaUseCase;
    }

    // 🔥 Criar reserva
    @PostMapping
    public ReservaResponse criar(@RequestBody CriarReservaRequest request) {

        Reserva reserva = Reserva.criar(
                ReservaId.novo(),
                new AreaComumId(request.areaComumId()),
                new UnidadeId(request.unidadeId()),
                new UsuarioId(request.usuarioId()),
                request.data(),
                request.horaInicio(),
                request.horaFim()
        );

        Reserva salva = criarReservaUseCase.executar(reserva);

        return ReservaResponse.from(salva);
    }

    @PutMapping("/{id}/cancelar")
    public void cancelar(@PathVariable String id) {
        cancelarReservaUseCase.executar(ReservaId.de(id));
    }

    @PutMapping("/{id}")
    public ReservaResponse atualizar(
            @PathVariable String id,
            @RequestParam LocalDate data,
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFim
    ) {

        Reserva atualizada = atualizarReservaUseCase.executar(
                ReservaId.de(id),
                data,
                horaInicio,
                horaFim
        );

        return ReservaResponse.from(atualizada);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ReservaResponse> listarPorUsuario(@PathVariable Long usuarioId) {

        List<Reserva> reservas =
                listarReservaUseCase.listarPorUsuario(new UsuarioId(usuarioId));

        return reservas.stream()
                .map(ReservaResponse::from)
                .toList();
    }
}
