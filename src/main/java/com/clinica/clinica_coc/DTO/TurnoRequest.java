package com.clinica.clinica_coc.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequest {
    private Long idPaciente;
    private Long idOdontologo;
    private LocalDateTime fechaHora;
    private String estadoTurno;
    private String motivoConsulta;
    private String tratamiento;
    private String evolucion;
}
