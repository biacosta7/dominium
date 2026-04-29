package br.com.cesar.gestaoCondominial.infraestrutura.dominium.persistence.documento;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.CategoriaDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.StatusDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class DocumentoRepositoryImpl implements DocumentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Documento> rowMapper = (rs, rowNum) -> {
        Date dataValidade = rs.getDate("data_validade");
        return Documento.reconstituir(
                DocumentoId.de(rs.getString("id")),
                rs.getString("nome"),
                CategoriaDocumento.valueOf(rs.getString("categoria")),
                StatusDocumento.valueOf(rs.getString("status")),
                dataValidade != null ? dataValidade.toLocalDate() : null,
                rs.getLong("sindico_id"),
                rs.getTimestamp("data_criacao").toLocalDateTime()
        );
    };

    @Override
    public void save(Documento d) {
        boolean existe = Boolean.TRUE.equals(
            jdbcTemplate.queryForObject("SELECT COUNT(*) > 0 FROM documentos WHERE id = ?", Boolean.class, d.getId().getValor())
        );

        if (!existe) {
            jdbcTemplate.update(
                "INSERT INTO documentos (id, nome, categoria, status, data_validade, sindico_id, data_criacao) VALUES (?,?,?,?,?,?,?)",
                d.getId().getValor(), d.getNome(), d.getCategoria().name(), d.getStatus().name(),
                d.getDataValidade() != null ? Date.valueOf(d.getDataValidade()) : null,
                d.getSindicoId(), Timestamp.valueOf(d.getDataCriacao())
            );
        } else {
            jdbcTemplate.update(
                "UPDATE documentos SET nome=?, categoria=?, status=?, data_validade=? WHERE id=?",
                d.getNome(), d.getCategoria().name(), d.getStatus().name(),
                d.getDataValidade() != null ? Date.valueOf(d.getDataValidade()) : null,
                d.getId().getValor()
            );
        }
    }

    @Override
    public Optional<Documento> findById(DocumentoId id) {
        List<Documento> results = jdbcTemplate.query(
            "SELECT * FROM documentos WHERE id = ?", rowMapper, id.getValor()
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Documento> findAtivos() {
        return jdbcTemplate.query("SELECT * FROM documentos WHERE status = 'ATIVO' ORDER BY nome", rowMapper);
    }

    @Override
    public List<Documento> findAll() {
        return jdbcTemplate.query("SELECT * FROM documentos ORDER BY nome", rowMapper);
    }

    public List<Documento> findVencendoAte(LocalDate limite) {
        return jdbcTemplate.query(
            "SELECT * FROM documentos WHERE status = 'ATIVO' AND data_validade IS NOT NULL AND data_validade <= ?",
            rowMapper, Date.valueOf(limite)
        );
    }
}
