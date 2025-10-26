package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.models.Especialidad;
import com.clinica.clinica_coc.repositories.EspecialidadRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.clinica.clinica_coc.DTO.EspecialidadDTO;

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

    // devolver DTOs con los nombres exactos esperados por el front
    public List<EspecialidadDTO> findAllDtos() {
        return especialidadRepositorio.findAll().stream()
                .map(e -> new EspecialidadDTO(e.getId_especialidad(), e.getNombre()))
                .collect(Collectors.toList());
    }

    // devolver DTO por id
    public EspecialidadDTO findDtoById(Long id) {
        Especialidad e = buscarPorId(id);
        if (e == null)
            return null;
        return new EspecialidadDTO(e.getId_especialidad(), e.getNombre());
    }

}
