package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.*;
import com.buhoristeca.lector.model.*;
import com.buhoristeca.lector.repositories.*;
import com.buhoristeca.lector.service.LibroService;
import com.buhoristeca.lector.service.FileStorageService;
import com.buhoristeca.lector.exceptions.*;
import com.buhoristeca.lector.mappers.LibroMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.openapitools.jackson.nullable.JsonNullable;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final EditorialRepository editorialRepository;
    private final IdiomaRepository idiomaRepository;
    private final GeneroRepository generoRepository;
    private final PrestamoRepository prestamoRepository;
    private final FileStorageService fileStorageService;
    private final LibroMapper libroMapper;


    @Autowired
    public LibroServiceImpl(LibroRepository libroRepository,
                            EditorialRepository editorialRepository,
                            IdiomaRepository idiomaRepository,
                            GeneroRepository generoRepository,
                            PrestamoRepository prestamoRepository,
                            FileStorageService fileStorageService,
                            LibroMapper libroMapper) {
        this.libroRepository = libroRepository;
        this.editorialRepository = editorialRepository;
        this.idiomaRepository = idiomaRepository;
        this.generoRepository = generoRepository;
        this.prestamoRepository = prestamoRepository;
        this.fileStorageService = fileStorageService;
        this.libroMapper = libroMapper;
    }

    @Override
    @Transactional
    public LibroResponseDTO crearLibro(LibroRequestDTO libroRequestDTO) {
        if (libroRequestDTO.getIsbn() != null && libroRepository.existsByIsbnAndActivoTrue(libroRequestDTO.getIsbn())) {
            throw new BusinessRuleException("Ya existe un libro activo con el ISBN: " + libroRequestDTO.getIsbn());
        }

        Editorial editorial = editorialRepository.findById(libroRequestDTO.getIdEditorial())
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con ID: " + libroRequestDTO.getIdEditorial()));
        Idioma idioma = idiomaRepository.findById(libroRequestDTO.getIdIdioma())
                .orElseThrow(() -> new ResourceNotFoundException("Idioma no encontrado con ID: " + libroRequestDTO.getIdIdioma()));

        Set<Genero> generos = new HashSet<>();
        if (libroRequestDTO.getGeneroIds() != null) {
            for (Long idGenero : libroRequestDTO.getGeneroIds()) {
                Genero genero = generoRepository.findById(idGenero)
                        .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + idGenero));
                generos.add(genero);
            }
        }

        Libro libro = libroMapper.libroRequestDTOToLibro(libroRequestDTO, editorial, idioma, generos);
        libro.setActivo(true);
        Libro libroGuardado = libroRepository.save(libro);
        return libroMapper.libroToLibroResponseDTO(libroGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResponseDTO> consultarLibros(String titulo, String autor, Long idGenero, String isbn, Pageable pageable) {
        Page<Libro> librosPage;
        if (StringUtils.hasText(isbn)) {
            librosPage = libroRepository.findByIsbnAndActivoTrue(isbn, pageable);
        } else if (StringUtils.hasText(titulo)) {
            librosPage = libroRepository.findByTituloContainingIgnoreCaseAndActivoTrue(titulo, pageable);
        } else if (StringUtils.hasText(autor)) {
            librosPage = libroRepository.findByAutorContainingIgnoreCaseAndActivoTrue(autor, pageable);
        } else if (idGenero != null) {
            Genero generoFilter = generoRepository.findById(idGenero).orElse(null);
            if (generoFilter != null) {
                librosPage = libroRepository.findByGenerosContainsAndActivoTrue(generoFilter, pageable);
            } else {
                librosPage = Page.empty(pageable);
            }
        }
        else {
            librosPage = libroRepository.findAllByActivoTrue(pageable);
        }
        return librosPage.map(libroMapper::libroToLibroResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponseDTO consultarLibroPorId(Long idLibro) {
        Libro libro = libroRepository.findByIdLibroAndActivoTrue(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro activo no encontrado con ID: " + idLibro));
        return libroMapper.libroToLibroResponseDTO(libro);
    }

    @Override
    @Transactional
    public LibroResponseDTO modificarLibro(Long idLibro, LibroRequestDTO libroRequestDTO) {
        Libro libroExistente = libroRepository.findById(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro));

        if (libroRequestDTO.getIsbn() != null &&
                !libroExistente.getIsbn().equals(libroRequestDTO.getIsbn()) &&
                libroRepository.existsByIsbnAndActivoTrue(libroRequestDTO.getIsbn())) {
            throw new BusinessRuleException("El nuevo ISBN ya está en uso por otro libro activo.");
        }

        Editorial editorial = editorialRepository.findById(libroRequestDTO.getIdEditorial())
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con ID: " + libroRequestDTO.getIdEditorial()));
        Idioma idioma = idiomaRepository.findById(libroRequestDTO.getIdIdioma())
                .orElseThrow(() -> new ResourceNotFoundException("Idioma no encontrado con ID: " + libroRequestDTO.getIdIdioma()));

        Set<Genero> generos = new HashSet<>();
        if (libroRequestDTO.getGeneroIds() != null) {
            for (Long idGen : libroRequestDTO.getGeneroIds()) {
                Genero genero = generoRepository.findById(idGen)
                        .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + idGen));
                generos.add(genero);
            }
        }

        libroExistente.setTitulo(libroRequestDTO.getTitulo());
        libroExistente.setAutor(libroRequestDTO.getAutor());
        libroExistente.setDescripcion(libroRequestDTO.getDescripcion());
        libroExistente.setFechaPublicacion(libroRequestDTO.getFechaPublicacion());
        libroExistente.setEditorial(editorial);
        libroExistente.setIdioma(idioma);
        libroExistente.setIsbn(libroRequestDTO.getIsbn());
        libroExistente.setCantidadEjemplares(libroRequestDTO.getCantidadEjemplares());
        libroExistente.setGeneros(generos);

        JsonNullable<String> rutaPdfNullable = libroRequestDTO.getRutaPdf();
        if (rutaPdfNullable != null) {
            if (rutaPdfNullable.isPresent()) {
                libroExistente.setRutaPdf(rutaPdfNullable.get());
            } else {
                libroExistente.setRutaPdf(null);
            }
        }

        Libro libroActualizado = libroRepository.save(libroExistente);
        return libroMapper.libroToLibroResponseDTO(libroActualizado);
    }

    @Override
    @Transactional
    public void bajaLogicaLibro(Long idLibro) {
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro));

        long prestamosActivos = prestamoRepository.countByLibroIdLibroAndEstadoPrestamoIn(
                idLibro, List.of(com.buhoristeca.lector.model.enums.EstadoPrestamo.PRESTADO, com.buhoristeca.lector.model.enums.EstadoPrestamo.VENCIDO));
        if (prestamosActivos > 0) {
            throw new BusinessRuleException("No se puede dar de baja el libro. Tiene préstamos activos o vencidos.");
        }

        libro.setActivo(false);
        libroRepository.save(libro);
    }

    @Override
    @Transactional
    public LibroResponseDTO cargarLibroDigital(Long idLibro, MultipartFile archivoPdf) {
        Libro libro = libroRepository.findByIdLibroAndActivoTrue(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro activo no encontrado con ID: " + idLibro));

        if (StringUtils.hasText(libro.getRutaPdf())) {
            fileStorageService.deleteFile(libro.getRutaPdf());
        }

        String nombreArchivo = fileStorageService.storeFile(archivoPdf, "libro_" + idLibro);
        libro.setRutaPdf(nombreArchivo);
        Libro libroActualizado = libroRepository.save(libro);
        return libroMapper.libroToLibroResponseDTO(libroActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource obtenerLibroDigital(Long idLibro) {
        Libro libro = libroRepository.findByIdLibroAndActivoTrue(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro activo no encontrado con ID: " + idLibro));

        if (!StringUtils.hasText(libro.getRutaPdf())) {
            throw new ResourceNotFoundException("El libro con ID: " + idLibro + " no tiene una versión digital asociada.");
        }
        return fileStorageService.loadAsResource(libro.getRutaPdf());
    }

    @Override
    @Transactional(readOnly = true)
    public InformeDisponibilidadLibroDTO obtenerDisponibilidadLibro(Long idLibro) {
        Libro libro = libroRepository.findByIdLibroAndActivoTrue(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro activo no encontrado con ID: " + idLibro));

        long cantidadPrestados = prestamoRepository.countByLibroIdLibroAndEstadoPrestamoIn(
                idLibro, List.of(com.buhoristeca.lector.model.enums.EstadoPrestamo.PRESTADO, com.buhoristeca.lector.model.enums.EstadoPrestamo.VENCIDO)
        );
        long cantidadDisponible = libro.getCantidadEjemplares() - cantidadPrestados;

        InformeDisponibilidadLibroDTO informe = new InformeDisponibilidadLibroDTO();
        informe.setLibro(libroMapper.libroToLibroSummaryResponseDTO(libro));
        informe.setCantidadTotal(libro.getCantidadEjemplares());
        informe.setCantidadPrestados((int) cantidadPrestados);
        informe.setCantidadDisponible((int) Math.max(0, cantidadDisponible));
        return informe;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InformeDisponibilidadLibroDTO> obtenerDisponibilidadCatalogoCompleto() {
        return libroRepository.findAllByActivoTrue().stream()
                .map(libro -> obtenerDisponibilidadLibro(libro.getIdLibro()))
                .collect(Collectors.toList());
    }
}
