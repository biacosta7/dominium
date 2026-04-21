package com.dominium.backend.domain.unidade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dominium.backend.domain.usuario.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(
    name = "unidades",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"numero", "bloco"})
    }
)
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    private String bloco;

    @ManyToOne
    @JoinColumn(name = "proprietario_id", nullable = false)
    private Usuario proprietario;

    @ManyToOne
    @JoinColumn(name = "inquilino_id")
    private Usuario inquilino;

    @Enumerated(EnumType.STRING)
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