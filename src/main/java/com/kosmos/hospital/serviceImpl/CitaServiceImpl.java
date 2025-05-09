package com.kosmos.hospital.serviceImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosmos.hospital.constants.AppConstants;
import com.kosmos.hospital.exception.BusinessException;
import com.kosmos.hospital.exception.ConflictException;
import com.kosmos.hospital.exception.NotFoundException;
import com.kosmos.hospital.model.Cita;
import com.kosmos.hospital.repository.CitaRepository;
import com.kosmos.hospital.repository.ConsultorioRepository;
import com.kosmos.hospital.repository.DoctorRepository;
import com.kosmos.hospital.service.CitaService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {
	
	@Autowired
	private CitaRepository citaRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private ConsultorioRepository consultorioRepository;
	

	@Override
	public Cita crearCita(Cita cita) throws Exception {
		// TODO Auto-generated method stub
		validarDisponibilidadConsultorio(cita);
		validarDiponibilidadDoctor(cita);
		validarLimiteCitasDoctor(cita);
		validarReglasNegocio(cita);
		validarLimiteCitasDoctor(cita);
		return citaRepository.save(cita);
	}

	@Override
	public void cancelarCita(Long id) {
		// TODO Auto-generated method stub
		citaRepository.findById(id).ifPresent(cita -> {
			cita.setActiva(false);
			citaRepository.save(cita);
		});
	}

	@Override
	public Cita actualizarCita(Long id, Cita citaActualizada) throws Exception {
		// TODO Auto-generated method stub
		return citaRepository.findById(id).map(citaExistente -> {
            // Copiar propiedades (excepto ID)
            citaExistente.setConsultorio(citaActualizada.getConsultorio());
            citaExistente.setDoctor(citaActualizada.getDoctor());
            citaExistente.setHorario(citaActualizada.getHorario());
            citaExistente.setNombrePaciente(citaActualizada.getNombrePaciente());
            
            try {
                return crearCita(citaExistente); // Reutiliza validaciones
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }).orElseThrow(() -> new NotFoundException("Cita no encontrada"));
	}

	@Override
	public List<Cita> buscarCitas(Long doctorId, Long consultorioId, LocalDate fecha) {
		// TODO Auto-generated method stub
		return citaRepository.findWithFilters(doctorId,consultorioId, fecha);
	}
	
	//metodos de validacion diponibilidad
	/*
	 * private void validarDisponibilidadConsultorio(Cita cita) throws
	 * ConflictException{
	 * if(citaRepository.findByConsultorioAndHorario(cita.getId(),
	 * cita.getHorario()).isPresent()) { throw new
	 * ConflictException("El consultorio ya esta ocupado en ese horario"); } }
	 */
	
	private void validarDiponibilidadDoctor(Cita cita) throws ConflictException{
		if(citaRepository.findByDoctorAndHorario(cita.getDoctor().getId(), cita.getHorario()).isPresent()) {
			throw new ConflictException("El doctor ya tiene una cita programada en ese horario");
		}
	}
	
	private void validarReglasNegocio(Cita cita) throws BusinessException{
		LocalDateTime inicioDia = cita.getHorario().toLocalDate().atStartOfDay();
		LocalDateTime finDia = inicioDia.plusDays(AppConstants.DAY);
		
		List<Cita> citasPaciente = citaRepository.findByPacienteBetweenDates(cita.getNombrePaciente(),
				inicioDia, finDia);
		if(!citasPaciente.isEmpty()) {
			if(citasPaciente.stream().anyMatch(ct -> ct.getHorario().equals(cita.getHorario()))) {
				throw new BusinessException("El paciente ya tiene una cita a la misma hora");
			}
			if(citasPaciente.stream().anyMatch(ct -> Math.abs(Duration.between(ct.getHorario(), cita.getHorario()).toHoursPart()) < AppConstants.MIN_HORAS_ENTRE_CITA)) {
				throw new BusinessException("El paciente debe tener al menos 2 horas entre citas");
			}
		}
	}
	
	private void validarLimiteCitasDoctor(Cita cita) throws BusinessException{
		int citasDia = citaRepository.countByDoctorAndDate(
				cita.getDoctor().getId()
				, cita.getHorario().toLocalDate());
		if(citasDia >= AppConstants.MAX_CITAS_POR_DIA) {
			throw new BusinessException("El doctor ya tiene 8 citas programadas para este dia");
		}
	}
    private void validarDisponibilidadConsultorio(Cita cita) throws BusinessException {
        if (citaRepository.existsByConsultorioAndHorario(
            cita.getConsultorio().getId(), 
            cita.getHorario())) {
            
            throw new BusinessException(
                "El consultorio ya tiene una cita programada para: " + 
                formatDateTime(cita.getHorario()));
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

	@Override
	public Optional<Cita> buscarPorId(Long id) {
		// TODO Auto-generated method stub
		return citaRepository.findById(id);
	}

}
