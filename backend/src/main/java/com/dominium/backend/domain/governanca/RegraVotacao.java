package com.dominium.backend.domain.governanca;

import com.dominium.backend.domain.governanca.pauta.Pauta;
import com.dominium.backend.domain.governanca.pauta.ResultadoPauta;
import com.dominium.backend.domain.governanca.pauta.TipoMaioria;
import com.dominium.backend.domain.governanca.pauta.TipoQuorum;
import com.dominium.backend.domain.governanca.voto.OpcaoVoto;
import com.dominium.backend.domain.governanca.voto.Voto;
import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.TipoVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegraVotacao {

    private final VinculoMoradorRepository vinculoRepository;
    private final com.dominium.backend.domain.unidade.repository.UnidadeRepository unidadeRepository;

    public RegraVotacao(
            VinculoMoradorRepository vinculoRepository,
            com.dominium.backend.domain.unidade.repository.UnidadeRepository unidadeRepository
    ){
        this.vinculoRepository = vinculoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public void validarElegebilidade(UsuarioId usuarioId, UnidadeId unidadeId){

        VinculoMorador vinculo = vinculoRepository
                .findByUsuarioAndUnidade(
                        usuarioId.getValor(),
                        unidadeId.getValor()
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vínculo não encontrado"));

        if (vinculo.getTipo() != TipoVinculo.TITULAR){
            throw new RuntimeException("Apenas titular pode votar");
        }

        if (vinculo.getStatus() != StatusVinculo.ATIVO){
            throw  new RuntimeException("Vinculo não está ativo");
        }

        com.dominium.backend.domain.unidade.Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
        
        if (unidade.getStatus() != com.dominium.backend.domain.unidade.StatusAdimplencia.ADIMPLENTE) {
            throw new RuntimeException("Unidade inadimplente não pode votar");
        }
    }

    private static final int QUORUM_MIN = 1;

    public void validarQuorum(Pauta pauta, List<Voto> votos){

        int totalVotos = votos.size();

        if (pauta.getTipoQuorum() == TipoQuorum.QUALIFICADO){
            if (totalVotos < QUORUM_MIN){
                throw new RuntimeException("Quórum qualificado não atingido");
            }
        }

        if (totalVotos == 0){
            throw new RuntimeException("Nenhum voto registrado");
        }
    }

    public ResultadoPauta calcularResultado(Pauta pauta, List<Voto> votos){

        validarQuorum(pauta, votos);

        long votosFavor = votos.stream()
                .filter(v -> v.getOpcaoVoto() == OpcaoVoto.FAVOR)
                .count();

        long votosContra = votos.stream()
                .filter((v -> v.getOpcaoVoto() == OpcaoVoto.CONTRA))
                .count();

        long totalValidos = votosFavor + votosContra;

        if (totalValidos == 0){
            return ResultadoPauta.ADIADO;
        }

        if (pauta.getTipoMaioria() == TipoMaioria.SIMPLES) {
            return votosFavor > votosContra
                    ? ResultadoPauta.APROVADO
                    : ResultadoPauta.REJEITADO;
        }

        if (pauta.getTipoMaioria() == TipoMaioria.ABSOLUTA) {
            return votosFavor > (totalValidos / 2)
                    ? ResultadoPauta.APROVADO
                    : ResultadoPauta.REJEITADO;
        }

        if (pauta.getTipoMaioria() == TipoMaioria.QUALIFICADA) {
            return votosFavor >= (totalValidos * 2 / 3)
                    ? ResultadoPauta.APROVADO
                    : ResultadoPauta.REJEITADO;
        }

        return ResultadoPauta.ADIADO;
    }

}
