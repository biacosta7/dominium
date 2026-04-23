package com.dominium.backend.domain.areacomum;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AreaComum {

    private Long id;
    private String nome;
    private int capacidadeMaxima;
    private StatusArea status;

    public boolean estaDisponivel() {
        return status == StatusArea.DISPONIVEL;
    }

}
