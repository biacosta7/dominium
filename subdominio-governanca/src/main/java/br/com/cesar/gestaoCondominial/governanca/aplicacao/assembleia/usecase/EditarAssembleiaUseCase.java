package br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EditarAssembleiaUseCase {

    private final AssembleiaRepository assembleiaRepository;
    private final UsuarioRepository usuarioRepository;

    public EditarAssembleiaUseCase(AssembleiaRepository assembleiaRepository, UsuarioRepository usuarioRepository) {
        this.assembleiaRepository = assembleiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Assembleia executar(Long sindicoId, Long assembleiaId, String titulo, LocalDateTime dataHora, String local, List<String> pauta) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode editar assembleias");
        }

        Assembleia assembleia = assembleiaRepository.findById(new AssembleiaId(assembleiaId))
                .orElseThrow(() -> new ResourceNotFoundException("Assembleia não encontrada"));

        assembleia.editar(titulo, dataHora, local, pauta);
        return assembleiaRepository.save(assembleia);
    }
}
