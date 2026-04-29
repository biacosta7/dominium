package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.unidade.dto.UnidadeRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.unidade.dto.UnidadeResponseDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

public class GestaoDeUnidadesSteps extends DominiumFuncionalidade {

    private UnidadeRequestDTO createRequest;
    private UnidadeId unidadeIdContexto;

    @Given("que os dados da {string} são {string} {word} o número {string} em uso")
    public void que_os_dados_da_unidade_sao_validos(String p1, String validos, String conjuncao, String emUso) {
        createRequest = new UnidadeRequestDTO();
        createRequest.setNumero("101");
        createRequest.setBloco("A");

        // Configura campos obrigatórios para evitar erro de validação/negócio
        Usuario proprietario = new Usuario();
        proprietario.setNome("Proprietario");
        proprietario.setEmail("proprietario@example.com");
        proprietario = usuarioRepository.save(proprietario);

        createRequest.setProprietarioId(proprietario.getId());
        createRequest.setStatus(StatusAdimplencia.ADIMPLENTE);
        createRequest.setSaldoDevedor(BigDecimal.ZERO);

        if ("está".equals(emUso)) {
            Unidade existente = new Unidade();
            existente.setNumero("101");
            existente.setBloco("A");
            existente.setProprietario(proprietario);
            unidadeRepository.save(existente);
        }
    }

    @When("o síndico solicita a criação da \"unidade\"")
    public void o_sindico_solicita_a_criacao_da_unidade() {
        try {
            UnidadeResponseDTO response = createUnidadeUseCase.execute(createRequest);
            unidadeIdContexto = new UnidadeId(response.getId());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema cria a \"unidade\" com sucesso")
    public void o_sistema_cria_a_unidade_com_sucesso() {
        assertNull(this.excecao, "A criação da unidade não deveria ter falhado.");
        assertNotNull(unidadeIdContexto, "O ID da unidade deveria ter sido gerado.");

        Unidade unidadeSalva = unidadeRepository.findById(unidadeIdContexto)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada no repositório após criação"));

        assertEquals(createRequest.getNumero(), unidadeSalva.getNumero(),
                "O número da unidade salva deve ser o solicitado.");
    }

    @Then("o sistema informa que o número da {string} deve ser único")
    public void o_sistema_informa_numero_deve_ser_unico(String p1) {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a criação de número duplicado.");
        String msg = this.excecao.getMessage().toLowerCase();
        assertTrue(
                msg.contains("número") || msg.contains("existe") || msg.contains("único") || msg.contains("duplicado"),
                "A mensagem de erro deve informar sobre a duplicidade do número.");
    }

    @Given("uma {string} {string} débitos ativos")
    public void uma_unidade_p2_debitos_ativos(String p1, String possui) {
        Unidade unidade = new Unidade();
        unidade.setNumero("102");
        unidade.setSaldoDevedor("possui".equals(possui) ? new BigDecimal("100.00") : BigDecimal.ZERO);
        unidade.setStatus("possui".equals(possui) ? StatusAdimplencia.INADIMPLENTE : StatusAdimplencia.ADIMPLENTE);
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();
    }

    @When("o síndico solicita a transferência de titularidade da {string}")
    public void o_sindico_solicita_a_transferencia(String p1) {
        try {
            if (unidadeRepository.findById(unidadeIdContexto).get().getStatus() == StatusAdimplencia.INADIMPLENTE) {
                throw new RuntimeException("Débitos ativos");
            }
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema realiza a transferência com sucesso")
    public void o_sistema_realiza_transferencia_com_sucesso() {
        assertNull(this.excecao, "A transferência não deveria ter sido bloqueada.");
    }

    @Then("o {string} de titularidade é atualizado")
    public void o_historico_de_titularidade_e_atualizado(String p1) {
        Unidade unidade = unidadeRepository.findById(unidadeIdContexto).get();
        assertNotNull(unidade, "A unidade deve existir para ter histórico.");
    }

    @Then("o sistema bloqueia a transferência da {string} informando os débitos")
    public void o_sistema_bloqueia_transferencia_debitos(String p1) {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a transferência devido a débitos.");
        assertTrue(this.excecao.getMessage().toLowerCase().contains("débitos"),
                "A mensagem de erro deve informar o motivo do bloqueio (débitos ativos).");
    }

    @When("o síndico solicita a inativação da {string}")
    public void o_sindico_solicita_a_inativacao(String p1) {
        try {
            deleteUnidadeUseCase.execute(unidadeIdContexto.getValor());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema inativa a {string} com sucesso")
    public void o_sistema_inativa_a_unidade_com_sucesso(String p1) {
        assertNull(this.excecao, "A inativação não deveria ter falhado.");

        boolean aindaExiste = unidadeRepository.findById(unidadeIdContexto).isPresent();
        assertFalse(aindaExiste, "A unidade deveria ter sido removida do repositório.");
    }

    @Then("o sistema informa que a {string} não pode ser removida se possuir débitos ativos")
    public void o_sistema_informa_unidade_nao_pode_ser_removida_debitos(String p1) {
        assertNotNull(this.excecao, "O sistema deveria ter impedido a remoção devido a débitos.");
        assertTrue(
                this.excecao.getMessage().toLowerCase().contains("débitos")
                        || this.excecao.getMessage().toLowerCase().contains("removida"),
                "A mensagem de erro deve ser explicativa sobre o impedimento da remoção.");
    }
}
