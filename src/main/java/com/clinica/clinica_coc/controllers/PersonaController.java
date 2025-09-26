package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.exception.RecursoNoEncontradoExcepcion;
import com.clinica.clinica_coc.models.Persona;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
// http://localhost:8080/api/personas
@RequestMapping("/api/personas")
@CrossOrigin(value = "http://localhost:5173")
public class PersonaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @Autowired
    private PersonaServicio personaServicio;

    // http://localhost:8080/api/personas
    @GetMapping()
    public List<Persona> listarPersonas() {
        List<Persona> personas = personaServicio.listarPersonas();
        personas.forEach((persona) -> {
            logger.info("ID: " + persona.toString());
        });

        return personas;
    }

    @PostMapping()
    public Persona agregarPersona(@RequestBody Persona persona) {
        logger.info("Persona a agregar: " + persona.toString());
        return personaServicio.guardarPersona(persona);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersonaPorId(@PathVariable Long id) {
        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            throw new RecursoNoEncontradoExcepcion("No se encontro el id: " + id);
        }

        return ResponseEntity.ok(persona);
    }

}
