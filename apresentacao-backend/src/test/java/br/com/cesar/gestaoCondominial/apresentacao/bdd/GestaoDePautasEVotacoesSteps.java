package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.StatusAssembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.StatusPauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.OpcaoVoto;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GestaoDePautasEVotacoesSteps extends DominiumFuncionalidade {

    private UnidadeId unidadeIdContexto;
    private UsuarioId usuarioIdContexto;
    private PautaId pautaIdContexto;

    @Given("o {string} {string} adimplente")
    public void o_morador_p2_adimplente(String p1, String estado) {
        setupUsuarioUnidadeEvinculo(estado);
        pautaIdContexto = criarPautaAtiva();
    }

    private void setupUsuarioUnidadeEvinculo(String estado) {
        Unidade unidade = new Unidade();
        unidade.setNumero("501");
        unidade.setBloco("E");
        unidade.setStatus("está".equals(estado) ? StatusAdimplencia.ADIMPLENTE : StatusAdimplencia.INADIMPLENTE);
        unidade = unidadeRepository.save(unidade);
        unidadeIdContexto = unidade.getId();

        Usuario usuario = new Usuario();
        usuario.setNome("Morador Teste");
        usuario.setEmail("morador@teste.com");
        usuario = usuarioRepository.save(usuario);
        usuarioIdContexto = new UsuarioId(usuario.getId());

        VinculoMorador vinculo = new VinculoMorador();
        vinculo.setUnidade(unidade);
        vinculo.setUsuario(usuario);
        vinculo.setTipo(TipoVinculo.TITULAR);
        vinculo.setStatus(StatusVinculo.ATIVO);
        vinculoMoradorRepository.save(vinculo);
    }

    @Given("a {string} {string} voto nesta pauta")
    public void a_unidade_p2_voto_nesta_pauta(String p1, String registrou) {
        setupUsuarioUnidadeEvinculo("está");
        pautaIdContexto = criarPautaAtiva();

        if ("já registrou".equals(registrou)) {
            votarUseCase.executar(pautaIdContexto, usuarioIdContexto, unidadeIdContexto, OpcaoVoto.FAVOR);
        }
    }

    @When("o {string} registra o seu {string}")
    public void o_morador_registra_o_seu_voto(String p1, String p2) {
        try {
            votarUseCase.executar(pautaIdContexto, usuarioIdContexto, unidadeIdContexto, OpcaoVoto.FAVOR);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @When("o {string} tenta registrar o seu {string}")
    public void o_morador_tenta_registrar_o_seu_voto(String p1, String p2) {
        o_morador_registra_o_seu_voto(p1, p2);
    }

    @Then("o sistema contabiliza o {string} para a {string}")
    public void o_sistema_contabiliza_o_voto(String p1, String p2) {
        assertNull(this.excecao, "O voto não deveria ter sido bloqueado.");

        boolean votoRegistrado = votoRepository.findByPautaAndUnidade(pautaIdContexto, unidadeIdContexto);

        assertTrue(votoRegistrado, "O voto deveria estar persistido no repositório.");
    }

    @Then("o sistema bloqueia o {string} informando a inadimplência")
    public void o_sistema_bloqueia_voto_inadimplencia(String p1) {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado o voto de unidade inadimplente.");
        assertTrue(this.excecao.getMessage().toLowerCase().contains("inadimplente"),
                "A mensagem de erro deve informar que o voto é restrito a unidades adimplentes.");
    }

    @Then("o sistema bloqueia o {string} garantindo apenas um voto por unidade")
    public void o_sistema_bloqueia_duplo_voto(String p1) {
        assertNotNull(this.excecao, "O sistema deveria ter bloqueado a tentativa de voto duplicado.");
        assertTrue(
                this.excecao.getMessage().toLowerCase().contains("voto")
                        || this.excecao.getMessage().toLowerCase().contains("registrado"),
                "A mensagem de erro deve informar que a unidade já votou.");
    }

    @Given("a {string} {string} o quórum mínimo exigido")
    public void a_pauta_p2_o_quorum_minimo(String p1, String atingiu) {
        setupUsuarioUnidadeEvinculo("está");
        pautaIdContexto = criarPautaAtiva();

        if ("atingiu".equals(atingiu)) {
            votarUseCase.executar(pautaIdContexto, usuarioIdContexto, unidadeIdContexto, OpcaoVoto.FAVOR);
        }
    }

    @When("o síndico encerra a {string}")
    public void o_sindico_encerra_a_votacao(String p1) {
        try {
            encerrarPautaUseCase.executar(pautaIdContexto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema finaliza a {string} e aplica as regras de aprovação")
    public void o_sistema_finaliza_a_votacao(String p1) {
        assertNull(this.excecao, "O encerramento da votação não deveria ter falhado.");

        Pauta pauta = pautaRepository.findById(pautaIdContexto).orElseThrow();
        assertNotEquals(StatusPauta.ABERTA, pauta.getStatus(),
                "A pauta deveria estar com status encerrado ou aprovado.");
    }

    private PautaId criarPautaAtiva() {
        Assembleia assembleia = new Assembleia();
        assembleia.setStatus(StatusAssembleia.AGENDADA);
        assembleia = assembleiaRepository.save(assembleia);

        Pauta pauta = new Pauta();
        pauta.setAssembleiaId(assembleia.getId());
        pauta.setStatus(StatusPauta.ABERTA);
        pauta = pautaRepository.save(pauta);
        return pauta.getId();
    }
}
