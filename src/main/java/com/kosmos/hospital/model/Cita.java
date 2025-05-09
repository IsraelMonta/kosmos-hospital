package com.kosmos.hospital.model;

import java.beans.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kosmos.hospital.constants.AppConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //declaramos que es una entidad
@Data //getters, setters and constructors
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cita")
public class Cita {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Consultorio consultorio;
	
	@ManyToOne
	private Doctor doctor;
	private LocalDateTime horario;
	private String nombrePaciente;
	private boolean activa = true;
	
	

	public void setHorario(LocalDateTime horario) {
		this.horario = ajustarIntervalos(horario);
	}
	
	private LocalDateTime ajustarIntervalos(LocalDateTime fechaHora) {
		int minutos = fechaHora.getMinute();
		int ajuste = minutos % AppConstants.INTERVALOS_CITA;
		if(ajuste != 0) {
			return fechaHora.plusMinutes(AppConstants.INTERVALOS_CITA - ajuste).withSecond(0).withNano(0);
		}
		return fechaHora.withSecond(0).withNano(0);
	}
}
