package com.clinica.clinica_coc.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "especialidad_odontologo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadOdontologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_esp_odo;

    @JoinColumn(name = "id_especialidad", referencedColumnName = "id_especialidad", nullable = false)
    @ManyToOne(optional = false)
    private Especialidad idEspecialidad;

    @JoinColumn(name = "id_odontologo", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(optional = false)
    private Persona idOdontologo;

}
