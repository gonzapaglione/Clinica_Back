package com.clinica.clinica_coc.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "cob_social")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoberturaSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cob_social;

    private String nombre_cobertura;

    // Relación inversa ManyToMany con Paciente
    @ManyToMany(mappedBy = "coberturas")
    private List<Paciente> pacientes;
}
