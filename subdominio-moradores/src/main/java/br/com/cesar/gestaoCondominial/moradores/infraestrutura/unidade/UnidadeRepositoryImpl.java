package br.com.cesar.gestaoCondominial.moradores.infraestrutura.unidade;

import java.util.List;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId; // Importado
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;

@Repository
public class UnidadeRepositoryImpl implements UnidadeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    public UnidadeRepositoryImpl(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    private RowMapper<Unidade> rowMapper = new RowMapper<Unidade>() {
        @Override
        public Unidade mapRow(ResultSet rs, int rowNum) throws SQLException {
            Unidade u = new Unidade();
            
            // Transformando o Long do banco no Value Object UnidadeId
            u.setId(new UnidadeId(rs.getLong("id")));
            
            u.setNumero(rs.getString("numero"));
            u.setBloco(rs.getString("bloco"));
            
            Long propId = rs.getLong("proprietario_id");
            if (!rs.wasNull()) {
                // Se UsuarioId também for VO, aqui precisaria de: new UsuarioId(propId)
                u.setProprietario(usuarioRepository.findById(propId).orElse(null));
            }
            
            Long inqId = rs.getLong("inquilino_id");
            if (!rs.wasNull()) {
                u.setInquilino(usuarioRepository.findById(inqId).orElse(null));
            }
            
            String statusStr = rs.getString("status");
            if (statusStr != null) {
                u.setStatus(StatusAdimplencia.valueOf(statusStr));
            }
            u.setSaldoDevedor(rs.getBigDecimal("saldo_devedor"));
            
            if (rs.getTimestamp("created_at") != null) 
                u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null) 
                u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            
            return u;
        }
    };

    @Override
    public Unidade save(Unidade unidade) {
        // Verificação robusta do Value Object
        if (unidade.getId() == null || unidade.getId().getValor() == null) {
            String sql = "INSERT INTO unidades(numero, bloco, proprietario_id, inquilino_id, status, saldo_devedor) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, unidade.getNumero());
                ps.setString(2, unidade.getBloco());
                
                // Assumindo que Proprietário ainda usa Long. Se for VO, use .getId().getValor()
                ps.setLong(3, unidade.getProprietario().getId());
                
                if (unidade.getInquilino() != null) {
                    ps.setLong(4, unidade.getInquilino().getId());
                } else {
                    ps.setNull(4, java.sql.Types.BIGINT);
                }
                
                ps.setString(5, unidade.getStatus() != null ? unidade.getStatus().name() : null);
                ps.setBigDecimal(6, unidade.getSaldoDevedor());
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
                // Buscamos especificamente pela chave "id" (ou "ID", dependendo do banco)
                Object generatedId = keyHolder.getKeys().get("id");
                
                // Alguns bancos retornam em maiúsculo "ID"
                if (generatedId == null) {
                    generatedId = keyHolder.getKeys().get("ID");
                }
            
                if (generatedId instanceof Number number) {
                    unidade.setId(new UnidadeId(number.longValue()));
                }
            }
        } else {
            String sql = "UPDATE unidades SET numero = ?, bloco = ?, proprietario_id = ?, inquilino_id = ?, status = ?, saldo_devedor = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                unidade.getNumero(), 
                unidade.getBloco(), 
                unidade.getProprietario().getId(), 
                unidade.getInquilino() != null ? unidade.getInquilino().getId() : null, 
                unidade.getStatus() != null ? unidade.getStatus().name() : null, 
                unidade.getSaldoDevedor(), 
                unidade.getId().getValor()); // Extraindo valor do VO para o WHERE
        }
        return unidade;
    }

    @Override
    public Optional<Unidade> findById(UnidadeId id) { // Assinatura alterada para UnidadeId
        List<Unidade> results = jdbcTemplate.query(
            "SELECT * FROM unidades WHERE id = ?", 
            rowMapper, 
            id.getValor() // Extraindo valor
        );
        return results.stream().findFirst();
    }

    @Override
    public void deleteById(UnidadeId id) { // Assinatura alterada para UnidadeId
        jdbcTemplate.update("DELETE FROM unidades WHERE id = ?", id.getValor());
    }

    @Override
    public Optional<Unidade> findByNumeroAndBloco(String numero, String bloco) {
        List<Unidade> results = jdbcTemplate.query("SELECT * FROM unidades WHERE numero = ? AND bloco = ?", rowMapper, numero, bloco);
        return results.stream().findFirst();
    }

    @Override
    public List<Unidade> findAll() {
        return jdbcTemplate.query("SELECT * FROM unidades", rowMapper);
    }
}