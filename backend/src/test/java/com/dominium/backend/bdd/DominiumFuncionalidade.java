package com.dominium.backend.bdd;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.dominium.backend.application.assembleia.usecase.*;
import com.dominium.backend.application.financeiro.usecase.*;
import com.dominium.backend.application.funcionario.usecase.*;
import com.dominium.backend.application.governanca.pauta.usecase.*;
import com.dominium.backend.application.governanca.voto.usecase.*;
import com.dominium.backend.application.morador.usecase.*;
import com.dominium.backend.application.multa.usecase.*;
import com.dominium.backend.application.ocorrencia.usecase.*;
import com.dominium.backend.application.recurso.usecase.*;
import com.dominium.backend.application.reservas.usecase.*;
import com.dominium.backend.application.security.PasswordEncryptor;
import com.dominium.backend.application.unidade.usecase.*;
import com.dominium.backend.application.usuario.usecase.*;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.areacomum.AreaComumRepository;
import com.dominium.backend.domain.areacomum.AreaComumService;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import com.dominium.backend.domain.assembleia.service.ServicoNotificacaoAssembleia;
import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.DespesaRepository;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import com.dominium.backend.domain.financeiro.service.RateioService;
import com.dominium.backend.domain.funcionario.AvaliacaoFuncionario;
import com.dominium.backend.domain.funcionario.Funcionario;
import com.dominium.backend.domain.funcionario.FuncionarioId;
import com.dominium.backend.domain.funcionario.OrdemServico;
import com.dominium.backend.domain.funcionario.OrdemServicoId;
import com.dominium.backend.domain.funcionario.StatusFuncionario;
import com.dominium.backend.domain.funcionario.StatusOrdemServico;
import com.dominium.backend.domain.funcionario.repository.AvaliacaoFuncionarioRepository;
import com.dominium.backend.domain.funcionario.repository.FuncionarioRepository;
import com.dominium.backend.domain.funcionario.repository.OrdemServicoRepository;
import com.dominium.backend.domain.governanca.RegraVotacao;
import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.pauta.PautaRepository;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.governanca.voto.VotoId;
import com.dominium.backend.domain.governanca.voto.VotoRepository;
import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;
import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.MultaId;
import com.dominium.backend.domain.multa.StatusMulta;
import com.dominium.backend.domain.multa.repository.MultaRepository;
import com.dominium.backend.domain.ocorrencia.Ocorrencia;
import com.dominium.backend.domain.ocorrencia.repository.OcorrenciaRepository;
import com.dominium.backend.domain.recurso.Recurso;
import com.dominium.backend.domain.recurso.RecursoId;
import com.dominium.backend.domain.recurso.repository.RecursoRepository;
import com.dominium.backend.domain.reservas.FilaDeEspera;
import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.ReservaId;
import com.dominium.backend.domain.reservas.StatusReserva;
import com.dominium.backend.domain.reservas.repository.FilaDeEsperaRepository;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;
import com.dominium.backend.domain.reservas.service.PoliticaReserva;
import com.dominium.backend.domain.shared.notification.NotificacaoService;
import com.dominium.backend.domain.unidade.HistoricoTitularidade;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.unidade.repository.HistoricoTitularidadeRepository;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.UsuarioId;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;

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
    protected CreateMultaAutomaticaUseCase createMultaAutomaticaUseCase;
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

    // ── Captura de exceção ───────────────────────────────────────────────────────
    protected RuntimeException excecao;

    protected long currentId = 1L;

    public DominiumFuncionalidade() {
        initRepositories();
        initUseCases();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Repositórios em Memória
    // ────────────────────────────────────────────────────────────────────────────
    private void initRepositories() {

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
                return db.values().stream().filter(u -> u.getNumero().equals(numero) && u.getBloco().equals(bloco))
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
                        .filter(v -> v.getUsuario().getId().equals(usuarioId) && v.getStatus() == status)
                        .collect(Collectors.toList());
            }

            @Override
            public List<VinculoMorador> findByUsuarioAndUnidade(Long usuarioId, Long unidadeId) {
                return db.values().stream().filter(v -> v.getUsuario().getId().equals(usuarioId)
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
                db.put(f.getId(), f);
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
                        .filter(f -> f.getAreaComumId().equals(areaId) && f.getDataDesejada().equals(data))
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
            private final Map<Long, AreaComum> db = new HashMap<>();

            @Override
            public Optional<AreaComum> findById(AreaComumId id) {
                return Optional.ofNullable(db.get(id.getValor()));
            }

            @Override
            public AreaComum save(AreaComum a) {
                if (a.getId() == null)
                    a.setId(new AreaComumId(currentId++));
                db.put(a.getId().getValor(), a);
                return a;
            }
        };

        funcionarioRepository = new FuncionarioRepository() {
            private final Map<String, Funcionario> db = new HashMap<>();

            @Override
            public Funcionario save(Funcionario f) {
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
            private final Map<String, OrdemServico> db = new HashMap<>();

            @Override
            public OrdemServico save(OrdemServico os) {
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
        createMultaAutomaticaUseCase = new CreateMultaAutomaticaUseCase(multaRepository, ocorrenciaRepository,
                unidadeRepository);
        registrarPagamentoMultaUseCase = new RegistrarPagamentoMultaUseCase(multaRepository);
        updateMultaStatusUseCase = new UpdateMultaStatusUseCase(multaRepository);
        contestarMultaUseCase = new ContestarMultaUseCase(multaRepository);
        listMultasByUnidadeUseCase = new ListMultasByUnidadeUseCase(multaRepository, unidadeRepository);

        // ── Recursos ─────────────────────────────────────────────────────────────
        abrirRecursoUseCase = new AbrirRecursoUseCase(recursoRepository, multaRepository);
        julgarRecursoUseCase = new JulgarRecursoUseCase(recursoRepository, multaRepository);

        // ── Notificações ────────────────────────────────────────────────────────
        NotificacaoService notificacaoService = (id, msg) -> {
        }; // no-op em testes

        // ── Assembleias ──────────────────────────────────────────────────────────
        ServicoNotificacaoAssembleia servicoNotificacao = assembleia -> {
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
        criarReservaUseCase = new CriarReservaUseCase(reservaRepository, politicaReserva, areaComumService);
        cancelarReservaUseCase = new CancelarReservaUseCase(reservaRepository, filaDeEsperaRepository,
                notificacaoService, unidadeRepository, usuarioRepository);
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
