package br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.dto;

import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    private String telefone;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotNull(message = "O tipo de usuário é obrigatório")
    private TipoUsuario tipo;
}
