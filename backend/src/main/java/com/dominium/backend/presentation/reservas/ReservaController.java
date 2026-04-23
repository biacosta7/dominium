package com.dominium.backend.presentation.reservas;

import com.dominium.backend.application.reservas.dto.CriarReservaRequest;
import com.dominium.backend.application.reservas.dto.ReservaResponse;
import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.areacomum.StatusArea;
import com.dominium.backend.domain.reservas.*;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.UsuarioId;
import com.dominium.backend.application.reservas.usecase.AtualizarReservaUseCase;
import com.dominium.backend.application.reservas.usecase.CancelarReservaUseCase;
import com.dominium.backend.application.reservas.usecase.CriarReservaUseCase;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final AtualizarReservaUseCase atualizarReservaUseCase;
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final CriarReservaUseCase criarReservaUseCase;

    public ReservaController(AtualizarReservaUseCase atualizarReservaUseCase, CancelarReservaUseCase cancelarReservaUseCase,
                             CriarReservaUseCase criarReservaUseCase) {
        this.atualizarReservaUseCase = atualizarReservaUseCase;
        this.cancelarReservaUseCase = cancelarReservaUseCase;
        this.criarReservaUseCase = criarReservaUseCase;
    }

    // 🔥 Criar reserva
    @PostMapping
    public ReservaResponse criar(@RequestBody CriarReservaRequest request) {

        Reserva reserva = Reserva.criar(
                ReservaId.novo(),
                new AreaComumId(request.areaComumId()),
                new Unidade(request.unidadeId()),
                new Usuario(request.usuarioId()),
                request.data(),
                request.horaInicio(),
                request.horaFim()
        );

        Reserva salva = criarReservaUseCase.executar(reserva);

        return ReservaResponse.from(salva);
    }

    // 🔥 Cancelar reserva
    @PutMapping("/{id}/cancelar")
    public void cancelar(@PathVariable String id) {
        cancelarReservaUseCase.executar(ReservaId.de(id));
    }
}
