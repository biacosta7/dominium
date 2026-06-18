package br.com.cesar.gestaoCondominial.apresentacao.dominium.ocorrencia;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoOcorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.TipoOcorrenciaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tipos-ocorrencia")
public class TipoOcorrenciaController {

    private final TipoOcorrenciaRepository tipoOcorrenciaRepository;

    public TipoOcorrenciaController(TipoOcorrenciaRepository tipoOcorrenciaRepository) {
        this.tipoOcorrenciaRepository = tipoOcorrenciaRepository;
    }

    @GetMapping
    public ResponseEntity<List<TipoOcorrenciaDTO>> listarTodos() {
        List<TipoOcorrenciaDTO> dtos = tipoOcorrenciaRepository.findAll().stream()
                .map(t -> new TipoOcorrenciaDTO(t.getId(), t.getNome(), t.getValorBaseMulta()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    public record TipoOcorrenciaDTO(Long id, String nome, BigDecimal valorBaseMulta) {}
}
