package com.kosmos.hospital.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosmos.hospital.model.Cita;
public interface CitaRepository extends JpaRepository<Cita, Long> {
	
	//Reglas de negocio
	 // Validar disponibilidad de consultorio
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
           "c.consultorio.id = :consultorioId AND " +
           "c.horario = :horario AND " +
           "c.activa = true")
    boolean existsByConsultorioAndHorario(
        @Param("consultorioId") Long consultorioId,
        @Param("horario") LocalDateTime horario);
	
	//Valida si el doctor esta disponible
    @Query("SELECT c FROM Cita c WHERE c.doctor.id = :doctorId AND c.horario = :horario AND c.activa = true")
    Optional<Cita> findByDoctorAndHorario(@Param("doctorId") Long doctorId, 
                                        @Param("horario") LocalDateTime horario);

    //Valida si el paciente no tiene una cita en almenos 2horas
    @Query("SELECT c FROM Cita c WHERE c.nombrePaciente = :nombrePaciente " +
           "AND c.horario BETWEEN :start AND :end " +
           "AND c.activa = true")
    List<Cita> findByPacienteBetweenDates(@Param("nombrePaciente") String nombrePaciente,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    //Valida si el doctor tiene menos de 8 citas
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.doctor.id = :doctorId " +
           "AND CAST(c.horario AS date) = CAST(:fecha AS date) " +
           "AND c.activa = true")
    int countByDoctorAndDate(@Param("doctorId") Long doctorId,
                           @Param("fecha") LocalDate fecha);

    // Métodos de consulta (que existan los datos)
    @Query("SELECT c FROM Cita c WHERE " +
           "(:doctorId IS NULL OR c.doctor.id = :doctorId) AND " +
           "(:consultorioId IS NULL OR c.consultorio.id = :consultorioId) AND " +
           "(:fecha IS NULL OR CAST(c.horario AS date) = CAST(:fecha AS date)) AND " +
           "c.activa = true " +
           "ORDER BY c.horario ASC")
    List<Cita> findWithFilters(@Param("doctorId") Long doctorId,
                              @Param("consultorioId") Long consultorioId,
                              @Param("fecha") LocalDate fecha);
    

}
