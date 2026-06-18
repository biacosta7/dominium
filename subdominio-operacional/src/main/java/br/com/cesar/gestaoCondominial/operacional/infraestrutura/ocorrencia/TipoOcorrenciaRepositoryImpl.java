package br.com.cesar.gestaoCondominial.operacional.infraestrutura.ocorrencia;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoOcorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.TipoOcorrenciaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoOcorrenciaRepositoryImpl implements TipoOcorrenciaRepository {

    private final JdbcTemplate jdbcTemplate;

    public TipoOcorrenciaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TipoOcorrencia> rowMapper = (rs, rowNum) ->
            new TipoOcorrencia(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getBigDecimal("valor_base_multa")
            );

    @Override
    public List<TipoOcorrencia> findAll() {
        return jdbcTemplate.query("SELECT * FROM tipos_ocorrencia ORDER BY nome", rowMapper);
    }

    @Override
    public Optional<TipoOcorrencia> findByNome(String nome) {
        List<TipoOcorrencia> results = jdbcTemplate.query(
                "SELECT * FROM tipos_ocorrencia WHERE nome = ?",
                rowMapper,
                nome
        );
        return results.stream().findFirst();
    }
}
