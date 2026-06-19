package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase;

import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto.FilaResponseDTO;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.FilaDeEsperaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListarFilaUseCase {

    private final FilaDeEsperaRepository repository;

    public ListarFilaUseCase(FilaDeEsperaRepository repository) {
        this.repository = repository;
    }

    public List<FilaResponseDTO> executar(UsuarioId usuarioId) {
        List<FilaDeEspera> entradas = repository.listarPorUsuario(usuarioId);
        List<FilaResponseDTO> resposta = new ArrayList<>();

        for (FilaDeEspera entrada : entradas) {
            List<FilaDeEspera> filaDoSlot = repository.listarPorSlot(
                    entrada.getAreaComumId(),
                    entrada.getDataDesejada(),
                    entrada.getHoraInicio(),
                    entrada.getHoraFim());

            int posicao = 1;
            for (FilaDeEspera item : filaDoSlot) {
                if (item.getId().getValor().equals(entrada.getId().getValor())) {
                    break;
                }
                posicao++;
            }

            resposta.add(FilaResponseDTO.from(entrada, posicao));
        }

        return resposta;
    }
}
