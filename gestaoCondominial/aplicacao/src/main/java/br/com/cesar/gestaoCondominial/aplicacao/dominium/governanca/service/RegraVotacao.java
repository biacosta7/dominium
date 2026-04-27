package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.service;

import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.ResultadoPauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.TipoQuorum;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto.OpcaoVoto;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.repository.VinculoMoradorRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.UsuarioId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegraVotacao {

    private final VinculoMoradorRepository vinculoRepository;

    public RegraVotacao(VinculoMoradorRepository vinculoRepository){
        this.vinculoRepository = vinculoRepository;
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
