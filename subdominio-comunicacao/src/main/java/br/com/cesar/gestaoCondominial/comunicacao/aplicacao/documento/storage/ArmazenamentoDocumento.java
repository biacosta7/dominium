package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage;

public interface ArmazenamentoDocumento {
    String salvar(String documentoId, int versao, String nomeOriginal, byte[] conteudo);
    byte[] carregar(String caminho);
}
