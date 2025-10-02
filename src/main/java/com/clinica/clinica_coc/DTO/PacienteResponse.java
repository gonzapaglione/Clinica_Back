package com.clinica.clinica_coc.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponse {

    private Long idPaciente;
    private String nombre;
    private String apellido;
    private Long DNI;
    private String email;
    private String telefono;
    private String estado;
    private List<String> coberturas;  // solo los nombres de las coberturas
}
