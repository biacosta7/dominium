package com.dominium.backend.infrastructure.persistence.governanca.voto;

import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.governanca.voto.OpcaoVoto;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.governanca.voto.VotoId;
import com.dominium.backend.domain.governanca.voto.VotoRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VotoRepositoryImpl implements VotoRepository {

    private final JdbcTemplate jdbc;

    // RowMapper embutido como lambda — mapeia ResultSet → Voto
    private final RowMapper<Voto> votoMapper = (rs, rowNum) -> Voto.reconstituir(
            new VotoId(rs.getLong("id")),
            new PautaId(rs.getLong("pauta_id")),
            new UnidadeId(rs.getLong("unidade_id")),
            new UsuarioId(rs.getLong("usuario_id")),
            OpcaoVoto.valueOf(rs.getString("opcao_voto"))
    );


    @Override
    public Voto save(Voto voto) {
        String sql = """
                INSERT INTO voto (pauta_id, unidade_id, usuario_id, opcao_voto)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1,   voto.getPautaId().getValor());
            ps.setLong(2,   voto.getUnidadeId().getValor());
            ps.setLong(3,   voto.getUsuarioId().getValor());
            ps.setString(4, voto.getOpcaoVoto().name());
            return ps;
        }, keyHolder);

        Long idGerado = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return Voto.reconstituir(
                new VotoId(idGerado),
                voto.getPautaId(),
                voto.getUnidadeId(),
                voto.getUsuarioId(),
                voto.getOpcaoVoto()
        );
    }


    @Override
    public Optional<Voto> findById(VotoId votoId) {
        String sql = "SELECT * FROM voto WHERE id = ?";

        return jdbc.query(sql, votoMapper, votoId.getValor())
                .stream()
                .findFirst();
    }

    @Override
    public List<Voto> buscarPorPauta(PautaId pautaId) {
        String sql = "SELECT * FROM voto WHERE pauta_id = ?";

        return jdbc.query(sql, votoMapper, pautaId.getValor());
    }

    @Override
    public boolean findByPautaAndUnidade(PautaId pautaId, UnidadeId unidadeId) {
        String sql = """
                SELECT COUNT(*) FROM voto
                WHERE pauta_id = ? AND unidade_id = ?
                """;

        Integer count = jdbc.queryForObject(sql, Integer.class,
                pautaId.getValor(),
                unidadeId.getValor()
        );

        return count != null && count > 0;
    }
}
