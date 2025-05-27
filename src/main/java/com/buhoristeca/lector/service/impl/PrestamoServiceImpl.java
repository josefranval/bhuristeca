package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.*;
import com.buhoristeca.lector.model.*;
import com.buhoristeca.lector.model.enums.EstadoPrestamo;
import com.buhoristeca.lector.repositories.*;
import com.buhoristeca.lector.service.PrestamoService;
import com.buhoristeca.lector.exceptions.*;
import com.buhoristeca.lector.mappers.PrestamoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoMapper prestamoMapper;


    @Value("${biblioteca.prestamo.diasPorDefecto:14}")
    private int diasPrestamoPorDefecto;

    @Autowired
    public PrestamoServiceImpl(PrestamoRepository prestamoRepository,
                               LibroRepository libroRepository,
                               UsuarioRepository usuarioRepository,
                               PrestamoMapper prestamoMapper) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoMapper = prestamoMapper;
    }

    @Override
    @Transactional
    public PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO prestamoRequestDTO) {
        Usuario usuario = usuarioRepository.findByIdUsuarioAndActivoTrue(prestamoRequestDTO.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario activo no encontrado con ID: " + prestamoRequestDTO.getIdUsuario()));

        Libro libro = libroRepository.findByIdLibroAndActivoTrue(prestamoRequestDTO.getIdLibro())
                .orElseThrow(() -> new ResourceNotFoundException("Libro activo no encontrado con ID: " + prestamoRequestDTO.getIdLibro()));

        if (prestamoRepository.existsByUsuarioAndEstadoPrestamo(usuario, EstadoPrestamo.VENCIDO)) {
            throw new PrestamoValidationException("El usuario tiene préstamos vencidos y no puede solicitar nuevos libros.");
        }

        long prestamosActivosDelLibro = prestamoRepository.countByLibroAndEstadoPrestamoIn(
                libro, List.of(EstadoPrestamo.PRESTADO, EstadoPrestamo.VENCIDO));
        if (prestamosActivosDelLibro >= libro.getCantidadEjemplares()) {
            throw new StockInsuficienteException("No hay ejemplares disponibles del libro: " + libro.getTitulo());
        }

        if (prestamoRepository.existsByUsuarioAndLibroAndEstadoPrestamoIn(
                usuario, libro, List.of(EstadoPrestamo.PRESTADO, EstadoPrestamo.VENCIDO))) {
            throw new PrestamoValidationException("El usuario ya tiene un ejemplar de este libro prestado y no devuelto.");
        }

        Prestamo prestamo = prestamoMapper.prestamoRequestDTOToPrestamo(prestamoRequestDTO, libro, usuario);
        prestamo.setFechaPrestamo(LocalDateTime.now());

        Integer plazoDiasFromDTO = prestamoRequestDTO.getPlazoPrestamoDias().orElse(null);
        int diasPlazo = plazoDiasFromDTO != null ? plazoDiasFromDTO : diasPrestamoPorDefecto;

        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(diasPlazo));
        prestamo.setEstadoPrestamo(EstadoPrestamo.PRESTADO);

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);
        return prestamoMapper.prestamoToPrestamoResponseDTO(prestamoGuardado);
    }

    @Override
    @Transactional
    public PrestamoResponseDTO registrarDevolucion(Long idPrestamo) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + idPrestamo));

        if (prestamo.getEstadoPrestamo() == EstadoPrestamo.DEVUELTO) {
            throw new PrestamoValidationException("El libro de este préstamo ya fue devuelto.");
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstadoPrestamo(EstadoPrestamo.DEVUELTO);

        Prestamo prestamoActualizado = prestamoRepository.save(prestamo);
        return prestamoMapper.prestamoToPrestamoResponseDTO(prestamoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listarTodosPrestamos(Long idUsuario, Long idLibro, EstadoPrestamoDTO estadoDTO, LocalDate fechaDesde, LocalDate fechaHasta) {
        EstadoPrestamo estadoEntity = null;
        if(estadoDTO != null) {
            try {
                estadoEntity = EstadoPrestamo.valueOf(estadoDTO.getValue());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Valor de estado de préstamo inválido: " + estadoDTO.getValue());
            }
        }
        List<Prestamo> todosLosPrestamos = prestamoRepository.findAll();
        final EstadoPrestamo filtroEstado = estadoEntity; // Necesario para lambda

        return todosLosPrestamos.stream()
                .filter(p -> idUsuario == null || p.getUsuario().getIdUsuario().equals(idUsuario))
                .filter(p -> idLibro == null || p.getLibro().getIdLibro().equals(idLibro))
                .filter(p -> filtroEstado == null || p.getEstadoPrestamo().equals(filtroEstado))
                .filter(p -> fechaDesde == null || !p.getFechaPrestamo().toLocalDate().isBefore(fechaDesde))
                .filter(p -> fechaHasta == null || p.getFechaPrestamo().toLocalDate().isBefore(fechaHasta.plusDays(1))) // Incluye fechaHasta
                .map(prestamoMapper::prestamoToPrestamoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> consultarPrestamosPorUsuario(Long idUsuario, boolean soloActivos) {
        Usuario usuario = usuarioRepository.findByIdUsuarioAndActivoTrue(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario activo no encontrado con ID: " + idUsuario));

        List<Prestamo> prestamos;
        if (soloActivos) {
            prestamos = prestamoRepository.findByUsuarioAndEstadoPrestamoIn(
                    usuario, List.of(EstadoPrestamo.PRESTADO, EstadoPrestamo.VENCIDO));
        } else {
            prestamos = prestamoRepository.findByUsuario(usuario);
        }
        return prestamos.stream().map(prestamoMapper::prestamoToPrestamoResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void verificarYActualizarPrestamosVencidos() {
        LocalDate hoy = LocalDate.now();
        List<Prestamo> prestamosPendientes = prestamoRepository.findByEstadoPrestamoAndFechaDevolucionEsperadaBefore(
                EstadoPrestamo.PRESTADO, hoy);

        if (!prestamosPendientes.isEmpty()) {
            for (Prestamo prestamo : prestamosPendientes) {
                prestamo.setEstadoPrestamo(EstadoPrestamo.VENCIDO);
            }
            prestamoRepository.saveAll(prestamosPendientes);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> obtenerPrestamosVencidos() {
        return prestamoRepository.findByEstadoPrestamo(EstadoPrestamo.VENCIDO)
                .stream()
                .map(prestamoMapper::prestamoToPrestamoResponseDTO)
                .collect(Collectors.toList());
    }
}
