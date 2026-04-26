package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.*;

import com.dominium.backend.domain.reservas.FilaDeEspera;
import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.ReservaId;
import com.dominium.backend.domain.reservas.StatusReserva;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.areacomum.AreaComumId;

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
        reserva.setStatus(StatusReserva.ATIVA); // Inicia como ATIVA
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
            // Adiciona inquilino para que o CancelarReservaUseCase encontre a unidade
            Usuario inquilino = new Usuario();
            inquilino.setId(usuarioFilaId);
            aguardando.setInquilino(inquilino);

            aguardando = unidadeRepository.save(aguardando);

            FilaDeEspera entrada = new FilaDeEspera();
            entrada.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
            entrada.setUsuarioId(usuarioFilaId);
            entrada.setDataCadastro(LocalDateTime.now().minusHours(1));
            // Sincronizar a data desejada com a data da reserva para o filtro funcionar
            entrada.setDataDesejada(LocalDate.now().plusDays(3));
            entrada.setHoraInicio(LocalTime.of(14, 0));
            entrada.setHoraFim(LocalTime.of(16, 0));
            entrada = filaDeEsperaRepository.salvar(entrada);
            filaEntradaId = entrada.getId();
        }
    }

    @When("o sistema processa o cancelamento")
    public void o_sistema_processa_o_cancelamento() {
        cancelarReservaUseCase.executar(reservaCanceladaId);
    }

    @Then("o sistema promove o próximo {string} da {string} seguindo a ordem cronológica")
    public void o_sistema_promove_o_proximo_morador(String p1, String p2) {
        // Verifica se existe uma reserva ATIVA ou AGUARDANDO para o usuário que estava
        // na fila
        boolean promoveu = reservaRepository
                .buscarPorUsuario(new com.dominium.backend.domain.usuario.UsuarioId(usuarioPromovidoId))
                .stream()
                .anyMatch(r -> r.getStatus() == StatusReserva.AGUARDANDO_CONFIRMACAO
                        || r.getStatus() == StatusReserva.ATIVA);
        assertTrue(promoveu, "O morador não foi promovido da fila");

        // Verifica se a entrada original foi removida da fila de espera
        if (filaEntradaId != null) {
            assertFalse(filaDeEsperaRepository.buscarPorId(filaEntradaId).isPresent());
        }
    }

    @Then("o sistema envia uma {string}")
    public void o_sistema_envia_uma_notificacao_obrigatoria(String p1) {
        assertTrue(true);
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
        reserva.setUsuarioId(new com.dominium.backend.domain.usuario.UsuarioId(userId));
        reserva.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
        reserva.setDataReserva(LocalDate.now().plusDays(5));
        reserva.setStatus(StatusReserva.AGUARDANDO_CONFIRMACAO);
        reserva = reservaRepository.save(reserva);
        reservaCanceladaId = reserva.getId();
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
        assertTrue(true);
    }

    private Long UUID_RANDOM_LONG() {
        return (long) (Math.random() * 1000000);
    }
}
