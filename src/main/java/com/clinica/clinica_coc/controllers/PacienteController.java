package com.clinica.clinica_coc.controllers;

import com.clinica.clinica_coc.DTO.BajaPacienteResponse;
import com.clinica.clinica_coc.DTO.PacienteRequest;
import com.clinica.clinica_coc.DTO.PacienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.clinica_coc.services.PacienteServicio;
import com.clinica.clinica_coc.services.PersonaServicio;
import com.clinica.clinica_coc.services.CoberturaSocialServicio;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.CoberturaSocial;

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
@RequestMapping("/api/pacientes") // http://localhost:8080/api/pacientes
//Conexion con el front
@CrossOrigin("http://localhost:5173")
public class PacienteController {

    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteServicio pacienteServicio;

    @Autowired
    private PersonaServicio personaServicio;

    @Autowired
    private CoberturaSocialServicio coberturaServicio;

    // GET: listar todos
    @GetMapping()
    public ResponseEntity<List<PacienteResponse>> listarPacientes() {
        List<Paciente> pacientes = pacienteServicio.listarPacientes();

        if (pacientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Convertimos cada Paciente a PacienteResponse
        List<PacienteResponse> response = pacientes.stream().map(p
                -> new PacienteResponse(
                        p.getId_paciente(),
                        p.getPersona().getNombre(),
                        p.getPersona().getApellido(),
                        p.getPersona().getDni(),
                        p.getPersona().getEmail(),
                        p.getPersona().getTelefono(),
                        p.getPersona().getIsActive(),
                        p.getCoberturas().stream()
                                .map(c -> c.getNombre_cobertura())
                                .toList()
                )
        ).toList();

        return ResponseEntity.ok(response);
    }

    // GET: listar por id
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> listarPersonaPorId(@PathVariable Long id) {
        Paciente p = pacienteServicio.buscarPacientePorId(id);

        if (p == null) {
            return ResponseEntity.notFound().build(); // 404 si no existe
        }

        PacienteResponse response = new PacienteResponse(
                p.getId_paciente(),
                p.getPersona().getNombre(),
                p.getPersona().getApellido(),
                p.getPersona().getDni(),
                p.getPersona().getEmail(),
                p.getPersona().getTelefono(),
                p.getPersona().getIsActive(),
                p.getCoberturas().stream()
                        .map(c -> c.getNombre_cobertura())
                        .toList()
        );

        return ResponseEntity.ok(response); // 200 OK : con el DTO del paciente
    }

    // POST: agregar paciente
    @PostMapping()
    public ResponseEntity<PacienteResponse> agregarPaciente(
            @RequestBody PacienteRequest request) {

        // Buscar persona
        Persona persona = personaServicio.buscarPersonaPorId(request.getPersonaId());
        if (persona == null) {
            return ResponseEntity.badRequest().body(null); // persona no encontrada
        }

        // Buscar coberturas
        List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(request.getCoberturasIds());

        if (coberturas.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // ninguna cobertura encontrada
        }

        Paciente paciente = new Paciente();
        paciente.setPersona(persona);
        paciente.setCoberturas(coberturas);

        // Guardar paciente
        Paciente pacienteGuardado = pacienteServicio.guardarPaciente(paciente);

        // Convertir a PacienteResponse (DTO)
        PacienteResponse response = new PacienteResponse(
                pacienteGuardado.getId_paciente(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getDni(),
                persona.getEmail(),
                persona.getTelefono(),
                persona.getIsActive(),
                coberturas.stream().map(c -> c.getNombre_cobertura()).toList()
        );

        return ResponseEntity.ok(response);
    }

    // PUT: editar paciente.
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponse> editarPaciente(
            @PathVariable Long id,
            @RequestBody PacienteRequest request) {

        // Buscar paciente existente
        Paciente paciente = pacienteServicio.buscarPacientePorId(id);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar coberturas
        List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(request.getCoberturasIds());
        paciente.setCoberturas(coberturas);

        // Guardar cambios
        Paciente guardado = pacienteServicio.guardarPaciente(paciente);

        // Convertir a PacienteResponse (DTO)
        PacienteResponse response = new PacienteResponse(
                guardado.getId_paciente(),
                guardado.getPersona().getNombre(),
                guardado.getPersona().getApellido(),
                guardado.getPersona().getDni(),
                guardado.getPersona().getEmail(),
                guardado.getPersona().getTelefono(),
                guardado.getPersona().getIsActive(),
                coberturas.stream().map(c -> c.getNombre_cobertura()).toList()
        );

        return ResponseEntity.ok(response);
    }

    // DELETE: baja logica paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<BajaPacienteResponse> bajaLogicaPaciente(@PathVariable Long id) {
        // Buscar paciente
        Paciente paciente = pacienteServicio.buscarPacientePorId(id);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }

        // Cambiar estado de la persona asociada a Inactivo
        Persona persona = paciente.getPersona();
        persona.setIsActive("Inactivo");
        personaServicio.guardarPersona(persona);

        // Crear DTO de respuesta
        BajaPacienteResponse response = new BajaPacienteResponse(
                paciente.getId_paciente(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getIsActive(),
                "Paciente dado de baja lógicamente"
        );

        return ResponseEntity.ok(response);
    }

}
