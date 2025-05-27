package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.IdiomaRequestDTO;
import com.buhoristeca.lector.dtos.IdiomaResponseDTO;
import com.buhoristeca.lector.model.Idioma; // Paquete corregido
import com.buhoristeca.lector.repositories.IdiomaRepository;
import com.buhoristeca.lector.service.IdiomaService;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.IdiomaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdiomaServiceImpl implements IdiomaService {

    private final IdiomaRepository idiomaRepository;
    private final IdiomaMapper idiomaMapper;

    @Autowired
    public IdiomaServiceImpl(IdiomaRepository idiomaRepository, IdiomaMapper idiomaMapper) {
        this.idiomaRepository = idiomaRepository;
        this.idiomaMapper = idiomaMapper;
    }

    @Override
    @Transactional
    public IdiomaResponseDTO crearIdioma(IdiomaRequestDTO idiomaRequestDTO) {
        idiomaRepository.findByNombreIdioma(idiomaRequestDTO.getNombreIdioma()).ifPresent(i -> {
            throw new BusinessRuleException("Ya existe un idioma con el nombre: " + idiomaRequestDTO.getNombreIdioma());
        });
        Idioma idioma = idiomaMapper.idiomaRequestDTOToIdioma(idiomaRequestDTO);
        Idioma idiomaGuardado = idiomaRepository.save(idioma);
        return idiomaMapper.idiomaToIdiomaResponseDTO(idiomaGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdiomaResponseDTO> listarIdiomas() {
        return idiomaRepository.findAll().stream()
                .map(idiomaMapper::idiomaToIdiomaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaResponseDTO obtenerIdiomaPorId(Long idIdioma) {
        Idioma idioma = idiomaRepository.findById(idIdioma)
                .orElseThrow(() -> new ResourceNotFoundException("Idioma no encontrado con ID: " + idIdioma));
        return idiomaMapper.idiomaToIdiomaResponseDTO(idioma);
    }

    @Override
    @Transactional
    public IdiomaResponseDTO actualizarIdioma(Long idIdioma, IdiomaRequestDTO idiomaRequestDTO) {
        Idioma idiomaExistente = idiomaRepository.findById(idIdioma)
                .orElseThrow(() -> new ResourceNotFoundException("Idioma no encontrado con ID: " + idIdioma));

        idiomaRepository.findByNombreIdioma(idiomaRequestDTO.getNombreIdioma()).ifPresent(i -> {
            if (!i.getIdIdioma().equals(idIdioma)) {
                throw new BusinessRuleException("Ya existe otro idioma con el nombre: " + idiomaRequestDTO.getNombreIdioma());
            }
        });

        idiomaExistente.setNombreIdioma(idiomaRequestDTO.getNombreIdioma());
        Idioma idiomaActualizado = idiomaRepository.save(idiomaExistente);
        return idiomaMapper.idiomaToIdiomaResponseDTO(idiomaActualizado);
    }

    @Override
    @Transactional
    public void eliminarIdioma(Long idIdioma) {
        Idioma idioma = idiomaRepository.findById(idIdioma)
                .orElseThrow(() -> new ResourceNotFoundException("Idioma no encontrado con ID: " + idIdioma));
        try {
            idiomaRepository.delete(idioma);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar el idioma. Puede estar asignado a libros.");
        }
    }
}