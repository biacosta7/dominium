package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento;

/**
 * Port (interface) que abstrai a verificação de permissão de síndico.
 *
 * Isola os use cases de documento do modelo interno do bounded context de moradores.
 * A implementação vive na infraestrutura e pode consultar qualquer fonte de dados
 * sem que os use cases precisem saber como.
 *
 * Lança DomainException se o usuário não existir ou não for síndico.
 */
public interface SindicoService {
    void validarPermissao(Long sindicoId);
}
