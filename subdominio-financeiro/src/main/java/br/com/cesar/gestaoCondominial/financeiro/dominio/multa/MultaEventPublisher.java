package br.com.cesar.gestaoCondominial.financeiro.dominio.multa;

public interface MultaEventPublisher {
    void registrar(MultaEventListener listener);
    void remover(MultaEventListener listener);
    void publicarMultaCriada(Multa multa);
    void publicarMultaPaga(Multa multa);
    void publicarMultaCancelada(Multa multa);
}
