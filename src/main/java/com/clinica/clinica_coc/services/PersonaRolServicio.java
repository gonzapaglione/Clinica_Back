package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.repositories.PersonaRolRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaRolServicio {

    @Autowired
    private PersonaRolRepositorio personaRolRepositorio;

    public PersonaRol guardar(PersonaRol personaRol) {
        return personaRolRepositorio.save(personaRol);
    }
}
