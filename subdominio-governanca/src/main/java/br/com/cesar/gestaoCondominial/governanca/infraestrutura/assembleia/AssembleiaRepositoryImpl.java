package br.com.cesar.gestaoCondominial.governanca.infraestrutura.assembleia;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.StatusAssembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.TipoAssembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("assembleiaRepositoryJdbc")
public class AssembleiaRepositoryImpl implements AssembleiaRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssembleiaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Assembleia save(Assembleia assembleia) {
        boolean isNew = assembleia.getId().getValor() == null;

        if (isNew) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO assembleias (titulo, data_hora, local, status, tipo, sindico_id, data_criacao) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, assembleia.getTitulo());
                ps.setTimestamp(2, Timestamp.valueOf(assembleia.getDataHora()));
                ps.setString(3, assembleia.getLocal());
                ps.setString(4, assembleia.getStatus().name());
                ps.setString(5, assembleia.getTipo().name());
                ps.setLong(6, assembleia.getSindicoId());
                ps.setTimestamp(7, Timestamp.valueOf(assembleia.getDataCriacao()));
                return ps;
            }, keyHolder);
            assembleia.setId(new AssembleiaId(keyHolder.getKey().longValue()));
            salvarPauta(assembleia);
        } else {
            jdbcTemplate.update(
                "UPDATE assembleias SET titulo = ?, data_hora = ?, local = ?, status = ?, tipo = ? WHERE id = ?",
                assembleia.getTitulo(),
                Timestamp.valueOf(assembleia.getDataHora()),
                assembleia.getLocal(),
                assembleia.getStatus().name(),
                assembleia.getTipo().name(),
                assembleia.getId().getValor()
            );
            // Só recria as pautas se a lista de pauta realmente mudou (ex.: edição de texto).
            // Cancelar/encerrar a assembleia reaproveita o mesmo objeto buscado via findById,
            // então a pauta não muda — recriar aqui destruiria votos e trocaria IDs sem necessidade.
            List<String> pautaAtual = buscarPauta(assembleia.getId());
            List<String> pautaNova = assembleia.getPauta() != null ? assembleia.getPauta() : List.of();
            if (!pautaAtual.equals(pautaNova)) {
                jdbcTemplate.update(
                    "DELETE FROM voto WHERE pauta_id IN (SELECT id FROM pauta WHERE assembleia_id = ?)",
                    assembleia.getId().getValor()
                );
                jdbcTemplate.update("DELETE FROM pauta WHERE assembleia_id = ?", assembleia.getId().getValor());
                salvarPauta(assembleia);
            }
        }

        return assembleia;
    }

    @Override
    public Optional<Assembleia> findById(AssembleiaId id) {
        List<Assembleia> results = jdbcTemplate.query(
            "SELECT * FROM assembleias WHERE id = ?",
            (rs, rowNum) -> Assembleia.reconstituir(
                new AssembleiaId(rs.getLong("id")),
                rs.getString("titulo"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("local"),
                new ArrayList<>(),
                StatusAssembleia.valueOf(rs.getString("status")),
                TipoAssembleia.valueOf(rs.getString("tipo")),
                rs.getLong("sindico_id"),
                rs.getTimestamp("data_criacao").toLocalDateTime()
            ),
            id.getValor()
        );

        if (results.isEmpty()) return Optional.empty();

        Assembleia assembleia = results.get(0);
        assembleia.setPauta(buscarPauta(assembleia.getId()));
        return Optional.of(assembleia);
    }

    @Override
    public List<Assembleia> findAll() {
        List<Assembleia> assembleias = jdbcTemplate.query(
            "SELECT * FROM assembleias ORDER BY data_hora",
            (rs, rowNum) -> Assembleia.reconstituir(
                new AssembleiaId(rs.getLong("id")),
                rs.getString("titulo"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("local"),
                new ArrayList<>(),
                StatusAssembleia.valueOf(rs.getString("status")),
                TipoAssembleia.valueOf(rs.getString("tipo")),
                rs.getLong("sindico_id"),
                rs.getTimestamp("data_criacao").toLocalDateTime()
            )
        );

        assembleias.forEach(a -> a.setPauta(buscarPauta(a.getId())));
        return assembleias;
    }

    private void salvarPauta(Assembleia assembleia) {
        if (assembleia.getPauta() == null) return;
        for (String item : assembleia.getPauta()) {
            jdbcTemplate.update(
                "INSERT INTO pauta (assembleia_id, titulo, descricao, tipo_quorum, tipo_maioria, status, resultado) VALUES (?, ?, ?, ?, ?, ?, ?)",
                assembleia.getId().getValor(),
                item,
                item,
                "SIMPLES",
                "SIMPLES",
                "ABERTA",
                "EM_ANDAMENTO"
            );
        }
    }

    private List<String> buscarPauta(AssembleiaId id) {
        return jdbcTemplate.query(
            "SELECT descricao FROM pauta WHERE assembleia_id = ? ORDER BY id",
            (rs, rowNum) -> rs.getString("descricao"),
            id.getValor()
        );
    }
}
