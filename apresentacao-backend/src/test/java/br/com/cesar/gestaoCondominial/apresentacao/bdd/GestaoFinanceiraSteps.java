package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.dto.DespesaRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;

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
        despesaRequest.setValor(new BigDecimal("5000.01")); // Valor que dispara a regra de assembleia
        
        // GARANTIA: Se não houver orçamento criado por outro step, cria um aqui
        Orcamento orcamento = orcamentoRepository.findByAno(LocalDate.now().getYear())
                .orElseGet(() -> {
                    Orcamento novo = new Orcamento();
                    novo.setAno(LocalDate.now().getYear());
                    novo.setValorTotal(new BigDecimal("10000.00"));
                    novo.setValorGasto(BigDecimal.ZERO);
                    return orcamentoRepository.save(novo);
                });
        
        this.orcamentoId = orcamento.getId();
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
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        BigDecimal saldoEsperado = orcamento.getValorTotal().subtract(despesaRequest.getValor());
        assertEquals(0, saldoEsperado.compareTo(orcamento.getSaldoDisponivel()));
    }

    @Then("classifica a despesa por categoria")
    public void classifica_a_despesa_por_categoria() {
        // Verifica se a despesa salva no banco tem a categoria correta
        Despesa despesaSalva = despesaRepository.findAll().stream()
                .filter(d -> d.getDescricao().equals(despesaRequest.getDescricao()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada no repositório"));

        assertEquals(despesaRequest.getCategoria(), despesaSalva.getCategoria(), 
            "A categoria da despesa gravada deve ser a mesma solicitada no cadastro.");
    }

    @Then("o sistema bloqueia a despesa para impedir o estouro do orçamento")
    public void o_sistema_bloqueia_a_despesa_estouro() {
        assertNotNull(this.excecao);
    }

    @Then("o sistema exige aprovação em assembleia para a despesa")
    public void o_sistema_exige_aprovacao_em_assembleia() {
        // Se houve erro antes mesmo de salvar, o teste deve falhar aqui
        if (this.excecao != null) {
            fail("O sistema lançou um erro inesperado: " + this.excecao.getMessage());
        }

        // Busca a despesa para conferir o status
        Despesa despesa = despesaRepository.findAll().stream()
                .filter(d -> d.getTipo() == TipoDespesa.EXTRAORDINARIA)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ERRO: A despesa extraordinária deveria ter sido salva como PENDENTE, mas não foi encontrada no banco."));

        assertEquals(StatusDespesa.PENDENTE, despesa.getStatus(), 
            "Despesas extraordinárias acima do limite devem nascer com status PENDENTE.");
    }

    @Then("a despesa aguarda rateio automático após aprovada")
    public void a_despesa_aguarda_rateio_automatico() {
        // Verifica se o gasto NÃO foi computado no orçamento ainda (pois está pendente)
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        
        assertEquals(0, BigDecimal.ZERO.compareTo(orcamento.getValorGasto()),
            "O orçamento não deve ser impactado enquanto a despesa estiver pendente de aprovação.");
    }
}
