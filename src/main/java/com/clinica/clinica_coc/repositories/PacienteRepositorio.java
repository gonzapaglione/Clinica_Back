package com.clinica.clinica_coc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinica.clinica_coc.models.Paciente;

@Repository
public interface PacienteRepositorio extends JpaRepository<Paciente, Long> {
}