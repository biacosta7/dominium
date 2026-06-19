package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarFuncionariosUseCase {

    private final FuncionarioRepository repository;

    public ListarFuncionariosUseCase(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public List<Funcionario> executar() {
        return repository.findAll();
    }
}
