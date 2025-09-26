package com.clinica.clinica_coc.services;

import java.util.List;

import com.clinica.clinica_coc.models.Paciente;

public interface IPacienteServicio {
    public List<Paciente> listarPacientes();

    public Paciente buscarPacientePorId(Long id);

    // Crear y actualizar comparten el mismo metodo
    public Paciente guardarPaciente(Paciente Paciente);

    public void eliminarPaciente(Paciente Paciente);

}
