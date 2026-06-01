package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.service;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception.AreaNaoDisponivelException;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception.CapacidadeExcedidaException;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.exception.ConflitoReservaException;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator.ReservaCollection;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator.ReservaIterator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoliticaReserva {

    public void validarNovaReserva(
            Reserva novaReserva,
            AreaComum area,
            List<Reserva> reservasExistentes
    ) {
        validarNovaReserva(novaReserva, area, new ReservaCollection(reservasExistentes).iterator());
    }

    public void validarNovaReserva(
            Reserva novaReserva,
            AreaComum area,
            ReservaIterator reservasExistentes
    ) {

        if (!area.estaDisponivel()) {
            throw new AreaNaoDisponivelException();
        }

        boolean conflito = false;
        int totalReservas = 0;

        while (reservasExistentes.hasNext()) {
            Reserva reserva = reservasExistentes.next();
            totalReservas++;
            if (reserva.conflitoCom(novaReserva)) {
                conflito = true;
            }
        }

        if (conflito) {
            throw new ConflitoReservaException();
        }

        if (!area.temCapacidade(totalReservas + 1)) {
            throw new CapacidadeExcedidaException();
        }
    }
}