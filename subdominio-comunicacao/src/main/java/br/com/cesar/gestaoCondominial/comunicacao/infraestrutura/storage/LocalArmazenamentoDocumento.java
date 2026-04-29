package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.storage;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage.ArmazenamentoDocumento;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalArmazenamentoDocumento implements ArmazenamentoDocumento {

    private static final String BASE_DIR = "uploads/documentos";

    @Override
    public String salvar(String documentoId, int versao, String nomeOriginal, byte[] conteudo) {
        try {
            Path dir = Paths.get(BASE_DIR, documentoId, "v" + versao);
            Files.createDirectories(dir);
            Path arquivo = dir.resolve(nomeOriginal);
            Files.write(arquivo, conteudo);
            return arquivo.toString();
        } catch (IOException e) {
            throw new DomainException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    @Override
    public byte[] carregar(String caminho) {
        try {
            return Files.readAllBytes(Paths.get(caminho));
        } catch (IOException e) {
            throw new DomainException("Erro ao carregar arquivo: " + e.getMessage());
        }
    }
}
