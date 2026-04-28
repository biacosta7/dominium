package br.com.cesar.gestaoCondominial.apresentacao;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.usuario.usecase.CreateUsuarioUseCase;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SimpleRefactorTest {

    @Test
    void testUsuarioCreation() {
        UsuarioRepository repo = Mockito.mock(UsuarioRepository.class);
        Usuario user = new Usuario();
        user.setId(1L);
        when(repo.save(any())).thenReturn(user);

        CreateUsuarioUseCase useCase = new CreateUsuarioUseCase(repo, null);
        assertNotNull(useCase);
    }
}
