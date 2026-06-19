package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

public interface FilaDeEsperaRepository {
    FilaDeEspera salvar(FilaDeEspera fila);
    Optional<FilaDeEspera> buscarPorId(String id);
    List<FilaDeEspera> listarPorArea(AreaComumId areaId);
    List<FilaDeEspera> listarPorUsuario(UsuarioId usuarioId);
    List<FilaDeEspera> listarPorSlot(AreaComumId areaId, LocalDate data, LocalTime inicio, LocalTime fim);
    Optional<FilaDeEspera> buscarProximoNaFila(AreaComumId areaId, LocalDate data, LocalTime inicio, LocalTime fim);
}