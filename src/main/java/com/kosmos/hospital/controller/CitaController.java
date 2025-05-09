package com.kosmos.hospital.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kosmos.hospital.constants.AppConstants;
import com.kosmos.hospital.exception.NotFoundException;
import com.kosmos.hospital.model.Cita;
import com.kosmos.hospital.repository.ConsultorioRepository;
import com.kosmos.hospital.repository.DoctorRepository;
import com.kosmos.hospital.service.CitaService;


@Controller
@RequestMapping("/citas")
public class CitaController {

	@Autowired
	CitaService citaService;
	
	@Autowired
	DoctorRepository doctorRepository;
	
	@Autowired
	ConsultorioRepository consultorioRepository;
	
	
	@GetMapping
	public String listarCitas(
			@RequestParam(required = false) Long doctorId,
			@RequestParam(required = false) Long consultorioId,
			@RequestParam(required = false) LocalDate fecha,
			Model model) {
		List<Cita> citas = citaService.buscarCitas(doctorId, consultorioId, fecha);
		model.addAttribute("citas", citas);
		model.addAttribute("doctores",doctorRepository.findAllActive());
		model.addAttribute("consultorios", consultorioRepository.findAllActive());
		return "citas/lista";
	}
	
	
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("doctores", doctorRepository.findAllActive());
        model.addAttribute("consultorios", consultorioRepository.findAllActive());
        return "citas/formulario";
    }
	
	@PostMapping("/guardar")
	public String guardarCita(@ModelAttribute Cita cita, RedirectAttributes redirect) {
		try {
			citaService.crearCita(cita);
			redirect.addFlashAttribute("SUCCESS", "Cita creada!");
		}catch(Exception e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/citas/nueva";
		}
		
		return "redirect:/citas";
	}
	
	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
	    Cita cita = citaService.buscarPorId(id)
	            .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
	    
	    model.addAttribute("cita", cita);
	    model.addAttribute("doctores", doctorRepository.findAll());
	    model.addAttribute("consultorios", consultorioRepository.findAll());
	    model.addAttribute("modoEdicion", true);  // Para diferenciar crear/editar
	    
	    return "citas/formulario";  // O "citas/editar" si usas plantilla separada
	}
	
	@PostMapping("/actualizar/{id}")
	public String actualizarCita(@PathVariable Long id, @ModelAttribute Cita cita, RedirectAttributes redirect) {
		try {
			citaService.actualizarCita(id, cita);
			redirect.addFlashAttribute("SUCCESS", "Cita Actualizada!");
			
		}catch(Exception e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/citas/editar/" + id;
		}
		return "redirect:/citas";
	}
	
    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, RedirectAttributes redirect) {
        citaService.cancelarCita(id);
        redirect.addFlashAttribute("success", "Cita cancelada exitosamente");
        return "redirect:/citas";
    }
    
    @ModelAttribute("horariosDisponibles")
    public List<String> getHorariosDisponibles() {
        List<String> horarios = new ArrayList<>();
        LocalTime hora = LocalTime.of(8, 0); // Inicio a las 8:00 AM
        
        while (hora.isBefore(LocalTime.of(20, 0))) { // Fin a las 8:00 PM
            horarios.add(hora.format(DateTimeFormatter.ofPattern("HH:mm")));
            hora = hora.plusMinutes(30);
        }
        
        return horarios;
    }
	
	
}
