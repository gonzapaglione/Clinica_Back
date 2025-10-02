package com.clinica.clinica_coc.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequest {

    private Long personaId;
    private List<Long> coberturasIds;
}
