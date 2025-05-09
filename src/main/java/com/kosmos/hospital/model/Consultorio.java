package com.kosmos.hospital.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //declaramos que es una identidad
@Data //getters, setters y constructores
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consultorio")
public class Consultorio {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer numero;
	private Integer piso;
	
}
