package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification;

/**
 * @deprecated Use {@link br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao} diretamente.
 * Este enum foi unificado com o tipo de domínio para eliminar a duplicação.
 * Mantido apenas para compatibilidade de compilação até remoção completa.
 */
@Deprecated
public enum TipoNotificacao {
    NOVA_ASSEMBLEIA,
    CANCELAMENTO_RESERVA,
    APLICACAO_MULTA,
    PROMOCAO_LISTA_ESPERA,
    GERACAO_TAXA,
    VENCIMENTO_DOCUMENTO,
    GERAL
}
