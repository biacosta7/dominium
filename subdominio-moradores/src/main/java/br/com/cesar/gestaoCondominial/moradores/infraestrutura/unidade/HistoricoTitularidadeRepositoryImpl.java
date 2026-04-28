package br.com.cesar.gestaoCondominial.moradores.infraestrutura.unidade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.HistoricoTitularidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.HistoricoTitularidadeRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;

@Repository
public class HistoricoTitularidadeRepositoryImpl implements HistoricoTitularidadeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    public HistoricoTitularidadeRepositoryImpl(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    private final RowMapper<HistoricoTitularidade> rowMapper = new RowMapper<>() {
        @Override
        public HistoricoTitularidade mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoricoTitularidade historico = new HistoricoTitularidade();
            
            historico.setId(rs.getLong("id"));
            
            // Converte o Long do banco para o Value Object UnidadeId
            historico.setUnidadeId(new UnidadeId(rs.getLong("unidade_id")));

            // Busca os objetos Usuario completos usando o repository de usuários
            Long antigoId = rs.getLong("proprietario_anterior_id");
            historico.setProprietarioAnterior(usuarioRepository.findById(antigoId).orElse(null));

            Long novoId = rs.getLong("novo_proprietario_id");
            historico.setNovoProprietario(usuarioRepository.findById(novoId).orElse(null));

            if (rs.getTimestamp("data_transferencia") != null) {
                historico.setDataTransferencia(rs.getTimestamp("data_transferencia").toLocalDateTime());
            }

            return historico;
        }
    };

    @Override
    public HistoricoTitularidade save(HistoricoTitularidade historico) {
        String sql = """
            INSERT INTO historico_titularidade 
            (unidade_id, proprietario_anterior_id, novo_proprietario_id, data_transferencia) 
            VALUES (?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // Extrai o valor do Value Object
            ps.setLong(1, historico.getUnidadeId().getValor());
            // Assume que o ID do Usuário é Long (ajuste se for VO)
            ps.setLong(2, historico.getProprietarioAnterior().getId());
            ps.setLong(3, historico.getNovoProprietario().getId());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(historico.getDataTransferencia()));
            return ps;
        }, keyHolder);

        // Lógica para recuperar o ID gerado (compatível com H2/MySQL)
        if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
            Number key = (Number) keyHolder.getKeys().get("id");
            if (key == null) key = (Number) keyHolder.getKeys().get("ID");
            if (key != null) historico.setId(key.longValue());
        }

        return historico;
    }

    @Override
    public List<HistoricoTitularidade> findByUnidadeId(UnidadeId unidadeId) {
        String sql = "SELECT * FROM historico_titularidade WHERE unidade_id = ? ORDER BY data_transferencia DESC";
        return jdbcTemplate.query(sql, rowMapper, unidadeId.getValor());
    }
}