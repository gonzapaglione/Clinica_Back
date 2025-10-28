package com.clinica.clinica_coc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import com.clinica.clinica_coc.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/coberturas").permitAll()
                        .requestMatchers("/api/especialidades").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/personas/cambiar-password").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/persona/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/persona/**").hasAnyAuthority("Paciente")
                        .requestMatchers(HttpMethod.GET, "/api/turnos/paciente/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/odontologos/persona/**").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.GET, "/api/horarios/**").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.POST, "/api/horarios").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/horarios/**").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/horarios/**").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.GET, "/api/turnos/**").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.GET, "/api/turnos/buscar").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.GET, "/api/turnos/buscarPorMes").hasAnyAuthority("Odontologo", "Admin")
                        .requestMatchers(HttpMethod.POST, "/api/paciente").hasAnyAuthority("Admin")
                        .requestMatchers(HttpMethod.POST, "/api/personas").hasAnyAuthority("Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/personas").hasAnyAuthority("Admin")
                        .requestMatchers(HttpMethod.GET, "/api/personas").hasAnyAuthority("Admin")
                        .requestMatchers(HttpMethod.POST, "/api/turnos").hasAnyAuthority("Paciente")

                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
