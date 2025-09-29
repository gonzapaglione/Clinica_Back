package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.clinica_coc.services.PacienteServicio;
import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Persona;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
// http://localhost:8080/api/pacientes
@RequestMapping("/api/pacientes")
@CrossOrigin("http://localhost:5173")
public class PacienteController {

    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteServicio pacienteServicio;

    @Autowired
    private PersonaServicio personaServicio;

    @GetMapping()
    public List<Paciente> listarPacientes() {
        List<Paciente> pacientes = pacienteServicio.listarPacientes();
        pacientes.forEach((paciente) -> logger.info("ID: " + paciente.toString()));
        return pacientes;
    }

    @PostMapping()
    public Paciente agregarPaciente(@RequestParam Long personaId, @RequestParam String cob_social,
            @RequestBody Paciente paciente) {
        logger.info("Paciente a agregar: " + paciente.toString());

        Persona persona = personaServicio.buscarPersonaPorId(personaId);
        if (persona == null) {
            throw new RuntimeException("Persona no encontrada");
        }

        paciente.setPersona(persona);
        paciente.setCob_social(cob_social);

        return pacienteServicio.guardarPaciente(paciente);
    }
}
