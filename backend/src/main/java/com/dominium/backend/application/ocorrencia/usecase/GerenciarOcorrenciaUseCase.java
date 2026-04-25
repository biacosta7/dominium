package com.dominium.backend.application.ocorrencia.usecase;

import com.dominium.backend.domain.ocorrencia.Ocorrencia;
import com.dominium.backend.domain.ocorrencia.repository.OcorrenciaRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.application.ocorrencia.dto.OcorrenciaRequestDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GerenciarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;

    public GerenciarOcorrenciaUseCase(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public Ocorrencia executar(OcorrenciaRequestDTO dto) {
        Ocorrencia ocorrencia = new Ocorrencia();

        // Agora esses métodos existem na classe Ocorrencia
        ocorrencia.setDescricao(dto.getDescricao());
        ocorrencia.setUnidadeId(new UnidadeId(dto.getUnidadeId()));
        ocorrencia.setDataRegistro(LocalDateTime.now());
        ocorrencia.setStatus(Ocorrencia.StatusOcorrencia.ABERTA);
        ocorrencia.setGeraMulta(dto.isGeraMulta());

        return repository.salvar(ocorrencia);
    }
}