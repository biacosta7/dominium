package br.com.cesar.gestaoCondominial.operacional.infraestrutura.funcionario;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.FuncionarioId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServico;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.OrdemServicoId;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.StatusOrdemServico;
import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.repository.OrdemServicoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrdemServicoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OrdemServico> rowMapper = (rs, rowNum) -> OrdemServico.reconstituir(
            OrdemServicoId.de(rs.getString("id")),
            rs.getString("descricao"),
            FuncionarioId.de(rs.getString("funcionario_id")),
            StatusOrdemServico.valueOf(rs.getString("status")),
            rs.getDate("data_inicio").toLocalDate(),
            rs.getDate("data_fim").toLocalDate()
    );

    @Override
    public OrdemServico save(OrdemServico os) {
        boolean existe = Boolean.TRUE.equals(
            jdbcTemplate.queryForObject("SELECT COUNT(*) > 0 FROM ordens_servico WHERE id = ?", Boolean.class, os.getId().getValor())
        );

        if (!existe) {
            jdbcTemplate.update(
                "INSERT INTO ordens_servico (id, descricao, funcionario_id, status, data_inicio, data_fim) VALUES (?,?,?,?,?,?)",
                os.getId().getValor(), os.getDescricao(), os.getFuncionarioId().getValor(),
                os.getStatus().name(), Date.valueOf(os.getDataInicio()), Date.valueOf(os.getDataFim())
            );
        } else {
            jdbcTemplate.update(
                "UPDATE ordens_servico SET descricao=?, status=?, data_inicio=?, data_fim=? WHERE id=?",
                os.getDescricao(), os.getStatus().name(),
                Date.valueOf(os.getDataInicio()), Date.valueOf(os.getDataFim()),
                os.getId().getValor()
            );
        }
        return os;
    }

    @Override
    public Optional<OrdemServico> findById(OrdemServicoId id) {
        List<OrdemServico> results = jdbcTemplate.query(
            "SELECT * FROM ordens_servico WHERE id = ?", rowMapper, id.getValor()
        );
        return results.stream().findFirst();
    }

    @Override
    public List<OrdemServico> findByFuncionarioId(FuncionarioId funcionarioId) {
        return jdbcTemplate.query(
            "SELECT * FROM ordens_servico WHERE funcionario_id = ?", rowMapper, funcionarioId.getValor()
        );
    }

    @Override
    public boolean existeAtivaParaFuncionario(FuncionarioId funcionarioId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) > 0 FROM ordens_servico WHERE funcionario_id = ? AND status = 'ABERTA'",
            Boolean.class, funcionarioId.getValor()
        ));
    }
}
