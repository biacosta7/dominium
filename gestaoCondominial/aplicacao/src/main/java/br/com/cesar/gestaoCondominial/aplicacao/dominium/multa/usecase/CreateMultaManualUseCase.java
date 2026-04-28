package br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto.CreateMultaRequestDTO;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.Multa;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.dominio.dominium.multa.repository.MultaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId; // Importado
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;

@Service
public class CreateMultaManualUseCase {

    private final MultaRepository multaRepository;
    private final UnidadeRepository unidadeRepository;

    public CreateMultaManualUseCase(
            MultaRepository multaRepository,
            UnidadeRepository unidadeRepository) {
        this.multaRepository = multaRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public MultaResponseDTO execute(CreateMultaRequestDTO request) {

        // Converte o Long do DTO para UnidadeId ao buscar no repositório
        Unidade unidade = unidadeRepository.findById(new UnidadeId(request.getUnidadeId()))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada."));

        // unidade.getId() agora já retorna um UnidadeId, então o repositório aceita direto
        long reincidencias = multaRepository.countByUnidadeIdAndDescricao(
                unidade.getId(),
                request.getDescricao()
        );

        BigDecimal valorFinal = calcularValorProgressivo(
                request.getValor(),
                reincidencias
        );

        Multa multa = new Multa();
        multa.setOcorrenciaId(request.getOcorrenciaId());
        multa.setUnidade(unidade);
        multa.setDescricao(request.getDescricao());
        multa.setValor(valorFinal);
        multa.setTipoValor(request.getTipoValor());
        multa.setStatus(StatusMulta.ABERTA);
        multa.setReincidencia((int) reincidencias);
        multa.setDataCriacao(LocalDateTime.now());

        Multa salva = multaRepository.save(multa);

        // O mapeamento para o DTO de resposta agora acontece no .fromEntity()
        return MultaResponseDTO.fromEntity(salva);
    }

    private BigDecimal calcularValorProgressivo(
            BigDecimal valorBase,
            long reincidencias) {

        if (reincidencias == 0) {
            return valorBase;
        }

        BigDecimal percentual = BigDecimal.valueOf(0.10 * reincidencias);

        return valorBase.add(
                valorBase.multiply(percentual)
        );
    }
}