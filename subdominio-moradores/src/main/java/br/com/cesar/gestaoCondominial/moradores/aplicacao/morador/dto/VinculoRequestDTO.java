package br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.dto;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto.UsuarioRequestDTO;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.TipoVinculo;

import lombok.Data;

@Data
public class VinculoRequestDTO {
    // Para vincular pessoa existente
    private Long usuarioId;

    // Para adicionar novo morador e vincular na mesma ação
    private UsuarioRequestDTO novoUsuario;

    private TipoVinculo tipo;
}
