package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase.*;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase.*;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.usecase.*;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase.*;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.usecase.*;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.morador.usecase.*;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.usecase.*;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase.*;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.usecase.*;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase.*;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.security.PasswordEncryptor;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.taxa.usecase.*;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.unidade.usecase.*;
import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.*;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.areacomum.AreaComumService;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.service.ServicoNotificacaoAssembleia;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.service.RateioService;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.AvaliacaoFuncionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.Funcionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServicoId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.StatusFuncionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.StatusOrdemServico;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.AvaliacaoFuncionarioRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.FuncionarioRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.OrdemServicoRepository;
import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.service.RegraVotacao;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.VotoRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.repository.VinculoMoradorRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaCondominial;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.TaxaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.taxa.repository.TaxaCondominialRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.RecursoId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository.RecursoRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEsperaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.StatusReserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.FilaDeEsperaRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.service.PoliticaReserva;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository.NotificacaoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase.*;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage.ArmazenamentoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.HistoricoTitularidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.HistoricoTitularidadeRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;

import org.springframework.test.util.ReflectionTestUtils;

public class DominiumFuncionalidade {

    // ── Repositórios ────────────────────────────────────────────────────────────
    protected UnidadeRepository unidadeRepository;
    protected UsuarioRepository usuarioRepository;
    protected VinculoMoradorRepository vinculoMoradorRepository;
    protected HistoricoTitularidadeRepository historicoTitularidadeRepository;
    protected OrcamentoRepository orcamentoRepository;
    protected DespesaRepository despesaRepository;
    protected MultaRepository multaRepository;
    protected RecursoRepository recursoRepository;
    protected AssembleiaRepository assembleiaRepository;
    protected PautaRepository pautaRepository;
    protected VotoRepository votoRepository;
    protected ReservaRepository reservaRepository;
    protected FilaDeEsperaRepository filaDeEsperaRepository;
    protected OcorrenciaRepository ocorrenciaRepository;
    protected AreaComumRepository areaComumRepository;
    protected FuncionarioRepository funcionarioRepository;
    protected AvaliacaoFuncionarioRepository avaliacaoFuncionarioRepository;
    protected OrdemServicoRepository ordemServicoRepository;
    protected TaxaCondominialRepository taxaCondominialRepository;
    protected DocumentoRepository documentoRepository;
    protected VersaoDocumentoRepository versaoDocumentoRepository;
    protected NotificacaoRepository notificacaoRepository;
    protected NotificacaoService notificacaoService;

    // ── Use Cases: Documentos ────────────────────────────────────────────────────
    protected CadastrarDocumentoUseCase cadastrarDocumentoUseCase;
    protected AtualizarDocumentoUseCase atualizarDocumentoUseCase;
    protected ListarDocumentosUseCase listarDocumentosUseCase;
    protected InativarDocumentoUseCase inativarDocumentoUseCase;
    protected NotificarDocumentosVencendoUseCase notificarDocumentosVencendoUseCase;

    // ── Use Cases: Unidades ──────────────────────────────────────────────────────
    protected CreateUnidadeUseCase createUnidadeUseCase;
    protected GetUnidadeUseCase getUnidadeUseCase;
    protected UpdateUnidadeUseCase updateUnidadeUseCase;
    protected DeleteUnidadeUseCase deleteUnidadeUseCase;
    protected TransferirTitularidadeUseCase transferirTitularidadeUseCase;
    protected GetHistoricoTitularidadeUseCase getHistoricoTitularidadeUseCase;

    // ── Use Cases: Usuários ──────────────────────────────────────────────────────
    protected CreateUsuarioUseCase createUsuarioUseCase;

    // ── Use Cases: Moradores ─────────────────────────────────────────────────────
    protected CreateVinculoMoradorUseCase vincularMoradorUseCase;
    protected UpdateVinculoMoradorUseCase updateVinculoMoradorUseCase;
    protected EndVinculoMoradorUseCase endVinculoMoradorUseCase;
    protected GetVinculosPorUnidadeUseCase getVinculosPorUnidadeUseCase;

    // ── Use Cases: Financeiro ────────────────────────────────────────────────────
    protected CadastrarOrcamentoUseCase cadastrarOrcamentoUseCase;
    protected RegistrarDespesaUseCase registrarDespesaUseCase;
    protected AprovarDespesaExtraordinariaUseCase aprovarDespesaExtraordinariaUseCase;
    protected ConsultarSaldoUseCase consultarSaldoUseCase;
    protected GetOrcamentoPorAnoUseCase getOrcamentoPorAnoUseCase;
    protected ListOrcamentosUseCase listOrcamentosUseCase;
    protected ListDespesasPorOrcamentoUseCase listDespesasPorOrcamentoUseCase;
    protected GetDespesaUseCase getDespesaUseCase;
    protected RateioService rateioService;

    // ── Use Cases: Multas ────────────────────────────────────────────────────────
    protected CreateMultaManualUseCase createMultaManualUseCase;
    protected RegistrarPagamentoMultaUseCase registrarPagamentoMultaUseCase;
    protected UpdateMultaStatusUseCase updateMultaStatusUseCase;
    protected ContestarMultaUseCase contestarMultaUseCase;
    protected ListMultasByUnidadeUseCase listMultasByUnidadeUseCase;

    // ── Use Cases: Recursos ──────────────────────────────────────────────────────
    protected AbrirRecursoUseCase abrirRecursoUseCase;
    protected JulgarRecursoUseCase julgarRecursoUseCase;

    // ── Use Cases: Assembleias ───────────────────────────────────────────────────
    protected CriarAssembleiaUseCase criarAssembleiaUseCase;
    protected EncerrarAssembleiaUseCase encerrarAssembleiaUseCase;
    protected CancelarAssembleiaUseCase cancelarAssembleiaUseCase;
    protected EditarAssembleiaUseCase editarAssembleiaUseCase;

    // ── Use Cases: Pautas e Votações ─────────────────────────────────────────────
    protected AbrirPautaUseCase abrirPautaUseCase;
    protected EncerrarPautaUseCase encerrarPautaUseCase;
    protected ListarPautasUseCase listarPautasUseCase;
    protected VotarUseCase votarUseCase;
    protected ListarVotosUseCase listarVotosUseCase;

    // ── Use Cases: Reservas ──────────────────────────────────────────────────────
    protected CriarReservaUseCase criarReservaUseCase;
    protected CancelarReservaUseCase cancelarReservaUseCase;
    protected AtualizarReservaUseCase atualizarReservaUseCase;
    protected ListarReservaUseCase listarReservaUseCase;
    protected AdicionarNaFilaUseCase adicionarNaFilaUseCase;
    protected ConfirmarReservaPromovidaUseCase confirmarReservaPromovidaUseCase;

    // ── Use Cases: Ocorrências ───────────────────────────────────────────────────
    protected GerenciarOcorrenciaUseCase gerenciarOcorrenciaUseCase;
    protected EncerrarOcorrenciaUseCase encerrarOcorrenciaUseCase;

    // ── Use Cases: Funcionários ──────────────────────────────────────────────────
    protected CadastrarFuncionarioUseCase cadastrarFuncionarioUseCase;
    protected CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    protected EncerrarOrdemServicoUseCase encerrarOrdemServicoUseCase;
    protected RegistrarAvaliacaoUseCase registrarAvaliacaoUseCase;
    protected RenovarContratoUseCase renovarContratoUseCase;
    protected GerarDespesasMensaisUseCase gerarDespesasMensaisUseCase;

    // ── Use Cases: Taxas ─────────────────────────────────────────────────────────
    protected GerarTaxaMensalUseCase gerarTaxaMensalUseCase;
    protected AtualizarValorTaxaUseCase atualizarValorTaxaUseCase;
    protected RegistrarPagamentoTaxaUseCase registrarPagamentoTaxaUseCase;
    protected ConsultarHistoricoTaxasUseCase consultarHistoricoTaxasUseCase;

    // ── Captura de exceção ───────────────────────────────────────────────────────
    protected RuntimeException excecao;

    protected List<Notificacao> listarTodasNotificacoes() {
        return notificacaoRepository.findByUsuarioId(-1L);
    }

    protected long currentId = 1L;

    protected final Map<Long, AreaComum> areaComumDb = new HashMap<>();

    protected AreaComum salvarAreaComum(AreaComum area) {
        if (area.getId() == null)
            area.setId(new AreaComumId(currentId++));
        areaComumDb.put(area.getId().getValor(), area);
        return area;
    }

    public DominiumFuncionalidade() {
        initRepositories();
        initUseCases();
    }

    private void initRepositories() {
        notificacaoRepository = new NotificacaoRepository() {
            private final Map<Long, Notificacao> db = new HashMap<>();

            @Override
            public Notificacao save(Notificacao n) {
                if (n.getId() == null)
                    n.setId(currentId++);
                db.put(n.getId(), n);
                return n;
            }

            @Override
            public Optional<Notificacao> findById(Long id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public List<Notificacao> findByUsuarioId(Long usuarioId) {
                if (usuarioId != null && usuarioId == -1L) {
                    return List.copyOf(db.values());
                }
                return db.values().stream()
                        .filter(n -> n.getUsuarioId().equals(usuarioId))
                        .collect(Collectors.toList());
            }
        };

        notificacaoService = new NotificacaoService() {
            @Override
            public void enviar(Long usuarioId, String mensagem,
                    br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.TipoNotificacao tipo) {
                br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao domainTipo = br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao
                        .valueOf(tipo.name());
                Notificacao n = Notificacao.criar(usuarioId, mensagem, domainTipo);
                notificacaoRepository.save(n);
            }
        };

        unidadeRepository = new UnidadeRepository() {
            private final Map<Long, Unidade> db = new HashMap<>();

            @Override
            public Unidade save(Unidade u) {
                if (u.getId() == null)
                    u.setId(new UnidadeId(currentId++));
                db.put(u.getId().getValor(), u);
                return u;
            }

            @Override
            public Optional<Unidade> findById(UnidadeId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public Optional<Unidade> findByNumeroAndBloco(String numero, String bloco) {
                return db.values().stream()
                        .filter(u -> u.getNumero().equals(numero) && u.getBloco() != null && u.getBloco().equals(bloco))
                        .findFirst();
            }

            @Override
            public List<Unidade> findAll() {
                return List.copyOf(db.values());
            }

            @Override
            public void deleteById(UnidadeId id) {
                db.remove(id.getValor());
            }
        };

        usuarioRepository = new UsuarioRepository() {
            private final Map<Long, Usuario> db = new HashMap<>();

            @Override
            public Usuario save(Usuario u) {
                if (u.getId() == null)
                    u.setId(currentId++);
                db.put(u.getId(), u);
                return u;
            }

            @Override
            public Optional<Usuario> findById(Long id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public Optional<Usuario> findByEmail(String email) {
                return db.values().stream().filter(u -> u.getEmail() != null && u.getEmail().equals(email)).findFirst();
            }

            @Override
            public List<Usuario> findAll() {
                return List.copyOf(db.values());
            }

            @Override
            public void deleteById(Long id) {
                db.remove(id);
            }
        };

        vinculoMoradorRepository = new VinculoMoradorRepository() {
            private final Map<Long, VinculoMorador> db = new HashMap<>();

            @Override
            public VinculoMorador save(VinculoMorador v) {
                if (v.getId() == null)
                    v.setId(currentId++);
                db.put(v.getId(), v);
                return v;
            }

            @Override
            public Optional<VinculoMorador> findById(Long id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public List<VinculoMorador> findByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status) {
                return db.values().stream()
                        .filter(v -> v.getUnidade().getId().getValor().equals(unidadeId) && v.getStatus() == status)
                        .collect(Collectors.toList());
            }

            @Override
            public List<VinculoMorador> findByUsuarioIdAndStatus(Long usuarioId, StatusVinculo status) {
                return db.values().stream()
                        .filter(v -> v.getUsuario() != null && v.getUsuario().getId().equals(usuarioId)
                                && v.getStatus() == status)
                        .collect(Collectors.toList());
            }

            @Override
            public List<VinculoMorador> findByUsuarioAndUnidade(Long usuarioId, Long unidadeId) {
                return db.values().stream().filter(v -> v.getUsuario() != null
                        && v.getUsuario().getId().equals(usuarioId)
                        && v.getUnidade().getId().getValor().equals(unidadeId)).collect(Collectors.toList());
            }

            @Override
            public long countByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status) {
                return findByUnidadeIdAndStatus(unidadeId, status).size();
            }

            @Override
            public void deleteById(Long id) {
                db.remove(id);
            }
        };

        historicoTitularidadeRepository = new HistoricoTitularidadeRepository() {
            private final Map<Long, HistoricoTitularidade> db = new HashMap<>();

            @Override
            public HistoricoTitularidade save(HistoricoTitularidade h) {
                if (h.getId() == null)
                    h.setId(currentId++);
                db.put(h.getId(), h);
                return h;
            }

            @Override
            public List<HistoricoTitularidade> findByUnidadeId(UnidadeId unidadeId) {
                return db.values().stream().filter(h -> h.getUnidadeId().equals(unidadeId))
                        .collect(Collectors.toList());
            }
        };

        orcamentoRepository = new OrcamentoRepository() {
            private final Map<Long, Orcamento> db = new HashMap<>();

            @Override
            public Orcamento save(Orcamento o) {
                if (o.getId() == null)
                    o.setId(currentId++);
                db.put(o.getId(), o);
                return o;
            }

            @Override
            public Optional<Orcamento> findById(Long id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public Optional<Orcamento> findByAno(Integer ano) {
                return db.values().stream().filter(o -> o.getAno().equals(ano)).findFirst();
            }

            @Override
            public List<Orcamento> findAll() {
                return List.copyOf(db.values());
            }
        };

        despesaRepository = new DespesaRepository() {
            private final Map<Long, Despesa> db = new HashMap<>();

            @Override
            public Despesa save(Despesa d) {
                if (d.getId() == null)
                    d.setId(currentId++);
                db.put(d.getId(), d);
                return d;
            }

            @Override
            public Optional<Despesa> findById(Long id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public List<Despesa> findByOrcamentoId(Long orcamentoId) {
                return db.values().stream().filter(d -> d.getOrcamentoId().equals(orcamentoId))
                        .collect(Collectors.toList());
            }

            @Override
            public List<Despesa> findAll() {
                return List.copyOf(db.values());
            }
        };

        multaRepository = new MultaRepository() {
            private final Map<Long, Multa> db = new HashMap<>();

            @Override
            public Multa save(Multa m) {
                if (m.getId() == null)
                    m.setId(new MultaId(currentId++));
                db.put(m.getId().getValor(), m);
                return m;
            }

            @Override
            public Optional<Multa> findById(MultaId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Multa> findByUnidadeId(UnidadeId unidadeId) {
                return db.values().stream().filter(m -> m.getUnidade().getId().equals(unidadeId))
                        .collect(Collectors.toList());
            }

            @Override
            public List<Multa> findByUnidadeIdAndStatus(UnidadeId unidadeId, StatusMulta status) {
                return db.values().stream()
                        .filter(m -> m.getUnidade().getId().equals(unidadeId) && m.getStatus() == status)
                        .collect(Collectors.toList());
            }

            @Override
            public List<Multa> findByOcorrenciaId(Long ocorrenciaId) {
                return db.values().stream()
                        .filter(m -> m.getOcorrenciaId() != null && m.getOcorrenciaId().equals(ocorrenciaId))
                        .collect(Collectors.toList());
            }

            @Override
            public long countByUnidadeIdAndDescricao(UnidadeId unidadeId, String descricao) {
                return db.values().stream()
                        .filter(m -> m.getUnidade().getId().equals(unidadeId) && m.getDescricao().equals(descricao))
                        .count();
            }

            @Override
            public List<Multa> findAll() {
                return List.copyOf(db.values());
            }

            @Override
            public void deleteById(MultaId id) {
                db.remove(id.getValor());
            }
        };

        recursoRepository = new RecursoRepository() {
            private final Map<UUID, Recurso> db = new HashMap<>();

            @Override
            public void salvar(Recurso r) {
                db.put(r.getId().getValue(), r);
            }

            @Override
            public Optional<Recurso> buscarPorId(RecursoId id) {
                return Optional.ofNullable(db.get(id.getValue()));
            }

            @Override
            public void atualizar(Recurso r) {
                salvar(r);
            }
        };

        assembleiaRepository = new AssembleiaRepository() {
            private final Map<Long, Assembleia> db = new HashMap<>();

            @Override
            public Assembleia save(Assembleia a) {
                if (a.getId() == null)
                    a.setId(new AssembleiaId(currentId++));
                db.put(a.getId().getValor(), a);
                return a;
            }

            @Override
            public Optional<Assembleia> findById(AssembleiaId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Assembleia> findAll() {
                return List.copyOf(db.values());
            }
        };

        pautaRepository = new PautaRepository() {
            private final Map<Long, Pauta> db = new HashMap<>();

            @Override
            public Pauta save(Pauta p) {
                if (p.getId() == null)
                    p.setId(new PautaId(currentId++));
                db.put(p.getId().getValor(), p);
                return p;
            }

            @Override
            public Optional<Pauta> findById(PautaId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Pauta> buscarAbertas() {
                return List.copyOf(db.values());
            }
        };

        votoRepository = new VotoRepository() {
            private final Map<Long, Voto> db = new HashMap<>();

            @Override
            public Voto save(Voto v) {
                if (v.getId() == null)
                    v.setId(new VotoId(currentId++));
                db.put(v.getId().getValor(), v);
                return v;
            }

            @Override
            public Optional<Voto> findById(VotoId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Voto> buscarPorPauta(PautaId pautaId) {
                return db.values().stream().filter(v -> v.getPautaId().equals(pautaId))
                        .collect(Collectors.toList());
            }

            @Override
            public boolean findByPautaAndUnidade(PautaId pautaId, UnidadeId unidadeId) {
                return db.values().stream().anyMatch(v -> v.getPautaId().equals(pautaId)
                        && v.getUnidadeId().equals(unidadeId));
            }
        };

        reservaRepository = new ReservaRepository() {
            private final Map<String, Reserva> db = new HashMap<>();

            @Override
            public Reserva save(Reserva r) {
                if (r.getId() == null)
                    r.setId(ReservaId.novo());
                db.put(r.getId().getValor(), r);
                return r;
            }

            @Override
            public Optional<Reserva> findById(ReservaId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Reserva> buscarPorUsuario(UsuarioId usuarioId) {
                return db.values().stream()
                        .filter(r -> r.getUsuarioId() != null && r.getUsuarioId().equals(usuarioId))
                        .collect(Collectors.toList());
            }

            @Override
            public List<Reserva> buscarAtivasPorPeriodo(AreaComumId areaId, LocalDate data, LocalTime inicio,
                    LocalTime fim) {
                return db.values().stream()
                        .filter(r -> r.getAreaComumId().equals(areaId)
                                && r.getDataReserva().equals(data)
                                && r.getHoraInicio().isBefore(fim)
                                && r.getHoraFim().isAfter(inicio)
                                && (r.getStatus() == StatusReserva.ATIVA
                                        || r.getStatus() == StatusReserva.AGUARDANDO_CONFIRMACAO))
                        .collect(Collectors.toList());
            }
        };

        filaDeEsperaRepository = new FilaDeEsperaRepository() {
            private final Map<String, FilaDeEspera> db = new HashMap<>();

            @Override
            public FilaDeEspera salvar(FilaDeEspera f) {
                if (f.getId() == null || f.getId().getValor() == null) {
                    f.setId(new FilaDeEsperaId(UUID.randomUUID().toString()));
                }
                db.put(f.getId().getValor(), f);
                return f;
            }

            @Override
            public Optional<FilaDeEspera> buscarPorId(String id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public List<FilaDeEspera> listarPorArea(AreaComumId areaId) {
                return db.values().stream()
                        .filter(f -> f.getAreaComumId().equals(areaId))
                        .sorted(Comparator.comparing(FilaDeEspera::getDataCadastro))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<FilaDeEspera> buscarProximoNaFila(AreaComumId areaId, LocalDate data, LocalTime inicio,
                    LocalTime fim) {
                return db.values().stream()
                        .filter(f -> f.getAreaComumId().equals(areaId) && f.getDataDesejada().equals(data)
                                && f.getStatus() == FilaDeEspera.StatusFila.AGUARDANDO)
                        .sorted(Comparator.comparing(FilaDeEspera::getDataCadastro))
                        .findFirst();
            }
        };

        ocorrenciaRepository = new OcorrenciaRepository() {
            private final Map<Long, Ocorrencia> db = new HashMap<>();

            @Override
            public Ocorrencia salvar(Ocorrencia o) {
                if (o.getId() == null)
                    o.setId(currentId++);
                db.put(o.getId(), o);
                return o;
            }

            @Override
            public Optional<Ocorrencia> buscarPorId(Long id) {
                return Optional.ofNullable(db.get(id));
            }

            @Override
            public List<Ocorrencia> listarTodas() {
                return List.copyOf(db.values());
            }
        };

        areaComumRepository = new AreaComumRepository() {
            @Override
            public Optional<AreaComum> findById(AreaComumId id) {
                return Optional.ofNullable(areaComumDb.get(id.getValor()));
            }

            @Override
            public AreaComum save(AreaComum area) {
                areaComumDb.put(area.getId().getValor(), area);
                return area;
            }
        };

        funcionarioRepository = new FuncionarioRepository() {
            private final Map<Long, Funcionario> db = new HashMap<>();

            @Override
            public Funcionario save(Funcionario f) {
                if (f.getId() == null || f.getId().getValor() == null) {
                    f.setId(new FuncionarioId(currentId++));
                }
                db.put(f.getId().getValor(), f);
                return f;
            }

            @Override
            public Optional<Funcionario> findById(FuncionarioId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Funcionario> findAll() {
                return List.copyOf(db.values());
            }

            @Override
            public List<Funcionario> findByStatus(StatusFuncionario status) {
                return db.values().stream().filter(f -> f.getStatus() == status).collect(Collectors.toList());
            }
        };

        avaliacaoFuncionarioRepository = new AvaliacaoFuncionarioRepository() {
            private final Map<Long, AvaliacaoFuncionario> db = new HashMap<>();

            @Override
            public AvaliacaoFuncionario save(AvaliacaoFuncionario a) {
                if (a.getId() == null)
                    a.setId(currentId++);
                db.put(a.getId(), a);
                return a;
            }

            @Override
            public List<AvaliacaoFuncionario> findByFuncionarioId(FuncionarioId funcionarioId) {
                return db.values().stream()
                        .filter(a -> a.getFuncionarioId().equals(funcionarioId))
                        .collect(Collectors.toList());
            }

            @Override
            public long contarNegativasRecentes(FuncionarioId funcionarioId, int limite) {
                return db.values().stream()
                        .filter(a -> a.getFuncionarioId().equals(funcionarioId) && !a.isPositiva())
                        .count();
            }
        };

        ordemServicoRepository = new OrdemServicoRepository() {
            private final Map<Long, OrdemServico> db = new HashMap<>();

            @Override
            public OrdemServico save(OrdemServico os) {
                if (os.getId() == null || os.getId().getValor() == null) {
                    os.setId(new OrdemServicoId(currentId++));
                }
                db.put(os.getId().getValor(), os);
                return os;
            }

            @Override
            public Optional<OrdemServico> findById(OrdemServicoId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<OrdemServico> findByFuncionarioId(FuncionarioId funcionarioId) {
                return db.values().stream()
                        .filter(os -> os.getFuncionarioId().equals(funcionarioId))
                        .collect(Collectors.toList());
            }

            @Override
            public boolean existeAtivaParaFuncionario(FuncionarioId funcionarioId) {
                return db.values().stream()
                        .anyMatch(os -> os.getFuncionarioId().equals(funcionarioId)
                                && os.getStatus() == StatusOrdemServico.ABERTA);
            }
        };

        documentoRepository = new DocumentoRepository() {
            private final Map<String, Documento> db = new HashMap<>();

            @Override
            public void save(Documento d) {
                db.put(d.getId().getValor(), d);
            }

            @Override
            public Optional<Documento> findById(DocumentoId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<Documento> findAll() {
                return List.copyOf(db.values());
            }

            @Override
            public List<Documento> findAtivos() {
                return db.values().stream().filter(Documento::isAtivo).collect(Collectors.toList());
            }
        };

        versaoDocumentoRepository = new VersaoDocumentoRepository() {
            private final Map<Long, VersaoDocumento> db = new HashMap<>();

            @Override
            public void save(VersaoDocumento v) {
                if (v.getId() == null)
                    v.setId(currentId++);
                db.put(v.getId(), v);
            }

            @Override
            public Optional<VersaoDocumento> findUltimaVersao(DocumentoId documentoId) {
                return db.values().stream()
                        .filter(v -> v.getDocumentoId().equals(documentoId))
                        .max(Comparator.comparingInt(VersaoDocumento::getVersao));
            }

            @Override
            public List<VersaoDocumento> findHistorico(DocumentoId documentoId) {
                return db.values().stream()
                        .filter(v -> v.getDocumentoId().equals(documentoId))
                        .sorted(Comparator.comparingInt(VersaoDocumento::getVersao))
                        .collect(Collectors.toList());
            }
        };

        taxaCondominialRepository = new TaxaCondominialRepository() {
            private final Map<Long, TaxaCondominial> db = new HashMap<>();

            @Override
            public void salvar(TaxaCondominial taxa) {
                if (taxa.getId() == null) {
                    taxa.setId(new TaxaId(currentId++));
                }
                db.put(taxa.getId().getValor(), taxa);
            }

            @Override
            public void atualizar(TaxaCondominial taxa) {
                salvar(taxa);
            }

            @Override
            public Optional<TaxaCondominial> buscarPorId(TaxaId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public List<TaxaCondominial> listarPorUnidade(UnidadeId unidadeId) {
                return db.values().stream()
                        .filter(t -> t.getUnidadeId().equals(unidadeId))
                        .collect(Collectors.toList());
            }

            @Override
            public List<TaxaCondominial> listarTodas() {
                return List.copyOf(db.values());
            }

            @Override
            public boolean existeTaxaAtrasadaPorUnidade(UnidadeId unidadeId) {
                return db.values().stream()
                        .anyMatch(t -> t.getUnidadeId().equals(unidadeId) && t.getStatus().name().equals("ATRASADA"));
            }
        };
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Use Cases
    // ────────────────────────────────────────────────────────────────────────────
    private void initUseCases() {

        // ── Segurança ────────────────────────────────────────────────────────────
        PasswordEncryptor passwordEncryptor = new PasswordEncryptor() {
            @Override
            public String encode(String raw) {
                return raw;
            }

            @Override
            public boolean matches(String raw, String encoded) {
                return raw.equals(encoded);
            }
        };

        // ── Usuários ─────────────────────────────────────────────────────────────
        createUsuarioUseCase = new CreateUsuarioUseCase(usuarioRepository, passwordEncryptor);

        // ── Unidades ─────────────────────────────────────────────────────────────
        createUnidadeUseCase = new CreateUnidadeUseCase(unidadeRepository, usuarioRepository);
        getUnidadeUseCase = new GetUnidadeUseCase(unidadeRepository);
        updateUnidadeUseCase = new UpdateUnidadeUseCase(unidadeRepository, usuarioRepository);
        deleteUnidadeUseCase = new DeleteUnidadeUseCase(unidadeRepository);
        transferirTitularidadeUseCase = new TransferirTitularidadeUseCase(unidadeRepository, usuarioRepository,
                historicoTitularidadeRepository);
        getHistoricoTitularidadeUseCase = new GetHistoricoTitularidadeUseCase(historicoTitularidadeRepository);

        // ── Moradores ────────────────────────────────────────────────────────────
        vincularMoradorUseCase = new CreateVinculoMoradorUseCase(vinculoMoradorRepository, unidadeRepository,
                usuarioRepository, createUsuarioUseCase);
        ReflectionTestUtils.setField(vincularMoradorUseCase, "maxMoradores", 5);
        updateVinculoMoradorUseCase = new UpdateVinculoMoradorUseCase(vinculoMoradorRepository);
        endVinculoMoradorUseCase = new EndVinculoMoradorUseCase(vinculoMoradorRepository, usuarioRepository);
        getVinculosPorUnidadeUseCase = new GetVinculosPorUnidadeUseCase(vinculoMoradorRepository);

        // ── Financeiro ───────────────────────────────────────────────────────────
        rateioService = new RateioService(unidadeRepository);
        cadastrarOrcamentoUseCase = new CadastrarOrcamentoUseCase(orcamentoRepository);
        registrarDespesaUseCase = new RegistrarDespesaUseCase(despesaRepository, orcamentoRepository, rateioService);
        aprovarDespesaExtraordinariaUseCase = new AprovarDespesaExtraordinariaUseCase(despesaRepository,
                orcamentoRepository, rateioService);
        consultarSaldoUseCase = new ConsultarSaldoUseCase(orcamentoRepository);
        getOrcamentoPorAnoUseCase = new GetOrcamentoPorAnoUseCase(orcamentoRepository);
        listOrcamentosUseCase = new ListOrcamentosUseCase(orcamentoRepository);
        listDespesasPorOrcamentoUseCase = new ListDespesasPorOrcamentoUseCase(despesaRepository, orcamentoRepository);
        getDespesaUseCase = new GetDespesaUseCase(despesaRepository);

        // ── Multas ───────────────────────────────────────────────────────────────
        createMultaManualUseCase = new CreateMultaManualUseCase(multaRepository, unidadeRepository);
        registrarPagamentoMultaUseCase = new RegistrarPagamentoMultaUseCase(multaRepository);
        updateMultaStatusUseCase = new UpdateMultaStatusUseCase(multaRepository);
        contestarMultaUseCase = new ContestarMultaUseCase(multaRepository);
        listMultasByUnidadeUseCase = new ListMultasByUnidadeUseCase(multaRepository, unidadeRepository);

        // ── Recursos ─────────────────────────────────────────────────────────────
        abrirRecursoUseCase = new AbrirRecursoUseCase(recursoRepository, multaRepository);
        julgarRecursoUseCase = new JulgarRecursoUseCase(recursoRepository, multaRepository);

        // ── Documentos ───────────────────────────────────────────────────────────
        ArmazenamentoDocumento armazenamentoDocumento = new ArmazenamentoDocumento() {
            @Override
            public String salvar(String documentoId, int versao, String nomeOriginal, byte[] conteudo) {
                return "uploads/" + documentoId + "/v" + versao + "/" + nomeOriginal;
            }

            @Override
            public byte[] carregar(String caminho) {
                return new byte[0];
            }
        };
        cadastrarDocumentoUseCase = new CadastrarDocumentoUseCase(documentoRepository, versaoDocumentoRepository,
                armazenamentoDocumento, usuarioRepository);
        atualizarDocumentoUseCase = new AtualizarDocumentoUseCase(documentoRepository, versaoDocumentoRepository,
                armazenamentoDocumento, usuarioRepository);
        listarDocumentosUseCase = new ListarDocumentosUseCase(documentoRepository);
        inativarDocumentoUseCase = new InativarDocumentoUseCase(documentoRepository, usuarioRepository);
        notificarDocumentosVencendoUseCase = new NotificarDocumentosVencendoUseCase(documentoRepository,
                notificacaoService);

        // ── Assembleias ──────────────────────────────────────────────────────────
        ServicoNotificacaoAssembleia servicoNotificacao = assembleia -> {
            this.notificacaoService.enviar(assembleia.getSindicoId(),
                    "Nova assembleia agendada: " + assembleia.getTitulo(),
                    br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.TipoNotificacao.NOVA_ASSEMBLEIA);
        };
        criarAssembleiaUseCase = new CriarAssembleiaUseCase(assembleiaRepository, usuarioRepository,
                servicoNotificacao);
        encerrarAssembleiaUseCase = new EncerrarAssembleiaUseCase(assembleiaRepository, usuarioRepository);
        cancelarAssembleiaUseCase = new CancelarAssembleiaUseCase(assembleiaRepository, usuarioRepository);
        editarAssembleiaUseCase = new EditarAssembleiaUseCase(assembleiaRepository, usuarioRepository);

        // ── Pautas e Votações ─────────────────────────────────────────────────────
        RegraVotacao regraVotacao = new RegraVotacao(vinculoMoradorRepository, unidadeRepository);
        abrirPautaUseCase = new AbrirPautaUseCase(pautaRepository);
        encerrarPautaUseCase = new EncerrarPautaUseCase(pautaRepository, votoRepository, regraVotacao);
        listarPautasUseCase = new ListarPautasUseCase(pautaRepository);
        votarUseCase = new VotarUseCase(votoRepository, pautaRepository, regraVotacao);
        listarVotosUseCase = new ListarVotosUseCase(votoRepository);

        // ── Reservas ─────────────────────────────────────────────────────────────
        AreaComumService areaComumService = new AreaComumService(areaComumRepository);
        PoliticaReserva politicaReserva = new PoliticaReserva();

        // ── Taxas ───────────────────────────────────────────────────────────────
        gerarTaxaMensalUseCase = new GerarTaxaMensalUseCase(taxaCondominialRepository);
        atualizarValorTaxaUseCase = new AtualizarValorTaxaUseCase(taxaCondominialRepository);
        registrarPagamentoTaxaUseCase = new RegistrarPagamentoTaxaUseCase(taxaCondominialRepository);
        consultarHistoricoTaxasUseCase = new ConsultarHistoricoTaxasUseCase(taxaCondominialRepository);
        criarReservaUseCase = new CriarReservaUseCase(reservaRepository, politicaReserva, areaComumService,
                unidadeRepository);
        cancelarReservaUseCase = new CancelarReservaUseCase(reservaRepository, filaDeEsperaRepository,
                notificacaoService, unidadeRepository, multaRepository);
        atualizarReservaUseCase = new AtualizarReservaUseCase(reservaRepository, politicaReserva, areaComumService);
        listarReservaUseCase = new ListarReservaUseCase(reservaRepository);
        adicionarNaFilaUseCase = new AdicionarNaFilaUseCase(filaDeEsperaRepository, areaComumService);
        confirmarReservaPromovidaUseCase = new ConfirmarReservaPromovidaUseCase(reservaRepository);

        // ── Ocorrências ──────────────────────────────────────────────────────────
        gerenciarOcorrenciaUseCase = new GerenciarOcorrenciaUseCase(ocorrenciaRepository);
        encerrarOcorrenciaUseCase = new EncerrarOcorrenciaUseCase(ocorrenciaRepository, createMultaManualUseCase);

        // ── Funcionários ─────────────────────────────────────────────────────────
        cadastrarFuncionarioUseCase = new CadastrarFuncionarioUseCase(funcionarioRepository, usuarioRepository);
        criarOrdemServicoUseCase = new CriarOrdemServicoUseCase(ordemServicoRepository, funcionarioRepository,
                usuarioRepository);
        encerrarOrdemServicoUseCase = new EncerrarOrdemServicoUseCase(ordemServicoRepository, funcionarioRepository,
                usuarioRepository);
        registrarAvaliacaoUseCase = new RegistrarAvaliacaoUseCase(avaliacaoFuncionarioRepository, funcionarioRepository,
                usuarioRepository);
        renovarContratoUseCase = new RenovarContratoUseCase(funcionarioRepository, avaliacaoFuncionarioRepository,
                usuarioRepository);
        gerarDespesasMensaisUseCase = new GerarDespesasMensaisUseCase(funcionarioRepository, despesaRepository,
                orcamentoRepository);
    }
}
