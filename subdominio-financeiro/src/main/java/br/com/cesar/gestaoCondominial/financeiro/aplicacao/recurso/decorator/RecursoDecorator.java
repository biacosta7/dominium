package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.decorator;

import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto.AbrirRecursoRequestDTO;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;

import java.time.LocalDateTime;
import java.util.UUID;

public final class RecursoDecorator {
    private RecursoDecorator() {}

    @FunctionalInterface
    public interface Abertura {
        UUID abrir(AbrirRecursoRequestDTO request, Multa multa);
    }

    public static final class Validacao implements Abertura {
        private final Abertura aberturaDecorada;

        public Validacao(Abertura aberturaDecorada) {
            this.aberturaDecorada = aberturaDecorada;
        }

        @Override
        public UUID abrir(AbrirRecursoRequestDTO request, Multa multa) {
            if (multa.getDataCriacao() != null
                    && multa.getDataCriacao().plusDays(15).isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Prazo maximo de 15 dias para recurso expirado.");
            }
            if (multa.getStatus() != StatusMulta.ABERTA) {
                throw new IllegalStateException("Somente multas abertas podem receber recurso.");
            }
            return aberturaDecorada.abrir(request, multa);
        }
    }
}
