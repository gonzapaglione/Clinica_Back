package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PacienteServicio implements IPacienteServicio {

    @Autowired
    private PacienteRepositorio PacienteRepositorio;

    @Override
    public List<Paciente> listarPacientes() {
        return PacienteRepositorio.findAll();
    }

    @Override
    public Paciente buscarPacientePorId(Long id) {
        return PacienteRepositorio.findById(id).orElse(null);
    }

    @Override
    public Paciente guardarPaciente(Paciente paciente) {
        if (paciente.getPersona() == null || paciente.getPersona().getId_persona() == null) {
            throw new RuntimeException("Debe asignarse una persona existente al paciente");
        }
        return PacienteRepositorio.save(paciente);
    }

    @Override
    public void eliminarPaciente(Paciente paciente) {
        PacienteRepositorio.delete(paciente);
    }

}