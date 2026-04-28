package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.StatusFuncionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GerarDespesasMensaisUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final DespesaRepository despesaRepository;
    private final OrcamentoRepository orcamentoRepository;

    public GerarDespesasMensaisUseCase(FuncionarioRepository funcionarioRepository,
                                        DespesaRepository despesaRepository,
                                        OrcamentoRepository orcamentoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.despesaRepository = despesaRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    @Transactional
    public void executar() {
        Optional<Orcamento> orcamento = orcamentoRepository.findByAno(LocalDate.now().getYear());
        if (orcamento.isEmpty()) return;

        List<Funcionario> ativos = funcionarioRepository.findByStatus(StatusFuncionario.ATIVO);

        for (Funcionario f : ativos) {
            if (f.getValorMensal() == null || f.getValorMensal().compareTo(BigDecimal.ZERO) <= 0) continue;
            if (f.contratoVencido()) continue;

            Despesa despesa = new Despesa(
                    "Pagamento mensal - " + f.getNome(),
                    f.getValorMensal(),
                    LocalDate.now(),
                    CategoriaDespesa.PESSOAL,
                    TipoDespesa.ORDINARIA,
                    StatusDespesa.APROVADA,
                    orcamento.get().getId()
            );
            despesaRepository.save(despesa);
        }
    }
}
