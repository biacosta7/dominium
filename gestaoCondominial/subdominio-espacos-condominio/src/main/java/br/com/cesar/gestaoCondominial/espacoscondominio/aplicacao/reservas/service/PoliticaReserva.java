package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.service;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception.AreaNaoDisponivelException;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception.CapacidadeExcedidaException;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception.ConflitoReservaException;
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