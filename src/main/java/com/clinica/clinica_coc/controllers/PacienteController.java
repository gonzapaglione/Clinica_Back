package com.clinica.clinica_coc.controllers;

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
    public ResponseEntity<List<Paciente>> listarPacientes() {
        List<Paciente> pacientes = pacienteServicio.listarPacientes();

        if (pacientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        pacientes.forEach(persona -> logger.info("Paciente: " + persona.toString()));
        return ResponseEntity.ok(pacientes);
    }

    // GET: listar por id
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> listarPersonaPorId(@PathVariable Long id) {
        Paciente paciente = pacienteServicio.buscarPacientePorId(id);

        if (paciente == null) {
            return ResponseEntity.notFound().build(); // 404 si no existe
        }

        return ResponseEntity.ok(paciente); // 200 OK con la persona
    }

    // POST: agregar paciente
    @PostMapping()
    public ResponseEntity<Paciente> agregarPaciente(
            @RequestParam Long personaId,
            @RequestParam List<Long> coberturasIds,
            @RequestBody Paciente paciente) {

        logger.info("Paciente a agregar: " + paciente.toString());

        // Buscar persona
        Persona persona = personaServicio.buscarPersonaPorId(personaId);
        if (persona == null) {
            return ResponseEntity.badRequest().body(null); // persona no encontrada
        }
        paciente.setPersona(persona);

        // Buscar coberturas
        List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(coberturasIds);
        if (coberturas.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // ninguna cobertura encontrada
        }
        paciente.setCoberturas(coberturas);

        // Guardar paciente
        Paciente pacienteGuardado = pacienteServicio.guardarPaciente(paciente);

        return ResponseEntity.ok(pacienteGuardado);
    }

    // PUT: editar paciente, las coberturas se pasan como Request Param en la URL o en Postman en la pestaña Params
    //Ejemplo: coberturasIds=1&coberturasIds=2

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> editarPaciente(
            @PathVariable Long id,
            @RequestParam List<Long> coberturasIds,
            @RequestBody Paciente pacienteActualizado) {

        // Buscar paciente existente
        Paciente paciente = pacienteServicio.buscarPacientePorId(id);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar coberturas
        List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(coberturasIds);
        paciente.setCoberturas(coberturas);

        // Actualizar datos de persona si quieres permitirlo
        Persona persona = paciente.getPersona();
        persona.setNombre(pacienteActualizado.getPersona().getNombre());
        persona.setApellido(pacienteActualizado.getPersona().getApellido());
        persona.setDni(pacienteActualizado.getPersona().getDni());
        persona.setEmail(pacienteActualizado.getPersona().getEmail());
        persona.setUsername(pacienteActualizado.getPersona().getUsername());
        persona.setPassword(pacienteActualizado.getPersona().getPassword());
        persona.setDomicilio(pacienteActualizado.getPersona().getDomicilio());
        persona.setTelefono(pacienteActualizado.getPersona().getTelefono());
        personaServicio.guardarPersona(persona);

        // Guardar paciente
        Paciente pacienteGuardado = pacienteServicio.guardarPaciente(paciente);

        return ResponseEntity.ok(pacienteGuardado);
    }

    // DELETE: baja logica paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<String> bajaLogicaPaciente(@PathVariable Long id) {
        // Buscar paciente
        Paciente paciente = pacienteServicio.buscarPacientePorId(id);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }

        // Cambiar estado de la persona asociada a Inactivo
        Persona persona = paciente.getPersona();
        persona.setIsActive("Inactivo");
        personaServicio.guardarPersona(persona);

        return ResponseEntity.ok("Paciente dado de baja lógicamente");

    }

}
