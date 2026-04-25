package com.dominium.backend.application.morador.dto;

import com.dominium.backend.application.usuario.dto.UsuarioResponseDTO;
import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.TipoVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;

import lombok.Data;

@Data
public class VinculoResponseDTO {
    private Long id;
    private Long unidadeId;
    private UsuarioResponseDTO usuario;
    private TipoVinculo tipo;
    private StatusVinculo status;

    public static VinculoResponseDTO fromEntity(VinculoMorador vinculo) {
        VinculoResponseDTO dto = new VinculoResponseDTO();
        dto.setId(vinculo.getId());
        dto.setUnidadeId(vinculo.getUnidade().getId());
        if (vinculo.getUsuario() != null) {
            UsuarioResponseDTO usuarioDto = new UsuarioResponseDTO();
            usuarioDto.setId(vinculo.getUsuario().getId());
            usuarioDto.setNome(vinculo.getUsuario().getNome());
            usuarioDto.setEmail(vinculo.getUsuario().getEmail());
            usuarioDto.setTelefone(vinculo.getUsuario().getTelefone());
            usuarioDto.setCpf(vinculo.getUsuario().getCpf());
            usuarioDto.setTipo(vinculo.getUsuario().getTipo());
            dto.setUsuario(usuarioDto);
        }
        dto.setTipo(vinculo.getTipo());
        dto.setStatus(vinculo.getStatus());
        return dto;
    }
}
