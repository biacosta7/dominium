package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.StatusReserva;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GestaoDeListaDeEsperaSteps extends DominiumFuncionalidade {

    private static final Long AREA_COMUM_ID = 1L;

    private ReservaId reservaCanceladaId;
    private Long usuarioPromovidoId;
    private String filaEntradaId;

    @Given("existe uma {string} cancelada")
    public void existe_uma_reserva_cancelada(String p1) {
        Unidade titular = new Unidade();
        titular.setNumero("801");
        titular = unidadeRepository.save(titular);

        Reserva reserva = new Reserva();
        reserva.setUnidadeId(titular.getId());
        reserva.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
        reserva.setDataReserva(LocalDate.now().plusDays(3));
        reserva.setHoraInicio(LocalTime.of(14, 0));
        reserva.setHoraFim(LocalTime.of(16, 0));
        reserva.setStatus(StatusReserva.ATIVA);
        reserva = reservaRepository.save(reserva);
        reservaCanceladaId = reserva.getId();
    }

    @Given("a {string} {string} moradores aguardando")
    public void a_fila_p2_moradores_aguardando(String p1, String possui) {
        if ("possui".equals(possui) || "possuiConflito".equals(possui)) {
            Long usuarioFilaId = UUID_RANDOM_LONG();
            usuarioPromovidoId = usuarioFilaId;

            Unidade aguardando = new Unidade();
            aguardando.setNumero("802");
            Usuario inquilino = new Usuario();
            inquilino.setId(usuarioFilaId);
            aguardando.setInquilino(inquilino);

            aguardando = unidadeRepository.save(aguardando);

            FilaDeEspera entrada = new FilaDeEspera();
            entrada.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
            entrada.setUsuarioId(new UsuarioId(usuarioFilaId));
            entrada.setDataCadastro(LocalDateTime.now().minusHours(1));
            entrada.setDataDesejada(LocalDate.now().plusDays(3));
            entrada.setHoraInicio(LocalTime.of(14, 0));
            entrada.setHoraFim(LocalTime.of(16, 0));
            entrada.setStatus(FilaDeEspera.StatusFila.AGUARDANDO);
            entrada = filaDeEsperaRepository.salvar(entrada);
            filaEntradaId = entrada.getId().getValor();
        }
    }

    @When("o sistema processa o cancelamento")
    public void o_sistema_processa_o_cancelamento() {
        cancelarReservaUseCase.executar(reservaCanceladaId);
    }

    @Then("o sistema promove o próximo {string} da {string} seguindo a ordem cronológica")
    public void o_sistema_promove_o_proximo_morador(String p1, String p2) {
        boolean promoveu = reservaRepository
                .buscarPorUsuario(new UsuarioId(usuarioPromovidoId))
                .stream()
                .anyMatch(r -> r.getStatus() == StatusReserva.AGUARDANDO_CONFIRMACAO
                        || r.getStatus() == StatusReserva.ATIVA);
        assertTrue(promoveu, "O morador não foi promovido da fila");

        if (filaEntradaId != null) {
            assertFalse(filaDeEsperaRepository.buscarPorId(filaEntradaId)
                    .filter(f -> f.getStatus() == FilaDeEspera.StatusFila.AGUARDANDO)
                    .isPresent());
        }
    }

    @Then("o sistema envia uma {string}")
    public void o_sistema_envia_uma_notificacao_obrigatoria(String p1) {
        boolean notificacaoEnviada = listarTodasNotificacoes().stream()
                .anyMatch(n -> n.getUsuarioId().equals(usuarioPromovidoId)
                        && n.getMensagem().toLowerCase().contains("promovid"));

        assertTrue(notificacaoEnviada, "O morador promovido deveria ter recebido uma notificação.");
    }

    @Given("o {string} foi promovido da {string}")
    public void o_morador_foi_promovido_da_fila(String p1, String p2) {
        Long userId = UUID_RANDOM_LONG();
        usuarioPromovidoId = userId;

        Unidade unidade = new Unidade();
        unidade.setNumero("803");
        Usuario inquilino = new Usuario();
        inquilino.setId(userId);
        unidade.setInquilino(inquilino);
        unidade = unidadeRepository.save(unidade);

        Reserva reserva = new Reserva();
        reserva.setUnidadeId(unidade.getId());
        reserva.setUsuarioId(new UsuarioId(userId));
        reserva.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
        reserva.setDataReserva(LocalDate.now().plusDays(5));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFim(LocalTime.of(12, 0));
        reserva.setStatus(StatusReserva.AGUARDANDO_CONFIRMACAO);
        reserva = reservaRepository.save(reserva);
        reservaCanceladaId = reserva.getId();

        Long proximoFilaId = UUID_RANDOM_LONG();
        usuarioPromovidoId = proximoFilaId;

        Unidade unidadeFila = new Unidade();
        unidadeFila.setNumero("804");
        Usuario inquilinoFila = new Usuario();
        inquilinoFila.setId(proximoFilaId);
        unidadeFila.setInquilino(inquilinoFila);
        unidadeRepository.save(unidadeFila);

        FilaDeEspera entrada = new FilaDeEspera();
        entrada.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
        entrada.setUsuarioId(new UsuarioId(proximoFilaId));
        entrada.setDataCadastro(LocalDateTime.now().plusSeconds(1));
        entrada.setDataDesejada(LocalDate.now().plus(5, java.time.temporal.ChronoUnit.DAYS));
        entrada.setHoraInicio(LocalTime.of(10, 0));
        entrada.setHoraFim(LocalTime.of(12, 0));
        entrada.setStatus(FilaDeEspera.StatusFila.AGUARDANDO);
        filaDeEsperaRepository.salvar(entrada);
    }

    @When("o {string} expirar sem resposta")
    public void o_prazo_expirar_sem_resposta(String p1) {
        // Simula o sistema cancelando a reserva por falta de confirmação
        cancelarReservaUseCase.executar(reservaCanceladaId);
    }

    @Then("o sistema cancela a pré-reserva")
    public void o_sistema_cancela_a_pre_reserva() {
        Reserva r = reservaRepository.findById(reservaCanceladaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        assertEquals(StatusReserva.CANCELADA, r.getStatus());
    }

    @Then("o sistema promove o próximo {string} da {string}")
    public void o_sistema_promove_o_proximo_da_fila(String p1, String p2) {
        Reserva reservaAnterior = reservaRepository.findById(reservaCanceladaId)
                .orElseThrow(() -> new AssertionError("Reserva original não encontrada"));
        assertEquals(StatusReserva.CANCELADA, reservaAnterior.getStatus(), "A reserva expirada deve estar cancelada.");

        boolean novaReservaCriada = reservaRepository.buscarPorUsuario(new UsuarioId(usuarioPromovidoId))
                .stream()
                .anyMatch(r -> r.getAreaComumId().equals(new AreaComumId(AREA_COMUM_ID))
                        && (r.getStatus() == StatusReserva.AGUARDANDO_CONFIRMACAO
                                || r.getStatus() == StatusReserva.ATIVA));

        assertTrue(novaReservaCriada, "O próximo morador da fila deveria ter sido promovido com uma nova reserva.");
        assertNull(this.excecao, "Não deve haver erro no processo de promoção em cadeia.");
    }

    private Long UUID_RANDOM_LONG() {
        return (long) (Math.random() * 1000000);
    }
}
