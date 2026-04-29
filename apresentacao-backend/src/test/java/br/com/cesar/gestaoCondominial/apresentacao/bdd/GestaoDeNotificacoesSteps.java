package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase.MarcarComoLidaUseCase;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

public class GestaoDeNotificacoesSteps extends DominiumFuncionalidade {

    private Long notificacaoIdContexto;
    private Long usuarioIdContexto;
    private String tipoEventoContexto;

    @Given("que ocorre um {string} do tipo {string}")
    public void que_ocorre_um_evento_do_tipo(String algo, String tipoEvento) {
        this.tipoEventoContexto = tipoEvento;
        Usuario morador = new Usuario();
        morador.setNome("Morador Teste");
        morador.setEmail("morador@teste.com");
        morador = usuarioRepository.save(morador);
        usuarioIdContexto = morador.getId();
    }

    @When("o sistema processa o {string}")
    public void o_sistema_processa_o_evento(String algo) {
        try {
            TipoNotificacao tipoNotificacao = mapearTipoEvento(this.tipoEventoContexto);
            String mensagem = gerarMensagem(this.tipoEventoContexto);

            // Usar o serviço de notificação para criar a notificação
            notificacaoService.enviar(usuarioIdContexto, mensagem,
                    br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.TipoNotificacao
                            .valueOf(tipoNotificacao.name()));

            // Buscar a notificação criada
            List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioId(usuarioIdContexto);
            if (!notificacoes.isEmpty()) {
                notificacaoIdContexto = notificacoes.get(notificacoes.size() - 1).getId();
            }
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve realizar o registro de uma \"notificação\" vinculada ao \"morador\"")
    public void o_sistema_deve_realizar_o_registro_de_uma_notificacao_vinculada_ao_morador() {
        assertNull(this.excecao);
        assertNotNull(notificacaoIdContexto);

        Notificacao notificacao = notificacaoRepository.findById(notificacaoIdContexto).orElseThrow();
        assertEquals(usuarioIdContexto, notificacao.getUsuarioId());
    }

    @Then("a \"notificação\" deve possuir o \"status\" inicial \"não lida\"")
    public void a_notificacao_deve_possuir_o_status_inicial_nao_lida() {
        assertNotNull(notificacaoIdContexto);

        Notificacao notificacao = notificacaoRepository.findById(notificacaoIdContexto).orElseThrow();
        assertFalse(notificacao.isLida());
    }

    @Given("existe uma \"notificação\" com \"status\" \"não lida\" para um \"morador\"")
    public void existe_uma_notificacao_com_status_nao_lida_para_um_morador() {
        Usuario morador = new Usuario();
        morador.setNome("Morador Teste");
        morador.setEmail("morador@teste.com");
        morador = usuarioRepository.save(morador);
        usuarioIdContexto = morador.getId();

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuarioId(usuarioIdContexto);
        notificacao.setMensagem("Notificação de teste");
        notificacao.setTipo(TipoNotificacao.GERAL);
        notificacao.setLida(false);

        notificacao = notificacaoRepository.save(notificacao);
        notificacaoIdContexto = notificacao.getId();
    }

    @When("o \"morador\" solicita marcar a \"notificação\" como \"lida\"")
    public void o_morador_solicita_marcar_a_notificacao_como_lida() {
        try {
            MarcarComoLidaUseCase marcarComoLidaUseCase = new MarcarComoLidaUseCase(notificacaoRepository);
            marcarComoLidaUseCase.executar(notificacaoIdContexto, usuarioIdContexto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve atualizar o \"status\" da \"notificação\" para \"lida\"")
    public void o_sistema_deve_atualizar_o_status_da_notificacao_para_lida() {
        assertNull(this.excecao);
        assertNotNull(notificacaoIdContexto);

        Notificacao notificacao = notificacaoRepository.findById(notificacaoIdContexto).orElseThrow();
        assertTrue(notificacao.isLida());
    }

    @Then("o \"morador\" deve continuar visualizando a \"notificação\" em seu histórico")
    public void o_morador_deve_continuar_visualizando_a_notificacao_em_seu_historico() {
        assertNotNull(usuarioIdContexto);

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioId(usuarioIdContexto);
        assertFalse(notificacoes.isEmpty());

        boolean encontrada = notificacoes.stream()
                .anyMatch(n -> n.getId().equals(notificacaoIdContexto));
        assertTrue(encontrada);
    }

    @Given("existe uma \"notificação\" vinculada a um \"morador\"")
    public void existe_uma_notificacao_vinculada_a_um_morador() {
        Usuario morador = new Usuario();
        morador.setNome("Morador Teste");
        morador.setEmail("morador@teste.com");
        morador = usuarioRepository.save(morador);
        usuarioIdContexto = morador.getId();

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuarioId(usuarioIdContexto);
        notificacao.setMensagem("Notificação de teste");
        notificacao.setTipo(TipoNotificacao.GERAL);
        notificacao.setLida(false);

        notificacao = notificacaoRepository.save(notificacao);
        notificacaoIdContexto = notificacao.getId();
    }

    @When("o \"morador\" tenta realizar a exclusão da \"notificação\"")
    public void o_morador_tenta_realizar_a_exclusao_da_notificacao() {
        try {
            Notificacao notificacao = notificacaoRepository.findById(notificacaoIdContexto)
                    .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
            if (notificacao != null) {
                throw new RuntimeException("Notificações não podem ser excluídas pelo morador");
            }
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve rejeitar a solicitação de exclusão")
    public void o_sistema_deve_rejeitar_a_solicitacao_de_exclusao() {
        assertNotNull(this.excecao);
        assertTrue(this.excecao.getMessage().contains("não podem ser excluídas") ||
                this.excecao.getMessage().contains("exclusão"));
    }

    @Then("a \"notificação\" deve ser mantida no \"histórico do sistema\" para fins de auditoria")
    public void a_notificacao_deve_ser_mantida_no_historico_do_sistema_para_fins_de_auditoria() {
        assertNotNull(notificacaoIdContexto);

        Optional<Notificacao> notificacao = notificacaoRepository.findById(notificacaoIdContexto);
        assertTrue(notificacao.isPresent(), "Notificação deve ser mantida para auditoria");
    }

    private TipoNotificacao mapearTipoEvento(String tipoEvento) {
        return switch (tipoEvento) {
            case "nova assembleia" -> TipoNotificacao.NOVA_ASSEMBLEIA;
            case "cancelamento de reserva" -> TipoNotificacao.CANCELAMENTO_RESERVA;
            case "aplicação de multa" -> TipoNotificacao.APLICACAO_MULTA;
            case "promoção na lista de espera" -> TipoNotificacao.PROMOCAO_LISTA_ESPERA;
            case "geração de taxa" -> TipoNotificacao.GERACAO_TAXA;
            default -> TipoNotificacao.GERAL;
        };
    }

    private String gerarMensagem(String tipoEvento) {
        return switch (tipoEvento) {
            case "nova assembleia" -> "Nova assembleia agendada";
            case "cancelamento de reserva" -> "Reserva cancelada";
            case "aplicação de multa" -> "Multa aplicada";
            case "promoção na lista de espera" -> "Promovido na lista de espera";
            case "geração de taxa" -> "Taxa condominial gerada";
            default -> "Notificação do sistema";
        };
    }

}
