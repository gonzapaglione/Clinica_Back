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
public class TurnoResponse {
    private Long id_turno;
    private Long id_paciente;
    private Long id_odontologo;
    private LocalDateTime fechaHora;
    private String estadoTurno;
    private String motivoConsulta;
    private String tratamiento;
    private String evolucion;
}
