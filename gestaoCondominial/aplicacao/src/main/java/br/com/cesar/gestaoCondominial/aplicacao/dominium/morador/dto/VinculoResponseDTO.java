package br.com.cesar.gestaoCondominial.aplicacao.dominium.morador.dto;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.usuario.dto.UsuarioResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.VinculoMorador;

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
        dto.setUnidadeId(vinculo.getUnidade().getId().getValor());
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
