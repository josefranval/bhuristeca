package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.LibrosResource;
import com.buhoristeca.lector.dtos.LibroRequestDTO;
import com.buhoristeca.lector.dtos.LibroResponseDTO;
import com.buhoristeca.lector.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class LibroController implements LibrosResource {

    private final LibroService libroService;
    private final NativeWebRequest request;


    @Autowired
    public LibroController(LibroService libroService, Optional<NativeWebRequest> request) {
        this.libroService = libroService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public void bajaLogicaLibro(Long idLibro) {
        libroService.bajaLogicaLibro(idLibro);
    }

    @Override
    public LibroResponseDTO cargarLibroDigital(Long idLibro, MultipartFile archivo) {
        return libroService.cargarLibroDigital(idLibro, archivo);
    }

    @Override
    public LibroResponseDTO consultarLibroPorId(Long idLibro) {
        return libroService.consultarLibroPorId(idLibro);
    }

    @Override
    public List<LibroResponseDTO> consultarLibros(String titulo, String autor, Integer idGenero, String isbn, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return libroService.consultarLibros(titulo, autor, (idGenero != null ? idGenero.longValue() : null), isbn, pageable).getContent();
    }

    @Override
    public LibroResponseDTO crearLibro(LibroRequestDTO libroRequestDTO) {
        return libroService.crearLibro(libroRequestDTO);
    }

    @Override
    public LibroResponseDTO modificarLibro(Long idLibro, LibroRequestDTO libroRequestDTO) {
        return libroService.modificarLibro(idLibro, libroRequestDTO);
    }

    @Override
    public Resource obtenerLibroDigital(Long idLibro) {
        Resource resource = libroService.obtenerLibroDigital(idLibro);
        String contentType = "application/pdf"; // Asumiendo PDF
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource).getBody();
    }
}