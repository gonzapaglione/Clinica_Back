package com.clinica.clinica_coc.services;

import java.util.List;

import com.clinica.clinica_coc.models.Persona;

public interface IPersonaServicio {

    public List<Persona> listarPersonas();

    public List<Persona> listarOdontologos();

    public Persona buscarPersonaPorId(Long id);

    public Persona buscarOdontologoPorId(Long id);

    // Crear y actualizar comparten el mismo metodo
    public Persona guardarPersona(Persona persona);

    public void darBajaPersona(Persona persona);

    // public Persona findByDni(Long DNI);
}
