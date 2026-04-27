package com.dominium.backend.infrastructure.persistence.funcionario;

import com.dominium.backend.domain.funcionario.repository.FuncionarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public FuncionarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Funcionario> rowMapper = (rs, rowNum) -> Funcionario.reconstituir(
            FuncionarioId.de(rs.getString("id")),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getString("email"),
            rs.getString("telefone"),
            TipoVinculo.valueOf(rs.getString("tipo_vinculo")),
            StatusFuncionario.valueOf(rs.getString("status")),
            rs.getDate("contrato_inicio").toLocalDate(),
            rs.getDate("contrato_fim").toLocalDate(),
            rs.getBigDecimal("valor_mensal"),
            rs.getLong("sindico_id"),
            rs.getTimestamp("data_cadastro").toLocalDateTime()
    );

    @Override
    public Funcionario save(Funcionario f) {
        boolean existe = Boolean.TRUE.equals(
            jdbcTemplate.queryForObject("SELECT COUNT(*) > 0 FROM funcionarios WHERE id = ?", Boolean.class, f.getId().getValor())
        );

        if (!existe) {
            jdbcTemplate.update(
                "INSERT INTO funcionarios (id, nome, cpf, email, telefone, tipo_vinculo, status, contrato_inicio, contrato_fim, valor_mensal, sindico_id, data_cadastro) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                f.getId().getValor(), f.getNome(), f.getCpf(), f.getEmail(), f.getTelefone(),
                f.getTipoVinculo().name(), f.getStatus().name(),
                Date.valueOf(f.getContratoInicio()), Date.valueOf(f.getContratoFim()),
                f.getValorMensal(), f.getSindicoId(),
                Timestamp.valueOf(f.getDataCadastro())
            );
        } else {
            jdbcTemplate.update(
                "UPDATE funcionarios SET nome=?, cpf=?, email=?, telefone=?, tipo_vinculo=?, status=?, contrato_inicio=?, contrato_fim=?, valor_mensal=? WHERE id=?",
                f.getNome(), f.getCpf(), f.getEmail(), f.getTelefone(),
                f.getTipoVinculo().name(), f.getStatus().name(),
                Date.valueOf(f.getContratoInicio()), Date.valueOf(f.getContratoFim()),
                f.getValorMensal(), f.getId().getValor()
            );
        }
        return f;
    }

    @Override
    public Optional<Funcionario> findById(FuncionarioId id) {
        List<Funcionario> results = jdbcTemplate.query(
            "SELECT * FROM funcionarios WHERE id = ?", rowMapper, id.getValor()
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Funcionario> findAll() {
        return jdbcTemplate.query("SELECT * FROM funcionarios ORDER BY nome", rowMapper);
    }

    @Override
    public List<Funcionario> findByStatus(StatusFuncionario status) {
        return jdbcTemplate.query(
            "SELECT * FROM funcionarios WHERE status = ?", rowMapper, status.name()
        );
    }
}
