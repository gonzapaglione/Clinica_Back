package com.clinica.clinica_coc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.clinica_coc.models.Horario;

import java.util.List;

@Repository
public interface HorarioRepositorio extends JpaRepository<Horario, Long> {
    @Query("SELECT h FROM Horario h WHERE h.odontologo.id_odontologo = :idOdontologo")
List<Horario> findHorariosPorOdontologo(@Param("idOdontologo") Long idOdontologo);
}