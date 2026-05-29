package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.observer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaEventListener;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaEventPublisher;

@Component
public class MultaEventPublisherImpl implements MultaEventPublisher {

    private final List<MultaEventListener> listeners;

    public MultaEventPublisherImpl(List<MultaEventListener> listeners) {
        this.listeners = new ArrayList<>(listeners);
    }

    @Override
    public void registrar(MultaEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void remover(MultaEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void publicarMultaCriada(Multa multa) {
        listeners.forEach(l -> l.onMultaCriada(multa));
    }

    @Override
    public void publicarMultaPaga(Multa multa) {
        listeners.forEach(l -> l.onMultaPaga(multa));
    }

    @Override
    public void publicarMultaCancelada(Multa multa) {
        listeners.forEach(l -> l.onMultaCancelada(multa));
    }
}
