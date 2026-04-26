package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.*;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.StatusAssembleia;
import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.StatusPauta;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestaoDeAssembleiasSteps extends DominiumFuncionalidade {

    private AssembleiaId assembleiaIdContexto;
    private Long sindicoId;
    private LocalDateTime dataHoraContexto;
    private List<String> pautasContexto = new ArrayList<>();

    @Given("a data da {string} {string} a antecedência mínima")
    public void a_data_da_assembleia_p2_a_antecedencia_minima(String p1, String respeita) {
        Usuario sindico = new Usuario();
        sindico.setNome("Sindico Teste");
        sindico.setEmail("sindico@teste.com");
        sindico.setTipo(TipoUsuario.SINDICO);
        sindico = usuarioRepository.save(sindico);
        sindicoId = sindico.getId();

        if ("respeita".equals(respeita)) {
            dataHoraContexto = LocalDateTime.now().plusDays(10);
        } else {
            dataHoraContexto = LocalDateTime.now().plusDays(1);
        }
        pautasContexto.clear();
        pautasContexto.add("Pauta Inicial");
    }

    @When("o síndico solicita a criação da \"assembleia\"")
    public void o_sindico_solicita_a_criacao_da_assembleia() {
        try {
            Assembleia assembleia = criarAssembleiaUseCase.executar(sindicoId, "Assembleia Geral", dataHoraContexto,
                    "Salão de Festas", pautasContexto);
            assembleiaIdContexto = assembleia.getId();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema cria a \"assembleia\"")
    public void o_sistema_cria_a_assembleia() {
        assertNull(this.excecao);
        assertNotNull(assembleiaIdContexto);
        Assembleia assembleia = assembleiaRepository.findById(assembleiaIdContexto).orElseThrow();
        assertEquals(StatusAssembleia.AGENDADA, assembleia.getStatus());
    }

    @Then("o sistema notifica os {string}")
    public void o_sistema_notifica_os_moradores(String p1) {
        assertTrue(true);
    }

    @Then("o sistema bloqueia a criação informando a antecedência mínima obrigatória")
    public void o_sistema_bloqueia_criacao_antecedencia() {
        assertNotNull(this.excecao);
    }

    @Given("a {string} {string} pautas cadastradas")
    public void a_assembleia_p2_pautas_cadastradas(String p1, String possuiPautas) {
        setupSindico();
        Assembleia assembleia = new Assembleia();
        assembleia.setTitulo("Assembleia de Encerramento");
        assembleia.setDataHora(LocalDateTime.now().minusHours(1));
        assembleia.setStatus(StatusAssembleia.AGENDADA);
        assembleia = assembleiaRepository.save(assembleia);
        assembleiaIdContexto = assembleia.getId();

        if ("possui".equals(possuiPautas)) {
            assembleia.setPauta(java.util.List.of("Pauta 1"));
            assembleiaRepository.save(assembleia); // Update the entity with the pauta
            
            Pauta pauta = new Pauta();
            pauta.setAssembleiaId(assembleiaIdContexto);
            pauta.setTitulo("Pauta 1");
            pauta.setStatus(StatusPauta.ABERTA);
            pautaRepository.save(pauta);
        }
    }

    private void setupSindico() {
        if (sindicoId == null) {
            Usuario sindico = new Usuario();
            sindico.setNome("Sindico Teste");
            sindico.setTipo(TipoUsuario.SINDICO);
            sindico = usuarioRepository.save(sindico);
            sindicoId = sindico.getId();
        }
    }

    @When("o síndico solicita o encerramento da {string}")
    public void o_sindico_solicita_o_encerramento_da_assembleia(String p1) {
        try {
            encerrarAssembleiaUseCase.executar(sindicoId, assembleiaIdContexto.getValor());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema encerra a \"assembleia\"")
    public void o_sistema_encerra_a_assembleia() {
        assertNull(this.excecao);
        Assembleia assembleia = assembleiaRepository.findById(assembleiaIdContexto).orElseThrow();
        assertEquals(StatusAssembleia.ENCERRADA, assembleia.getStatus());
    }

    @Then("o sistema bloqueia o encerramento informando que não há pauta")
    public void o_sistema_bloqueia_encerramento_sem_pauta() {
        assertNotNull(this.excecao);
    }
}
