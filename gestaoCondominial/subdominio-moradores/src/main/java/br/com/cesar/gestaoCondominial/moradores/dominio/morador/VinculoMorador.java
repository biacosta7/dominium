package br.com.cesar.gestaoCondominial.moradores.dominio.morador;

import java.time.LocalDateTime;

import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class VinculoMorador {

    private Long id;

    private Usuario usuario;

    private Unidade unidade;

    private TipoVinculo tipo;

    private StatusVinculo status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
