package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.documento;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class VersaoDocumentoRepositoryImpl implements VersaoDocumentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public VersaoDocumentoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<VersaoDocumento> rowMapper = (rs, rowNum) -> {
        VersaoDocumento v = new VersaoDocumento();
        v.setId(rs.getLong("id"));
        v.setDocumentoId(DocumentoId.de(rs.getString("documento_id")));
        v.setVersao(rs.getInt("numero_versao"));
        v.setNomeArquivo(rs.getString("nome_arquivo"));
        v.setCaminhoArquivo(rs.getString("caminho_arquivo"));
        v.setEnviadoPor(rs.getLong("uploadado_por"));
        v.setDataUpload(rs.getTimestamp("uploadado_em").toLocalDateTime());
        return v;
    };

    @Override
    public void save(VersaoDocumento v) {
        String sql = "INSERT INTO versoes_documento (documento_id, numero_versao, nome_arquivo, caminho_arquivo, uploadado_por, uploadado_em) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, v.getDocumentoId().getValor());
            ps.setInt(2, v.getVersao());
            ps.setString(3, v.getNomeArquivo());
            ps.setString(4, v.getCaminhoArquivo());
            ps.setLong(5, v.getEnviadoPor());
            ps.setTimestamp(6, Timestamp.valueOf(v.getDataUpload()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            v.setId(keyHolder.getKey().longValue());
        }
    }

    @Override
    public Optional<VersaoDocumento> findUltimaVersao(DocumentoId documentoId) {
        List<VersaoDocumento> results = jdbcTemplate.query(
            "SELECT * FROM versoes_documento WHERE documento_id = ? ORDER BY numero_versao DESC LIMIT 1",
            rowMapper, documentoId.getValor()
        );
        return results.stream().findFirst();
    }

    @Override
    public List<VersaoDocumento> findHistorico(DocumentoId documentoId) {
        return jdbcTemplate.query(
            "SELECT * FROM versoes_documento WHERE documento_id = ? ORDER BY numero_versao",
            rowMapper, documentoId.getValor()
        );
    }
}
