package br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarAssembleiasUseCase {

    private final AssembleiaRepository assembleiaRepository;

    public ListarAssembleiasUseCase(AssembleiaRepository assembleiaRepository) {
        this.assembleiaRepository = assembleiaRepository;
    }

    public List<Assembleia> executar() {
        return assembleiaRepository.findAll();
    }
}
