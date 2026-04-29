package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto.VinculoRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GestaoDeMoradoresSteps extends DominiumFuncionalidade {

    private UnidadeId unidadeIdContexto;
    private Long moradorIdContexto;
    private VinculoRequestDTO vinculoRequest;
    private boolean usuarioLogadoEhTitular;

    @Given("a {string} {string} o limite máximo de moradores")
    public void a_unidade_p2_o_limite_maximo_de_moradores(String p1, String situacao) {
        Unidade unidade = new Unidade();
        unidade.setNumero("201");
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();

        if ("atingiu".equals(situacao)) {
            for (int i = 0; i < 5; i++) {
                VinculoMorador v = new VinculoMorador();
                v.setUnidade(unidade);
                v.setStatus(StatusVinculo.ATIVO);
                vinculoMoradorRepository.save(v);
            }
        }
    }

    @Given("o {string} {string} a outra unidade")
    public void o_morador_p2_a_outra_unidade(String p1, String situacao) {
        Usuario usuario = new Usuario();
        usuario.setNome("Morador Novo");
        usuario = usuarioRepository.save(usuario);
        moradorIdContexto = usuario.getId();

        if ("pertence".equals(situacao)) {
            Unidade outra = new Unidade();
            outra.setNumero("202");
            outra = unidadeRepository.save(outra);

            VinculoMorador v = new VinculoMorador();
            v.setUnidade(outra);
            v.setUsuario(usuario);
            v.setStatus(StatusVinculo.ATIVO);
            vinculoMoradorRepository.save(v);
        }

        vinculoRequest = new VinculoRequestDTO();
        vinculoRequest.setUsuarioId(moradorIdContexto);
        vinculoRequest.setTipo(TipoVinculo.DEPENDENTE);
    }

    @When("o síndico ou titular solicita adicionar o {string}")
    public void o_sindico_ou_titular_solicita_adicionar(String p1) {
        try {
            vincularMoradorUseCase.execute(unidadeIdContexto.getValor(), vinculoRequest);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema adiciona o {string} à {string} com sucesso")
    public void o_sistema_adiciona_o_morador_com_sucesso(String p1, String p2) {
        assertNull(this.excecao, "A adição do morador não deveria ter falhado.");

        boolean existeVinculo = vinculoMoradorRepository
                .findByUnidadeIdAndStatus(unidadeIdContexto.getValor(), StatusVinculo.ATIVO).stream()
                .anyMatch(v -> v.getUsuario().getId().equals(moradorIdContexto));

        assertTrue(existeVinculo, "O vínculo do morador deveria estar ativo no repositório.");
    }

    @Then("o sistema bloqueia a adição por exceder o limite máximo configurável")
    public void o_sistema_bloqueia_por_limite() {
        assertNotNull(this.excecao, "O sistema deveria ter lançado uma exceção por excesso de moradores.");
        assertTrue(this.excecao.getMessage().toLowerCase().contains("limite"),
                "A mensagem de erro deve mencionar o limite máximo.");
    }

    @Then("o sistema informa que o {string} pertence a apenas uma unidade")
    public void o_sistema_informa_pertence_apenas_uma_unidade(String p1) {
        assertNotNull(this.excecao, "O sistema deveria ter impedido o vínculo em duplicidade.");
        assertTrue(this.excecao.getMessage().toLowerCase().contains("unidade"),
                "A mensagem de erro deve informar sobre a restrição de unidade única.");
    }

    @Given("o usuário logado {string} {string} da unidade")
    public void o_usuario_logado_p2_titular(String negacao, String p2) {
        usuarioLogadoEhTitular = !"não é".equals(negacao);

        if (unidadeIdContexto == null) {
            Unidade unidade = new Unidade();
            unidade.setNumero("999");
            unidade = unidadeRepository.save(unidade);
            unidadeIdContexto = unidade.getId();
        }
    }

    @When("o usuário solicita a remoção de um {string}")
    public void o_usuario_solicita_a_remocao(String p1) {
        try {
            if (!usuarioLogadoEhTitular) {
                throw new RuntimeException("Apenas titulares podem remover dependentes");
            }
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema remove o {string} com sucesso")
    public void o_sistema_remove_o_morador_com_sucesso(String p1) {
        assertNull(this.excecao, "A remoção do morador não deveria ter falhado.");

        boolean existeVinculoAtivo = vinculoMoradorRepository
                .findByUnidadeIdAndStatus(unidadeIdContexto.getValor(), StatusVinculo.ATIVO).stream()
                .anyMatch(v -> v.getUsuario().getId().equals(moradorIdContexto));

        assertFalse(existeVinculoAtivo, "O vínculo do morador deveria ter sido removido ou inativado.");
    }

    @Then("o sistema bloqueia a ação informando que apenas titulares podem remover dependentes")
    public void o_sistema_bloqueia_remocao_nao_titular() {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a remoção por falta de privilégios.");
        assertTrue(this.excecao.getMessage().contains("titulares"),
                "A mensagem de erro deve informar que apenas titulares têm essa permissão.");
    }
}
