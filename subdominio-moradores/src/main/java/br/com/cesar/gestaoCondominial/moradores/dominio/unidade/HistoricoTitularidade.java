package br.com.cesar.gestaoCondominial.moradores.dominio.unidade;

import java.time.LocalDateTime;

import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class HistoricoTitularidade {
    private Long id;
    private UnidadeId unidadeId;
    private Usuario proprietarioAnterior;
    private Usuario novoProprietario;
    private LocalDateTime dataTransferencia;
}