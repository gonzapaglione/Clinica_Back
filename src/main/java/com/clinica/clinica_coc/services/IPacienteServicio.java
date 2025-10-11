package com.clinica.clinica_coc.services;

import java.util.List;

import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Persona;

public interface IPacienteServicio {
    public List<Paciente> listarPacientes();

    public Paciente buscarPacientePorId(Long id);

    // Crear y actualizar comparten el mismo metodo
    public Paciente guardarPaciente(Paciente Paciente);

    public void eliminarPaciente(Paciente Paciente);

    public Paciente crearPacienteConPersonaYRol(Persona persona, List<Long> coberturasIds);
}
