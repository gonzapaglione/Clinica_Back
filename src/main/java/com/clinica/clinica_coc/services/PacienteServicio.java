package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.DTO.PacienteRequest;
import com.clinica.clinica_coc.DTO.PersonaRequest;
import com.clinica.clinica_coc.models.CoberturaSocial;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.models.Rol;
import com.clinica.clinica_coc.repositories.CoberturaSocialRepositorio;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;
import com.clinica.clinica_coc.repositories.RolRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PacienteServicio implements IPacienteServicio {

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private com.clinica.clinica_coc.repositories.PersonaRolRepositorio personaRolRepositorio;

    @Autowired
    private PersonaServicio personaServicio;

    @Autowired
    private CoberturaSocialServicio coberturaServicio;

    @Autowired
    private CoberturaSocialRepositorio coberturaSocialRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PersonaRolServicio personaRolServicio;

    @Autowired
    private RolRepositorio rolRepositorio; // nuevo servicio para roles

    @Override
    public List<Paciente> listarPacientes() {
        return pacienteRepositorio.findAll();
    }

    @Override
    public Paciente buscarPacientePorId(Long id) {
        return pacienteRepositorio.findById(id).orElse(null);
    }

    @Override
    public Paciente guardarPaciente(Paciente paciente) {
        if (paciente.getPersona() == null || paciente.getPersona().getId_persona() == null) {
            throw new RuntimeException("Debe asignarse una persona existente al paciente");
        }
        return pacienteRepositorio.save(paciente);
    }

    @Override
    public void eliminarPaciente(Paciente paciente) {
        pacienteRepositorio.delete(paciente);
    }

    @Transactional
    public Paciente bajaLogicaPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepositorio.findById(idPaciente)
                .orElse(null);

        if (paciente == null) {
            return null;
        }

        Persona persona = paciente.getPersona();
        if (persona == null) {
            throw new RuntimeException("El paciente no tiene una persona asociada.");
        }

        // Realizar baja lógica sobre el estado del paciente
        paciente.setEstado_paciente("Inactivo");
        pacienteRepositorio.save(paciente);

        // Eliminar la tupla persona_rol correspondiente al rol Paciente (id 1L)
        try {
            Long idPersona = persona.getId_persona();
            Long idRolPaciente = 1L; // 1L = Paciente
            java.util.List<com.clinica.clinica_coc.models.PersonaRol> rolesAEliminar = personaRolRepositorio
                    .findSpecificRolesForPersona(idPersona, idRolPaciente);
            if (rolesAEliminar != null && !rolesAEliminar.isEmpty()) {
                personaRolServicio.eliminarTodos(rolesAEliminar);
            }
        } catch (Exception e) {
            // Registrar/log si es necesario; no interrumpir la baja lógica
            System.out.println("Advertencia al eliminar persona_rol en baja de paciente: " + e.getMessage());
        }

        return paciente;
    }

    public Paciente crearPacienteConPersonaYRol(PersonaRequest personaRequest, List<Long> coberturasIds, String estadoPaciente) {
        // 1. Crear Persona a partir de PersonaRequest
        Persona persona = new Persona();
        persona.setNombre(personaRequest.getNombre());
        persona.setApellido(personaRequest.getApellido());
        persona.setDni(personaRequest.getDni());
        persona.setEmail(personaRequest.getEmail());
        persona.setPassword(passwordEncoder.encode(personaRequest.getPassword()));
        persona.setDomicilio(personaRequest.getDomicilio());
        persona.setTelefono(personaRequest.getTelefono());
        persona.setIsActive("Activo");
        persona = personaServicio.guardarPersona(persona);

        // 2. Asignar rol "Paciente"
        Rol rolPaciente = rolRepositorio.findById(1L)
                .orElseThrow(() -> new RuntimeException("Rol paciente no encontrado"));
        PersonaRol personaRol = new PersonaRol();
        personaRol.setIdPersona(persona);
        personaRol.setIdRol(rolPaciente);
        personaRolServicio.guardar(personaRol);

        // 3. Buscar coberturas usando el servicio
        List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(coberturasIds);

        // 4. Crear paciente
        Paciente paciente = new Paciente();
        paciente.setPersona(persona);
        // Establecer estado del paciente: si el request trae uno, usarlo; si no, default a Activo
        if (estadoPaciente != null && !estadoPaciente.trim().isEmpty()) {
            paciente.setEstado_paciente(estadoPaciente);
        } else {
            paciente.setEstado_paciente("Activo");
        }
        paciente.setCoberturas(coberturas);

        return pacienteRepositorio.save(paciente);
    }

    @Transactional
    public Paciente editarPaciente(Long id, PacienteRequest request) {

        // 1. Buscar paciente
        Paciente paciente = pacienteRepositorio.findById(id).orElse(null);
        if (paciente == null)
            return null;

        // 2. Actualizar datos de la persona
        Persona persona = paciente.getPersona();
        if (request.getPersona() != null) {
            personaServicio.editarPersona(persona.getId_persona(), request.getPersona());
        }
        List<Long> coberturasIds = request.getCoberturasIds(); // Obtén los IDs del request

        // Inicializa la lista en el paciente si es null para evitar
        // NullPointerException
        if (paciente.getCoberturas() == null) {
            paciente.setCoberturas(new ArrayList<>());
        }

        // Limpia las coberturas anteriores
        paciente.getCoberturas().clear();

        // Si se proporcionaron nuevos IDs de cobertura...
        if (coberturasIds != null && !coberturasIds.isEmpty()) {
            List<CoberturaSocial> nuevasCoberturas = coberturaSocialRepositorio.findAllById(coberturasIds);
            // Añade las nuevas coberturas a la lista del paciente
            paciente.getCoberturas().addAll(nuevasCoberturas);
        }

        // 4. Guardar paciente
        return pacienteRepositorio.save(paciente);
    }

    public Paciente buscarPacientePorIdPersona(Long idPersona) {
        return pacienteRepositorio.findByPersonaId(idPersona)
                .orElse(null);
    }

}
