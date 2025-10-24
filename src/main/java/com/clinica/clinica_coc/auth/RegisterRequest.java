package com.clinica.clinica_coc.auth;

import java.util.List;

import com.clinica.clinica_coc.DTO.PersonaRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    // Reutilizamos PersonaRequest para mantener el mismo formato del registro de
    // odontólogo
    private PersonaRequest persona;
    // IDs de coberturas para pacientes
    private List<Long> coberturaIds;

}
