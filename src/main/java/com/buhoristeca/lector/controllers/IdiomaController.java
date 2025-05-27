package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.IdiomasResource;
import com.buhoristeca.lector.dtos.IdiomaRequestDTO;
import com.buhoristeca.lector.dtos.IdiomaResponseDTO;
import com.buhoristeca.lector.service.IdiomaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class IdiomaController implements IdiomasResource {

    private final IdiomaService idiomaService;
    private final NativeWebRequest request;

    @Autowired
    public IdiomaController(IdiomaService idiomaService, Optional<NativeWebRequest> request) {
        this.idiomaService = idiomaService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public IdiomaResponseDTO actualizarIdioma(Long idIdioma, IdiomaRequestDTO idiomaRequestDTO) {
        return idiomaService.actualizarIdioma(idIdioma, idiomaRequestDTO);
    }

    @Override
    public IdiomaResponseDTO crearIdioma(IdiomaRequestDTO idiomaRequestDTO) {
        return idiomaService.crearIdioma(idiomaRequestDTO);
    }

    @Override
    public void eliminarIdioma(Long idIdioma) {
        idiomaService.eliminarIdioma(idIdioma);
    }

    @Override
    public List<IdiomaResponseDTO> listarIdiomas() {
        return idiomaService.listarIdiomas();
    }

    @Override
    public IdiomaResponseDTO obtenerIdiomaPorId(Long idIdioma) {
        return idiomaService.obtenerIdiomaPorId(idIdioma);
    }
}