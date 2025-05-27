package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.PrestamosResource;
import com.buhoristeca.lector.dtos.PrestamoRequestDTO;
import com.buhoristeca.lector.dtos.PrestamoResponseDTO;
import com.buhoristeca.lector.dtos.EstadoPrestamoDTO;
import com.buhoristeca.lector.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PrestamoController implements PrestamosResource {

    private final PrestamoService prestamoService;
    private final NativeWebRequest request;

    @Autowired
    public PrestamoController(PrestamoService prestamoService, Optional<NativeWebRequest> request) {
        this.prestamoService = prestamoService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public List<PrestamoResponseDTO> consultarPrestamosPorUsuario(Long idUsuario, Boolean soloActivos) {
        return prestamoService.consultarPrestamosPorUsuario(idUsuario, soloActivos);
    }

    @Override
    public List<PrestamoResponseDTO> listarTodosPrestamos(Integer idUsuario, Integer idLibro, EstadoPrestamoDTO estadoPrestamoDTO, LocalDate fechaDesde, LocalDate fechaHasta) {
           return prestamoService.listarTodosPrestamos(
                (idUsuario != null ? idUsuario.longValue() : null),
                (idLibro != null ? idLibro.longValue() : null),
                estadoPrestamoDTO,
                fechaDesde,
                fechaHasta);
    }

    @Override
    public PrestamoResponseDTO registrarDevolucion(Long idPrestamo) {
        return prestamoService.registrarDevolucion(idPrestamo);
    }

    @Override
    public PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO prestamoRequestDTO) {
        return prestamoService.registrarPrestamo(prestamoRequestDTO);
    }
}