package com.clinica.clinica_coc.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.clinica_coc.models.Odontologo;

@Repository
public interface OdontologoRepositorio extends JpaRepository<Odontologo, Long> {
    @Query("SELECT o FROM Odontologo o WHERE o.persona.id_persona = :idPersona")
    Optional<Odontologo> findByPersonaId(@Param("idPersona") Long idPersona);
}
