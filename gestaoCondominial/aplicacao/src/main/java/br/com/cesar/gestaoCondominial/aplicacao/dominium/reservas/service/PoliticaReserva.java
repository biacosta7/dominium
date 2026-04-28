package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.service;

import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.exception.AreaNaoDisponivelException;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.exception.CapacidadeExcedidaException;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.exception.ConflitoReservaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoliticaReserva {

    public void validarNovaReserva(
            Reserva novaReserva,
            AreaComum area,
            List<Reserva> reservasExistentes
    ) {

        if (!area.estaDisponivel()) {
            throw new AreaNaoDisponivelException();
        }

        boolean conflito = reservasExistentes.stream()
                .anyMatch(r -> r.conflitoCom(novaReserva));

        if (conflito) {
            throw new ConflitoReservaException();
        }

        if (!area.temCapacidade(reservasExistentes.size() + 1)) {
            throw new CapacidadeExcedidaException();
        }
    }
}