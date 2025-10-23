package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.DTO.PacienteRequest;
import com.clinica.clinica_coc.DTO.PersonaRequest;
import com.clinica.clinica_coc.models.CoberturaSocial;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.models.Rol;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;
import com.clinica.clinica_coc.repositories.RolRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PacienteServicio implements IPacienteServicio {

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private PersonaServicio personaServicio;

    @Autowired
    private CoberturaSocialServicio coberturaServicio;

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

    public Paciente crearPacienteConPersonaYRol(PersonaRequest personaRequest, List<Long> coberturasIds) {
        // 1. Crear Persona a partir de PersonaRequest
        Persona persona = new Persona();
        persona.setNombre(personaRequest.getNombre());
        persona.setApellido(personaRequest.getApellido());
        persona.setDni(personaRequest.getDni());
        persona.setEmail(personaRequest.getEmail());
        persona.setPassword(personaRequest.getPassword());
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
        paciente.setCoberturas(coberturas);

        return pacienteRepositorio.save(paciente);
    }

    public Paciente editarPaciente(Long id, PacienteRequest request) {

        // 1. Buscar paciente
        Paciente paciente = pacienteRepositorio.findById(id).orElse(null);
        if (paciente == null)
            return null;

        // 2. Actualizar datos de la persona
        Persona persona = paciente.getPersona();
        if (request.getPersona() != null) {
            persona.setNombre(request.getPersona().getNombre());
            persona.setApellido(request.getPersona().getApellido());
            persona.setDni(request.getPersona().getDni());
            persona.setEmail(request.getPersona().getEmail());
            persona.setTelefono(request.getPersona().getTelefono());
            persona.setDomicilio(request.getPersona().getDomicilio());
            if (request.getPersona().getPassword() != null) {
                persona.setPassword(request.getPersona().getPassword());
            }
            if (request.getPersona().getIsActive() != null) {
                persona.setIsActive(request.getPersona().getIsActive());
            }
            personaServicio.guardarPersona(persona); // se guarda usando el servicio
        }

        // 3. Actualizar coberturas si vienen
        if (request.getCoberturasIds() != null && !request.getCoberturasIds().isEmpty()) {
            List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(request.getCoberturasIds());
            paciente.setCoberturas(coberturas);
        }

        // 4. Guardar paciente
        return pacienteRepositorio.save(paciente);
    }

}
