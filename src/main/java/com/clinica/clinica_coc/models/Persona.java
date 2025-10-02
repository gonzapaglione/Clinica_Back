package com.clinica.clinica_coc.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long dni;

    private String email;
    private String username;
    private String password;

    private String domicilio;
    private String telefono;
    
    @Column(name = "isActive", nullable = false, columnDefinition = "ENUM('Activo','Inactivo') default 'Activo'")
    private String isActive = "Activo";
}
