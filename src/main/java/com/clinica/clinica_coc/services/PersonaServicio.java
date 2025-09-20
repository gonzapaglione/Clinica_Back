package com.clinica.clinica_coc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.repositories.PersonaRepositorio;

@Service
public class PersonaServicio {

    @Autowired
    private PersonaRepositorio personaRepositorio;

    public List<Persona> listarPersonas() {
        return personaRepositorio.findAll();
    }

    public Persona guardarPersona(Persona paciente) {
        return personaRepositorio.save(paciente);
    }

    public void eliminarPersona(Long id) {
        personaRepositorio.deleteById(id);
    }

    public Persona personaPorId(Long id) {
        return personaRepositorio.findById(id).orElse(null);
    }

    public Persona editarPersona(Long id, Persona persona) {
        Persona existente = personaRepositorio.findById(id).orElse(null);
        if (existente != null) {
            persona.setId_persona(id);// Asegúrate de que el ID sea el correcto
            return personaRepositorio.save(persona);
        }
        return null; // O lanza una excepción si prefieres
    }
}