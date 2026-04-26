package com.dominium.backend.bdd;

import static org.junit.jupiter.api.Assertions.*;

import com.dominium.backend.domain.financeiro.CategoriaDespesa;
import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.funcionario.AvaliacaoFuncionario;
import com.dominium.backend.domain.funcionario.Funcionario;
import com.dominium.backend.domain.funcionario.FuncionarioId;
import com.dominium.backend.domain.funcionario.OrdemServico;
import com.dominium.backend.domain.funcionario.OrdemServicoId;
import com.dominium.backend.domain.funcionario.StatusFuncionario;
import com.dominium.backend.domain.funcionario.StatusOrdemServico;
import com.dominium.backend.domain.funcionario.TipoVinculo;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GestaoDeFuncionariosSteps extends DominiumFuncionalidade {

    private Long sindicoId;
    private TipoVinculo tipoVinculoContexto;
    private FuncionarioId funcionarioIdContexto;
    private OrdemServicoId ordemServicoIdContexto;
    private LocalDate contratoInicioContexto;
    private LocalDate contratoFimContexto;
    private BigDecimal valorMensalContexto;
    private int despesasAntes;

    private void setupSindico() {
        if (sindicoId == null) {
            Usuario sindico = new Usuario();
            sindico.setNome("Síndico Funcionários");
            sindico.setEmail("sindico.func@teste.com");
            sindico.setTipo(TipoUsuario.SINDICO);
            sindico = usuarioRepository.save(sindico);
            sindicoId = sindico.getId();
        }
    }

    private TipoVinculo parseTipoVinculo(String tipo) {
        return TipoVinculo.valueOf(tipo.toUpperCase());
    }

    // ── Cadastro de funcionário ─────────────────────────────────────────────────

    @Given("o síndico informa os dados do {string} do tipo {string}")
    public void o_sindico_informa_os_dados_do_funcionario_do_tipo(String p1, String tipo) {
        setupSindico();
        tipoVinculoContexto = parseTipoVinculo(tipo);
        contratoInicioContexto = LocalDate.now().minusMonths(1);
        contratoFimContexto = LocalDate.now().plusMonths(11);
        valorMensalContexto = new BigDecimal("3500.00");
    }

    @When("o síndico solicita o cadastro do {string}")
    public void o_sindico_solicita_o_cadastro_do_funcionario(String p1) {
        try {
            Funcionario f = cadastrarFuncionarioUseCase.executar(
                    sindicoId, "Funcionário Teste", "12345678900",
                    "func@teste.com", "11999990000",
                    tipoVinculoContexto, contratoInicioContexto, contratoFimContexto, valorMensalContexto);
            funcionarioIdContexto = f.getId();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema cadastra o {string} com status {string}")
    public void o_sistema_cadastra_o_funcionario_com_status(String p1, String statusEsperado) {
        assertNull(this.excecao);
        assertNotNull(funcionarioIdContexto);
        Funcionario f = funcionarioRepository.findById(funcionarioIdContexto).orElseThrow();
        assertEquals(StatusFuncionario.valueOf(statusEsperado), f.getStatus());
    }

    // ── Vincular ordem de serviço ───────────────────────────────────────────────

    @Given("o {string} do tipo {string} {string} cadastrado")
    public void o_funcionario_do_tipo_p2_cadastrado(String p1, String tipo, String estado) {
        setupSindico();
        Funcionario f = cadastrarFuncionarioUseCase.executar(
                sindicoId, "Funcionário Cadastrado", "98765432100",
                "func.cadastro@teste.com", "11988887777",
                parseTipoVinculo(tipo), LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(11), new BigDecimal("3000.00"));
        funcionarioIdContexto = f.getId();
    }

    @When("o síndico cria uma {string} para o {string}")
    public void o_sindico_cria_uma_ordem_de_servico_para_o_funcionario(String p1, String p2) {
        try {
            OrdemServico os = criarOrdemServicoUseCase.executar(
                    sindicoId, funcionarioIdContexto.getValor(),
                    "Manutenção elétrica", LocalDate.now(), LocalDate.now().plusDays(7));
            ordemServicoIdContexto = os.getId();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema vincula o {string} à {string}")
    public void o_sistema_vincula_o_funcionario_a_ordem(String p1, String p2) {
        assertNull(this.excecao);
        assertNotNull(ordemServicoIdContexto);
        OrdemServico os = ordemServicoRepository.findById(ordemServicoIdContexto).orElseThrow();
        assertEquals(funcionarioIdContexto, os.getFuncionarioId());
    }

    @Then("o {string} passa a ter status {string}")
    public void o_funcionario_passa_a_ter_status(String p1, String statusEsperado) {
        Funcionario f = funcionarioRepository.findById(funcionarioIdContexto).orElseThrow();
        assertEquals(StatusFuncionario.valueOf(statusEsperado), f.getStatus());
    }

    // ── Bloqueio de OS para CLT ─────────────────────────────────────────────────

    @When("o síndico tenta criar uma {string} para o {string}")
    public void o_sindico_tenta_criar_uma_ordem_de_servico_para_o_funcionario(String p1, String p2) {
        o_sindico_cria_uma_ordem_de_servico_para_o_funcionario(p1, p2);
    }

    @Then("o sistema bloqueia a criação informando que ordens são exclusivas para prestadores eventuais")
    public void o_sistema_bloqueia_a_criacao_de_os_para_nao_eventual() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().toLowerCase().contains("eventu"));
    }

    // ── Encerrar ordem de serviço ───────────────────────────────────────────────

    @Given("o {string} do tipo {string} possui uma {string} ativa")
    public void o_funcionario_eventual_possui_os_ativa(String p1, String tipo, String p3) {
        setupSindico();
        Funcionario f = cadastrarFuncionarioUseCase.executar(
                sindicoId, "Eventual com OS", "11122233344",
                "eventual.os@teste.com", "11977776666",
                parseTipoVinculo(tipo), LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6), new BigDecimal("2000.00"));
        funcionarioIdContexto = f.getId();

        OrdemServico os = criarOrdemServicoUseCase.executar(
                sindicoId, funcionarioIdContexto.getValor(),
                "Reparo hidráulico", LocalDate.now(), LocalDate.now().plusDays(5));
        ordemServicoIdContexto = os.getId();
    }

    @When("o síndico conclui a {string}")
    public void o_sindico_conclui_a_ordem_de_servico(String p1) {
        try {
            encerrarOrdemServicoUseCase.executar(sindicoId, ordemServicoIdContexto.getValor());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema marca a {string} como {string}")
    public void o_sistema_marca_a_os_como(String p1, String statusEsperado) {
        assertNull(this.excecao);
        OrdemServico os = ordemServicoRepository.findById(ordemServicoIdContexto).orElseThrow();
        assertEquals(StatusOrdemServico.valueOf(statusEsperado), os.getStatus());
    }

    // ── Renovação de contrato ───────────────────────────────────────────────────

    private void cadastrarFuncionarioParaRenovacao() {
        setupSindico();
        Funcionario f = cadastrarFuncionarioUseCase.executar(
                sindicoId, "Funcionário Renovação", "55566677788",
                "renov@teste.com", "11966665555",
                TipoVinculo.CLT, LocalDate.now().minusYears(1),
                LocalDate.now().plusMonths(1), new BigDecimal("4000.00"));
        funcionarioIdContexto = f.getId();
    }

    @Given("o {string} {string} avaliações negativas recentes")
    public void o_funcionario_possui_avaliacoes_negativas(String p1, String possui) {
        cadastrarFuncionarioParaRenovacao();
        // sem avaliações cadastradas
    }

    @Given("o {string} {string} 3 avaliações negativas recentes")
    public void o_funcionario_possui_3_avaliacoes_negativas(String p1, String possui) {
        cadastrarFuncionarioParaRenovacao();
        for (int i = 0; i < 3; i++) {
            registrarAvaliacaoUseCase.executar(sindicoId, funcionarioIdContexto.getValor(),
                    false, "Avaliação negativa " + (i + 1));
        }
    }

    @When("o síndico solicita a renovação do contrato do {string}")
    public void o_sindico_solicita_renovacao_do_contrato(String p1) {
        try {
            renovarContratoUseCase.executar(sindicoId, funcionarioIdContexto.getValor(),
                    LocalDate.now().plusYears(1), new BigDecimal("4500.00"));
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema renova o contrato do {string}")
    public void o_sistema_renova_o_contrato(String p1) {
        assertNull(this.excecao);
        Funcionario f = funcionarioRepository.findById(funcionarioIdContexto).orElseThrow();
        assertTrue(f.getContratoFim().isAfter(LocalDate.now().plusMonths(6)));
    }

    @Then("o sistema bloqueia a renovação informando avaliações negativas")
    public void o_sistema_bloqueia_renovacao_por_avaliacoes() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().toLowerCase().contains("avaliações negativas"));
    }

    // ── Despesa mensal automática ───────────────────────────────────────────────

    @Given("existe um {string} cadastrado para o ano corrente")
    public void existe_orcamento_ano_corrente(String p1) {
        Orcamento orcamento = new Orcamento(LocalDate.now().getYear(), new BigDecimal("100000.00"));
        orcamentoRepository.save(orcamento);
    }

    @Given("o {string} {string} ativo com contrato vigente")
    public void o_funcionario_esta_ativo_contrato_vigente(String p1, String p2) {
        setupSindico();
        Funcionario f = cadastrarFuncionarioUseCase.executar(
                sindicoId, "Funcionário Despesa", "44455566677",
                "despesa@teste.com", "11955554444",
                TipoVinculo.CLT, LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(10), new BigDecimal("5000.00"));
        funcionarioIdContexto = f.getId();
        despesasAntes = despesaRepository.findAll().size();
    }

    @Given("o {string} {string} contrato vencido")
    public void o_funcionario_possui_contrato_vencido(String p1, String p2) {
        setupSindico();
        Funcionario f = cadastrarFuncionarioUseCase.executar(
                sindicoId, "Funcionário Vencido", "33344455566",
                "vencido@teste.com", "11944443333",
                TipoVinculo.CLT, LocalDate.now().minusYears(2),
                LocalDate.now().minusDays(1), new BigDecimal("5000.00"));
        funcionarioIdContexto = f.getId();
        despesasAntes = despesaRepository.findAll().size();
    }

    @Given("o {string} do tipo {string} {string} contrato vencido")
    public void o_funcionario_do_tipo_possui_contrato_vencido(String p1, String tipo, String p3) {
        setupSindico();
        Funcionario f = cadastrarFuncionarioUseCase.executar(
                sindicoId, "Eventual Vencido", "22233344455",
                "eventual.vencido@teste.com", "11933332222",
                parseTipoVinculo(tipo), LocalDate.now().minusYears(2),
                LocalDate.now().minusDays(1), new BigDecimal("1500.00"));
        funcionarioIdContexto = f.getId();
    }

    @When("o sistema executa a geração de despesas mensais")
    public void o_sistema_executa_geracao_despesas_mensais() {
        try {
            gerarDespesasMensaisUseCase.executar();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema registra uma despesa de pessoal com o valor mensal do {string}")
    public void o_sistema_registra_despesa_de_pessoal(String p1) {
        assertNull(this.excecao);
        Funcionario f = funcionarioRepository.findById(funcionarioIdContexto).orElseThrow();
        List<Despesa> despesas = despesaRepository.findAll();
        assertEquals(despesasAntes + 1, despesas.size());
        Despesa nova = despesas.get(despesas.size() - 1);
        assertEquals(CategoriaDespesa.PESSOAL, nova.getCategoria());
        assertEquals(0, nova.getValor().compareTo(f.getValorMensal()));
    }

    @Then("o sistema não registra despesa para o {string}")
    public void o_sistema_nao_registra_despesa(String p1) {
        assertNull(this.excecao);
        assertEquals(despesasAntes, despesaRepository.findAll().size());
    }

    // ── Bloqueio de ativação por contrato vencido ───────────────────────────────

    @Then("o sistema bloqueia a ativação informando que o contrato está vencido")
    public void o_sistema_bloqueia_ativacao_contrato_vencido() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().toLowerCase().contains("vencido"));
    }
}
