package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import com.dominium.backend.application.unidade.dto.UnidadeRequestDTO;
import com.dominium.backend.domain.unidade.StatusAdimplencia;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.Usuario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GestaoDeUnidadesSteps extends DominiumFuncionalidade {

    private UnidadeRequestDTO unidadeRequest;
    private UnidadeId unidadeIdContexto;
    private Long novoProprietarioIdContexto;
    private Usuario proprietario;

    @Given("que os dados da {string} são {string} e o número {string} em uso")
    public void que_os_dados_da_p1_sao_p2_e_o_numero_p3_em_uso(String p1, String p2, String emUso) {
        proprietario = new Usuario();
        proprietario.setNome("Proprietário Teste");
        proprietario.setEmail("teste@teste.com");
        proprietario = usuarioRepository.save(proprietario);

        unidadeRequest = new UnidadeRequestDTO();
        unidadeRequest.setNumero("101");
        unidadeRequest.setBloco("A");
        unidadeRequest.setStatus(StatusAdimplencia.ADIMPLENTE);
        unidadeRequest.setProprietarioId(proprietario.getId());

        if ("está".equals(emUso)) {
            Unidade unidadeExistente = new Unidade();
            unidadeExistente.setNumero("101");
            unidadeExistente.setBloco("A");
            unidadeRepository.save(unidadeExistente);
        }
    }

    @When("o síndico solicita a criação da {string}")
    public void o_sindico_solicita_a_criacao_da_p1(String p1) {
        try {
            createUnidadeUseCase.execute(unidadeRequest);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema cria a {string} com sucesso")
    public void o_sistema_cria_a_p1_com_sucesso(String p1) {
        var unidadeOpt = unidadeRepository.findByNumeroAndBloco("101", "A");
        assertTrue(unidadeOpt.isPresent());
    }

    @Then("o sistema informa que o número da {string} deve ser único")
    public void o_sistema_informa_que_o_numero_da_p1_deve_ser_unico(String p1) {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Já existe uma unidade com esse número e bloco"));
    }

    @Given("uma {string} {string} débitos ativos")
    public void uma_p1_p2_debitos_ativos(String p1, String possuiDebitos) {
        proprietario = new Usuario();
        proprietario.setNome("Dono Atual");
        proprietario = usuarioRepository.save(proprietario);

        Usuario novoDono = new Usuario();
        novoDono.setNome("Novo Dono");
        novoDono = usuarioRepository.save(novoDono);
        novoProprietarioIdContexto = novoDono.getId();

        Unidade unidade = new Unidade();
        unidade.setNumero("202");
        unidade.setBloco("B");
        unidade.setStatus(StatusAdimplencia.ADIMPLENTE);
        unidade.setProprietario(proprietario);
        
        if ("possui".equals(possuiDebitos)) {
            unidade.setSaldoDevedor(new BigDecimal("500.00"));
        } else {
            unidade.setSaldoDevedor(BigDecimal.ZERO);
        }
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();
    }

    @When("o síndico solicita a transferência de titularidade da {string}")
    public void o_sindico_solicita_a_transferencia_de_titularidade_da_p1(String p1) {
        try {
            transferirTitularidadeUseCase.execute(unidadeIdContexto.getValor(), novoProprietarioIdContexto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema realiza a transferência com sucesso")
    public void o_sistema_realiza_a_transferencia_com_sucesso() {
        var unidade = unidadeRepository.findById(unidadeIdContexto).get();
        assertEquals(novoProprietarioIdContexto, unidade.getProprietario().getId());
    }

    @Then("o {string} de titularidade é atualizado")
    public void o_p1_de_titularidade_e_atualizado(String p1) {
        var historico = historicoTitularidadeRepository.findByUnidadeId(unidadeIdContexto);
        assertTrue(historico.size() > 0);
    }

    @Then("o sistema bloqueia a transferência da {string} informando os débitos")
    public void o_sistema_bloqueia_a_transferencia_da_p1_informando_os_debitos(String p1) {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Transferência não permitida. Unidade possui débitos pendentes"));
    }

    @When("o síndico solicita a inativação da {string}")
    public void o_sindico_solicita_a_inativacao_da_p1(String p1) {
        try {
            deleteUnidadeUseCase.execute(unidadeIdContexto.getValor());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema inativa a {string} com sucesso")
    public void o_sistema_inativa_a_p1_com_sucesso(String p1) {
        var unidadeOpt = unidadeRepository.findById(unidadeIdContexto);
        assertTrue(unidadeOpt.isEmpty());
    }

    @Then("o sistema informa que a {string} não pode ser removida se possuir débitos ativos")
    public void o_sistema_informa_que_a_p1_nao_pode_ser_removida_se_possuir_debitos_ativos(String p1) {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Unidade não pode ser removida/inativada pois possui débitos ativos"));
    }
}
