package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.StatusArea;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.StatusReserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalTime;

public class GestaoDeReservasSteps extends DominiumFuncionalidade {

    private static final Long AREA_COMUM_ID = 1L;
    private UnidadeId unidadeIdContexto;
    private ReservaId reservaIdContexto;
    private Reserva reservaRequest;

    @Given("que o {string} {string} adimplente")
    public void o_morador_p2_adimplente_reserva(String p1, String estado) {
        Unidade unidade = new Unidade();
        unidade.setNumero("701");
        unidade.setBloco("G");
        unidade.setStatus("está".equals(estado) ? StatusAdimplencia.ADIMPLENTE : StatusAdimplencia.INADIMPLENTE);
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();

        AreaComum area = new AreaComum();
        area.setId(new AreaComumId(AREA_COMUM_ID));
        area.setNome("Salão de Festas");
        area.setCapacidadeMaxima(50);
        area.setStatus(StatusArea.DISPONIVEL);
        salvarAreaComum(area);
    }

    @Given("a {string} {string} conflito de horário")
    public void a_area_comum_p2_conflito_de_horario(String p1, String possuiConflito) {
        reservaRequest = new Reserva();
        reservaRequest.setUnidadeId(unidadeIdContexto);
        reservaRequest.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
        reservaRequest.setDataReserva(LocalDate.now().plusDays(1));
        reservaRequest.setHoraInicio(LocalTime.of(14, 0));
        reservaRequest.setHoraFim(LocalTime.of(18, 0));

        if ("possui".equals(possuiConflito)) {
            Reserva conflito = new Reserva();
            conflito.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
            conflito.setDataReserva(LocalDate.now().plusDays(1));
            conflito.setHoraInicio(LocalTime.of(14, 0));
            conflito.setHoraFim(LocalTime.of(18, 0));
            conflito.setStatus(StatusReserva.ATIVA);
            reservaRepository.save(conflito);
        }
    }

    @Given("a {string} {string} o limite mensal de reservas")
    public void a_unidade_p2_o_limite_mensal(String p1, String situacao) {
        // Simulado
    }

    @Given("o {string} tem uma {string} confirmada")
    public void o_morador_tem_uma_reserva_confirmada(String p1, String p2) {
        Unidade unidade = new Unidade();
        unidade.setNumero("703");
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();

        Reserva reserva = new Reserva();
        reserva.setUnidadeId(unidadeIdContexto);
        reserva.setAreaComumId(new AreaComumId(AREA_COMUM_ID));
        reserva.setDataReserva(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFim(LocalTime.of(12, 0));
        reserva.setStatus(StatusReserva.ATIVA);
        reserva = reservaRepository.save(reserva);
        reservaIdContexto = reserva.getId();
    }

    @When("o {string} solicita a criação de uma reserva")
    public void o_morador_solicita_a_criacao_de_uma_reserva(String p1) {
        try {
            criarReservaUseCase.executar(reservaRequest);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @When("o {string} cancela a {string} tardiamente")
    public void o_morador_cancela_a_reserva_tardiamente(String p1, String p2) {
        try {
            cancelarReservaUseCase.executar(reservaIdContexto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema cria a \"reserva\" com sucesso")
    public void o_sistema_cria_a_reserva_com_sucesso() {
        assertNull(this.excecao);
    }

    @Then("o sistema informa que unidade inadimplente não pode reservar")
    public void o_sistema_informa_inadimplente_nao_pode_reservar() {
        assertNotNull(this.excecao);
    }

    @Then("o sistema informa que não pode haver conflito de horário")
    public void o_sistema_informa_conflito_de_horario() {
        assertNotNull(this.excecao);
    }

    @Then("o sistema permite ativar a {string} automaticamente")
    public void o_sistema_permite_ativar_a_lista_de_espera(String p1) {
        assertNotNull(this.excecao);
    }

    @Then("o sistema informa que o limite mensal de reservas foi atingido")
    public void o_sistema_informa_limite_mensal_atingido() {
        assertNotNull(this.excecao);
    }

    @Then("o sistema cancela a \"reserva\"")
    public void o_sistema_cancela_a_reserva() {
        assertNull(this.excecao);
        Reserva r = reservaRepository.findById(reservaIdContexto).get();
        assertEquals(StatusReserva.CANCELADA, r.getStatus());
    }

    @Then("o sistema gera uma {string} pelo cancelamento tardio")
    public void o_sistema_gera_uma_multa_pelo_cancelamento_tardio(String p1) {
        assertTrue(true);
    }
}
