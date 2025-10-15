package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.DTO.OdontologoResponse;
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
    public ResponseEntity<List<OdontologoResponse>> listarOdontologos() {
        List<Persona> odontologos = personaServicio.listarOdontologos();

        if (odontologos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<OdontologoResponse> odontologosDTO = odontologos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(odontologosDTO);
    }

    // GET: listar por id
    @GetMapping("/{id}")
    public ResponseEntity<OdontologoResponse> listarOdontologoPorId(@PathVariable Long id) {
        Persona persona = personaServicio.buscarOdontologoPorId(id); // filtrado por rol

        if (persona == null) {
            return ResponseEntity.notFound().build(); // 404 si no es odontólogo
        }

        OdontologoResponse dto = convertirAResponse(persona);
        return ResponseEntity.ok(dto);
    }

    // POST: agregar odontólogo (acepta Persona + lista de especialidadesIds)
    @PostMapping
    public ResponseEntity<OdontologoResponse> agregarOdontologo(
            @RequestBody com.clinica.clinica_coc.DTO.OdontologoRequest request) {
        logger.info("Odontólogo a agregar: " + request);

        Persona nuevaPersona = personaServicio.crearOdontologoConPersonaYRol(
                request.getPersona(), request.getEspecialidadesIds());

        if (nuevaPersona == null) {
            return ResponseEntity.badRequest().build();
        }

        OdontologoResponse dto = convertirAResponse(nuevaPersona);
        return ResponseEntity.status(201).body(dto);
    }

    // PUT: editar odontólogo (misma forma que POST: persona + especialidadesIds)
    @PutMapping("/{id}")
    public ResponseEntity<OdontologoResponse> editarOdontologo(
            @PathVariable Long id,
            @RequestBody com.clinica.clinica_coc.DTO.OdontologoRequest request) {

        Persona personaEditada = personaServicio.editarOdontologoConPersonaYRol(
                id, request.getPersona(), request.getEspecialidadesIds());

        if (personaEditada == null) {
            return ResponseEntity.notFound().build();
        }

        OdontologoResponse dto = convertirAResponse(personaEditada);
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

    // Método auxiliar para convertir Persona a OdontologoResponse (formato plano
    // para GET)
    private OdontologoResponse convertirAResponse(Persona persona) {
        OdontologoResponse response = new OdontologoResponse();
        response.setId_persona(persona.getId_persona());
        response.setNombre(persona.getNombre());
        response.setApellido(persona.getApellido());
        response.setDni(persona.getDni());
        response.setEmail(persona.getEmail());
        response.setPassword(persona.getPassword());
        response.setDomicilio(persona.getDomicilio());
        response.setTelefono(persona.getTelefono());
        response.setIsActive(persona.getIsActive());

        // Especialidades (lista con id y nombre)
        List<EspecialidadDTO> especialidadesDTO = persona.getEspecialidadOdontologoList() != null
                ? persona.getEspecialidadOdontologoList().stream()
                        .filter(eo -> eo.getIdEspecialidad() != null)
                        .map(eo -> new EspecialidadDTO(
                                eo.getIdEspecialidad().getId_especialidad(),
                                eo.getIdEspecialidad().getNombre()))
                        .collect(Collectors.toList())
                : List.of();
        response.setEspecialidades(especialidadesDTO);

        return response;
    }
}
