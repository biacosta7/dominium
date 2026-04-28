package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.service;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.ResultadoPauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoQuorum;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.OpcaoVoto;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.Voto;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.StatusVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.TipoVinculo;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.moradores.dominio.morador.repository.VinculoMoradorRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegraVotacao {

    private final VinculoMoradorRepository vinculoRepository;
    private final br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository unidadeRepository;

    public RegraVotacao(
            VinculoMoradorRepository vinculoRepository,
            br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository unidadeRepository
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

        br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));

        if (unidade.getStatus() != br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia.ADIMPLENTE) {
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
