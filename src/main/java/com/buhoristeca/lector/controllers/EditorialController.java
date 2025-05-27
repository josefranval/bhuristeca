package com.buhoristeca.lector.controllers;


import com.buhoristeca.lector.controllers.resources.EditorialesResource;
import com.buhoristeca.lector.dtos.EditorialRequestDTO;
import com.buhoristeca.lector.dtos.EditorialResponseDTO;
import com.buhoristeca.lector.service.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class EditorialController implements EditorialesResource {

    private final EditorialService editorialService;
    private final NativeWebRequest request;

    @Autowired
    public EditorialController(EditorialService editorialService, Optional<NativeWebRequest> request) {
        this.editorialService = editorialService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public EditorialResponseDTO actualizarEditorial(Long idEditorial, EditorialRequestDTO editorialRequestDTO) {
        return editorialService.actualizarEditorial(idEditorial, editorialRequestDTO);
    }

    @Override
    public EditorialResponseDTO crearEditorial(EditorialRequestDTO editorialRequestDTO) {
        return editorialService.crearEditorial(editorialRequestDTO);
    }

    @Override
    public void eliminarEditorial(Long idEditorial) {
        editorialService.eliminarEditorial(idEditorial);
    }

    @Override
    public List<EditorialResponseDTO> listarEditoriales() {
        return editorialService.listarEditoriales();
    }

    @Override
    public EditorialResponseDTO obtenerEditorialPorId(Long idEditorial) {
        return editorialService.obtenerEditorialPorId(idEditorial);
    }
}
