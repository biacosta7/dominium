package br.com.cesar.gestaoCondominial.infraestrutura.dominium.persistence.governanca.pauta;

import com.dominium.backend.domain.assembleia.AssembleiaId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.*;
import com.dominium.backend.domain.governanca.pauta.PautaRepository;
import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.PautaId;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PautaRepositoryImpl implements PautaRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Pauta> pautaMapper = (rs, rowNum) -> Pauta.reconstituir(
            new PautaId(rs.getLong("id")),
            new AssembleiaId(rs.getLong("assembleia_id")),
            rs.getString("titulo"),
            rs.getString("descricao"),
            TipoQuorum.valueOf(rs.getString("tipo_quorum")),
            TipoMaioria.valueOf(rs.getString("tipo_maioria")),
            StatusPauta.valueOf(rs.getString("status")),
            ResultadoPauta.valueOf(rs.getString("resultado"))
    );


    @Override
    public Pauta save(Pauta pauta) {
        if (pauta.getId() == null || pauta.getId().getValor() == null) {
            return inserir(pauta);
        }
        return atualizar(pauta);
    }

    private Pauta inserir(Pauta pauta) {
        String sql = """
                INSERT INTO pauta (assembleia_id, titulo, descricao, tipo_quorum, tipo_maioria, status, resultado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1,   pauta.getAssembleiaId().getValor());
            ps.setString(2, pauta.getTitulo());
            ps.setString(3, pauta.getDescricao());
            ps.setString(4, pauta.getTipoQuorum().name());
            ps.setString(5, pauta.getTipoMaioria().name());
            ps.setString(6, pauta.getStatus().name());
            ps.setString(7, pauta.getResultadoFinal().name());
            return ps;
        }, keyHolder);

        Long idGerado = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return Pauta.reconstituir(
                new PautaId(idGerado),
                pauta.getAssembleiaId(),
                pauta.getTitulo(),
                pauta.getDescricao(),
                pauta.getTipoQuorum(),
                pauta.getTipoMaioria(),
                pauta.getStatus(),
                pauta.getResultadoFinal()
        );
    }

    private Pauta atualizar(Pauta pauta) {
        String sql = """
                UPDATE pauta
                SET assembleia_id = ?,
                    titulo        = ?,
                    descricao     = ?,
                    tipo_quorum   = ?,
                    tipo_maioria  = ?,
                    status        = ?,
                    resultado     = ?
                WHERE id = ?
                """;

        jdbc.update(sql,
                pauta.getAssembleiaId().getValor(),
                pauta.getTitulo(),
                pauta.getDescricao(),
                pauta.getTipoQuorum().name(),
                pauta.getTipoMaioria().name(),
                pauta.getStatus().name(),
                pauta.getResultadoFinal().name(),
                pauta.getId().getValor()
        );

        return pauta;
    }

    @Override
    public Optional<Pauta> findById(PautaId pautaId) {
        String sql = "SELECT * FROM pauta WHERE id = ?";

        return jdbc.query(sql, pautaMapper, pautaId.getValor())
                .stream()
                .findFirst();
    }

    @Override
    public List<Pauta> buscarAbertas() {
        String sql = "SELECT * FROM pauta WHERE status = 'ABERTA'";

        return jdbc.query(sql, pautaMapper);
    }
}
