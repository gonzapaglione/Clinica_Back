package com.clinica.clinica_coc.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")

@Data // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING) // guarda el nombre del enum (ej: "ADMIN") en la BD
    private Rol rol;
    private String email;

    @Column(name = "id_persona")
    private Long id_persona;

}