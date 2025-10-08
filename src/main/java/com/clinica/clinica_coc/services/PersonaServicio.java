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
    public List<Persona> listarOdontologos() {
        return personaRepositorio.findAll().stream()
                .filter(persona -> persona.getPersonaRolList() != null &&
                        persona.getPersonaRolList().stream()
                                .anyMatch(pr -> pr.getIdRol().getId_rol() == 2))
                .toList();
    }

    @Override
    public Persona buscarOdontologoPorId(Long id) {
        return personaRepositorio.findById(id)
                .filter(persona -> persona.getPersonaRolList() != null &&
                        persona.getPersonaRolList().stream()
                                .anyMatch(pr -> pr.getIdRol().getId_rol().equals(2L)))
                .orElse(null); // Devuelve null si no tiene el rol de odontólogo o no existe
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
    public void darBajaPersona(Persona persona) {
        persona.setIsActive("Inactivo");
        personaRepositorio.save(persona);
    }

}