package com.dominium.backend.domain.usuario;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {
    
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;
    private TipoUsuario tipo;

}        