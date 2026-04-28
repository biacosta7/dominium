package br.com.cesar.gestaoCondominial.aplicacao.dominium.assembleia.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.repository.AssembleiaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EncerrarAssembleiaUseCase {

    private final AssembleiaRepository assembleiaRepository;
    private final UsuarioRepository usuarioRepository;

    public EncerrarAssembleiaUseCase(AssembleiaRepository assembleiaRepository, UsuarioRepository usuarioRepository) {
        this.assembleiaRepository = assembleiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Assembleia executar(Long sindicoId, Long assembleiaId) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode encerrar assembleias");
        }

        Assembleia assembleia = assembleiaRepository.findById(new AssembleiaId(assembleiaId))
                .orElseThrow(() -> new ResourceNotFoundException("Assembleia não encontrada"));

        assembleia.encerrar();
        return assembleiaRepository.save(assembleia);
    }
}
