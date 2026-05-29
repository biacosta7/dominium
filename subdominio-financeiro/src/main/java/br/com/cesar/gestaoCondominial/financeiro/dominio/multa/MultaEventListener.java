package br.com.cesar.gestaoCondominial.financeiro.dominio.multa;

public interface MultaEventListener {
    void onMultaCriada(Multa multa);
    void onMultaPaga(Multa multa);
    void onMultaCancelada(Multa multa);
}
