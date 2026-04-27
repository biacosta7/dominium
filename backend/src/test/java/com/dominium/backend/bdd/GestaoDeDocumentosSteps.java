package com.dominium.backend.bdd;

import com.dominium.backend.application.documento.usecase.*;
import com.dominium.backend.domain.documento.*;
import com.dominium.backend.domain.shared.notification.NotificacaoService;
import com.dominium.backend.domain.shared.notification.TipoNotificacao;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GestaoDeDocumentosSteps extends DominiumFuncionalidade {

    private Long usuarioLogadoId;
    private Documento ultimoDocumentoSalvo;
    private List<Documento> listagemResultado;
    private List<NotificationEntry> notificacoesEnviadas = new ArrayList<>();

    private static class NotificationEntry {
        Long usuarioId;
        String mensagem;
        TipoNotificacao tipo;

        NotificationEntry(Long usuarioId, String mensagem, TipoNotificacao tipo) {
            this.usuarioId = usuarioId;
            this.mensagem = mensagem;
            this.tipo = tipo;
        }
    }

    @Before
    public void setupDocumento() {
        notificacoesEnviadas.clear();
        excecao = null;
        
        NotificacaoService capturingService = (id, msg, tipo) -> notificacoesEnviadas.add(new NotificationEntry(id, msg, tipo));
        notificarDocumentosVencendoUseCase = new NotificarDocumentosVencendoUseCase(documentoRepository, capturingService);
    }

    @Given("o usuário logado é um {string}")
    public void o_usuario_logado_e_um(String tipo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário " + tipo);
        usuario.setEmail(tipo.toLowerCase() + "@teste.com");
        usuario.setTipo(TipoUsuario.valueOf(tipo));
        usuario = usuarioRepository.save(usuario);
        usuarioLogadoId = usuario.getId();
    }

    @When("o síndico cadastra o documento {string} na categoria {string}")
    public void o_sindico_cadastra_o_documento_na_categoria(String nome, String categoria) {
        try {
            ultimoDocumentoSalvo = cadastrarDocumentoUseCase.executar(usuarioLogadoId, nome, CategoriaDocumento.valueOf(categoria), LocalDate.now().plusYears(1), nome + ".pdf", "conteudo".getBytes());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o documento é salvo com status {string}")
    public void o_documento_e_salvo_com_status(String status) {
        assertNotNull(ultimoDocumentoSalvo);
        assertEquals(StatusDocumento.valueOf(status.toUpperCase()), ultimoDocumentoSalvo.getStatus());
    }

    @Then("o documento está vinculado à categoria {string}")
    public void o_documento_esta_vinculado_a_categoria(String categoria) {
        assertEquals(CategoriaDocumento.valueOf(categoria), ultimoDocumentoSalvo.getCategoria());
    }

    @When("o morador tenta cadastrar o documento {string} na categoria {string}")
    public void o_morador_tenta_cadastrar_o_documento_na_categoria(String nome, String categoria) {
        try {
            cadastrarDocumentoUseCase.executar(usuarioLogadoId, nome, CategoriaDocumento.valueOf(categoria), LocalDate.now().plusYears(1), nome + ".pdf", "conteudo".getBytes());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema nega o acesso informando que apenas o síndico pode realizar esta ação")
    public void o_sistema_nega_o_acesso_informando_que_apenas_o_sindico_pode_realizar_esta_acao() {
        assertNotNull(excecao, "Uma exceção deveria ter sido lançada");
        String msg = excecao.getMessage().toLowerCase();
        assertTrue(msg.contains("sindico") || msg.contains("síndico") || msg.contains("acesso negado"), 
            "Mensagem de erro inesperada: " + excecao.getMessage());
    }

    @Given("existe um documento {string} cadastrado")
    public void existe_um_documento_cadastrado(String nome) {
        Usuario sindico = new Usuario();
        sindico.setNome("Sindico Inicial");
        sindico.setTipo(TipoUsuario.SINDICO);
        sindico = usuarioRepository.save(sindico);

        ultimoDocumentoSalvo = cadastrarDocumentoUseCase.executar(sindico.getId(), nome, CategoriaDocumento.ATA, LocalDate.now().plusYears(1), nome + ".pdf", "conteudo".getBytes());
    }

    @When("o síndico atualiza o arquivo do documento {string}")
    public void o_sindico_atualiza_o_arquivo_do_documento(String nome) {
        Documento doc = documentoRepository.findAll().stream()
                .filter(d -> d.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
        try {
            atualizarDocumentoUseCase.executar(usuarioLogadoId, doc.getId().getValor(), nome + "_v2.pdf", "novo conteudo".getBytes());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema incrementa o número da versão do documento")
    public void o_sistema_incrementa_o_numero_da_versao_do_documento() {
        int versoes = versaoDocumentoRepository.contarVersoes(ultimoDocumentoSalvo.getId());
        assertTrue(versoes > 1);
    }

    @Then("o histórico mantém a versão anterior armazenada")
    public void o_historico_mantem_a_versao_anterior_armazenada() {
        List<VersaoDocumento> versoes = versaoDocumentoRepository.findByDocumentoId(ultimoDocumentoSalvo.getId());
        assertTrue(versoes.stream().anyMatch(v -> v.getNumeroVersao() == 1));
        assertTrue(versoes.stream().anyMatch(v -> v.getNumeroVersao() == 2));
    }

    @Given("existem os documentos:")
    public void existem_os_documentos(DataTable dataTable) {
        Usuario sindico = new Usuario();
        sindico.setNome("Sindico");
        sindico.setTipo(TipoUsuario.SINDICO);
        sindico = usuarioRepository.save(sindico);

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String nome = row.get("Nome");
            String categoria = row.get("Categoria");
            String status = row.get("Status");

            Documento doc = Documento.criar(DocumentoId.novo(), nome, CategoriaDocumento.valueOf(categoria), LocalDate.now().plusYears(1), sindico.getId());
            if ("INATIVO".equals(status)) {
                doc.inativar();
            }
            documentoRepository.save(doc);
            versaoDocumentoRepository.save(VersaoDocumento.criar(doc.getId(), 1, nome + ".pdf", "caminho", sindico.getId()));
        }
    }

    @When("o morador solicita a listagem de documentos")
    public void o_morador_solicita_a_listagem_de_documentos() {
        listagemResultado = listarDocumentosUseCase.executar(false);
    }

    @Then("a lista deve conter apenas os documentos {string}")
    public void a_lista_deve_conter_apenas_os_documentos(String status) {
        String statusNormalizado = status.toUpperCase();
        if (statusNormalizado.endsWith("S")) {
            statusNormalizado = statusNormalizado.substring(0, statusNormalizado.length() - 1);
        }
        final String finalStatus = statusNormalizado;
        assertTrue(listagemResultado.stream().allMatch(d -> d.getStatus().name().equals(finalStatus)));
    }

    @Then("o documento {string} não deve aparecer na lista")
    public void o_documento_nao_deve_aparecer_na_lista(String nome) {
        assertTrue(listagemResultado.stream().noneMatch(d -> d.getNome().equals(nome)));
    }

    @Given("existe um documento {string} cadastrado com status {string}")
    public void existe_um_documento_cadastrado_com_status(String nome, String status) {
        Usuario sindico = new Usuario();
        sindico.setNome("Sindico");
        sindico.setTipo(TipoUsuario.SINDICO);
        sindico = usuarioRepository.save(sindico);

        Documento doc = Documento.criar(DocumentoId.novo(), nome, CategoriaDocumento.ATA, LocalDate.now().plusYears(1), sindico.getId());
        if ("INATIVO".equals(status)) {
            doc.inativar();
        }
        documentoRepository.save(doc);
        ultimoDocumentoSalvo = doc;
    }

    @When("o síndico solicita a inativação do documento {string}")
    public void o_sindico_solicita_a_inativacao_do_documento(String nome) {
        Documento doc = documentoRepository.findAll().stream()
                .filter(d -> d.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
        try {
            inativarDocumentoUseCase.executar(usuarioLogadoId, doc.getId().getValor());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o status do documento muda para {string}")
    public void o_status_do_documento_muda_para(String status) {
        Documento doc = documentoRepository.findById(ultimoDocumentoSalvo.getId()).orElseThrow();
        assertEquals(StatusDocumento.valueOf(status.toUpperCase()), doc.getStatus());
    }

    @Then("o documento não aparece na listagem padrão")
    public void o_documento_nao_aparece_na_listagem_padrao() {
        List<Documento> ativos = listarDocumentosUseCase.executar(false);
        assertTrue(ativos.stream().noneMatch(d -> d.getId().equals(ultimoDocumentoSalvo.getId())));
    }

    @Given("existe um documento {string} com status {string}")
    public void existe_um_documento_com_status(String nome, String status) {
        existe_um_documento_cadastrado_com_status(nome, status);
    }

    @When("o síndico solicita a consulta do histórico de documentos incluindo inativos")
    public void o_sindico_solicita_a_consulta_do_historico_de_documentos_incluindo_inativos() {
        listagemResultado = listarDocumentosUseCase.executar(true);
    }

    @Then("o documento {string} deve ser exibido no histórico")
    public void o_documento_deve_ser_exibido_no_historico(String nome) {
        assertTrue(listagemResultado.stream().anyMatch(d -> d.getNome().equals(nome)));
    }

    @Given("existe um documento {string} com validade para {string}")
    public void existe_um_documento_com_validade_para(String nome, String quando) {
        Usuario sindico = new Usuario();
        sindico.setNome("Sindico");
        sindico.setTipo(TipoUsuario.SINDICO);
        sindico = usuarioRepository.save(sindico);

        LocalDate validade;
        if (quando.contains("30 dias")) {
            validade = LocalDate.now().plusDays(30);
        } else {
            validade = LocalDate.now().plusYears(1);
        }

        Documento doc = Documento.criar(DocumentoId.novo(), nome, CategoriaDocumento.OUTRO, validade, sindico.getId());
        documentoRepository.save(doc);
        ultimoDocumentoSalvo = doc;
    }

    @When("o sistema verifica documentos com prazo de validade próximo")
    public void o_sistema_verifica_documentos_com_prazo_de_validade_proximo() {
        notificarDocumentosVencendoUseCase.executar();
    }

    @Then("uma notificação deve ser enviada para o síndico sobre o vencimento do {string}")
    public void uma_notificacao_deve_ser_enviada_para_o_sindico_sobre_o_vencimento_do(String nome) {
        assertTrue(notificacoesEnviadas.stream()
                .anyMatch(n -> n.mensagem.contains(nome) && n.mensagem.contains("vence em")));
    }
}
