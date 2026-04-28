package br.com.cesar.gestaoCondominial.apresentacao.dominium.governanca.voto;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.dto.VotarRequest;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.dto.VotoResponse;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.usecase.ListarVotosUseCase;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.usecase.VotarUseCase;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votos")
public class VotoController {

    private final VotarUseCase votarUseCase;
    private final ListarVotosUseCase listarVotosUseCase;

    public VotoController(VotarUseCase votarUseCase, ListarVotosUseCase listarVotosUseCase) {
        this.votarUseCase = votarUseCase;
        this.listarVotosUseCase = listarVotosUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> votar(@RequestBody @Valid VotarRequest request) {
        votarUseCase.executar(
                new PautaId(request.pautaId()),
                new UsuarioId(request.usuarioId()),
                new UnidadeId(request.unidadeId()),
                request.opcao()
        );

        // 204: ação concluída, sem corpo de retorno (voto não é um recurso consultável diretamente aqui)
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/pauta/{pautaId}")
    public ResponseEntity<List<VotoResponse>> listarPorPauta(@PathVariable Long pautaId) {
        List<VotoResponse> votos = listarVotosUseCase.ListarPorPauta(new PautaId(pautaId))
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(votos);
    }

    // Mapeamento domínio → resposta HTTP
    private VotoResponse toResponse(Voto voto) {
        return new VotoResponse(
                voto.getId().getValor(),
                voto.getPautaId().getValor(),
                voto.getUnidadeId().getValor(),
                voto.getUsuarioId().getValor(),
                voto.getOpcaoVoto().name()
        );
    }
}
