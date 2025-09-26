package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.repositories.PersonaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonaServicio implements IPersonaServicio {

    @Autowired
    private PersonaRepositorio personaRepositorio;

    @Override
    public List<Persona> listarPersonas() {
        return personaRepositorio.findAll();
    }

    @Override
    public Persona buscarPersonaPorId(Long id) {
        return personaRepositorio.findById(id).orElse(null);
    }

    @Override
    public Persona guardarPersona(Persona persona) {
        return personaRepositorio.save(persona);
    }

    @Override
    public void eliminarPersona(Persona persona) {
        personaRepositorio.delete(persona);
    }

}