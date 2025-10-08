package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.DTO.OdontologoDTO;
import com.clinica.clinica_coc.DTO.EspecialidadDTO;
import com.clinica.clinica_coc.models.Persona;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/odontologos")
@CrossOrigin("http://localhost:5173")
public class OdontologoController {

    private static final Logger logger = LoggerFactory.getLogger(OdontologoController.class);

    @Autowired
    private PersonaServicio personaServicio;

    // GET: listar todos los odontólogos con especialidades
    @GetMapping
    public ResponseEntity<List<OdontologoDTO>> listarOdontologos() {
        List<Persona> odontologos = personaServicio.listarOdontologos();

        if (odontologos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<OdontologoDTO> odontologosDTO = odontologos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(odontologosDTO);
    }

    // GET: listar por id
    @GetMapping("/{id}")
    public ResponseEntity<OdontologoDTO> listarOdontologoPorId(@PathVariable Long id) {
        Persona persona = personaServicio.buscarOdontologoPorId(id); // filtrado por rol

        if (persona == null) {
            return ResponseEntity.notFound().build(); // 404 si no es odontólogo
        }

        OdontologoDTO dto = convertirADTO(persona);
        return ResponseEntity.ok(dto);
    }

    // POST: agregar odontólogo
    @PostMapping
    public ResponseEntity<OdontologoDTO> agregarOdontologo(@RequestBody Persona persona) {
        logger.info("Odontólogo a agregar: " + persona);

        Persona nuevaPersona = personaServicio.guardarPersona(persona);

        if (nuevaPersona == null) {
            return ResponseEntity.badRequest().build();
        }

        OdontologoDTO dto = convertirADTO(nuevaPersona);
        return ResponseEntity.status(201).body(dto);
    }

    // PUT: editar odontólogo
    @PutMapping("/{id}")
    public ResponseEntity<OdontologoDTO> editarOdontologo(
            @PathVariable Long id,
            @RequestBody Persona personaActualizada) {

        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        persona.setNombre(personaActualizada.getNombre());
        persona.setApellido(personaActualizada.getApellido());
        persona.setDni(personaActualizada.getDni());
        persona.setEmail(personaActualizada.getEmail());
        persona.setPassword(personaActualizada.getPassword());
        persona.setDomicilio(personaActualizada.getDomicilio());
        persona.setTelefono(personaActualizada.getTelefono());
        persona.setIsActive(personaActualizada.getIsActive());

        Persona personaGuardada = personaServicio.guardarPersona(persona);

        OdontologoDTO dto = convertirADTO(personaGuardada);
        return ResponseEntity.ok(dto);
    }

    // DELETE: baja lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<String> bajaLogicaOdontologo(@PathVariable Long id) {
        Persona persona = personaServicio.buscarPersonaPorId(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        persona.setIsActive("Inactivo");
        personaServicio.guardarPersona(persona);

        return ResponseEntity.ok("Odontólogo dado de baja lógicamente");
    }

    // Método auxiliar para convertir Persona a OdontologoDTO
    private OdontologoDTO convertirADTO(Persona persona) {
        OdontologoDTO dto = new OdontologoDTO();
        dto.setId_persona(persona.getId_persona());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setDni(persona.getDni());
        dto.setEmail(persona.getEmail());
        dto.setPassword(persona.getPassword());
        dto.setDomicilio(persona.getDomicilio());
        dto.setTelefono(persona.getTelefono());
        dto.setIsActive(persona.getIsActive());

        // Especialidades
        List<EspecialidadDTO> especialidadesDTO = persona.getEspecialidadOdontologoList() != null
                ? persona.getEspecialidadOdontologoList().stream()
                        .map(eo -> new EspecialidadDTO(
                                eo.getIdEspecialidad().getId_especialidad(),
                                eo.getIdEspecialidad().getNombre()))
                        .collect(Collectors.toList())
                : List.of();
        dto.setEspecialidades(especialidadesDTO);

        return dto;
    }
}
