package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.IdiomaRequestDTO;
import com.buhoristeca.lector.dtos.IdiomaResponseDTO;
import java.util.List;

public interface IdiomaService {
    IdiomaResponseDTO crearIdioma(IdiomaRequestDTO idiomaRequestDTO);
    List<IdiomaResponseDTO> listarIdiomas();
    IdiomaResponseDTO obtenerIdiomaPorId(Long idIdioma);
    IdiomaResponseDTO actualizarIdioma(Long idIdioma, IdiomaRequestDTO idiomaRequestDTO);
    void eliminarIdioma(Long idIdioma);
}