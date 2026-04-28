package br.com.cesar.gestaoCondominial.operacional.infraestrutura.funcionario;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.AvaliacaoFuncionario;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.AvaliacaoFuncionarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AvaliacaoFuncionarioRepositoryImpl implements AvaliacaoFuncionarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public AvaliacaoFuncionarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AvaliacaoFuncionario> rowMapper = (rs, rowNum) -> {
        AvaliacaoFuncionario a = new AvaliacaoFuncionario();
        a.setId(rs.getLong("id"));
        a.setFuncionarioId(FuncionarioId.de(rs.getString("funcionario_id")));
        a.setPositiva(rs.getBoolean("positiva"));
        a.setComentario(rs.getString("comentario"));
        a.setData(rs.getDate("data").toLocalDate());
        return a;
    };

    @Override
    public AvaliacaoFuncionario save(AvaliacaoFuncionario avaliacao) {
        String sql = "INSERT INTO avaliacoes_funcionario (funcionario_id, positiva, comentario, data) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, avaliacao.getFuncionarioId().getValor().toString());
            ps.setBoolean(2, avaliacao.isPositiva());
            ps.setString(3, avaliacao.getComentario());
            ps.setDate(4, Date.valueOf(avaliacao.getData()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            avaliacao.setId(keyHolder.getKey().longValue());
        }
        return avaliacao;
    }

    @Override
    public List<AvaliacaoFuncionario> findByFuncionarioId(FuncionarioId funcionarioId) {
        return jdbcTemplate.query(
            "SELECT * FROM avaliacoes_funcionario WHERE funcionario_id = ? ORDER BY data DESC",
            rowMapper, funcionarioId.getValor()
        );
    }

    @Override
    public long contarNegativasRecentes(FuncionarioId funcionarioId, int limite) {
        // conta negativas dentro das últimas `limite` avaliações
        List<AvaliacaoFuncionario> recentes = jdbcTemplate.query(
            "SELECT * FROM avaliacoes_funcionario WHERE funcionario_id = ? ORDER BY data DESC LIMIT ?",
            rowMapper, funcionarioId.getValor(), limite
        );
        return recentes.stream().filter(a -> !a.isPositiva()).count();
    }
}
