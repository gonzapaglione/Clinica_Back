package com.clinica.clinica_coc.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.clinica.clinica_coc.DTO.TurnoRequest;
import com.clinica.clinica_coc.DTO.TurnoResponse;
import com.clinica.clinica_coc.services.TurnoServicio;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "http://localhost:5173")
public class TurnoController {

    @Autowired
    private TurnoServicio turnoServicio;

    @GetMapping
    public ResponseEntity<List<TurnoResponse>> listarTurnos() {
        List<TurnoResponse> turnos = turnoServicio.listarTurnos();
        if (turnos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(turnos);
    }

@GetMapping("/buscar")
    public ResponseEntity<List<TurnoResponse>> buscarTurnos(
            @RequestParam(required = false) String paciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) List<String> estados,
            @RequestParam(required = false) Long odontologoId,
            @RequestParam(required = false, defaultValue = "DESC") String orden) {

        List<TurnoResponse> turnos = turnoServicio.buscarTurnosConFiltros(paciente, fechaInicio, fechaFin, estados, odontologoId, orden);

        if (turnos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/buscarPorMes")
    public  ResponseEntity<List<TurnoResponse>> buscarTurnosPorCriterios(
            @RequestParam Long odontologoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
            ){ 
            
            List<TurnoResponse> turnosEncontrados = turnoServicio.buscarTurnosPorMes(
                    odontologoId, fechaInicio, fechaFin
            );
            System.out.println("Turnos encontrados: "+ turnosEncontrados.size());

            if (turnosEncontrados.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            
            // Devolver 200 OK con los turnos
            return ResponseEntity.ok(turnosEncontrados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> obtenerTurno(@PathVariable Long id) {
        TurnoResponse turno = turnoServicio.obtenerTurno(id);
        return ResponseEntity.ok(turno);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<TurnoResponse>> listarTurnosPorPaciente(@PathVariable Long idPaciente) {
        List<TurnoResponse> turnos = turnoServicio.listarTurnosPorPaciente(idPaciente);
        if (turnos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/odontologo/{idOdontologo}")
    public ResponseEntity<List<TurnoResponse>> listarTurnosPorOdontologoYFecha(
            @PathVariable Long idOdontologo,
            @RequestParam("fecha") String fecha) {

        List<TurnoResponse> turnos = turnoServicio.listarTurnosPorOdontologoYFecha(idOdontologo, fecha);
        
        if (turnos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(turnos);
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> crearTurno(@RequestBody TurnoRequest request) {
        TurnoResponse turnoCreado = turnoServicio.crearTurno(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponse> actualizarTurno(@PathVariable Long id, @RequestBody TurnoRequest request) {
        TurnoResponse turnoActualizado = turnoServicio.actualizarTurno(id, request);
        return ResponseEntity.ok(turnoActualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TurnoResponse> actualizarParcialmenteTurno(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> campos) {
        
        TurnoResponse turnoActualizado = turnoServicio.actualizarTurnoParcial(id, campos);
        return ResponseEntity.ok(turnoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        turnoServicio.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }
}
