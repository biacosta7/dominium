// package com.dominium.backend.application.multa.usecase;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// import org.springframework.stereotype.Service;

// import com.dominium.backend.application.multa.dto.MultaResponseDTO;
// import com.dominium.backend.domain.multa.Multa;
// import com.dominium.backend.domain.multa.StatusMulta;
// import com.dominium.backend.domain.multa.TipoValorMulta;
// import com.dominium.backend.domain.multa.repository.MultaRepository;
// import com.dominium.backend.domain.ocorrencia.Ocorrencia;
// import com.dominium.backend.domain.ocorrencia.repository.OcorrenciaRepository;

// @Service
// public class CreateMultaAutomaticaUseCase {

//     private final MultaRepository multaRepository;
//     private final OcorrenciaRepository ocorrenciaRepository;

//     public CreateMultaAutomaticaUseCase(
//             MultaRepository multaRepository,
//             OcorrenciaRepository ocorrenciaRepository
//     ) {
//         this.multaRepository = multaRepository;
//         this.ocorrenciaRepository = ocorrenciaRepository;
//     }

//     public MultaResponseDTO execute(Long ocorrenciaId) {

//         Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
//                 .orElseThrow(() ->
//                         new IllegalArgumentException("Ocorrência não encontrada."));

//         long reincidencias = multaRepository.countByUnidadeIdAndDescricao(
//                 ocorrencia.getUnidade().getId(),
//                 ocorrencia.getDescricao()
//         );

//         BigDecimal valorBase = definirValorBase(ocorrencia);

//         BigDecimal valorFinal = aplicarProgressividade(
//                 valorBase,
//                 reincidencias
//         );

//         Multa multa = new Multa();
//         multa.setOcorrenciaId(ocorrencia.getId());
//         multa.setUnidade(ocorrencia.getUnidade());
//         multa.setDescricao(ocorrencia.getDescricao());
//         multa.setTipoValor(TipoValorMulta.FIXO);
//         multa.setValor(valorFinal);
//         multa.setValorBase(valorBase);
//         multa.setStatus(StatusMulta.ABERTA);
//         multa.setReincidencia((int) reincidencias);
//         multa.setDataCriacao(LocalDateTime.now());

//         Multa salva = multaRepository.save(multa);

//         return MultaResponseDTO.fromEntity(salva);
//     }

//     private BigDecimal definirValorBase(Ocorrencia ocorrencia) {
//         // Regra inicial simples.
//         // Futuramente isso pode vir de configuração do condomínio.
//         return BigDecimal.valueOf(150.00);
//     }

//     private BigDecimal aplicarProgressividade(
//             BigDecimal valorBase,
//             long reincidencias
//     ) {
//         if (reincidencias == 0) {
//             return valorBase;
//         }

//         BigDecimal percentual =
//                 BigDecimal.valueOf(0.10 * reincidencias);

//         return valorBase.add(
//                 valorBase.multiply(percentual)
//         );
//     }
// }