package com.clinica.clinica_coc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.clinica_coc.DTO.OdontologoRequest;
import com.clinica.clinica_coc.DTO.OdontologoResponse;
import com.clinica.clinica_coc.DTO.PersonaBasicDTO;
import com.clinica.clinica_coc.DTO.BajaResponse;
import com.clinica.clinica_coc.DTO.EspecialidadDTO;
import com.clinica.clinica_coc.models.Odontologo;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.services.OdontologoServicio;

import java.util.List;

@RestController
@RequestMapping("/api/odontologos")
@CrossOrigin("http://localhost:5173")
public class OdontologoController {

    private static final Logger logger = LoggerFactory.getLogger(OdontologoController.class);

    @Autowired
    private OdontologoServicio odontologoServicio;

    // GET: listar todos los odontólogos
    @GetMapping
    public ResponseEntity<List<OdontologoResponse>> listarOdontologos() {
        List<Odontologo> odontologos = odontologoServicio.listarOdontologos();

        if (odontologos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<OdontologoResponse> odontologosDTO = odontologos.stream()
                .map(this::convertirAResponse)
                .toList();

        return ResponseEntity.ok(odontologosDTO);
    }

    // GET: obtener odontólogo por id
    @GetMapping("/{id}")
    public ResponseEntity<OdontologoResponse> obtenerOdontologoPorId(@PathVariable Long id) {
        Odontologo odontologo = odontologoServicio.buscarOdontologoPorId(id);

        if (odontologo == null) {
            return ResponseEntity.notFound().build();
        }

        OdontologoResponse dto = convertirAResponse(odontologo);
        return ResponseEntity.ok(dto);
    }

    // POST: crear nuevo odontólogo
    @PostMapping
    public ResponseEntity<OdontologoResponse> crearOdontologo(@RequestBody OdontologoRequest request) {
        logger.info("Creando odontólogo: " + request);

        Odontologo nuevoOdontologo = odontologoServicio.crearOdontologoConPersonaYRol(
                request.getPersona(), request.getEspecialidadesIds());

        if (nuevoOdontologo == null) {
            return ResponseEntity.badRequest().build();
        }

        OdontologoResponse dto = convertirAResponse(nuevoOdontologo);
        return ResponseEntity.status(201).body(dto);
    }

    // PUT: editar odontólogo
    @PutMapping("/{id}")
    public ResponseEntity<OdontologoResponse> editarOdontologo(
            @PathVariable Long id,
            @RequestBody OdontologoRequest request) {

        Odontologo odontologoEditado = odontologoServicio.editarOdontologo(
                id, request.getPersona(), request.getEspecialidadesIds());

        if (odontologoEditado == null) {
            return ResponseEntity.notFound().build();
        }

        OdontologoResponse dto = convertirAResponse(odontologoEditado);
        return ResponseEntity.ok(dto);
    }

    // DELETE: dar de baja lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<BajaResponse> bajaLogicaOdontologo(@PathVariable Long id) {
        Odontologo odontologo = odontologoServicio.buscarOdontologoPorId(id);
        if (odontologo == null) {
            return ResponseEntity.notFound().build();
        }

        odontologo.getPersona().setIsActive("Inactivo");
        odontologoServicio.guardarOdontologo(odontologo);

        // Recargar odontologo con datos actualizados
        Odontologo odontologoActualizado = odontologoServicio.buscarOdontologoPorId(id);
        OdontologoResponse response = convertirAResponse(odontologoActualizado);

        BajaResponse bajaResponse = new BajaResponse(
                "Odontólogo dado de baja lógicamente",
                response);

        return ResponseEntity.ok(bajaResponse);
    }

    // Método auxiliar: convertir Odontologo a OdontologoResponse
    private OdontologoResponse convertirAResponse(Odontologo odontologo) {
        PersonaBasicDTO personaDTO = convertirPersonaABasicDTO(odontologo.getPersona());

        List<EspecialidadDTO> especialidadesDTO = odontologo.getEspecialidadOdontologoList() != null
                ? odontologo.getEspecialidadOdontologoList().stream()
                        .map(eo -> new EspecialidadDTO(
                                eo.getEspecialidad().getId_especialidad(),
                                eo.getEspecialidad().getNombre()))
                        .toList()
                : List.of();

        return new OdontologoResponse(
                odontologo.getId_odontologo(),
                personaDTO,
                especialidadesDTO);
    }

    // Método auxiliar para convertir Persona a PersonaBasicDTO (sin roles)
    private PersonaBasicDTO convertirPersonaABasicDTO(Persona persona) {
        return new PersonaBasicDTO(
                persona.getId_persona(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getDni(),
                persona.getEmail(),
                persona.getDomicilio(),
                persona.getTelefono(),
                persona.getIsActive());
    }
}
