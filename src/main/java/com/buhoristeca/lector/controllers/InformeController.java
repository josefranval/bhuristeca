package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.InformesResource;
import com.buhoristeca.lector.dtos.InformeDisponibilidadLibroDTO;
import com.buhoristeca.lector.dtos.LibroResponseDTO;
import com.buhoristeca.lector.dtos.PrestamoResponseDTO;
import com.buhoristeca.lector.dtos.ReportePopularidadGeneros200ResponseInnerDTO;
import com.buhoristeca.lector.service.InformeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class InformeController implements InformesResource {

    private final InformeService informeService;
    private final NativeWebRequest request;

    @Autowired
    public InformeController(InformeService informeService, Optional<NativeWebRequest> request) {
        this.informeService = informeService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public List<InformeDisponibilidadLibroDTO> reporteDisponibilidadCatalogo(Long idLibro) {
        return informeService.generarReporteDisponibilidadCatalogo(idLibro);
    }

    @Override
    public List<LibroResponseDTO> reporteLibrosMenosPrestados(LocalDate fechaDesde, LocalDate fechaHasta) {
        return informeService.generarReporteLibrosMenosPrestados(fechaDesde, fechaHasta);
    }

    @Override
    public List<ReportePopularidadGeneros200ResponseInnerDTO> reportePopularidadGeneros(LocalDate fechaDesde, LocalDate fechaHasta) {
        return informeService.generarReportePopularidadGeneros(fechaDesde, fechaHasta);
    }

    @Override
    public List<PrestamoResponseDTO> reportePrestamosPorTipoUsuario(Integer idRolUsuario, LocalDate fechaDesde, LocalDate fechaHasta) {
        return informeService.generarReportePrestamosPorTipoUsuario((idRolUsuario != null ? idRolUsuario.longValue() : null), fechaDesde, fechaHasta);
    }

    @Override
    public List<PrestamoResponseDTO> reportePrestamosVencidos() {
        return informeService.generarReportePrestamosVencidos();
    }
}
