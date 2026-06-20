package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.documento;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.SindicoService;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

/**
 * Anti-Corruption Layer: implementa SindicoService consultando o repositório de usuários
 * do bounded context de moradores, sem expor esse detalhe para os use cases de documento.
 */
@Service
public class SindicoServiceImpl implements SindicoService {

    private final UsuarioRepository usuarioRepository;

    public SindicoServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void validarPermissao(Long sindicoId) {
        var usuario = usuarioRepository.findById(sindicoId)
            .orElseThrow(() -> new DomainException("Usuário não encontrado"));
        if (usuario.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode realizar esta operação");
        }
    }
}
