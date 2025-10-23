package com.clinica.clinica_coc.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "persona")

@Data // genera getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor

public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_persona;

    private String nombre;
    private String apellido;
    @Column(name = "DNI", unique = true)
    private Long dni;

    private String email;
    private String password;

    private String domicilio;
    private String telefono;

    @Column(name = "is_active", nullable = false, columnDefinition = "ENUM('Activo','Inactivo') default 'Activo'")
    private String isActive = "Activo";

    @OneToMany(mappedBy = "idPersona")
    private List<PersonaRol> personaRolList = new ArrayList<>();

    // Nota: la relación entre odontólogo y especialidades ahora se maneja por las
    // entidades Odontologo y
    // EspecialidadOdontologo. Se eliminó la relación directa desde Persona.

}
