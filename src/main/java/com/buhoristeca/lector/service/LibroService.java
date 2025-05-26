package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.LibroRequestDTO;
import com.buhoristeca.lector.dtos.LibroResponseDTO;
import com.buhoristeca.lector.dtos.InformeDisponibilidadLibroDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.util.List;

public interface LibroService {
    LibroResponseDTO crearLibro(LibroRequestDTO libroRequestDTO);
    Page<LibroResponseDTO> consultarLibros(String titulo, String autor, Long idGenero, String isbn, Pageable pageable);
    LibroResponseDTO consultarLibroPorId(Long idLibro);
    LibroResponseDTO modificarLibro(Long idLibro, LibroRequestDTO libroRequestDTO);
    void bajaLogicaLibro(Long idLibro);
    LibroResponseDTO cargarLibroDigital(Long idLibro, MultipartFile archivoPdf);
    Resource obtenerLibroDigital(Long idLibro);
    InformeDisponibilidadLibroDTO obtenerDisponibilidadLibro(Long idLibro);
    List<InformeDisponibilidadLibroDTO> obtenerDisponibilidadCatalogoCompleto();
}