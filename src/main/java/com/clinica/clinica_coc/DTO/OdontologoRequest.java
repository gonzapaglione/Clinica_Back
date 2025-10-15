package com.clinica.clinica_coc.DTO;

import com.clinica.clinica_coc.models.Persona;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OdontologoRequest {
    private Persona persona;
    private List<Long> especialidadesIds;
}
