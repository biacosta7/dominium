package com.dominium.backend.domain.funcionario;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AvaliacaoFuncionario {

    private Long id;
    private FuncionarioId funcionarioId;
    private boolean positiva;
    private String comentario;
    private LocalDate data;

    public static AvaliacaoFuncionario criar(FuncionarioId funcionarioId, boolean positiva, String comentario) {
        AvaliacaoFuncionario a = new AvaliacaoFuncionario();
        a.funcionarioId = funcionarioId;
        a.positiva = positiva;
        a.comentario = comentario;
        a.data = LocalDate.now();
        return a;
    }
}
