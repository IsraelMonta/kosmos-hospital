package com.kosmos.hospital.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.kosmos.hospital.model.Cita;

public interface CitaService {
	Cita crearCita(Cita cita) throws Exception;
	void cancelarCita(Long id);
	Cita actualizarCita(Long id, Cita cita) throws Exception;
	List<Cita> buscarCitas(Long doctorId, Long consultorioId, LocalDate fecha);
	Optional<Cita> buscarPorId(Long id);

}
