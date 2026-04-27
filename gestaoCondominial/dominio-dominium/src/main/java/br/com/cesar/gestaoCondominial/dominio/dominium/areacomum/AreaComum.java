package br.com.cesar.gestaoCondominial.dominio.dominium.areacomum;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AreaComum {

    private AreaComumId id;
    private String nome;
    private int capacidadeMaxima;
    private StatusArea status;


    public boolean estaDisponivel() {
        return status == StatusArea.DISPONIVEL;
    }

    public boolean temCapacidade(int quantidade) {
        return quantidade <= capacidadeMaxima;
    }

}
