package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.GeneroRequestDTO;
import com.buhoristeca.lector.dtos.GeneroResponseDTO;
import java.util.List;

public interface GeneroService {
    GeneroResponseDTO crearGenero(GeneroRequestDTO generoRequestDTO);
    List<GeneroResponseDTO> listarGeneros();
    GeneroResponseDTO obtenerGeneroPorId(Long idGenero);
    GeneroResponseDTO actualizarGenero(Long idGenero, GeneroRequestDTO generoRequestDTO);
    void eliminarGenero(Long idGenero);
}