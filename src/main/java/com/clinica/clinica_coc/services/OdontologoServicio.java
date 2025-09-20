package com.clinica.clinica_coc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinica.clinica_coc.models.Odontologo;
import com.clinica.clinica_coc.repositories.OdontologoRepositorio;

@Service
public class OdontologoServicio {

    @Autowired
    private OdontologoRepositorio odontologoRepositorio;

    public List<Odontologo> listarOdontologos() {
        return odontologoRepositorio.findAll();
    }

    public Odontologo guardarOdontologo(Odontologo paciente) {
        return odontologoRepositorio.save(paciente);
    }

    public void eliminarOdontologo(Long id) {
        odontologoRepositorio.deleteById(id);
    }

    public Odontologo odontologoPorId(Long id) {
        return odontologoRepositorio.findById(id).orElse(null);

    }
}