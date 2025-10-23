package com.clinica.clinica_coc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinica.clinica_coc.DTO.PersonaRequest;
import com.clinica.clinica_coc.models.Especialidad;
import com.clinica.clinica_coc.models.EspecialidadOdontologo;
import com.clinica.clinica_coc.models.Odontologo;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.models.Rol;
import com.clinica.clinica_coc.repositories.EspecialidadOdontologoRepositorio;
import com.clinica.clinica_coc.repositories.EspecialidadRepositorio;
import com.clinica.clinica_coc.repositories.OdontologoRepositorio;
import com.clinica.clinica_coc.repositories.RolRepositorio;

@Service
public class OdontologoServicio implements IOdontologoServicio {

    @Autowired
    private OdontologoRepositorio odontologoRepositorio;

    @Autowired
    private PersonaServicio personaServicio;

    @Autowired
    private EspecialidadRepositorio especialidadRepositorio;

    @Autowired
    private EspecialidadOdontologoRepositorio especialidadOdontologoRepositorio;

    @Autowired
    private PersonaRolServicio personaRolServicio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Override
    public List<Odontologo> listarOdontologos() {
        return odontologoRepositorio.findAll();
    }

    @Override
    public Odontologo buscarOdontologoPorId(Long id) {
        return odontologoRepositorio.findById(id).orElse(null);
    }

    @Override
    public Odontologo guardarOdontologo(Odontologo odontologo) {
        if (odontologo.getPersona() == null || odontologo.getPersona().getId_persona() == null) {
            throw new RuntimeException("Debe asignarse una persona existente al odontólogo");
        }
        return odontologoRepositorio.save(odontologo);
    }

    @Override
    public void eliminarOdontologo(Odontologo odontologo) {
        odontologoRepositorio.delete(odontologo);
    }

    @Override
    public Odontologo crearOdontologoConPersonaYRol(PersonaRequest personaRequest, List<Long> especialidadesIds) {
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

        // 2. Asignar rol "Odontólogo"
        Rol rolOdontologo = rolRepositorio.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol odontólogo no encontrado"));
        PersonaRol personaRol = new PersonaRol();
        personaRol.setIdPersona(persona);
        personaRol.setIdRol(rolOdontologo);
        personaRolServicio.guardar(personaRol);

        // 3. Crear odontólogo
        Odontologo odontologo = new Odontologo();
        odontologo.setPersona(persona);
        odontologo = odontologoRepositorio.save(odontologo);

        // 4. Asignar especialidades si vienen
        if (especialidadesIds != null && !especialidadesIds.isEmpty()) {
            for (Long especialidadId : especialidadesIds) {
                Especialidad especialidad = especialidadRepositorio.findById(especialidadId)
                        .orElseThrow(
                                () -> new RuntimeException("Especialidad no encontrada con id: " + especialidadId));

                EspecialidadOdontologo especialidadOdontologo = new EspecialidadOdontologo();
                especialidadOdontologo.setOdontologo(odontologo);
                especialidadOdontologo.setEspecialidad(especialidad);
                especialidadOdontologoRepositorio.save(especialidadOdontologo);
            }
        }

        return odontologo;
    }

    public Odontologo editarOdontologo(Long id, PersonaRequest personaRequest, List<Long> especialidadesIds) {
        Odontologo odontologo = odontologoRepositorio.findById(id).orElse(null);
        if (odontologo == null)
            return null;

        // Actualizar datos de la persona
        Persona persona = odontologo.getPersona();
        if (personaRequest != null) {
            persona.setNombre(personaRequest.getNombre());
            persona.setApellido(personaRequest.getApellido());
            persona.setDni(personaRequest.getDni());
            persona.setEmail(personaRequest.getEmail());
            persona.setTelefono(personaRequest.getTelefono());
            persona.setDomicilio(personaRequest.getDomicilio());
            if (personaRequest.getPassword() != null) {
                persona.setPassword(personaRequest.getPassword());
            }
            if (personaRequest.getIsActive() != null) {
                persona.setIsActive(personaRequest.getIsActive());
            }
            personaServicio.guardarPersona(persona);
        }

        // Actualizar especialidades si vienen
        if (especialidadesIds != null && !especialidadesIds.isEmpty()) {
            // Eliminar relaciones previas
            List<EspecialidadOdontologo> relacionesPrevias = odontologo.getEspecialidadOdontologoList();
            if (relacionesPrevias != null && !relacionesPrevias.isEmpty()) {
                especialidadOdontologoRepositorio.deleteAll(relacionesPrevias);
            }

            // Crear nuevas relaciones
            for (Long especialidadId : especialidadesIds) {
                Especialidad especialidad = especialidadRepositorio.findById(especialidadId)
                        .orElseThrow(
                                () -> new RuntimeException("Especialidad no encontrada con id: " + especialidadId));

                EspecialidadOdontologo especialidadOdontologo = new EspecialidadOdontologo();
                especialidadOdontologo.setOdontologo(odontologo);
                especialidadOdontologo.setEspecialidad(especialidad);
                especialidadOdontologoRepositorio.save(especialidadOdontologo);
            }
        }

        return odontologoRepositorio.findById(id).orElse(null);
    }
}
