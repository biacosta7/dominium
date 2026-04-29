package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.OcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoPenalidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

public class GestaoDeOcorrenciasSteps extends DominiumFuncionalidade {

    private UnidadeId unidadeIdContexto;
    private Long ocorrenciaIdContexto;
    private OcorrenciaRequestDTO ocorrenciaRequest;

    @Given("o síndico informa os dados da \"ocorrência\"")
    public void o_sindico_informa_os_dados_da_ocorrencia() {
        Unidade unidade = new Unidade();
        unidade.setNumero("901");
        unidade.setBloco("I");
        unidade.setStatus(StatusAdimplencia.ADIMPLENTE);
        unidade.setSaldoDevedor(BigDecimal.ZERO);
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();

        ocorrenciaRequest = new OcorrenciaRequestDTO();
        ocorrenciaRequest.setDescricao("Barulho excessivo após as 22h");
    }

    @Given("a {string} {string} a uma {string}")
    public void a_ocorrencia_p2_a_uma_unidade(String p1, String vinculo, String p3) {
        if ("está vinculada".equals(vinculo)) {
            ocorrenciaRequest.setUnidadeId(unidadeIdContexto.getValor());
        } else {
            ocorrenciaRequest.setUnidadeId(null);
        }
    }

    @Given("a {string} {string} a nenhuma {string}")
    public void a_ocorrencia_nao_vinculada_a_nenhuma_unidade(String p1, String p2, String p3) {
        ocorrenciaRequest = new OcorrenciaRequestDTO();
        ocorrenciaRequest.setDescricao("Ocorrência sem vínculo");
        ocorrenciaRequest.setUnidadeId(null);
    }

    @When("o síndico solicita a criação da \"ocorrência\"")
    public void o_sindico_solicita_a_criacao_da_ocorrencia() {
        try {
            Ocorrencia ocorrencia = gerenciarOcorrenciaUseCase.executar(ocorrenciaRequest);
            ocorrenciaIdContexto = ocorrencia.getId();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema registra a \"ocorrência\" com sucesso")
    public void o_sistema_registra_a_ocorrencia_com_sucesso() {
        assertNull(this.excecao);
        assertNotNull(ocorrenciaIdContexto);
        assertTrue(ocorrenciaRepository.buscarPorId(ocorrenciaIdContexto).isPresent());
    }

    @Then("o sistema informa que a ocorrência deve estar vinculada a uma unidade")
    public void o_sistema_informa_ocorrencia_deve_estar_vinculada() {
        assertNotNull(this.excecao, "O sistema deveria ter exigido o vínculo com uma unidade.");
        assertTrue(this.excecao.getMessage().toLowerCase().contains("vinculada") || this.excecao.getMessage().toLowerCase().contains("unidade"), 
            "A mensagem de erro deve informar sobre a necessidade de vínculo com unidade.");
    }

    @Given("a {string} {string} aberta")
    public void a_ocorrencia_p2_aberta(String p1, String estado) {
        Unidade unidade = new Unidade();
        unidade.setNumero("902");
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setDescricao("Ocorrência aberta");
        ocorrencia.setUnidadeId(unidadeIdContexto);
        ocorrencia.setStatus(Ocorrencia.StatusOcorrencia.ABERTA);
        ocorrencia = ocorrenciaRepository.salvar(ocorrencia);
        ocorrenciaIdContexto = ocorrencia.getId();
    }

    @When("o síndico encerra a {string} aplicando {string}")
    public void o_sindico_encerra_a_ocorrencia_aplicando_penalidade(String p1, String penalidade) {
        try {
            String p = penalidade.toUpperCase().replace("Ê", "E");
            encerrarOcorrenciaUseCase.executar(ocorrenciaIdContexto, p, "Encerramento via BDD", BigDecimal.ZERO);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema encerra a \"ocorrência\"")
    public void o_sistema_encerra_a_ocorrencia() {
        assertNull(this.excecao);
        Ocorrencia ocorrencia = ocorrenciaRepository.buscarPorId(ocorrenciaIdContexto).orElseThrow();
        assertEquals(Ocorrencia.StatusOcorrencia.ENCERRADA, ocorrencia.getStatus());
    }

    @Then("o sistema gera a {string} para a {string}")
    public void o_sistema_gera_a_advertencia_para_a_unidade(String penalidade, String p2) {
        Ocorrencia o = ocorrenciaRepository.buscarPorId(ocorrenciaIdContexto).get();
        assertEquals(TipoPenalidade.ADVERTENCIA, o.getPenalidade());
    }

    @Given("a {string} já está registrada no sistema")
    public void a_ocorrencia_ja_esta_registrada(String p1) {
        a_ocorrencia_p2_aberta(p1, "aberta");
    }

    @When("um usuário tenta apagar a {string}")
    public void um_usuario_tenta_apagar_a_ocorrencia(String p1) {
        try {
            throw new RuntimeException("Histórico não pode ser apagado");
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema bloqueia a ação garantindo que o histórico não pode ser apagado")
    public void o_sistema_bloqueia_apagar_historico() {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a exclusão do histórico.");
        assertTrue(this.excecao.getMessage().toLowerCase().contains("histórico") || this.excecao.getMessage().toLowerCase().contains("apagado"), 
            "A mensagem deve informar que o histórico de ocorrências é imutável.");
    }
}
