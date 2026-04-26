package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.*;

import com.dominium.backend.application.taxa.dto.AtualizarTaxaRequestDTO;
import com.dominium.backend.application.taxa.dto.TaxaRequestDTO;
import com.dominium.backend.application.taxa.dto.TaxaResponseDTO;
import com.dominium.backend.application.taxa.usecase.*;
import com.dominium.backend.domain.taxa.StatusTaxa;
import com.dominium.backend.domain.taxa.TaxaCondominial;
import com.dominium.backend.domain.taxa.TaxaId;
import com.dominium.backend.domain.taxa.repository.TaxaCondominialRepository;
import com.dominium.backend.domain.unidade.StatusAdimplencia;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GestaoDeTaxaCondominialSteps extends DominiumFuncionalidade {

    @Autowired
    private TaxaCondominialRepository taxaCondominialRepository;
    @Autowired
    private GerarTaxaMensalUseCase gerarTaxaMensalUseCase;
    @Autowired
    private AtualizarValorTaxaUseCase atualizarValorTaxaUseCase;
    @Autowired
    private RegistrarPagamentoTaxaUseCase registrarPagamentoTaxaUseCase;
    @Autowired
    private ConsultarHistoricoTaxasUseCase consultarHistoricoTaxasUseCase;

    private Long unidadeIdContexto;
    private Long taxaIdContexto;
    private boolean existePendenciaContexto;
    private List<TaxaResponseDTO> historicoContexto;

    private void criarUnidadeDeContexto(String numero) {
        Unidade unidade = new Unidade();
        unidade.setNumero(numero);
        unidade.setBloco("A");
        unidade.setStatus(StatusAdimplencia.ADIMPLENTE);
        unidade = unidadeRepository.save(unidade);
        this.unidadeIdContexto = unidade.getId().getValor();
    }

    @Given("uma {string} existe no sistema para taxa")
    public void uma_unidade_existe_no_sistema_para_taxa(String p1) {
        criarUnidadeDeContexto("101");
    }

    @When("o sistema gera a {string} mensal com valor base de {string} e vencimento para {string}")
    public void o_sistema_gera_taxa_mensal(String p1, String valor, String data) {
        try {
            TaxaRequestDTO request = new TaxaRequestDTO();
            request.setUnidadeId(unidadeIdContexto);
            request.setValorBase(new BigDecimal(valor));
            request.setDataVencimento(LocalDate.parse(data));

            TaxaResponseDTO response = gerarTaxaMensalUseCase.executar(request);
            taxaIdContexto = response.getId();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("a {string} é criada com status {string}")
    public void a_taxa_criada_com_status(String p1, String status) {
        assertNull(this.excecao);
        TaxaCondominial taxa = taxaCondominialRepository.buscarPorId(new TaxaId(taxaIdContexto)).orElseThrow();
        assertEquals(StatusTaxa.valueOf(status), taxa.getStatus());
    }

    @Given("uma {string} {string} existe para a {string} com valor base de {string}")
    public void uma_taxa_existe_com_valor_base(String p1, String status, String p3, String valorBase) {
        criarUnidadeDeContexto("102");
        TaxaCondominial taxa = new TaxaCondominial(
                new UnidadeId(unidadeIdContexto),
                new BigDecimal(valorBase),
                BigDecimal.ZERO,
                LocalDate.now().plusDays(10)
        );
        taxaCondominialRepository.salvar(taxa);
        taxaIdContexto = taxa.getId().getValor();
    }

    @When("o síndico atualiza o valor base para {string} e multas para {string}")
    public void atualizar_valor_base_e_multas(String valorBase, String multas) {
        try {
            AtualizarTaxaRequestDTO request = new AtualizarTaxaRequestDTO();
            request.setNovoValorBase(new BigDecimal(valorBase));
            request.setNovasMultas(new BigDecimal(multas));
            atualizarValorTaxaUseCase.executar(taxaIdContexto, request);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o valor total da {string} deve ser {string}")
    public void o_valor_total_deve_ser(String p1, String valorEsperado) {
        assertNull(this.excecao);
        TaxaCondominial taxa = taxaCondominialRepository.buscarPorId(new TaxaId(taxaIdContexto)).orElseThrow();
        assertEquals(0, new BigDecimal(valorEsperado).compareTo(taxa.getValorTotal()));
    }

    @Given("uma {string} para pagamento {string} existe para a {string} com valor base de {string}")
    public void uma_taxa_para_pagamento_existe(String p1, String status, String p3, String valorBase) {
        uma_taxa_existe_com_valor_base(p1, status, p3, valorBase); // Reutiliza o Given anterior
    }

    @When("o sistema registra o {string} da {string}")
    public void o_sistema_registra_o_pagamento(String p1, String p2) {
        try {
            registrarPagamentoTaxaUseCase.executar(taxaIdContexto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema atualiza o status da {string} para {string}")
    public void o_sistema_atualiza_status_para(String p1, String statusEsperado) {
        assertNull(this.excecao);
        TaxaCondominial taxa = taxaCondominialRepository.buscarPorId(new TaxaId(taxaIdContexto)).orElseThrow();
        assertEquals(StatusTaxa.valueOf(statusEsperado), taxa.getStatus());
        assertNotNull(taxa.getDataPagamento());
    }

    @Given("a {string} possui {string} taxas cadastradas")
    public void a_unidade_possui_taxas_cadastradas(String p1, String quantidade) {
        criarUnidadeDeContexto("103");
        int qtd = Integer.parseInt(quantidade);
        for (int i = 0; i < qtd; i++) {
            TaxaCondominial taxa = new TaxaCondominial(
                    new UnidadeId(unidadeIdContexto),
                    new BigDecimal("300.00"),
                    BigDecimal.ZERO,
                    LocalDate.now().minusMonths(i)
            );
            taxaCondominialRepository.salvar(taxa);
        }
    }

    @When("o morador consulta o histórico de taxas da {string}")
    public void o_morador_consulta_o_historico(String p1) {
        try {
            historicoContexto = consultarHistoricoTaxasUseCase.executar(unidadeIdContexto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema retorna uma lista contendo {string} taxas")
    public void o_sistema_retorna_lista_com_qtd(String quantidade) {
        assertNull(this.excecao);
        assertEquals(Integer.parseInt(quantidade), historicoContexto.size());
    }

    @Given("uma {string} está com status {string} para a {string}")
    public void uma_taxa_esta_com_status_atrasada(String p1, String status, String p3) {
        criarUnidadeDeContexto("104");
        TaxaCondominial taxa = new TaxaCondominial(
                new UnidadeId(unidadeIdContexto),
                new BigDecimal("500.00"),
                BigDecimal.ZERO,
                LocalDate.now().minusDays(5) // Venceu há 5 dias
        );
        taxa.verificarAtraso();
        taxaCondominialRepository.salvar(taxa);
    }

    @When("o sistema verifica se existe taxa atrasada para a {string}")
    public void o_sistema_verifica_se_existe_taxa_atrasada(String p1) {
        existePendenciaContexto = taxaCondominialRepository.existeTaxaAtrasadaPorUnidade(new UnidadeId(unidadeIdContexto));
    }

    @Then("o sistema deve retornar que existe pendência")
    public void o_sistema_deve_retornar_que_existe_pendencia() {
        assertTrue(existePendenciaContexto, "Deveria existir pendência para a unidade.");
    }
}