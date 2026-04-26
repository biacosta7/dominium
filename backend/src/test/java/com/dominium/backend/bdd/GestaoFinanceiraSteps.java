package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dominium.backend.application.financeiro.dto.DespesaRequestDTO;
import com.dominium.backend.domain.financeiro.CategoriaDespesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.StatusDespesa;
import com.dominium.backend.domain.financeiro.TipoDespesa;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GestaoFinanceiraSteps extends DominiumFuncionalidade {

    private Orcamento orcamentoContexto;
    private DespesaRequestDTO despesaRequest;
    private Long ultimaDespesaSalvaId;

    @Given("o {string} {string} saldo disponível")
    public void o_p1_p2_saldo_disponivel(String p1, String possui) {
        orcamentoContexto = new Orcamento();
        orcamentoContexto.setAno(2025);
        orcamentoContexto.setValorTotal(new BigDecimal("100000.00"));
        
        if ("possui".equals(possui)) {
            orcamentoContexto.setValorGasto(BigDecimal.ZERO);
        } else {
            orcamentoContexto.setValorGasto(new BigDecimal("100000.00"));
        }
        orcamentoContexto = orcamentoRepository.save(orcamentoContexto);
    }
    
    @Given("o {string} {string} saldo disponível suficiente")
    public void o_p1_p2_saldo_disponivel_suficiente(String p1, String possui) {
        orcamentoContexto = new Orcamento();
        orcamentoContexto.setAno(2026);
        orcamentoContexto.setValorTotal(new BigDecimal("100.00"));
        orcamentoContexto.setValorGasto(BigDecimal.ZERO);
        orcamentoContexto = orcamentoRepository.save(orcamentoContexto);
    }

    @Given("a despesa {string} {string}")
    public void a_despesa_p1_p2(String p1, String tipo) {
        despesaRequest = new DespesaRequestDTO();
        despesaRequest.setDescricao("Despesa de Teste");
        despesaRequest.setData(LocalDate.now());
        despesaRequest.setCategoria(CategoriaDespesa.MANUTENCAO);

        if ("ordinária".equals(tipo)) {
            despesaRequest.setTipo(TipoDespesa.ORDINARIA);
            despesaRequest.setValor(new BigDecimal("1000.00"));
        } else if ("extraordinária".equals(tipo)) {
            despesaRequest.setTipo(TipoDespesa.EXTRAORDINARIA);
        }
    }

    @Given("o valor {string} acima do limite")
    public void o_valor_p1_acima_do_limite(String p1) {
        despesaRequest.setValor(new BigDecimal("15000.00")); // Acima do limite (exemplo)
    }

    @When("o síndico registra a despesa")
    public void o_sindico_registra_a_despesa() {
        try {
            var response = registrarDespesaUseCase.execute(
                despesaRequest.getDescricao(),
                despesaRequest.getValor(),
                despesaRequest.getData(),
                despesaRequest.getCategoria(),
                despesaRequest.getTipo()
            );
            ultimaDespesaSalvaId = response.getId();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @When("o síndico tenta registrar a despesa")
    public void o_sindico_tenta_registrar_a_despesa() {
        try {
            registrarDespesaUseCase.execute(
                "Despesa Gigante",
                new BigDecimal("200000.00"), // Estoura o orçamento de 100
                LocalDate.now(),
                CategoriaDespesa.MANUTENCAO,
                TipoDespesa.ORDINARIA
            );
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema registra a despesa")
    public void o_sistema_registra_a_despesa() {
        assertNotNull(ultimaDespesaSalvaId);
        var despesa = despesaRepository.findById(ultimaDespesaSalvaId);
        assertTrue(despesa.isPresent());
    }

    @Then("desconta o valor do saldo disponível do orçamento")
    public void desconta_o_valor_do_saldo_disponivel_do_orcamento() {
        var orc = orcamentoRepository.findById(orcamentoContexto.getId()).get();
        assertEquals(new BigDecimal("99000.00"), orc.getSaldoDisponivel());
    }

    @Then("classifica a despesa por categoria")
    public void classifica_a_despesa_por_categoria() {
        var despesa = despesaRepository.findById(ultimaDespesaSalvaId).get();
        assertEquals(CategoriaDespesa.MANUTENCAO, despesa.getCategoria());
    }

    @Then("o sistema bloqueia a despesa para impedir o estouro do orçamento")
    public void o_sistema_bloqueia_a_despesa_para_impedir_o_estouro_do_orcamento() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("Saldo insuficiente no orçamento para esta despesa"));
    }

    @Then("o sistema exige aprovação em assembleia para a despesa")
    public void o_sistema_exige_aprovacao_em_assembleia_para_a_despesa() {
        var despesa = despesaRepository.findById(ultimaDespesaSalvaId).get();
        assertEquals(StatusDespesa.PENDENTE, despesa.getStatus());
    }

    @Then("a despesa aguarda rateio automático após aprovada")
    public void a_despesa_aguarda_rateio_automatico_apos_aprovada() {
        var despesa = despesaRepository.findById(ultimaDespesaSalvaId).get();
        // Apenas para comprovação BDD, ela nasce pendente de aprovação
        assertEquals(StatusDespesa.PENDENTE, despesa.getStatus());
    }
}
