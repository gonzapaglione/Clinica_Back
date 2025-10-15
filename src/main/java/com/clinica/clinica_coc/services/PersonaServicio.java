package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.repositories.PersonaRepositorio;
import com.clinica.clinica_coc.repositories.RolRepositorio;
import com.clinica.clinica_coc.repositories.EspecialidadOdontologoRepositorio;
import com.clinica.clinica_coc.models.EspecialidadOdontologo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonaServicio implements IPersonaServicio {

    @Autowired
    private PersonaRepositorio personaRepositorio;

    @Autowired
    private PersonaRolServicio personaRolServicio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private EspecialidadServicio especialidadServicio;

    @Autowired
    private EspecialidadOdontologoRepositorio especialidadOdontologoRepositorio;

    @Override
    public List<Persona> listarPersonas() {
        return personaRepositorio.findAll();
    }

    @Override
    public List<Persona> listarOdontologos() {
        return personaRepositorio.findAll().stream()
                .filter(persona -> persona.getPersonaRolList() != null &&
                        persona.getPersonaRolList().stream()
                                .anyMatch(pr -> pr.getIdRol().getId_rol() == 2))
                .toList();
    }

    @Override
    public Persona buscarOdontologoPorId(Long id) {
        return personaRepositorio.findById(id)
                .filter(persona -> persona.getPersonaRolList() != null &&
                        persona.getPersonaRolList().stream()
                                .anyMatch(pr -> pr.getIdRol().getId_rol().equals(2L)))
                .orElse(null); // Devuelve null si no tiene el rol de odontólogo o no existe
    }

    @Override
    public Persona buscarPersonaPorId(Long id) {
        return personaRepositorio.findById(id).orElse(null);
    }

    @Override
    public Persona guardarPersona(Persona persona) {
        return personaRepositorio.save(persona);
    }

    public Persona crearOdontologoConPersonaYRol(Persona persona, List<Long> especialidadesIds) {
        // 1. Guardar persona
        persona.setIsActive("Activo");
        persona = personaRepositorio.save(persona);

        // 2. Asignar rol "Odontólogo" (id 2)
        var rolOdontologo = rolRepositorio.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol odontólogo no encontrado"));
        var personaRol = new com.clinica.clinica_coc.models.PersonaRol();
        personaRol.setIdPersona(persona);
        personaRol.setIdRol(rolOdontologo);
        personaRolServicio.guardar(personaRol);

        // 3. Buscar especialidades
        var especialidades = especialidadServicio.buscarPorIds(especialidadesIds);

        // 4. Crear relaciones EspecialidadOdontologo y persistirlas
        if (especialidades != null && !especialidades.isEmpty()) {
            var listaGuardada = new java.util.ArrayList<EspecialidadOdontologo>();
            for (var esp : especialidades) {
                var eo = new EspecialidadOdontologo();
                eo.setIdEspecialidad(esp);
                eo.setIdOdontologo(persona);
                var eoGuardado = especialidadOdontologoRepositorio.save(eo);
                listaGuardada.add(eoGuardado);
            }
            // asignar la lista guardada a la persona para que esté presente en la respuesta
            persona.setEspecialidadOdontologoList(listaGuardada);
        }

        // 5. Recargar persona para incluir relaciones y devolverla (fallback)
        return personaRepositorio.findById(persona.getId_persona()).orElse(persona);
    }

    public Persona editarOdontologoConPersonaYRol(Long id, Persona personaActualizada, List<Long> especialidadesIds) {
        Persona persona = personaRepositorio.findById(id).orElse(null);
        if (persona == null)
            return null;

        // actualizar campos básicos
        persona.setNombre(personaActualizada.getNombre());
        persona.setApellido(personaActualizada.getApellido());
        persona.setDni(personaActualizada.getDni());
        persona.setEmail(personaActualizada.getEmail());
        if (personaActualizada.getPassword() != null)
            persona.setPassword(personaActualizada.getPassword());
        persona.setDomicilio(personaActualizada.getDomicilio());
        persona.setTelefono(personaActualizada.getTelefono());
        persona.setIsActive(personaActualizada.getIsActive());

        persona = personaRepositorio.save(persona);

        // Eliminar relaciones previas de especialidad
        var relacionesPrevias = persona.getEspecialidadOdontologoList();
        if (relacionesPrevias != null && !relacionesPrevias.isEmpty()) {
            especialidadOdontologoRepositorio.deleteAll(relacionesPrevias);
            persona.setEspecialidadOdontologoList(new java.util.ArrayList<>());
        }

        // Crear y guardar nuevas relaciones
        if (especialidadesIds != null && !especialidadesIds.isEmpty()) {
            var especialidades = especialidadServicio.buscarPorIds(especialidadesIds);
            var listaGuardada = new java.util.ArrayList<com.clinica.clinica_coc.models.EspecialidadOdontologo>();
            for (var esp : especialidades) {
                var eo = new com.clinica.clinica_coc.models.EspecialidadOdontologo();
                eo.setIdEspecialidad(esp);
                eo.setIdOdontologo(persona);
                var eoGuardado = especialidadOdontologoRepositorio.save(eo);
                listaGuardada.add(eoGuardado);
            }
            persona.setEspecialidadOdontologoList(listaGuardada);
        }

        return personaRepositorio.findById(persona.getId_persona()).orElse(persona);
    }

    @Override
    public void darBajaPersona(Persona persona) {
        persona.setIsActive("Inactivo");
        personaRepositorio.save(persona);
    }

}