package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.StatusArea;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.StatusReserva;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

        if (!"está".equals(estado)) {
            br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial taxa = new br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial(
                    null,
                    unidadeIdContexto,
                    new java.math.BigDecimal("100.00"),
                    java.math.BigDecimal.ZERO,
                    new java.math.BigDecimal("100.00"),
                    java.time.LocalDate.now().minusDays(5),
                    null,
                    br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.StatusTaxa.ATRASADA);
            taxaCondominialRepository.salvar(taxa);
        }

        AreaComum area = new AreaComum();
        area.setId(new AreaComumId(AREA_COMUM_ID));
        area.setNome("Salão de Festas");
        area.setCapacidadeMaxima(50);
        area.setStatus(StatusArea.DISPONIVEL);
        salvarAreaComum(area);

        reservaRequest = Reserva.criar(
                ReservaId.novo(),
                area.getId(),
                unidadeIdContexto,
                new br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId(1L),
                LocalDate.now(),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0));
    }

    @Given("a {string} {string} conflito de horário")
    public void a_area_comum_p2_conflito_de_horario(String p1, String possuiConflito) {
        if (reservaRequest == null) {
            reservaRequest = new Reserva();
            reservaRequest.setUnidadeId(unidadeIdContexto);
            reservaRequest.setUsuarioId(new br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId(1L));
        }

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
        if ("atingiu".equals(situacao)) {
            for (int i = 0; i < 2; i++) {
                Reserva r = new Reserva();
                r.setUsuarioId(new br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId(1L));
                r.setUnidadeId(unidadeIdContexto);
                r.setAreaComumId(new br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId(
                        AREA_COMUM_ID));
                r.setDataReserva(LocalDate.now());
                r.setHoraInicio(LocalTime.of(8 + i, 0));
                r.setHoraFim(LocalTime.of(9 + i, 0));
                r.setStatus(StatusReserva.ATIVA);
                reservaRepository.save(r);
            }
        }
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
        assertNull(this.excecao, "A criação da reserva não deveria ter falhado.");
        List<Reserva> reservas = reservaRepository.buscarPorUsuario(reservaRequest.getUsuarioId());

        boolean existeReserva = reservas.stream()
                .anyMatch(r -> r.getUnidadeId().equals(reservaRequest.getUnidadeId())
                        && r.getStatus() == StatusReserva.ATIVA);

        assertTrue(existeReserva, "A reserva deveria ter sido registrada no repositório com status ATIVA.");
    }

    @Then("o sistema informa que unidade inadimplente não pode reservar")
    public void o_sistema_informa_inadimplente_nao_pode_reservar() {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a reserva para unidade inadimplente.");
        String msg = this.excecao.getMessage().toLowerCase();
        assertTrue(msg.contains("inadimplente") || msg.contains("débitos") || msg.contains("atraso"),
                "A mensagem de erro deve informar sobre a restrição de inadimplência. Mensagem atual: " + msg);
    }

    @Then("o sistema informa que não pode haver conflito de horário")
    public void o_sistema_informa_conflito_de_horario() {
        assertNotNull(this.excecao, "O sistema deveria ter detectado o conflito de horário.");
        String msg = this.excecao.getMessage().toLowerCase();
        assertTrue(msg.contains("conflito") || msg.contains("horário") || msg.contains("indisponível"),
                "A mensagem de erro deve informar sobre o conflito de horários.");
    }

    @Then("o sistema permite ativar a {string} automaticamente")
    public void o_sistema_permite_ativar_a_lista_de_espera(String p1) {
        assertNotNull(this.excecao);
    }

    @Then("o sistema informa que o limite mensal de reservas foi atingido")
    public void o_sistema_informa_limite_mensal_atingido() {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a reserva por excesso de limite.");
        String msg = this.excecao.getMessage().toLowerCase();
        assertTrue(msg.contains("limite") || msg.contains("máximo") || msg.contains("atingido"),
                "A mensagem deve informar que o limite de reservas foi atingido. Mensagem atual: " + msg);
    }

    @Then("o sistema cancela a \"reserva\"")
    public void o_sistema_cancela_a_reserva() {
        assertNull(this.excecao);
        Reserva r = reservaRepository.findById(reservaIdContexto).get();
        assertEquals(StatusReserva.CANCELADA, r.getStatus());
    }

    @Then("o sistema gera uma {string} pelo cancelamento tardio")
    public void o_sistema_gera_uma_multa_pelo_cancelamento_tardio(String p1) {
        boolean multaGerada = multaRepository.findByUnidadeId(unidadeIdContexto).stream()
                .anyMatch(m -> m.getUnidade().getId().equals(unidadeIdContexto));

        assertTrue(multaGerada, "Uma multa deveria ter sido gerada para a unidade devido ao cancelamento tardio.");
    }
}
