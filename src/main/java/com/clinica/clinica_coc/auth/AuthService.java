package com.clinica.clinica_coc.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.DTO.OdontologoRequest;
import com.clinica.clinica_coc.DTO.RegisterRecepcionistaRequest;
import com.clinica.clinica_coc.jwt.JwtService;
import com.clinica.clinica_coc.models.CoberturaSocial;
import com.clinica.clinica_coc.repositories.PersonaRepositorio;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;
import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.models.Rol;
import com.clinica.clinica_coc.repositories.RolRepositorio;
import com.clinica.clinica_coc.services.PersonaRolServicio;
import com.clinica.clinica_coc.services.CoberturaSocialServicio;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final PersonaRepositorio personaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final CoberturaSocialServicio coberturaServicio;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepositorio rolRepositorio;
    private final PersonaRolServicio personaRolServicio;
    private final com.clinica.clinica_coc.services.OdontologoServicio odontologoServicio;

    public AuthResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails user = personaRepositorio.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);
        // try to extract id from user if it's a Persona
        Long idPersona = null;
        if (user instanceof Persona) {
            idPersona = ((Persona) user).getId_persona();
        }
        Long idPaciente = null;
        if (idPersona != null) {
            var pacienteOpt = pacienteRepositorio.findByPersona_Id_persona(idPersona);
            if (pacienteOpt.isPresent()) {
                idPaciente = pacienteOpt.get().getId_paciente();
            }
        }
        return AuthResponse.builder().token(token).id_persona(idPersona).id_paciente(idPaciente).build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1) Crear y guardar Persona a partir de PersonaRequest dentro de
        // RegisterRequest
        var personaReq = request.getPersona();
        if (personaReq == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "PersonaRequest es requerido en el cuerpo de registro");
        }

        Persona persona = new Persona();
        persona.setNombre(personaReq.getNombre());
        persona.setApellido(personaReq.getApellido());
        persona.setEmail(personaReq.getEmail());
        persona.setPassword(passwordEncoder.encode(personaReq.getPassword()));
        persona.setDomicilio(personaReq.getDomicilio());
        persona.setTelefono(personaReq.getTelefono());
        persona.setDni(personaReq.getDni());

        Persona savedPersona = personaRepositorio.save(persona);

        // 2) Crear Paciente asociado y asignar coberturas si vienen ids
        Paciente paciente = new Paciente();
        paciente.setPersona(savedPersona);

        if (request.getCoberturaIds() != null && !request.getCoberturaIds().isEmpty()) {
            List<CoberturaSocial> coberturas = coberturaServicio.buscarPorIds(request.getCoberturaIds());
            paciente.setCoberturas(coberturas);
        } else {
            paciente.setCoberturas(new ArrayList<>());
        }

        pacienteRepositorio.save(paciente);

        // 3) Asignar rol de Paciente a la Persona (id_rol = 1)
        Rol rolPaciente = rolRepositorio.findById(1L)
                .orElseThrow(() -> new RuntimeException("Rol Paciente no encontrado (esperado id=1)"));
        PersonaRol personaRol = new PersonaRol();
        personaRol.setIdPersona(savedPersona);
        personaRol.setIdRol(rolPaciente);
        personaRolServicio.guardar(personaRol);

        // 4) Generar token y devolver token + paciente
        String token = jwtService.getToken(savedPersona);
        return AuthResponse.builder().token(token).id_persona(savedPersona.getId_persona()).build();
    }

    @Transactional
    public AuthResponse registerOdontologo(OdontologoRequest request) {
        // Delegar la creación al servicio de odontologo
        var odontologo = odontologoServicio.crearOdontologoConPersonaYRol(request.getPersona(),
                request.getEspecialidadesIds());
        // Generar token para la persona creada
        String token = jwtService.getToken(odontologo.getPersona());
        return AuthResponse.builder().token(token).id_persona(odontologo.getPersona().getId_persona()).build();
    }

    @Transactional
    public AuthResponse registerRecepcionista(RegisterRecepcionistaRequest request) {
        // Crear persona a partir de PersonaRequest dentro de
        // RegisterRecepcionistaRequest
        var personaReq = request.getPersona();
        if (personaReq == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "PersonaRequest es requerido en el cuerpo de registro");
        }

        // No hay campo cobertura en RegisterRecepcionistaRequest por diseño

        Persona persona = new Persona();
        persona.setNombre(personaReq.getNombre());
        persona.setApellido(personaReq.getApellido());
        persona.setEmail(personaReq.getEmail());
        persona.setPassword(passwordEncoder.encode(personaReq.getPassword()));
        persona.setDomicilio(personaReq.getDomicilio());
        persona.setTelefono(personaReq.getTelefono());
        persona.setDni(personaReq.getDni());

        Persona savedPersona = personaRepositorio.save(persona);

        // Asignar rol Recepcionista (id_rol = 4)
        Rol rolRecep = rolRepositorio.findById(4L)
                .orElseThrow(() -> new RuntimeException("Rol Recepcionista no encontrado (esperado id=4)"));
        PersonaRol personaRol = new PersonaRol();
        personaRol.setIdPersona(savedPersona);
        personaRol.setIdRol(rolRecep);
        personaRolServicio.guardar(personaRol);

        return AuthResponse.builder().token(jwtService.getToken(savedPersona)).id_persona(savedPersona.getId_persona()).build();
    }

}
