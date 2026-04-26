package com.dominium.backend.bdd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dominium.backend.application.financeiro.usecase.AprovarDespesaExtraordinariaUseCase;
import com.dominium.backend.application.financeiro.usecase.CadastrarOrcamentoUseCase;
import com.dominium.backend.application.financeiro.usecase.ConsultarSaldoUseCase;
import com.dominium.backend.application.financeiro.usecase.GetDespesaUseCase;
import com.dominium.backend.application.financeiro.usecase.GetOrcamentoPorAnoUseCase;
import com.dominium.backend.application.financeiro.usecase.ListDespesasPorOrcamentoUseCase;
import com.dominium.backend.application.financeiro.usecase.ListOrcamentosUseCase;
import com.dominium.backend.application.financeiro.usecase.RegistrarDespesaUseCase;
import com.dominium.backend.application.morador.usecase.CreateVinculoMoradorUseCase;
import com.dominium.backend.application.morador.usecase.EndVinculoMoradorUseCase;
import com.dominium.backend.application.morador.usecase.GetVinculosPorUnidadeUseCase;
import com.dominium.backend.application.morador.usecase.UpdateVinculoMoradorUseCase;
import com.dominium.backend.application.unidade.usecase.CreateUnidadeUseCase;
import com.dominium.backend.application.unidade.usecase.DeleteUnidadeUseCase;
import com.dominium.backend.application.unidade.usecase.GetHistoricoTitularidadeUseCase;
import com.dominium.backend.application.unidade.usecase.GetUnidadeUseCase;
import com.dominium.backend.application.unidade.usecase.TransferirTitularidadeUseCase;
import com.dominium.backend.application.unidade.usecase.UpdateUnidadeUseCase;
import com.dominium.backend.application.usuario.usecase.CreateUsuarioUseCase;
import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.DespesaRepository;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import com.dominium.backend.domain.financeiro.service.RateioService;
import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import com.dominium.backend.domain.unidade.repository.HistoricoTitularidadeRepository;
import com.dominium.backend.domain.unidade.HistoricoTitularidade;
import org.springframework.test.util.ReflectionTestUtils;

public class DominiumFuncionalidade {

    protected UnidadeRepository unidadeRepository;
    protected UsuarioRepository usuarioRepository;
    protected VinculoMoradorRepository vinculoMoradorRepository;
    protected HistoricoTitularidadeRepository historicoTitularidadeRepository;
    protected OrcamentoRepository orcamentoRepository;
    protected DespesaRepository despesaRepository;

    // Unidades
    protected CreateUnidadeUseCase createUnidadeUseCase;
    protected GetUnidadeUseCase getUnidadeUseCase;
    protected UpdateUnidadeUseCase updateUnidadeUseCase;
    protected DeleteUnidadeUseCase deleteUnidadeUseCase;
    protected TransferirTitularidadeUseCase transferirTitularidadeUseCase;
    protected GetHistoricoTitularidadeUseCase getHistoricoTitularidadeUseCase;

    // Usuarios
    protected CreateUsuarioUseCase createUsuarioUseCase;

    // Moradores
    protected CreateVinculoMoradorUseCase createVinculoMoradorUseCase;
    protected UpdateVinculoMoradorUseCase updateVinculoMoradorUseCase;
    protected EndVinculoMoradorUseCase endVinculoMoradorUseCase;
    protected GetVinculosPorUnidadeUseCase getVinculosPorUnidadeUseCase;

    // Financeiro
    protected CadastrarOrcamentoUseCase cadastrarOrcamentoUseCase;
    protected RegistrarDespesaUseCase registrarDespesaUseCase;
    protected AprovarDespesaExtraordinariaUseCase aprovarDespesaExtraordinariaUseCase;
    protected ConsultarSaldoUseCase consultarSaldoUseCase;
    protected GetOrcamentoPorAnoUseCase getOrcamentoPorAnoUseCase;
    protected ListOrcamentosUseCase listOrcamentosUseCase;
    protected ListDespesasPorOrcamentoUseCase listDespesasPorOrcamentoUseCase;
    protected GetDespesaUseCase getDespesaUseCase;
    protected RateioService rateioService;

    // Exception
    protected RuntimeException excecao;

    private long currentId = 1L;

    public DominiumFuncionalidade() {
        initRepositories();

        // Unidades
        createUnidadeUseCase = new CreateUnidadeUseCase(unidadeRepository, usuarioRepository);
        getUnidadeUseCase = new GetUnidadeUseCase(unidadeRepository);
        updateUnidadeUseCase = new UpdateUnidadeUseCase(unidadeRepository, usuarioRepository);
        deleteUnidadeUseCase = new DeleteUnidadeUseCase(unidadeRepository);
        transferirTitularidadeUseCase = new TransferirTitularidadeUseCase(unidadeRepository, usuarioRepository, historicoTitularidadeRepository);

        // Usuarios
        com.dominium.backend.application.security.PasswordEncryptor passwordEncryptor = new com.dominium.backend.application.security.PasswordEncryptor() {
            @Override public String encode(String rawPassword) { return rawPassword; }
            @Override public boolean matches(String rawPassword, String encodedPassword) { return rawPassword.equals(encodedPassword); }
        };
        createUsuarioUseCase = new CreateUsuarioUseCase(usuarioRepository, passwordEncryptor);

        // Moradores
        createVinculoMoradorUseCase = new CreateVinculoMoradorUseCase(vinculoMoradorRepository, unidadeRepository, usuarioRepository, createUsuarioUseCase);
        ReflectionTestUtils.setField(createVinculoMoradorUseCase, "maxMoradores", 5);

        updateVinculoMoradorUseCase = new UpdateVinculoMoradorUseCase(vinculoMoradorRepository);
        endVinculoMoradorUseCase = new EndVinculoMoradorUseCase(vinculoMoradorRepository, usuarioRepository);
        getVinculosPorUnidadeUseCase = new GetVinculosPorUnidadeUseCase(vinculoMoradorRepository);

        // Financeiro
        rateioService = new RateioService(unidadeRepository);
        cadastrarOrcamentoUseCase = new CadastrarOrcamentoUseCase(orcamentoRepository);
        registrarDespesaUseCase = new RegistrarDespesaUseCase(despesaRepository, orcamentoRepository, rateioService);
        aprovarDespesaExtraordinariaUseCase = new AprovarDespesaExtraordinariaUseCase(despesaRepository, orcamentoRepository, rateioService);
        consultarSaldoUseCase = new ConsultarSaldoUseCase(orcamentoRepository);
        getOrcamentoPorAnoUseCase = new GetOrcamentoPorAnoUseCase(orcamentoRepository);
        listOrcamentosUseCase = new ListOrcamentosUseCase(orcamentoRepository);
        listDespesasPorOrcamentoUseCase = new ListDespesasPorOrcamentoUseCase(despesaRepository, orcamentoRepository);
        getDespesaUseCase = new GetDespesaUseCase(despesaRepository);
    }

    private void initRepositories() {
        unidadeRepository = new UnidadeRepository() {
            private Map<Long, Unidade> db = new HashMap<>();
            @Override public Unidade save(Unidade u) {
                if (u.getId() == null) u.setId(new UnidadeId(currentId++));
                db.put(u.getId().getValor(), u);
                return u;
            }
            @Override public Optional<Unidade> findById(UnidadeId id) { return Optional.ofNullable(db.get(id.getValor())); }
            @Override public Optional<Unidade> findByNumeroAndBloco(String numero, String bloco) {
                return db.values().stream().filter(u -> u.getNumero().equals(numero) && u.getBloco().equals(bloco)).findFirst();
            }
            @Override public List<Unidade> findAll() { return List.copyOf(db.values()); }
            @Override public void deleteById(UnidadeId id) { db.remove(id.getValor()); }
        };

        usuarioRepository = new UsuarioRepository() {
            private Map<Long, Usuario> db = new HashMap<>();
            @Override public Usuario save(Usuario u) {
                if (u.getId() == null) u.setId(currentId++);
                db.put(u.getId(), u);
                return u;
            }
            @Override public Optional<Usuario> findById(Long id) { return Optional.ofNullable(db.get(id)); }
            @Override public Optional<Usuario> findByEmail(String email) {
                return db.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
            }
            @Override public List<Usuario> findAll() { return List.copyOf(db.values()); }
            @Override public void deleteById(Long id) { db.remove(id); }
        };

        vinculoMoradorRepository = new VinculoMoradorRepository() {
            private Map<Long, VinculoMorador> db = new HashMap<>();
            @Override public VinculoMorador save(VinculoMorador v) {
                if (v.getId() == null) v.setId(currentId++);
                db.put(v.getId(), v);
                return v;
            }
            @Override public Optional<VinculoMorador> findById(Long id) { return Optional.ofNullable(db.get(id)); }
            @Override public List<VinculoMorador> findByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status) {
                return db.values().stream().filter(v -> v.getUnidade().getId().getValor().equals(unidadeId) && v.getStatus() == status).collect(Collectors.toList());
            }
            @Override public List<VinculoMorador> findByUsuarioIdAndStatus(Long usuarioId, StatusVinculo status) {
                return db.values().stream().filter(v -> v.getUsuario().getId().equals(usuarioId) && v.getStatus() == status).collect(Collectors.toList());
            }
            @Override public List<VinculoMorador> findByUsuarioAndUnidade(Long usuarioId, Long unidadeId) {
                return db.values().stream().filter(v -> v.getUsuario().getId().equals(usuarioId) && v.getUnidade().getId().getValor().equals(unidadeId)).collect(Collectors.toList());
            }
            @Override public long countByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status) {
                return findByUnidadeIdAndStatus(unidadeId, status).size();
            }
            @Override public void deleteById(Long id) { db.remove(id); }
        };

        historicoTitularidadeRepository = new HistoricoTitularidadeRepository() {
            private Map<Long, HistoricoTitularidade> db = new HashMap<>();
            @Override public HistoricoTitularidade save(HistoricoTitularidade h) {
                if (h.getId() == null) h.setId(currentId++);
                db.put(h.getId(), h);
                return h;
            }
            @Override public List<HistoricoTitularidade> findByUnidadeId(UnidadeId unidadeId) {
                return db.values().stream().filter(h -> h.getUnidadeId().equals(unidadeId)).collect(Collectors.toList());
            }
        };

        orcamentoRepository = new OrcamentoRepository() {
            private Map<Long, Orcamento> db = new HashMap<>();
            @Override public Orcamento save(Orcamento o) {
                if (o.getId() == null) o.setId(currentId++);
                db.put(o.getId(), o);
                return o;
            }
            @Override public Optional<Orcamento> findById(Long id) { return Optional.ofNullable(db.get(id)); }
            @Override public Optional<Orcamento> findByAno(Integer ano) {
                return db.values().stream().filter(o -> o.getAno().equals(ano)).findFirst();
            }
            @Override public List<Orcamento> findAll() { return List.copyOf(db.values()); }
        };

        despesaRepository = new DespesaRepository() {
            private Map<Long, Despesa> db = new HashMap<>();
            @Override public Despesa save(Despesa d) {
                if (d.getId() == null) d.setId(currentId++);
                db.put(d.getId(), d);
                return d;
            }
            @Override public Optional<Despesa> findById(Long id) { return Optional.ofNullable(db.get(id)); }
            @Override public List<Despesa> findByOrcamentoId(Long orcamentoId) {
                return db.values().stream().filter(d -> d.getOrcamentoId().equals(orcamentoId)).collect(Collectors.toList());
            }
            @Override public List<Despesa> findAll() { return List.copyOf(db.values()); }
        };
    }
}
