package br.com.cesar.gestaoCondominial.governanca.aplicacao.assembleia.usecase;

import br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase.EncerrarPautaUseCase;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.repository.AssembleiaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.StatusPauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EncerrarAssembleiaUseCase {

    private final AssembleiaRepository assembleiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PautaRepository pautaRepository;
    private final EncerrarPautaUseCase encerrarPautaUseCase;

    public EncerrarAssembleiaUseCase(
            AssembleiaRepository assembleiaRepository,
            UsuarioRepository usuarioRepository,
            PautaRepository pautaRepository,
            EncerrarPautaUseCase encerrarPautaUseCase
    ) {
        this.assembleiaRepository = assembleiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pautaRepository = pautaRepository;
        this.encerrarPautaUseCase = encerrarPautaUseCase;
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
        Assembleia salva = assembleiaRepository.save(assembleia);
        encerrarPautasEmAberto(assembleia.getId());
        return salva;
    }

    // Ao concluir a assembleia, as votações ainda em aberto também se encerram
    // (mesma regra do encerramento manual: sem voto fecha como ADIADO).
    private void encerrarPautasEmAberto(AssembleiaId assembleiaId) {
        List<Pauta> pautas = pautaRepository.buscarPorAssembleia(assembleiaId);
        for (Pauta pauta : pautas) {
            if (pauta.getStatus() != StatusPauta.ABERTA) continue;
            encerrarPautaUseCase.executar(pauta.getId());
        }
    }
}
