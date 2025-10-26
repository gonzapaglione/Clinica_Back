package com.clinica.clinica_coc.services;

import com.clinica.clinica_coc.DTO.CoberturaSocialDTO;
import com.clinica.clinica_coc.models.CoberturaSocial;
import com.clinica.clinica_coc.repositories.CoberturaSocialRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoberturaSocialServicio implements ICoberturaSocialServicio {

    @Autowired
    private CoberturaSocialRepositorio coberturaRepositorio;

    @Override
    public List<CoberturaSocial> listarCoberturas() {
        return coberturaRepositorio.findAll();
    }

    @Override
    public CoberturaSocial buscarPorId(Long id) {
        return coberturaRepositorio.findById(id).orElse(null);
    }

    @Override
    public List<CoberturaSocial> buscarPorIds(List<Long> ids) {
        return coberturaRepositorio.findAllById(ids);
    }

    @Override
    public CoberturaSocial guardarCobertura(CoberturaSocial cobertura) {
        return coberturaRepositorio.save(cobertura);
    }

    @Override
    public void eliminarCobertura(Long id) {
        if (coberturaRepositorio.existsById(id)) {
            coberturaRepositorio.deleteById(id);
        }
    }

    // Devolver DTOs con los nombres exactos esperados por el front
    public List<CoberturaSocialDTO> findAllDtos() {
        return coberturaRepositorio.findAll().stream()
                .map(c -> new CoberturaSocialDTO(c.getId_cob_social(), c.getNombre_cobertura()))
                .collect(Collectors.toList());
    }

    // Devolver DTO por id
    public CoberturaSocialDTO findDtoById(Long id) {
        CoberturaSocial c = buscarPorId(id);
        if (c == null)
            return null;
        return new CoberturaSocialDTO(c.getId_cob_social(), c.getNombre_cobertura());
    }
}
