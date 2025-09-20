package com.clinica.clinica_coc.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.models.Persona;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaServicio personaServicio;

    public PersonaController(PersonaServicio personaServicio) {
        this.personaServicio = personaServicio;
    }

    @GetMapping("/listar")
    public List<Persona> listarPersonas() {
        return personaServicio.listarPersonas();
    }

    @GetMapping("{id}")
    public Persona obtenerPersonaPorId(@PathVariable Long id) {
        return personaServicio.personaPorId(id);
    }

    @PostMapping
    public Persona guardarPersona(@RequestBody Persona persona) {
        return personaServicio.guardarPersona(persona);
    }

    @PutMapping("/{id}")
    public Persona actualizarUsuario(@PathVariable Long id, @RequestBody Persona persona) {
        return personaServicio.editarPersona(id, persona);
    }

    @DeleteMapping("/{id}")
    public void eliminarPersona(@PathVariable Long id) {
        personaServicio.eliminarPersona(id);
    }

}
