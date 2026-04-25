package com.dominium.backend.application.morador.dto;

import com.dominium.backend.application.usuario.dto.UsuarioRequestDTO;
import com.dominium.backend.domain.morador.TipoVinculo;

import lombok.Data;

@Data
public class VinculoRequestDTO {
    // Para vincular pessoa existente
    private Long usuarioId;

    // Para adicionar novo morador e vincular na mesma ação
    private UsuarioRequestDTO novoUsuario;

    private TipoVinculo tipo;
}
