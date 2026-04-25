package com.dominium.backend.application.usuario.dto;

import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private TipoUsuario tipo;

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setCpf(usuario.getCpf());
        dto.setTipo(usuario.getTipo());
        return dto;
    }
}
