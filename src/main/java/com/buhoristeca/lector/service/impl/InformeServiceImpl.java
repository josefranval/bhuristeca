package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.*;
import com.buhoristeca.lector.model.enums.EstadoPrestamo; // Paquete corregido
import com.buhoristeca.lector.repositories.*;
import com.buhoristeca.lector.service.InformeService;
import com.buhoristeca.lector.service.LibroService;
import com.buhoristeca.lector.mappers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformeServiceImpl implements InformeService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final LibroService libroService;
    private final PrestamoMapper prestamoMapper;
    private final LibroMapper libroMapper;
    private final InformeMapper informeMapper;


    @Autowired
    public InformeServiceImpl(PrestamoRepository prestamoRepository,
                              LibroRepository libroRepository,
                              LibroService libroService,
                              PrestamoMapper prestamoMapper,
                              LibroMapper libroMapper,
                              InformeMapper informeMapper) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.libroService = libroService;
        this.prestamoMapper = prestamoMapper;
        this.libroMapper = libroMapper;
        this.informeMapper = informeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> generarReportePrestamosPorTipoUsuario(Long idRolUsuario, LocalDate fechaDesde, LocalDate fechaHasta) {
        return prestamoRepository.findAll().stream()
                .filter(p -> idRolUsuario == null || p.getUsuario().getRolUsuario().getIdRolUsuario().equals(idRolUsuario))
                .filter(p -> fechaDesde == null || !p.getFechaPrestamo().toLocalDate().isBefore(fechaDesde))
                .filter(p -> fechaHasta == null || !p.getFechaPrestamo().toLocalDate().isAfter(fechaHasta))
                .map(prestamoMapper::prestamoToPrestamoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InformeDisponibilidadLibroDTO> generarReporteDisponibilidadCatalogo(Long idLibro) {
        if (idLibro != null) {
            return List.of(libroService.obtenerDisponibilidadLibro(idLibro));
        }
        return libroService.obtenerDisponibilidadCatalogoCompleto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDTO> generarReporteLibrosMenosPrestados(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Long> idsLibrosPrestados = prestamoRepository.findLibrosPrestadosEntreFechas(
                fechaDesde != null ? fechaDesde.atStartOfDay() : null,
                fechaHasta != null ? fechaHasta.plusDays(1).atStartOfDay() : null
        );

        List<Long> idsFiltrados = idsLibrosPrestados.isEmpty() ? List.of(-1L) : idsLibrosPrestados;
        return libroRepository.findByIdLibroNotInAndActivoTrue(idsFiltrados)
                .stream()
                .map(libroMapper::libroToLibroResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> generarReportePrestamosVencidos() {
        return prestamoRepository.findByEstadoPrestamo(EstadoPrestamo.VENCIDO)
                .stream()
                .map(prestamoMapper::prestamoToPrestamoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportePopularidadGeneros200ResponseInnerDTO> generarReportePopularidadGeneros(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Object[]> results = prestamoRepository.countPrestamosPorGeneroEntreFechas(
                fechaDesde != null ? fechaDesde.atStartOfDay() : null,
                fechaHasta != null ? fechaHasta.plusDays(1).atStartOfDay() : null
        );

        return results.stream().map(result -> {
            Long idGenero = ((Number) result[0]).longValue();
            String nombreGenero = (String) result[1];
            Integer totalPrestamos = ((Number) result[2]).intValue();

            GeneroResponseDTO generoDTO = new GeneroResponseDTO();
            generoDTO.setIdGenero(idGenero);
            generoDTO.setNombreGenero(nombreGenero);

            return informeMapper.toReportePopularidadDTO(generoDTO, totalPrestamos);
        }).collect(Collectors.toList());
    }
}