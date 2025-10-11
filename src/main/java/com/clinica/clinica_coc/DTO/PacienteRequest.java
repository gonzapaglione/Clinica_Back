package com.clinica.clinica_coc.DTO;

import java.util.List;

import com.clinica.clinica_coc.models.Persona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequest {

    private Persona persona;
    private List<Long> coberturasIds;
}
