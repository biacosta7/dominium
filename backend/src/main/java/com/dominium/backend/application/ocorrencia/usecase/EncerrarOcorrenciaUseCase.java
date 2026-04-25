package com.dominium.backend.application.ocorrencia.usecase;

import com.dominium.backend.domain.ocorrencia.Ocorrencia;
import com.dominium.backend.domain.ocorrencia.TipoPenalidade;
import com.dominium.backend.domain.ocorrencia.repository.OcorrenciaRepository;
import com.dominium.backend.application.multa.usecase.CreateMultaManualUseCase;
import com.dominium.backend.application.multa.dto.CreateMultaRequestDTO;
import com.dominium.backend.domain.multa.TipoValorMulta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EncerrarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;
    private final CreateMultaManualUseCase createMultaUseCase;

    public EncerrarOcorrenciaUseCase(OcorrenciaRepository repository, CreateMultaManualUseCase createMultaUseCase) {
        this.repository = repository;
        this.createMultaUseCase = createMultaUseCase;
    }

    public Ocorrencia executar(Long ocorrenciaId, String penalidadeStr, String observacao, BigDecimal valorMulta) {
        Ocorrencia ocorrencia = repository.buscarPorId(ocorrenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Ocorrência não encontrada com o ID: " + ocorrenciaId));

        TipoPenalidade penalidade = TipoPenalidade.NENHUMA;
        if (penalidadeStr != null && !penalidadeStr.isEmpty()) {
            try {
                penalidade = TipoPenalidade.valueOf(penalidadeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Penalidade inválida: " + penalidadeStr);
            }
        }

        ocorrencia.encerrar(penalidade, observacao);
        Ocorrencia salva = repository.salvar(ocorrencia);

        if (penalidade == TipoPenalidade.MULTA) {
            CreateMultaRequestDTO multaDTO = new CreateMultaRequestDTO();
            multaDTO.setOcorrenciaId(salva.getId());
            multaDTO.setUnidadeId(salva.getUnidadeId().getValor());
            multaDTO.setDescricao("Multa gerada automaticamente. Ocorrência: " + salva.getDescricao());
            multaDTO.setValor(valorMulta != null ? valorMulta : BigDecimal.valueOf(150.00));
            multaDTO.setTipoValor(TipoValorMulta.FIXO);

            createMultaUseCase.execute(multaDTO);
        }

        return salva;
    }
}
