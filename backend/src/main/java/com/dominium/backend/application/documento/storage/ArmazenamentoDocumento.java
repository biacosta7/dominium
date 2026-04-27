package com.dominium.backend.application.documento.storage;

public interface ArmazenamentoDocumento {
    String salvar(String documentoId, int versao, String nomeOriginal, byte[] conteudo);
    byte[] carregar(String caminho);
}
