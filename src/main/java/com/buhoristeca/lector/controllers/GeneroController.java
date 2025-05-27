package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.GenerosResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.buhoristeca.lector.controllers.resources.GenerosResource;
import com.buhoristeca.lector.dtos.GeneroRequestDTO;
import com.buhoristeca.lector.dtos.GeneroResponseDTO;
import com.buhoristeca.lector.service.GeneroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class GeneroController implements GenerosResource {
    private final GeneroService generoService;
    private final NativeWebRequest request;

    @Autowired
    public GeneroController(GeneroService generoService, Optional<NativeWebRequest> request) {
        this.generoService = generoService;
        this.request = request.orElse(null);
    }
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }


    @Override
    public GeneroResponseDTO actualizarGenero(Long idGenero, GeneroRequestDTO generoRequestDTO) {
        return generoService.actualizarGenero(idGenero, generoRequestDTO);
    }

    @Override
    public GeneroResponseDTO crearGenero(GeneroRequestDTO generoRequestDTO) {
        return generoService.crearGenero(generoRequestDTO);
    }

    @Override
    public void eliminarGenero(Long idGenero) {
        generoService.eliminarGenero(idGenero);
    }

    @Override
    public List<GeneroResponseDTO> listarGeneros() {
        return generoService.listarGeneros();
    }

    @Override
    public GeneroResponseDTO obtenerGeneroPorId(Long idGenero) {
        return generoService.obtenerGeneroPorId(idGenero);
    }
}
