package br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;

@Service
public class DeleteUnidadeUseCase {

    private final UnidadeRepository unidadeRepository;

    public DeleteUnidadeUseCase(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public void execute(Long id) {

        // 1. Buscar unidade
        Unidade unidade = unidadeRepository.findById(new UnidadeId(id))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        // 2. Regra de negócio: não pode deletar com débito
        if (unidade.getSaldoDevedor() != null 
                && unidade.getSaldoDevedor().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Não é possível excluir unidade com débitos ativos");
        }

        // 3. Deletar
        unidadeRepository.deleteById(new UnidadeId(id));
    }
}