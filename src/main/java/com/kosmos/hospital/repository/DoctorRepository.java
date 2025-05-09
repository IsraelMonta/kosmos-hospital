package com.kosmos.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosmos.hospital.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	
	//Valida si existe el doctor y si esta desocupado
    @Query("SELECT d FROM Doctor d WHERE d.especialidad = :especialidad")
    List<Doctor> findByEspecialidad(@Param("especialidad") String especialidad);
    
    @Query("SELECT d FROM Doctor d")
    List<Doctor> findAllActive();

}
