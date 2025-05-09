package com.kosmos.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosmos.hospital.model.Consultorio;

public interface ConsultorioRepository extends JpaRepository<Consultorio,Long> {
	//Consulta si existe el consultorio y si esta desocupado
    @Query("SELECT c FROM Consultorio c WHERE c.piso = :piso")
    List<Consultorio> findByPiso(@Param("piso") Integer piso);
    
    @Query("SELECT c FROM Consultorio c")
    List<Consultorio> findAllActive();
}
