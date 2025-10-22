package com.clinica.clinica_coc.controllers;

import com.clinica.clinica_coc.DTO.EspecialidadDTO;
import com.clinica.clinica_coc.services.EspecialidadServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "http://localhost:5173")
public class EspecialidadController {

    @Autowired
    private EspecialidadServicio especialidadServicio;

    @GetMapping
    public ResponseEntity<?> listarEspecialidades() {
        try {
            List<EspecialidadDTO> lista = especialidadServicio.findAllDtos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al listar especialidades: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEspecialidadPorId(@PathVariable Long id) {
        try {
            EspecialidadDTO dto = especialidadServicio.findDtoById(id);
            if (dto == null) {
                return ResponseEntity.status(404).body("Especialidad no encontrada");
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener especialidad: " + e.getMessage());
        }
    }
}
