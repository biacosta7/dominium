package br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.HistoricoTitularidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.HistoricoTitularidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.Usuario;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;

@Service
public class TransferirTitularidadeUseCase {

    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoTitularidadeRepository historicoRepository;

    public TransferirTitularidadeUseCase(UnidadeRepository unidadeRepository,
                                         UsuarioRepository usuarioRepository,
                                         HistoricoTitularidadeRepository historicoRepository) {
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoRepository = historicoRepository;
    }

    public void execute(Long unidadeId, Long novoProprietarioId) {
        // 1. Buscar unidade (Convertendo Long para UnidadeId)
        Unidade unidade = unidadeRepository.findById(new UnidadeId(unidadeId))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        // 2. Regra: não pode transferir com débito
        if (unidade.getSaldoDevedor() != null &&
            unidade.getSaldoDevedor().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Não é possível transferir titularidade com débitos ativos");
        }

        // 3. Buscar novo proprietário (Ajuste se UsuarioId for VO)
        Usuario novoProprietario = usuarioRepository.findById(novoProprietarioId)
                .orElseThrow(() -> new IllegalArgumentException("Novo proprietário não encontrado"));

        Usuario proprietarioAnterior = unidade.getProprietario();

        // 4. Criar histórico
        HistoricoTitularidade historico = new HistoricoTitularidade();
        historico.setUnidadeId(unidade.getId()); // Usando o VO da unidade
        historico.setProprietarioAnterior(proprietarioAnterior);
        historico.setNovoProprietario(novoProprietario);
        historico.setDataTransferencia(LocalDateTime.now());

        historicoRepository.save(historico);

        // 5. Atualizar unidade
        unidade.setProprietario(novoProprietario);
        unidade.setUpdatedAt(LocalDateTime.now());

        unidadeRepository.save(unidade);
    }
}