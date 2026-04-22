package com.dominium.backend.repository;

import com.dominium.backend.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>{

    List<Reserva> findByUnidadeId(Long unidadeId);

    @Query("""
    SELECT r FROM Reserva r
    WHERE r.dataReserva = :data
    AND r.status = 'ATIVA'
    AND r.espacoReservado = :espaco
    AND (
        (:inicio BETWEEN r.horaInicio AND r.horaFim) OR
        (:fim BETWEEN r.horaInicio AND r.horaFim) OR
        (r.horaInicio BETWEEN :inicio AND :fim)
    )
""")
    List<Reserva> verificarConflito(
            @Param("data") LocalDate data,
            @Param("inicio") LocalTime inicio,
            @Param("fim") LocalTime fim,
            @Param("espaco") String espaco
    );
}