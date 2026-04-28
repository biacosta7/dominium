package br.com.cesar.gestaoCondominial.moradores.infraestrutura.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Usuario> rowMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        u.setTelefone(rs.getString("telefone"));
        u.setCpf(rs.getString("cpf"));
        u.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
        return u;
    };

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            String sql = "INSERT INTO usuarios(nome, email, senha, telefone, cpf, tipo) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, usuario.getNome());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getSenha());
                ps.setString(4, usuario.getTelefone());
                ps.setString(5, usuario.getCpf());
                ps.setString(6, usuario.getTipo().name());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                usuario.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, telefone = ?, cpf = ?, tipo = ? WHERE id = ?";
            jdbcTemplate.update(sql, usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getTelefone(), usuario.getCpf(), usuario.getTipo().name(), usuario.getId());
        }
        return usuario;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        List<Usuario> results = jdbcTemplate.query("SELECT * FROM usuarios WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> results = jdbcTemplate.query("SELECT * FROM usuarios WHERE email = ?", rowMapper, email);
        return results.stream().findFirst();
    }

    @Override
    public List<Usuario> findAll() {
        return jdbcTemplate.query("SELECT * FROM usuarios", rowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM usuarios WHERE id = ?", id);
    }
}
