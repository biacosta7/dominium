package br.com.cesar.gestaoCondominial.dominio.dominium.unidade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Unidade {

    private UnidadeId id;

    private String numero;

    private String bloco;

    private Usuario proprietario;

    private Usuario inquilino;

    private StatusAdimplencia status;

    private BigDecimal saldoDevedor;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
    
//     private String nome;
//     private String email;
//     private String senha;
//     private String telefone;
//     private String cpf;
//     @Enumerated(EnumType.STRING)
//     private TipoUsuario tipo;

// }        