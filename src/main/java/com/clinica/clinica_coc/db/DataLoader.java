package com.clinica.clinica_coc.db;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import com.clinica.clinica_coc.models.CoberturaSocial;
import com.clinica.clinica_coc.models.DiaSemana;
import com.clinica.clinica_coc.models.Especialidad;
import com.clinica.clinica_coc.models.EspecialidadOdontologo;
import com.clinica.clinica_coc.models.Horario;
import com.clinica.clinica_coc.models.MotivoConsultaEnum;
import com.clinica.clinica_coc.models.Odontologo;
import com.clinica.clinica_coc.models.Paciente;
import com.clinica.clinica_coc.models.Persona;
import com.clinica.clinica_coc.models.PersonaRol;
import com.clinica.clinica_coc.models.Rol;
import com.clinica.clinica_coc.models.Turno;

import com.clinica.clinica_coc.repositories.CoberturaSocialRepositorio;
import com.clinica.clinica_coc.repositories.EspecialidadOdontologoRepositorio;
import com.clinica.clinica_coc.repositories.EspecialidadRepositorio;
import com.clinica.clinica_coc.repositories.HorarioRepositorio;
import com.clinica.clinica_coc.repositories.OdontologoRepositorio;
import com.clinica.clinica_coc.repositories.PacienteRepositorio;
import com.clinica.clinica_coc.repositories.PersonaRepositorio;
import com.clinica.clinica_coc.repositories.PersonaRolRepositorio;
import com.clinica.clinica_coc.repositories.RolRepositorio;
import com.clinica.clinica_coc.repositories.TurnoRepositorio;

@Component
public class DataLoader implements CommandLineRunner {

        @Autowired
        private RolRepositorio rolRepositorio;

        @Autowired
        private EspecialidadRepositorio especialidadRepositorio;

        @Autowired
        private CoberturaSocialRepositorio coberturaRepositorio;

        @Autowired
        private PersonaRepositorio personaRepositorio;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private PacienteRepositorio pacienteRepositorio;

        @Autowired
        private OdontologoRepositorio odontologoRepositorio;

        @Autowired
        private PersonaRolRepositorio personaRolRepositorio;

        @Autowired
        private EspecialidadOdontologoRepositorio especialidadOdontologoRepositorio;

        @Autowired
        private TurnoRepositorio turnoRepositorio;

        @Autowired
        private HorarioRepositorio horarioRepositorio;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                System.out.println(">>> Iniciando precarga de datos...");

                if (datosPrecargados()) {
                        System.out.println(">>> Datos de prueba ya existentes. Saltando precarga completa");
                        return;
                }

                precargarRoles();
                precargarEspecialidades();
                precargarCoberturas();

                precargarPersonasYUsuarios();
                crearHorariosDemo();
                System.out.println(">>> Precarga completada exitosamente");
        }

        private void precargarRoles() {
                if (rolRepositorio.count() == 0) {
                        rolRepositorio.saveAll(List.of(
                                        new Rol(null, "Paciente", new ArrayList<>()),
                                        new Rol(null, "Odontologo", new ArrayList<>()),
                                        new Rol(null, "Admin", new ArrayList<>())));
                        System.out.println("   - Roles precargados");
                }
        }

        private void precargarEspecialidades() {
                if (especialidadRepositorio.count() == 0) {
                        especialidadRepositorio.saveAll(List.of(
                                        new Especialidad(null, "Ortodoncia", "Activo"),
                                        new Especialidad(null, "Endodoncia", "Activo"),
                                        new Especialidad(null, "Periodoncia", "Activo"),
                                        new Especialidad(null, "Implantología", "Activo")));
                        System.out.println("   - Especialidades precargadas");
                }
        }

        private void precargarCoberturas() {
                if (coberturaRepositorio.count() == 0) {
                        coberturaRepositorio.saveAll(List.of(
                                        new CoberturaSocial(null, "OSDE", "Activo", new ArrayList<>()),
                                        new CoberturaSocial(null, "PAMI", "Activo", new ArrayList<>()),
                                        new CoberturaSocial(null, "Galeno", "Activo", new ArrayList<>()),
                                        new CoberturaSocial(null, "SwissMedical", "Activo", new ArrayList<>())));
                        System.out.println("   - Coberturas precargadas");
                }
        }

        private void precargarPersonasYUsuarios() {
                if (personaRepositorio.count() > 0) {
                        System.out.println("   - Personas ya existentes. Saltando precarga de usuarios.");
                        return;
                }

                PersonasIniciales personas = crearPersonasBasicas();
                PacientesIniciales pacientes = crearPacientes(personas);
                OdontologosIniciales odontologos = crearOdontologos(personas);

                asignarRoles(personas);
                asignarEspecialidades(odontologos);
                crearTurnosDemo(pacientes, odontologos);

                System.out.println("   - Personas, pacientes, odontólogos y turnos precargados");
        }

        private PersonasIniciales crearPersonasBasicas() {
                Persona per1 = new Persona(null, "Gonzalo", "Lopez", 30111222L, "paciente1@gmail.com",
                                passwordEncoder.encode("paciente"), "Av. Siempreviva 123", "111-222", "Activo",
                                new ArrayList<>());
                Persona per2 = new Persona(null, "Lourdes", "Guerrieri", 30122333L, "paciente2@gmail.com",
                                passwordEncoder.encode("paciente"), "Calle 9 456", "111-333", "Activo",
                                new ArrayList<>());
                Persona per3 = new Persona(null, "Lautaro", "Mercado", 30133444L, "paciente3@gmail.com",
                                passwordEncoder.encode("paciente"), "Bv. San Martín 12", "111-444", "Activo",
                                new ArrayList<>());
                Persona per4 = new Persona(null, "Dr. Diego", "Ruiz", 40111222L, "odontologo1@gmail.com",
                                passwordEncoder.encode("odontologo"), "Calle Doc 1", "222-111", "Activo",
                                new ArrayList<>());
                Persona per5 = new Persona(null, "Dra. Laura", "Sosa", 40122333L, "odontologo2@gmail.com",
                                passwordEncoder.encode("odontologo"), "Calle Doc 2", "222-333", "Activo",
                                new ArrayList<>());
                Persona per6 = new Persona(null, "Admin", "Sistema", 50000000L, "admin@gmail.com",
                                passwordEncoder.encode("administrador"), "Oficina", "000-000", "Activo",
                                new ArrayList<>());

                personaRepositorio.saveAll(List.of(per1, per2, per3, per4, per5, per6));
                return new PersonasIniciales(per1, per2, per3, per4, per5, per6);
        }

        private PacientesIniciales crearPacientes(PersonasIniciales personas) {
                Optional<CoberturaSocial> osdeOpt = coberturaRepositorio.findByNombreNativoConParam("OSDE");
                Optional<CoberturaSocial> pamiOpt = coberturaRepositorio.findByNombreNativoConParam("PAMI");
                CoberturaSocial osde = osdeOpt.orElse(null);
                CoberturaSocial pami = pamiOpt.orElse(null);

                Paciente pac1 = new Paciente(null, personas.paciente1(), "Activo",
                                osde != null ? List.of(osde) : new ArrayList<>());
                Paciente pac2 = new Paciente(null, personas.paciente2(), "Activo",
                                pami != null ? List.of(pami) : new ArrayList<>());
                Paciente pac3 = new Paciente(null, personas.paciente3(), "Activo",
                                osde != null && pami != null ? List.of(osde, pami) : new ArrayList<>());

                pacienteRepositorio.saveAll(List.of(pac1, pac2, pac3));
                return new PacientesIniciales(pac1, pac2, pac3, osde, pami);
        }

        private OdontologosIniciales crearOdontologos(PersonasIniciales personas) {
                Odontologo od1 = new Odontologo(null, personas.odontologo1(), new ArrayList<>(), "Activo");
                Odontologo od2 = new Odontologo(null, personas.odontologo2(), new ArrayList<>(), "Activo");

                odontologoRepositorio.saveAll(List.of(od1, od2));
                return new OdontologosIniciales(od1, od2);
        }

        private void asignarRoles(PersonasIniciales personas) {
                List<Rol> roles = rolRepositorio.findAll();
                Rol rolPaciente = roles.stream().filter(r -> "Paciente".equalsIgnoreCase(r.getNombre_rol()))
                                .findFirst().orElse(null);
                Rol rolOdontologo = roles.stream().filter(r -> "Odontologo".equalsIgnoreCase(r.getNombre_rol()))
                                .findFirst().orElse(null);
                Rol rolAdmin = roles.stream().filter(r -> "Admin".equalsIgnoreCase(r.getNombre_rol()))
                                .findFirst().orElse(null);

                List<PersonaRol> personaRoles = new ArrayList<>();
                if (rolPaciente != null) {
                        personaRoles.add(construirPersonaRol(personas.paciente1(), rolPaciente));
                        personaRoles.add(construirPersonaRol(personas.paciente2(), rolPaciente));
                        personaRoles.add(construirPersonaRol(personas.paciente3(), rolPaciente));
                }
                if (rolOdontologo != null) {
                        personaRoles.add(construirPersonaRol(personas.odontologo1(), rolOdontologo));
                        personaRoles.add(construirPersonaRol(personas.odontologo2(), rolOdontologo));
                }
                if (rolAdmin != null) {
                        personaRoles.add(construirPersonaRol(personas.admin(), rolAdmin));
                }

                if (!personaRoles.isEmpty()) {
                        personaRolRepositorio.saveAll(personaRoles);
                }
        }

        private void asignarEspecialidades(OdontologosIniciales odontologos) {
                Optional<Especialidad> orto = especialidadRepositorio.findByNombre("Ortodoncia");
                Optional<Especialidad> endo = especialidadRepositorio.findByNombre("Endodoncia");
                Especialidad ortodoncia = orto.orElse(null);
                Especialidad endodoncia = endo.orElse(null);

                List<EspecialidadOdontologo> especialidadesAsignadas = new ArrayList<>();
                if (ortodoncia != null) {
                        especialidadesAsignadas
                                        .add(new EspecialidadOdontologo(null, odontologos.odontologo1(), ortodoncia));
                        especialidadesAsignadas
                                        .add(new EspecialidadOdontologo(null, odontologos.odontologo2(), ortodoncia));
                }
                if (endodoncia != null) {
                        especialidadesAsignadas
                                        .add(new EspecialidadOdontologo(null, odontologos.odontologo2(), endodoncia));
                }
                if (!especialidadesAsignadas.isEmpty()) {
                        especialidadOdontologoRepositorio.saveAll(especialidadesAsignadas);
                }
        }

        private void crearTurnosDemo(PacientesIniciales pacientes, OdontologosIniciales odontologos) {
                List<Turno> turnos = new ArrayList<>();
                LocalDateTime momentoActual = LocalDateTime.now();
                turnos.add(new Turno(null, pacientes.paciente1(), odontologos.odontologo1(), pacientes.osde(),
                                momentoActual.minusDays(10).withHour(18).withMinute(0).withSecond(0).withNano(0),
                                "ATENDIDO", MotivoConsultaEnum.REVISION_PERIODICA, "Limpieza realizada", "OK"));
                turnos.add(new Turno(null, pacientes.paciente2(), odontologos.odontologo2(), pacientes.pami(),
                                momentoActual.plusDays(5).withHour(8).withMinute(0).withSecond(0).withNano(0),
                                "PROXIMO", MotivoConsultaEnum.CARIES, null, null));
                turnos.add(new Turno(null, pacientes.paciente3(), odontologos.odontologo1(), pacientes.osde(),
                                momentoActual.plusDays(2).withHour(17).withMinute(30).withSecond(0).withNano(0),
                                "CANCELADO", MotivoConsultaEnum.OTRO, null, null));
                turnos.add(new Turno(null, pacientes.paciente1(), odontologos.odontologo2(), null,
                                momentoActual.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0),
                                "PENDIENTE", MotivoConsultaEnum.DOLOR_DENTAL, null, null));

                turnoRepositorio.saveAll(turnos);
        }

        private PersonaRol construirPersonaRol(Persona persona, Rol rol) {
                PersonaRol personaRol = new PersonaRol();
                personaRol.setIdPersona(persona);
                personaRol.setIdRol(rol);
                return personaRol;
        }

        private void crearHorariosDemo() {
                Odontologo odontologo1 = odontologoRepositorio.findByEmail("odontologo1@gmail.com");
                Odontologo odontologo2 = odontologoRepositorio.findByEmail("odontologo2@gmail.com");

                List<Horario> horariosNuevos = new ArrayList<>();

                if (odontologo1 != null
                                && horarioRepositorio.findHorariosPorOdontologo(odontologo1.getId_odontologo())
                                                .isEmpty()) {
                        horariosNuevos.add(crearHorario(odontologo1, DiaSemana.Lunes, LocalTime.of(17, 0),
                                        LocalTime.of(21, 0)));
                        horariosNuevos.add(crearHorario(odontologo1, DiaSemana.Miércoles, LocalTime.of(17, 0),
                                        LocalTime.of(21, 0)));
                        horariosNuevos.add(crearHorario(odontologo1, DiaSemana.Viernes, LocalTime.of(17, 0),
                                        LocalTime.of(21, 0)));
                }

                if (odontologo2 != null
                                && horarioRepositorio.findHorariosPorOdontologo(odontologo2.getId_odontologo())
                                                .isEmpty()) {
                        horariosNuevos.add(crearHorario(odontologo2, DiaSemana.Martes, LocalTime.of(7, 0),
                                        LocalTime.of(12, 0)));
                        horariosNuevos.add(crearHorario(odontologo2, DiaSemana.Jueves, LocalTime.of(7, 0),
                                        LocalTime.of(12, 0)));
                        horariosNuevos.add(crearHorario(odontologo2, DiaSemana.Viernes, LocalTime.of(7, 0),
                                        LocalTime.of(12, 0)));
                }

                if (!horariosNuevos.isEmpty()) {
                        horarioRepositorio.saveAll(horariosNuevos);
                        System.out.println("   - Horarios de odontólogos precargados");
                }
        }

        private Horario crearHorario(Odontologo odontologo, DiaSemana dia, LocalTime inicio, LocalTime fin) {
                Horario horario = new Horario();
                horario.setOdontologo(odontologo);
                horario.setDiaSemana(dia);
                horario.setHoraInicio(inicio);
                horario.setHoraFin(fin);
                horario.setDuracionTurno(30);
                return horario;
        }

        private boolean datosPrecargados() {
                return rolRepositorio.count() > 0
                                && especialidadRepositorio.count() > 0
                                && coberturaRepositorio.count() > 0
                                && personaRepositorio.count() > 0
                                && pacienteRepositorio.count() > 0
                                && odontologoRepositorio.count() > 0
                                && personaRolRepositorio.count() > 0
                                && especialidadOdontologoRepositorio.count() > 0
                                && turnoRepositorio.count() > 0
                                && horarioRepositorio.count() > 0;
        }

        private record PersonasIniciales(Persona paciente1, Persona paciente2, Persona paciente3,
                        Persona odontologo1, Persona odontologo2, Persona admin) {
        }

        private record PacientesIniciales(Paciente paciente1, Paciente paciente2, Paciente paciente3,
                        CoberturaSocial osde, CoberturaSocial pami) {
        }

        private record OdontologosIniciales(Odontologo odontologo1, Odontologo odontologo2) {
        }

}
