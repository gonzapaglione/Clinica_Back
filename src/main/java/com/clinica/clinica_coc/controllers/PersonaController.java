package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.DTO.PersonaDTO;
import com.clinica.clinica_coc.DTO.RolDTO;
import com.clinica.clinica_coc.models.Persona;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin("http://localhost:5173")
public class PersonaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @Autowired
    private PersonaServicio personaServicio;

    // GET: listar todas las personas con roles
    @GetMapping
    public ResponseEntity<List<PersonaDTO>> listarPersonas() {
        List<Persona> personas = personaServicio.listarPersonas();

        if (personas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<PersonaDTO> personasDTO = personas.stream().map(this::convertirADTO).toList();
        return ResponseEntity.ok(personasDTO);
    }

    // GET: listar persona por ID con roles
    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> listarPersonaPorId(@PathVariable Long id) {
        Persona persona = personaServicio.buscarPersonaPorId(id);

        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        PersonaDTO dto = convertirADTO(persona);
        return ResponseEntity.ok(dto);
    }

    // POST: agregar persona
    @PostMapping
    public ResponseEntity<PersonaDTO> agregarPersona(@RequestBody Persona persona) {
        logger.info("Persona a agregar: " + persona);

        Persona nuevaPersona = personaServicio.guardarPersona(persona);

        if (nuevaPersona == null) {
            return ResponseEntity.badRequest().build();
        }

        // Convertir a DTO
        PersonaDTO dto = new PersonaDTO();
        dto.setId_persona(nuevaPersona.getId_persona());
        dto.setNombre(nuevaPersona.getNombre());
        dto.setApellido(nuevaPersona.getApellido());
        dto.setDni(nuevaPersona.getDni());
        dto.setEmail(nuevaPersona.getEmail());
        dto.setPassword(nuevaPersona.getPassword());
        dto.setDomicilio(nuevaPersona.getDomicilio());
        dto.setTelefono(nuevaPersona.getTelefono());
        dto.setIsActive(nuevaPersona.getIsActive());

        // Roles
        List<RolDTO> rolesDTO = nuevaPersona.getPersonaRolList().stream()
                .map(pr -> new RolDTO(pr.getIdRol().getId_rol(), pr.getIdRol().getNombre_rol()))
                .toList();
        dto.setRoles(rolesDTO);

        return ResponseEntity.status(201).body(dto);
    }

    // PUT: editar persona
    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> editarPersona(
            @PathVariable Long id,
            @RequestBody Persona personaActualizada) {

        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos
        persona.setNombre(personaActualizada.getNombre());
        persona.setApellido(personaActualizada.getApellido());
        persona.setDni(personaActualizada.getDni());
        persona.setEmail(personaActualizada.getEmail());
        persona.setPassword(personaActualizada.getPassword());
        persona.setDomicilio(personaActualizada.getDomicilio());
        persona.setTelefono(personaActualizada.getTelefono());
        persona.setIsActive(personaActualizada.getIsActive());
        Persona personaGuardada = personaServicio.guardarPersona(persona);

        // Convertir a DTO
        PersonaDTO dto = new PersonaDTO();
        dto.setId_persona(personaGuardada.getId_persona());
        dto.setNombre(personaGuardada.getNombre());
        dto.setApellido(personaGuardada.getApellido());
        dto.setDni(personaGuardada.getDni());
        dto.setEmail(personaGuardada.getEmail());
        dto.setPassword(personaGuardada.getPassword());
        dto.setDomicilio(personaGuardada.getDomicilio());
        dto.setTelefono(personaGuardada.getTelefono());
        dto.setIsActive(personaGuardada.getIsActive());

        // Roles
        List<RolDTO> rolesDTO = personaGuardada.getPersonaRolList().stream()
                .map(pr -> new RolDTO(pr.getIdRol().getId_rol(), pr.getIdRol().getNombre_rol()))
                .toList();
        dto.setRoles(rolesDTO);

        return ResponseEntity.ok(dto);
    }

    // DELETE: baja lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<String> bajaLogicaPersona(@PathVariable Long id) {
        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        persona.setIsActive("Inactivo");
        personaServicio.guardarPersona(persona);

        return ResponseEntity.ok("Persona dada de baja lógicamente");
    }

    // Método auxiliar para convertir Persona a PersonaDTO
    private PersonaDTO convertirADTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        dto.setId_persona(persona.getId_persona());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setDni(persona.getDni());
        dto.setEmail(persona.getEmail());
        dto.setPassword(persona.getPassword());
        dto.setDomicilio(persona.getDomicilio());
        dto.setTelefono(persona.getTelefono());
        dto.setIsActive(persona.getIsActive());

        // Roles
        List<RolDTO> rolesDTO = persona.getPersonaRolList() != null
                ? persona.getPersonaRolList().stream()
                        .map(pr -> new RolDTO(pr.getIdRol().getId_rol(), pr.getIdRol().getNombre_rol()))
                        .toList()
                : List.of();
        dto.setRoles(rolesDTO);

        return dto;
    }
}
