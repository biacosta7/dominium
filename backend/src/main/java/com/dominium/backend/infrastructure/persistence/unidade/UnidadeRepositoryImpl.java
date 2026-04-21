package com.dominium.backend.infrastructure.persistence.unidade;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.infrastructure.persistence.unidade.SpringDataUnidadeRepository;

@Repository
public class UnidadeRepositoryImpl implements UnidadeRepository {

    private final SpringDataUnidadeRepository springDataRepository;

    public UnidadeRepositoryImpl(SpringDataUnidadeRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Unidade save(Unidade unidade) {
        return springDataRepository.save(unidade);
    }

    @Override
    public Optional<Unidade> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<Unidade> findByNumeroAndBloco(String numero, String bloco) {
        return springDataRepository.findByNumeroAndBloco(numero, bloco);
    }

    @Override
    public List<Unidade> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}