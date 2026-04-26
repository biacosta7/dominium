package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dominium.backend.application.morador.dto.VinculoRequestDTO;
import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.TipoVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.Usuario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GestaoDeMoradoresSteps extends DominiumFuncionalidade {

    private Unidade unidadeContexto;
    private Usuario moradorContexto;
    private VinculoMorador vinculoParaRemoverContexto;
    private Long usuarioLogadoId;

    @Given("a {string} {string} o limite máximo de moradores")
    public void a_p1_p2_o_limite_maximo_de_moradores(String p1, String atingiu) {
        unidadeContexto = new Unidade();
        unidadeContexto.setNumero("301");
        unidadeContexto.setBloco("C");
        unidadeContexto = unidadeRepository.save(unidadeContexto);

        if ("atingiu".equals(atingiu)) {
            for (int i = 0; i < 5; i++) {
                Usuario u = new Usuario();
                u.setNome("Morador " + i);
                u = usuarioRepository.save(u);
                
                VinculoMorador v = new VinculoMorador();
                v.setUnidade(unidadeContexto);
                v.setUsuario(u);
                v.setTipo(TipoVinculo.DEPENDENTE);
                v.setStatus(StatusVinculo.ATIVO);
                vinculoMoradorRepository.save(v);
            }
        }
    }

    @Given("o {string} {string} a outra unidade")
    public void o_p1_p2_a_outra_unidade(String p1, String pertence) {
        moradorContexto = new Usuario();
        moradorContexto.setNome("Morador Novo");
        moradorContexto = usuarioRepository.save(moradorContexto);

        if ("pertence".equals(pertence)) {
            Unidade outraUnidade = new Unidade();
            outraUnidade.setNumero("302");
            outraUnidade.setBloco("C");
            outraUnidade = unidadeRepository.save(outraUnidade);

            VinculoMorador v = new VinculoMorador();
            v.setUnidade(outraUnidade);
            v.setUsuario(moradorContexto);
            v.setTipo(TipoVinculo.TITULAR);
            v.setStatus(StatusVinculo.ATIVO);
            vinculoMoradorRepository.save(v);
        }
    }

    @When("o síndico ou titular solicita adicionar o {string}")
    public void o_sindico_ou_titular_solicita_adicionar_o_p1(String p1) {
        VinculoRequestDTO request = new VinculoRequestDTO();
        request.setUsuarioId(moradorContexto.getId());
        request.setTipo(TipoVinculo.DEPENDENTE);

        try {
            createVinculoMoradorUseCase.execute(unidadeContexto.getId().getValor(), request);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema adiciona o {string} à {string} com sucesso")
    public void o_sistema_adiciona_o_p1_a_p2_com_sucesso(String p1, String p2) {
        var vinculos = vinculoMoradorRepository.findByUsuarioAndUnidade(moradorContexto.getId(), unidadeContexto.getId().getValor());
        assertTrue(vinculos.size() > 0);
    }

    @Then("o sistema bloqueia a adição por exceder o limite máximo configurável")
    public void o_sistema_bloqueia_a_adicao_por_exceder_o_limite_maximo_configuravel() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Limite máximo de moradores por unidade atingido"));
    }

    @Then("o sistema informa que o {string} pertence a apenas uma unidade")
    public void o_sistema_informa_que_o_p1_pertence_a_apenas_uma_unidade(String p1) {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Morador já possui vínculo ativo com outra unidade"));
    }

    @Given("o usuário logado {string} {string} da unidade")
    public void o_usuario_logado_p1_p2_da_unidade(String eOuNao, String titular) {
        unidadeContexto = new Unidade();
        unidadeContexto.setNumero("401");
        unidadeContexto.setBloco("D");
        unidadeContexto = unidadeRepository.save(unidadeContexto);

        Usuario uLogado = new Usuario();
        uLogado.setNome("Logado");
        uLogado = usuarioRepository.save(uLogado);
        usuarioLogadoId = uLogado.getId();

        VinculoMorador v = new VinculoMorador();
        v.setUnidade(unidadeContexto);
        v.setUsuario(uLogado);
        v.setTipo("é".equals(eOuNao) ? TipoVinculo.TITULAR : TipoVinculo.DEPENDENTE);
        v.setStatus(StatusVinculo.ATIVO);
        vinculoMoradorRepository.save(v);

        Usuario dependente = new Usuario();
        dependente.setNome("Dependente");
        dependente = usuarioRepository.save(dependente);

        vinculoParaRemoverContexto = new VinculoMorador();
        vinculoParaRemoverContexto.setUnidade(unidadeContexto);
        vinculoParaRemoverContexto.setUsuario(dependente);
        vinculoParaRemoverContexto.setTipo(TipoVinculo.DEPENDENTE);
        vinculoParaRemoverContexto.setStatus(StatusVinculo.ATIVO);
        vinculoParaRemoverContexto = vinculoMoradorRepository.save(vinculoParaRemoverContexto);
    }

    @When("o usuário solicita a remoção de um {string}")
    public void o_usuario_solicita_a_remocao_de_um_p1(String p1) {
        try {
            endVinculoMoradorUseCase.execute(vinculoParaRemoverContexto.getId(), usuarioLogadoId);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema remove o {string} com sucesso")
    public void o_sistema_remove_o_p1_com_sucesso(String p1) {
        var vinculo = vinculoMoradorRepository.findById(vinculoParaRemoverContexto.getId()).get();
        assertEquals(StatusVinculo.INATIVO, vinculo.getStatus());
    }

    @Then("o sistema bloqueia a ação informando que apenas titulares podem remover dependentes")
    public void o_sistema_bloqueia_a_acao_informando_que_apenas_titulares_podem_remover_dependentes() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Apenas um titular ou síndico pode remover dependentes"));
    }
}
