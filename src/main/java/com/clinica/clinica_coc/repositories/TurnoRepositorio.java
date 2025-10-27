package com.clinica.clinica_coc.repositories;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.clinica.clinica_coc.models.Turno;

@Repository
public interface TurnoRepositorio extends JpaRepository<Turno, Long>, JpaSpecificationExecutor<Turno> {

    @Query("SELECT t FROM Turno t " +
           "LEFT JOIN FETCH t.paciente p " +
           "LEFT JOIN FETCH p.persona " + 
           "LEFT JOIN FETCH t.odontologo o " +
           "LEFT JOIN FETCH o.persona " + 
           "WHERE p.id_paciente = :idPaciente")
    List<Turno> findByPacienteId(@Param("idPaciente") Long idPaciente);

    @Query("SELECT t FROM Turno t WHERE t.odontologo.id_odontologo = :idOdontologo")
    List<Turno> findByOdontologoId(@Param("idOdontologo") Long idOdontologo);

    List<Turno> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);


    @Query("SELECT t FROM Turno t " +
           "LEFT JOIN FETCH t.paciente p " +
           "LEFT JOIN FETCH p.persona " +
           "LEFT JOIN FETCH t.odontologo o " +
           "LEFT JOIN FETCH o.persona " +
           "WHERE t.odontologo.id_odontologo = :idOdontologo AND t.fechaHora BETWEEN :inicio AND :fin " +
           "ORDER BY t.fechaHora ASC") 
    List<Turno> findByOdontologoIdAndFechaHoraBetween(
            @Param("idOdontologo") Long idOdontologo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);


    @Query("SELECT t FROM Turno t " +
           "LEFT JOIN FETCH t.paciente p " +
           "LEFT JOIN FETCH p.persona " +
           "LEFT JOIN FETCH t.odontologo o " +
           "LEFT JOIN FETCH o.persona " +
           "WHERE t.id_turno = :idTurno")
    Optional<Turno> findByIdWithDetails(@Param("idTurno") Long idTurno);

    @Query("SELECT t FROM Turno t " +
           "LEFT JOIN FETCH t.paciente p " +
           "LEFT JOIN FETCH p.persona " + 
           "LEFT JOIN FETCH t.odontologo o " +
           "LEFT JOIN FETCH o.persona " + 
           "WHERE t.estadoTurno = 'PROXIMO' " + 
           "ORDER BY t.fechaHora ASC")
    List<Turno> findProximosTurnos();

    @Query("SELECT t FROM Turno t " +
           "LEFT JOIN FETCH t.paciente p " +
           "LEFT JOIN FETCH p.persona " + 
           "LEFT JOIN FETCH t.odontologo o " +
           "LEFT JOIN FETCH o.persona " + 
           "WHERE t.odontologo.id_odontologo = :odontologoId " +
           "AND t.fechaHora BETWEEN :inicioRango AND :finRango ")
    List<Turno> findTurnosByMes(
            @Param("odontologoId") Long odontologoId,
            @Param("inicioRango") LocalDateTime inicioRango, 
            @Param("finRango") LocalDateTime finRango
    );
}