package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.EditorialRequestDTO;
import com.buhoristeca.lector.dtos.EditorialResponseDTO;
import java.util.List;

public interface EditorialService {
    EditorialResponseDTO crearEditorial(EditorialRequestDTO editorialRequestDTO);
    List<EditorialResponseDTO> listarEditoriales();
    EditorialResponseDTO obtenerEditorialPorId(Long idEditorial);
    EditorialResponseDTO actualizarEditorial(Long idEditorial, EditorialRequestDTO editorialRequestDTO);
    void eliminarEditorial(Long idEditorial);
}