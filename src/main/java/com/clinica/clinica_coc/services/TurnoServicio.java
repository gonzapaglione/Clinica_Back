package com.clinica.clinica_coc.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.clinica.clinica_coc.DTO.TurnoRequest;
import com.clinica.clinica_coc.DTO.TurnoResponse;
import com.clinica.clinica_coc.exceptions.ResourceNotFoundException;
import com.clinica.clinica_coc.models.Odontologo;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Turno;
import com.clinica.clinica_coc.repositories.OdontologoRepositorio;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;
import com.clinica.clinica_coc.repositories.TurnoRepositorio;

@Service
public class TurnoServicio {

    @Autowired
    private TurnoRepositorio turnoRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private OdontologoRepositorio odontologoRepositorio;

    @Transactional(readOnly = true)
    public List<TurnoResponse> listarTurnos() {
        return turnoRepositorio.findAll().stream()
                .map(this::mapTurnoToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TurnoResponse obtenerTurno(Long idTurno) {
        Turno turno = turnoRepositorio.findById(idTurno)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + idTurno));
        return mapTurnoToResponse(turno);
    }

    @Transactional(readOnly = true)
    public List<TurnoResponse> listarTurnosPorPaciente(Long idPaciente) {
        return turnoRepositorio.findByPacienteId(idPaciente).stream()
                .map(this::mapTurnoToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TurnoResponse> listarTurnosPorOdontologo(Long idOdontologo) {
        return turnoRepositorio.findByOdontologoId(idOdontologo).stream()
                .map(this::mapTurnoToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TurnoResponse crearTurno(TurnoRequest request) {
        Paciente paciente = pacienteRepositorio.findById(request.getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado con id: " + request.getIdPaciente()));

        Odontologo odontologo = odontologoRepositorio.findById(request.getIdOdontologo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Odontólogo no encontrado con id: " + request.getIdOdontologo()));

        if (request.getFechaHora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha y hora del turno es obligatoria");
        }

        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setOdontologo(odontologo);
        turno.setFechaHora(request.getFechaHora());
        turno.setEstadoTurno(validarEstado(request.getEstadoTurno()));
        turno.setMotivoConsulta(request.getMotivoConsulta());
        turno.setTratamiento(request.getTratamiento());
        turno.setEvolucion(request.getEvolucion());

        Turno turnoGuardado = turnoRepositorio.save(turno);
        return mapTurnoToResponse(turnoGuardado);
    }

    @Transactional
    public TurnoResponse actualizarTurno(Long idTurno, TurnoRequest request) {
        Turno turno = turnoRepositorio.findById(idTurno)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + idTurno));

        if (request.getIdPaciente() != null
                && !turno.getPaciente().getId_paciente().equals(request.getIdPaciente())) {
            Paciente nuevoPaciente = pacienteRepositorio.findById(request.getIdPaciente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Paciente no encontrado con id: " + request.getIdPaciente()));
            turno.setPaciente(nuevoPaciente);
        }

        if (request.getIdOdontologo() != null
                && !turno.getOdontologo().getId_odontologo().equals(request.getIdOdontologo())) {
            Odontologo nuevoOdontologo = odontologoRepositorio.findById(request.getIdOdontologo())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Odontólogo no encontrado con id: " + request.getIdOdontologo()));
            turno.setOdontologo(nuevoOdontologo);
        }

        if (request.getFechaHora() != null) {
            turno.setFechaHora(request.getFechaHora());
        }

        if (request.getEstadoTurno() != null) {
            turno.setEstadoTurno(validarEstado(request.getEstadoTurno()));
        }

        turno.setMotivoConsulta(request.getMotivoConsulta());
        turno.setTratamiento(request.getTratamiento());
        turno.setEvolucion(request.getEvolucion());

        Turno turnoActualizado = turnoRepositorio.save(turno);
        return mapTurnoToResponse(turnoActualizado);
    }

    @Transactional
    public void eliminarTurno(Long idTurno) {
        if (!turnoRepositorio.existsById(idTurno)) {
            throw new ResourceNotFoundException("Turno no encontrado con id: " + idTurno);
        }
        turnoRepositorio.deleteById(idTurno);
    }

    private String validarEstado(String estado) {
        String estadoNormalizado = estado == null ? "Pendiente" : estado.trim();
        if (estadoNormalizado.isEmpty()) {
            estadoNormalizado = "Pendiente";
        }
        if (estadoNormalizado.length() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El estado del turno no puede superar los 10 caracteres");
        }
        return estadoNormalizado;
    }

    private TurnoResponse mapTurnoToResponse(Turno turno) {
        Paciente paciente = turno.getPaciente();
        Odontologo odontologo = turno.getOdontologo();

        return TurnoResponse.builder()
                .id_turno(turno.getId_turno())
                .id_paciente(paciente != null ? paciente.getId_paciente() : null)
                .id_odontologo(odontologo != null ? odontologo.getId_odontologo() : null)
                .fechaHora(turno.getFechaHora())
                .estadoTurno(turno.getEstadoTurno())
                .motivoConsulta(turno.getMotivoConsulta())
                .tratamiento(turno.getTratamiento())
                .evolucion(turno.getEvolucion())
                .build();
    }
}
