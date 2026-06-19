package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.proxy;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.StatusFuncionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;

@Primary
@Service
public class FuncionarioRepositoryValidacaoProxy implements FuncionarioRepository {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioRepositoryValidacaoProxy.class);

    private final FuncionarioRepository delegate;

    public FuncionarioRepositoryValidacaoProxy(
            @Qualifier("funcionarioRepositoryJdbc") FuncionarioRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Funcionario save(Funcionario funcionario) {
        return delegate.save(funcionario);
    }

    @Override
    public Optional<Funcionario> findById(FuncionarioId id) {
        return delegate.findById(id).map(this::aplicarRegraVencimento);
    }

    @Override
    public List<Funcionario> findAll() {
        return delegate.findAll().stream()
                .map(this::aplicarRegraVencimento)
                .toList();
    }

    @Override
    public List<Funcionario> findByStatus(StatusFuncionario status) {
        return delegate.findByStatus(status).stream()
                .map(this::aplicarRegraVencimento)
                .toList();
    }

    private Funcionario aplicarRegraVencimento(Funcionario funcionario) {
        if (funcionario.contratoVencido() && funcionario.getStatus() == StatusFuncionario.ATIVO) {
            log.warn("[PROXY-FUNCIONARIO] Contrato vencido detectado | funcionário='{}' | vencimento={} | bloqueando acesso",
                    funcionario.getNome(),
                    funcionario.getContratoFim());
            funcionario.bloquear();
            delegate.save(funcionario);
        }
        return funcionario;
    }
}
