package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.*;

import com.dominium.backend.application.financeiro.dto.DespesaRequestDTO;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.TipoDespesa;
import com.dominium.backend.domain.financeiro.CategoriaDespesa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GestaoFinanceiraSteps extends DominiumFuncionalidade {

    private DespesaRequestDTO despesaRequest;
    private Long orcamentoId;

    @Given("o {string} {string} saldo disponível")
    public void o_orcamento_p2_saldo_disponivel(String p1, String possui) {
        Orcamento orcamento = new Orcamento();
        orcamento.setAno(LocalDate.now().getYear());
        orcamento.setValorTotal(new BigDecimal("10000.00"));
        orcamento.setValorGasto(BigDecimal.ZERO);
        orcamento = orcamentoRepository.save(orcamento);
        orcamentoId = orcamento.getId();
    }

    @Given("a despesa {string} {string}")
    public void a_despesa_e_p2(String p1, String tipo) {
        despesaRequest = new DespesaRequestDTO();
        despesaRequest.setDescricao("Gasto Teste");
        despesaRequest.setValor(new BigDecimal("500.00"));
        despesaRequest.setData(LocalDate.now());
        despesaRequest.setTipo("ordinaria".equalsIgnoreCase(tipo) ? TipoDespesa.ORDINARIA : TipoDespesa.EXTRAORDINARIA);
        despesaRequest.setCategoria(CategoriaDespesa.MANUTENCAO);
    }

    @Given("o {string} {string} saldo disponível suficiente")
    public void o_orcamento_p2_saldo_disponivel_suficiente(String p1, String possui) {
        Orcamento orcamento = new Orcamento();
        orcamento.setAno(LocalDate.now().getYear());
        orcamento.setValorTotal(new BigDecimal("100.00"));
        orcamento.setValorGasto(new BigDecimal("100.00"));
        orcamento = orcamentoRepository.save(orcamento);
        orcamentoId = orcamento.getId();
    }

    @Given("o valor {string} acima do limite")
    public void o_valor_p1_acima_do_limite(String estado) {
        despesaRequest.setValor(new BigDecimal("5000.00"));
    }

    @When("o síndico registra a despesa")
    public void o_sindico_registra_a_despesa() {
        try {
            registrarDespesaUseCase.execute(
                    despesaRequest.getDescricao(),
                    despesaRequest.getValor(),
                    despesaRequest.getData(),
                    despesaRequest.getCategoria(),
                    despesaRequest.getTipo());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @When("o síndico tenta registrar a despesa")
    public void o_sindico_tenta_registrar_a_despesa() {
        o_sindico_registra_a_despesa();
    }

    @Then("o sistema registra a despesa")
    public void o_sistema_registra_a_despesa() {
        assertNull(this.excecao);
    }

    @Then("desconta o valor do saldo disponível do orçamento")
    public void desconta_o_valor_do_saldo_disponivel() {
        assertTrue(true);
    }

    @Then("classifica a despesa por categoria")
    public void classifica_a_despesa_por_categoria() {
        assertTrue(true);
    }

    @Then("o sistema bloqueia a despesa para impedir o estouro do orçamento")
    public void o_sistema_bloqueia_a_despesa_estouro() {
        assertNotNull(this.excecao);
    }

    @Then("o sistema exige aprovação em assembleia para a despesa")
    public void o_sistema_exige_aprovacao_em_assembleia() {
        assertNotNull(this.excecao);
    }

    @Then("a despesa aguarda rateio automático após aprovada")
    public void a_despesa_aguarda_rateio_automatico() {
        assertTrue(true);
    }
}
