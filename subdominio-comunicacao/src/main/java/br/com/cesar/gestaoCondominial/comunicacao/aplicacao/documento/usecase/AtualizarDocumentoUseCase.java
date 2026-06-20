package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.SindicoService;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage.ArmazenamentoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.*;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;
    private final ArmazenamentoDocumento armazenamento;
    private final SindicoService sindicoService;

    public AtualizarDocumentoUseCase(DocumentoRepository documentoRepository,
                                      VersaoDocumentoRepository versaoRepository,
                                      ArmazenamentoDocumento armazenamento,
                                      SindicoService sindicoService) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
        this.armazenamento = armazenamento;
        this.sindicoService = sindicoService;
    }

    @Transactional
    public VersaoDocumento executar(Long sindicoId, String documentoId, String nomeArquivo, byte[] conteudo) {
        sindicoService.validarPermissao(sindicoId);

        Documento documento = documentoRepository.findById(DocumentoId.de(documentoId))
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));

        if (!documento.isAtivo()) {
            throw new DomainException("Não é possível atualizar um documento inativo");
        }

        VersaoDocumento ultimaVersao = versaoRepository.findUltimaVersao(documento.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma versão encontrada para o documento"));
        int proximaVersao = ultimaVersao.getVersao() + 1;

        String caminho = armazenamento.salvar(documentoId, proximaVersao, nomeArquivo, conteudo);
        VersaoDocumento novaVersao = VersaoDocumento.criar(documento.getId(), proximaVersao, nomeArquivo, caminho, sindicoId);
        versaoRepository.save(novaVersao);
        return novaVersao;
    }
}
