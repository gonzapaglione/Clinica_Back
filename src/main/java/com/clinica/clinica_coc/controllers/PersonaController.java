package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.models.Persona;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/personas") // http://localhost:8080/api/personas
//Conexion con el front
@CrossOrigin(value = "http://localhost:5173")

public class PersonaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @Autowired
    private PersonaServicio personaServicio;

    // GET: listar todas las personas
    @GetMapping()
    public ResponseEntity<List<Persona>> listarPersonas() {
        List<Persona> personas = personaServicio.listarPersonas();

        if (personas.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No content
        }

        personas.forEach(persona -> logger.info("ID: " + persona.toString()));
        return ResponseEntity.ok(personas); // 200 OK con la lista
    }

    // GET: listar por id
    @GetMapping("/{id}")
    public ResponseEntity<Persona> listarPersonaPorId(@PathVariable Long id) {
        Persona persona = personaServicio.buscarPersonaPorId(id);

        if (persona == null) {
            return ResponseEntity.notFound().build(); // 404 si no existe
        }

        return ResponseEntity.ok(persona); // 200 OK con la persona
    }

    // POST: agregar persona
    @PostMapping
    public ResponseEntity<Persona> agregarPersona(@RequestBody Persona persona) {
        logger.info("Persona a agregar: " + persona.toString());
        Persona nuevaPersona = personaServicio.guardarPersona(persona);

        if (nuevaPersona == null) {
            // Si hubo un error al guardar
            return ResponseEntity.badRequest().build();
        }

        // 201 Created con el objeto creado en el body
        return ResponseEntity.status(201).body(nuevaPersona);
    }

    // PUT: editar persona
    @PutMapping("/{id}")
    public ResponseEntity<Persona> editarPersona(
            @PathVariable Long id,
            @RequestBody Persona personaActualizada) {

        // Buscar la persona existente
        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos
        persona.setNombre(personaActualizada.getNombre());
        persona.setApellido(personaActualizada.getApellido());
        persona.setDni(personaActualizada.getDni());
        persona.setEmail(personaActualizada.getEmail());
        persona.setUsername(personaActualizada.getUsername());
        persona.setPassword(personaActualizada.getPassword());
        persona.setDomicilio(personaActualizada.getDomicilio());
        persona.setTelefono(personaActualizada.getTelefono());

        // Guardar cambios
        Persona personaGuardada = personaServicio.guardarPersona(persona);

        return ResponseEntity.ok(personaGuardada);
    }

    // DELETE: baja logica
    @DeleteMapping("/{id}")
    public ResponseEntity<String> bajaLogicaPersona(@PathVariable Long id) {

        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        // Cambiar estado a Inactivo
        persona.setIsActive("Inactivo");
        personaServicio.guardarPersona(persona);

        return ResponseEntity.ok("Persona dada de baja lógicamente");
    }

}
