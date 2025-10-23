package com.clinica.clinica_coc.controllers;

import com.clinica.clinica_coc.DTO.CoberturaSocialDTO;
import com.clinica.clinica_coc.services.CoberturaSocialServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coberturas")
@CrossOrigin(origins = "http://localhost:5173")
public class CoberturaSocialController {

    @Autowired
    private CoberturaSocialServicio coberturaSocialServicio;

    @GetMapping
    public ResponseEntity<?> listarCoberturas() {
        try {
            List<CoberturaSocialDTO> lista = coberturaSocialServicio.findAllDtos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al listar coberturas: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCoberturaPorId(@PathVariable Long id) {
        try {
            CoberturaSocialDTO dto = coberturaSocialServicio.findDtoById(id);
            if (dto == null) {
                return ResponseEntity.status(404).body("Cobertura no encontrada");
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener cobertura: " + e.getMessage());
        }
    }
}
