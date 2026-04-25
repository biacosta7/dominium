package com.dominium.backend.application.unidade.usecase;

import org.springframework.stereotype.Service;

import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;

@Service
public class DeleteUnidadeUseCase {

    private final UnidadeRepository unidadeRepository;

    public DeleteUnidadeUseCase(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public void execute(Long id) {

        // 1. Buscar unidade
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        // 2. Regra de negócio: não pode deletar com débito
        if (unidade.getSaldoDevedor() != null 
                && unidade.getSaldoDevedor().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Não é possível excluir unidade com débitos ativos");
        }

        // 3. Deletar
        unidadeRepository.deleteById(id);
    }
}