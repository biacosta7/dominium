package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

@Service
public class GetDespesaUseCase {

    private final DespesaRepository despesaRepository;

    public GetDespesaUseCase(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    public Despesa execute(Long id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada: " + id));
    }
}
