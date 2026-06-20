package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;

/**
 * Resultado agregado para operações de documento que precisam retornar
 * tanto o documento quanto sua última versão ao mesmo tempo.
 * Evita que o controller acesse o VersaoDocumentoRepository diretamente.
 */
public record DocumentoComVersao(Documento documento, VersaoDocumento ultimaVersao) {}
