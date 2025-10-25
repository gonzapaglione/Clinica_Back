package com.clinica.clinica_coc.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.jwt.JwtService;
import com.clinica.clinica_coc.models.CoberturaSocial;
import com.clinica.clinica_coc.models.Rol;
import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.repositories.PersonaRepositorio;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;
import com.clinica.clinica_coc.repositories.RolRepositorio;
import com.clinica.clinica_coc.repositories.PersonaRolRepositorio;
import com.clinica.clinica_coc.services.CoberturaSocialServicio;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final PersonaRepositorio personaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final CoberturaSocialServicio coberturaServicio;
    private final RolRepositorio rolRepositorio;
    private final PersonaRolRepositorio personaRolRepositorio;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails = personaRepositorio.findByEmailWithRoles(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado post-autenticación"));
        Persona persona = (Persona) userDetails;
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("idUsuario", persona.getId_persona());
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(java.util.stream.Collectors.toList()));

        // 5. Genera el token USANDO los claims
        String token = jwtService.getToken(extraClaims, userDetails);
        return AuthResponse.builder().token(token).build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1) Crear y guardar Persona (aquí no configuré PasswordEncoder — añade según
        // tu SecurityConfig)
        Persona persona = new Persona();
        persona.setNombre(request.getNombre());
        persona.setApellido(request.getApellido());
        persona.setEmail(request.getEmail());
        persona.setPassword(passwordEncoder.encode(request.getPassword()));
        persona.setDomicilio(request.getDomicilio());
        persona.setTelefono(request.getTelefono());
        persona.setDni(request.getDni());

        Persona savedPersona = personaRepositorio.save(persona);
        Rol rolPaciente = rolRepositorio.findById(1L)
                .orElseThrow(() -> new RuntimeException("Error: Rol PACIENTE no encontrado en la base de datos."));

        // b. Crear la relación en la tabla persona_rol
        PersonaRol personaRol = new PersonaRol();
        personaRol.setIdPersona(savedPersona); // Vincula a la persona recién creada
        personaRol.setIdRol(rolPaciente); // Vincula al rol Paciente

        // c. Guardar la relación
        personaRolRepositorio.save(personaRol);

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

        // 3) Generar token o devolver respuesta básica
        return AuthResponse.builder().token(jwtService.getToken(savedPersona)).build();
    }

}
