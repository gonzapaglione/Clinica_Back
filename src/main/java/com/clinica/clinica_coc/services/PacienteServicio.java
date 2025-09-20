package com.clinica.clinica_coc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;

@Service
public class PacienteServicio {

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    public List<Paciente> listarPacientes() {
        return pacienteRepositorio.findAll();
    }

    public Paciente guardarPaciente(Paciente paciente) {
        return pacienteRepositorio.save(paciente);
    }

    public void eliminarPaciente(Long id) {
        pacienteRepositorio.deleteById(id);
    }

    public Paciente pacientePorId(Long id) {
        return pacienteRepositorio.findById(id).orElse(null);

    }
}