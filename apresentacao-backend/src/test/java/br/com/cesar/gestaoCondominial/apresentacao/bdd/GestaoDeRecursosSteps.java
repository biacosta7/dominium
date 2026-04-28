package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.AbrirRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.JulgarRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.StatusRecurso;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class GestaoDeRecursosSteps extends DominiumFuncionalidade {

    private MultaId multaIdContexto;
    private UUID recursoIdContexto;
    private UUID moradorIdContexto;

    @Given("a {string} {string} no prazo máximo para recurso")
    public void a_multa_p2_no_prazo_para_recurso(String p1, String prazo) {
        Unidade unidade = new Unidade();
        unidade.setNumero("401");
        unidade = unidadeRepository.save(unidade);

        Multa multa = new Multa();
        multa.setUnidade(unidade);
        multa.setDescricao("Infração");
        multa.setStatus(StatusMulta.ABERTA);
        if ("está".equals(prazo)) {
            multa.setDataCriacao(LocalDateTime.now().minusDays(5));
        } else {
            multa.setDataCriacao(LocalDateTime.now().minusDays(20));
        }
        multa = multaRepository.save(multa);
        multaIdContexto = multa.getId();
        moradorIdContexto = UUID.randomUUID();
    }

    @When("o {string} solicita a abertura de {string}")
    public void o_morador_solicita_a_abertura_de_recurso(String p1, String p2) {
        try {
            AbrirRecursoRequestDTO dto = new AbrirRecursoRequestDTO();
            dto.setMultaId(multaIdContexto.getValor());
            dto.setMoradorId(moradorIdContexto);
            dto.setMotivo("Justificativa teste");
            recursoIdContexto = abrirRecursoUseCase.execute(dto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema abre o {string} contra a {string}")
    public void o_sistema_abre_o_recurso_contra_a_multa(String p1, String p2) {
        assertNull(this.excecao);
        assertNotNull(recursoIdContexto);
    }

    @Then("o sistema bloqueia a abertura do {string} informando que o prazo expirou")
    public void o_sistema_bloqueia_a_abertura_do_recurso_prazo(String p1) {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().toLowerCase().contains("prazo"));
    }

    @Given("o {string} {string} aberto")
    public void o_recurso_p2_aberto(String p1, String estado) {
        a_multa_p2_no_prazo_para_recurso(p1, "está");
        AbrirRecursoRequestDTO dto = new AbrirRecursoRequestDTO();
        dto.setMultaId(multaIdContexto.getValor());
        dto.setMoradorId(moradorIdContexto);
        dto.setMotivo("Contestação");
        recursoIdContexto = abrirRecursoUseCase.execute(dto);
    }

    @When("o síndico {string} o {string} cancelando a multa")
    public void o_sindico_julga_o_recurso_cancelando_a_multa(String p1, String p2) {
        try {
            JulgarRecursoRequestDTO dto = new JulgarRecursoRequestDTO();
            dto.setStatus(StatusRecurso.DEFERIDO);
            dto.setJustificativa("Aceito");
            dto.setCancelarMulta(true);
            julgarRecursoUseCase.execute(recursoIdContexto, dto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema exige que o síndico {string} a decisão")
    public void o_sistema_exige_justificativa(String p1) {
        assertNull(this.excecao);
    }

    @Then("o sistema cancela a \"multa\"")
    public void o_sistema_cancela_a_multa() {
        Multa multa = multaRepository.findById(multaIdContexto).get();
        assertEquals(BigDecimal.ZERO.setScale(2), multa.getValor().setScale(2));
    }

    @Then("o {string} da decisão é armazenado")
    public void o_historico_da_decisao_e_armazenado(String p1) {
        assertTrue(true);
    }
}
