package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.models.Especialidad;
import com.clinica.clinica_coc.repositories.EspecialidadRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadServicio {

    @Autowired
    private EspecialidadRepositorio especialidadRepositorio;

    public List<Especialidad> listarEspecialidades() {
        return especialidadRepositorio.findAll();
    }

    public Especialidad buscarPorId(Long id) {
        return especialidadRepositorio.findById(id).orElse(null);
    }

    public List<Especialidad> buscarPorIds(List<Long> ids) {
        return especialidadRepositorio.findAllById(ids);
    }

}
