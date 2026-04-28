package br.com.cesar.gestaoCondominial.operacional.dominio.funcionario;

import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Funcionario {

    private static final int MAX_AVALIACOES_NEGATIVAS = 3;

    private FuncionarioId id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private TipoVinculo tipoVinculo;
    private StatusFuncionario status;
    private LocalDate contratoInicio;
    private LocalDate contratoFim;
    private BigDecimal valorMensal;
    private Long sindicoId;
    private LocalDateTime dataCadastro;

    public static Funcionario criar(
            FuncionarioId id,
            String nome,
            String cpf,
            String email,
            String telefone,
            TipoVinculo tipoVinculo,
            LocalDate contratoInicio,
            LocalDate contratoFim,
            BigDecimal valorMensal,
            Long sindicoId
    ) {
        Funcionario f = new Funcionario();
        f.id = id;
        f.nome = nome;
        f.cpf = cpf;
        f.email = email;
        f.telefone = telefone;
        f.tipoVinculo = tipoVinculo;
        f.contratoInicio = contratoInicio;
        f.contratoFim = contratoFim;
        f.valorMensal = valorMensal;
        f.sindicoId = sindicoId;
        f.dataCadastro = LocalDateTime.now();
        // EVENTUAL começa INATIVO até ter uma ordem de serviço ativa
        f.status = tipoVinculo == TipoVinculo.EVENTUAL ? StatusFuncionario.INATIVO : StatusFuncionario.ATIVO;
        return f;
    }

    public static Funcionario reconstituir(
            FuncionarioId id,
            String nome,
            String cpf,
            String email,
            String telefone,
            TipoVinculo tipoVinculo,
            StatusFuncionario status,
            LocalDate contratoInicio,
            LocalDate contratoFim,
            BigDecimal valorMensal,
            Long sindicoId,
            LocalDateTime dataCadastro
    ) {
        Funcionario f = new Funcionario();
        f.id = id;
        f.nome = nome;
        f.cpf = cpf;
        f.email = email;
        f.telefone = telefone;
        f.tipoVinculo = tipoVinculo;
        f.status = status;
        f.contratoInicio = contratoInicio;
        f.contratoFim = contratoFim;
        f.valorMensal = valorMensal;
        f.sindicoId = sindicoId;
        f.dataCadastro = dataCadastro;
        return f;
    }

    public boolean contratoVencido() {
        return LocalDate.now().isAfter(contratoFim);
    }

    public void ativar() {
        if (contratoVencido()) {
            throw new DomainException("Funcionário com contrato vencido não pode ser ativado");
        }
        this.status = StatusFuncionario.ATIVO;
    }

    public void inativar() {
        this.status = StatusFuncionario.INATIVO;
    }

    public void bloquear() {
        this.status = StatusFuncionario.BLOQUEADO;
    }

    public void renovarContrato(LocalDate novaDataFim, BigDecimal novoValor, long avaliacoesNegativasRecentes) {
        if (avaliacoesNegativasRecentes >= MAX_AVALIACOES_NEGATIVAS) {
            throw new DomainException(
                "Contrato não pode ser renovado: funcionário possui " + avaliacoesNegativasRecentes + " avaliações negativas recentes"
            );
        }
        this.contratoFim = novaDataFim;
        this.valorMensal = novoValor;
        if (this.status == StatusFuncionario.BLOQUEADO) {
            this.status = StatusFuncionario.ATIVO;
        }
    }
}
