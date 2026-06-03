package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.CreateMultaRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto.MultaResponseDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaEventPublisher;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.strategy.CalculoMultaStrategy;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;

@Service
public class CreateMultaManualUseCase {

        private final MultaRepository multaRepository;
        private final UnidadeRepository unidadeRepository;
        private final MultaEventPublisher eventPublisher;
        private final CalculoMultaStrategy calculoMultaStrategy;

        public CreateMultaManualUseCase(
                        MultaRepository multaRepository,
                        UnidadeRepository unidadeRepository,
                        MultaEventPublisher eventPublisher,
                        CalculoMultaStrategy calculoMultaStrategy) {
                this.multaRepository = multaRepository;
                this.unidadeRepository = unidadeRepository;
                this.eventPublisher = eventPublisher;
                this.calculoMultaStrategy = calculoMultaStrategy;
        }

        public MultaResponseDTO execute(CreateMultaRequestDTO request) {

                Unidade unidade = unidadeRepository.findById(new UnidadeId(request.getUnidadeId()))
                                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada."));

                long reincidencias = multaRepository.countByUnidadeIdAndDescricao(
                                unidade.getId(),
                                request.getDescricao());

                BigDecimal valorFinal = calculoMultaStrategy.calcular(
                                request.getValor(),
                                reincidencias);

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

                eventPublisher.publicarMultaCriada(salva);

                return MultaResponseDTO.fromEntity(salva);
        }
}