package com.clinica.clinica_coc.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.clinica_coc.models.Turno;

@Repository
public interface TurnoRepositorio extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t WHERE t.paciente.id_paciente = :idPaciente")
    List<Turno> findByPacienteId(@Param("idPaciente") Long idPaciente);

    @Query("SELECT t FROM Turno t WHERE t.odontologo.id_odontologo = :idOdontologo")
    List<Turno> findByOdontologoId(@Param("idOdontologo") Long idOdontologo);

    List<Turno> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
