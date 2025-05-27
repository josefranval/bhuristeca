package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.GeneroRequestDTO;
import com.buhoristeca.lector.dtos.GeneroResponseDTO;
import com.buhoristeca.lector.model.Genero; // Paquete corregido
import com.buhoristeca.lector.repositories.GeneroRepository;
import com.buhoristeca.lector.service.GeneroService;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.GeneroMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Autowired
    public GeneroServiceImpl(GeneroRepository generoRepository, GeneroMapper generoMapper) {
        this.generoRepository = generoRepository;
        this.generoMapper = generoMapper;
    }

    @Override
    @Transactional
    public GeneroResponseDTO crearGenero(GeneroRequestDTO generoRequestDTO) {
        generoRepository.findByNombreGenero(generoRequestDTO.getNombreGenero()).ifPresent(g -> {
            throw new BusinessRuleException("Ya existe un género con el nombre: " + generoRequestDTO.getNombreGenero());
        });
        Genero genero = generoMapper.generoRequestDTOToGenero(generoRequestDTO);
        Genero generoGuardado = generoRepository.save(genero);
        return generoMapper.generoToGeneroResponseDTO(generoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneroResponseDTO> listarGeneros() {
        return generoRepository.findAll().stream()
                .map(generoMapper::generoToGeneroResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResponseDTO obtenerGeneroPorId(Long idGenero) {
        Genero genero = generoRepository.findById(idGenero)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + idGenero));
        return generoMapper.generoToGeneroResponseDTO(genero);
    }

    @Override
    @Transactional
    public GeneroResponseDTO actualizarGenero(Long idGenero, GeneroRequestDTO generoRequestDTO) {
        Genero generoExistente = generoRepository.findById(idGenero)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + idGenero));

        generoRepository.findByNombreGenero(generoRequestDTO.getNombreGenero()).ifPresent(g -> {
            if (!g.getIdGenero().equals(idGenero)) {
                throw new BusinessRuleException("Ya existe otro género con el nombre: " + generoRequestDTO.getNombreGenero());
            }
        });

        generoExistente.setNombreGenero(generoRequestDTO.getNombreGenero());
        Genero generoActualizado = generoRepository.save(generoExistente);
        return generoMapper.generoToGeneroResponseDTO(generoActualizado);
    }

    @Override
    @Transactional
    public void eliminarGenero(Long idGenero) {
        Genero genero = generoRepository.findById(idGenero)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + idGenero));

        if (generoRepository.isGeneroEnUso(idGenero)) {
            throw new BusinessRuleException("No se puede eliminar el género porque está asignado a uno o más libros.");
        }
        try {
            generoRepository.delete(genero);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar el género. Puede estar asignado a libros.");
        }
    }
}