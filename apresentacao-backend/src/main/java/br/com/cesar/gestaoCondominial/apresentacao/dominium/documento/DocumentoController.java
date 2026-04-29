package br.com.cesar.gestaoCondominial.apresentacao.dominium.documento;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.dto.DocumentoResponse;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.dto.VersaoDocumentoResponse;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase.*;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.CategoriaDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.apresentacao.dominium.exception.ExceptionHandler;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final CadastrarDocumentoUseCase cadastrarUseCase;
    private final AtualizarDocumentoUseCase atualizarUseCase;
    private final InativarDocumentoUseCase inativarUseCase;
    private final ListarDocumentosUseCase listarUseCase;
    private final BaixarDocumentoUseCase baixarUseCase;
    private final VersaoDocumentoRepository versaoRepository;
    private final ExceptionHandler exceptionHandler;

    public DocumentoController(CadastrarDocumentoUseCase cadastrarUseCase,
                               AtualizarDocumentoUseCase atualizarUseCase,
                               InativarDocumentoUseCase inativarUseCase,
                               ListarDocumentosUseCase listarUseCase,
                               BaixarDocumentoUseCase baixarUseCase,
                               VersaoDocumentoRepository versaoRepository,
                               ExceptionHandler exceptionHandler) {
        this.cadastrarUseCase = cadastrarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.inativarUseCase = inativarUseCase;
        this.listarUseCase = listarUseCase;
        this.baixarUseCase = baixarUseCase;
        this.versaoRepository = versaoRepository;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> cadastrar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @RequestParam String nome,
            @RequestParam CategoriaDocumento categoria,
            @RequestParam(required = false) LocalDate dataValidade,
            @RequestParam MultipartFile arquivo
    ) {
        return exceptionHandler.withHandler(() -> {
            try {
                var documento = cadastrarUseCase.executar(
                        sindicoId, nome, categoria, dataValidade,
                        arquivo.getOriginalFilename(), arquivo.getBytes()
                );
                var versao = versaoRepository.findUltimaVersao(documento.getId()).orElse(null);
                return ResponseEntity.status(HttpStatus.CREATED).body(DocumentoResponse.from(documento, versao));
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a leitura do arquivo de upload", e);
            }
        });
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> atualizar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable String id,
            @RequestParam MultipartFile arquivo
    ) {
        return exceptionHandler.withHandler(() -> {
            try {
                VersaoDocumento novaVersao = atualizarUseCase.executar(
                        sindicoId, id, arquivo.getOriginalFilename(), arquivo.getBytes()
                );
                return ResponseEntity.ok(VersaoDocumentoResponse.from(novaVersao));
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a leitura do arquivo de upload", e);
            }
        });
    }

    @PutMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(
            @RequestHeader("X-Sindico-Id") Long sindicoId,
            @PathVariable String id
    ) {
        return exceptionHandler.withHandler(() -> {
            var documento = inativarUseCase.executar(sindicoId, id);
            var versao = versaoRepository.findUltimaVersao(documento.getId()).orElse(null);
            return ResponseEntity.ok(DocumentoResponse.from(documento, versao));
        });
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "false") boolean incluirInativos
    ) {
        return exceptionHandler.withHandler(() -> {
            List<DocumentoResponse> response = listarUseCase.executar(incluirInativos)
                    .stream()
                    .map(d -> DocumentoResponse.from(d, versaoRepository.findUltimaVersao(d.getId()).orElse(null)))
                    .toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<?> historico(@PathVariable String id) {
        return exceptionHandler.withHandler(() -> {
            List<VersaoDocumentoResponse> historico = versaoRepository
                    .findHistorico(DocumentoId.de(id))
                    .stream()
                    .map(VersaoDocumentoResponse::from)
                    .toList();
            return ResponseEntity.ok(historico);
        });
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        BaixarDocumentoUseCase.ResultadoDownload resultado = baixarUseCase.executar(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(resultado.nomeArquivo()).build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().headers(headers).body(resultado.conteudo());
    }
}
