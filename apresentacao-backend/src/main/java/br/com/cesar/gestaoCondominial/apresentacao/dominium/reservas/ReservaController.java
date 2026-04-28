package br.com.cesar.gestaoCondominial.apresentacao.dominium.reservas;

import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.AdicionarNaFilaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.AtualizarReservaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.CancelarReservaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.ConfirmarReservaPromovidaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.CriarReservaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.ListarReservaUseCase;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.CriarReservaRequest;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.ReservaResponse;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import org.springframework.http.ResponseEntity;
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
    private final AdicionarNaFilaUseCase adicionarNaFilaUseCase;
    private final ConfirmarReservaPromovidaUseCase confirmarReservaPromovidaUseCase;
    private final ExceptionHandler exceptionHandler;

    public ReservaController(
            AtualizarReservaUseCase atualizarReservaUseCase,
            CancelarReservaUseCase cancelarReservaUseCase,
            CriarReservaUseCase criarReservaUseCase,
            ListarReservaUseCase listarReservaUseCase,
            AdicionarNaFilaUseCase adicionarNaFilaUseCase,
            ConfirmarReservaPromovidaUseCase confirmarReservaPromovidaUseCase,
            ExceptionHandler exceptionHandler) {
        this.atualizarReservaUseCase = atualizarReservaUseCase;
        this.cancelarReservaUseCase = cancelarReservaUseCase;
        this.criarReservaUseCase = criarReservaUseCase;
        this.listarReservaUseCase = listarReservaUseCase;
        this.adicionarNaFilaUseCase = adicionarNaFilaUseCase;
        this.confirmarReservaPromovidaUseCase = confirmarReservaPromovidaUseCase;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(@RequestBody CriarReservaRequest request) {
        return exceptionHandler.withHandler(() -> {
            Reserva reserva = Reserva.criar(
                    ReservaId.novo(),
                    new AreaComumId(request.areaComumId()),
                    new UnidadeId(request.unidadeId()),
                    new UsuarioId(request.usuarioId()),
                    request.data(),
                    request.horaInicio(),
                    request.horaFim());

            Reserva salva = criarReservaUseCase.executar(reserva);
            return ResponseEntity.ok(ReservaResponse.from(salva));
        });
    }

    @PostMapping("/fila")
    public ResponseEntity<FilaDeEspera> entrarNaFila(@RequestBody CriarReservaRequest request) {
        return exceptionHandler.withHandler(() -> {
            FilaDeEspera fila = adicionarNaFilaUseCase.executar(
                    new AreaComumId(request.areaComumId()),
                    request.usuarioId(),
                    request.data(),
                    request.horaInicio(),
                    request.horaFim());
            return ResponseEntity.ok(fila);
        });
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable String id) {
        return exceptionHandler.withHandler(() -> {
            confirmarReservaPromovidaUseCase.executar(ReservaId.de(id));
            return ResponseEntity.noContent().build();
        });
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {
        return exceptionHandler.withHandler(() -> {
            cancelarReservaUseCase.executar(ReservaId.de(id));
            return ResponseEntity.noContent().build();
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> atualizar(
            @PathVariable String id,
            @RequestParam LocalDate data,
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFim) {
        return exceptionHandler.withHandler(() -> {
            Reserva atualizada = atualizarReservaUseCase.executar(
                    ReservaId.de(id),
                    data,
                    horaInicio,
                    horaFim);
            return ResponseEntity.ok(ReservaResponse.from(atualizada));
        });
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return exceptionHandler.withHandler(() -> {
            List<Reserva> reservas = listarReservaUseCase.listarPorUsuario(new UsuarioId(usuarioId));
            List<ReservaResponse> response = reservas.stream()
                    .map(ReservaResponse::from)
                    .toList();
            return ResponseEntity.ok(response);
        });
    }
}
