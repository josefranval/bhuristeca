package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.EditorialRequestDTO;
import com.buhoristeca.lector.dtos.EditorialResponseDTO;
import com.buhoristeca.lector.model.Editorial;
import com.buhoristeca.lector.repositories.EditorialRepository;
import com.buhoristeca.lector.service.EditorialService;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.EditorialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorialServiceImpl implements EditorialService {

    private final EditorialRepository editorialRepository;
    private final EditorialMapper editorialMapper;

    @Autowired
    public EditorialServiceImpl(EditorialRepository editorialRepository, EditorialMapper editorialMapper) {
        this.editorialRepository = editorialRepository;
        this.editorialMapper = editorialMapper;
    }

    @Override
    @Transactional
    public EditorialResponseDTO crearEditorial(EditorialRequestDTO editorialRequestDTO) {
        editorialRepository.findByNombreEditorial(editorialRequestDTO.getNombreEditorial()).ifPresent(e -> {
            throw new BusinessRuleException("Ya existe una editorial con el nombre: " + editorialRequestDTO.getNombreEditorial());
        });
        Editorial editorial = editorialMapper.editorialRequestDTOToEditorial(editorialRequestDTO);
        Editorial editorialGuardada = editorialRepository.save(editorial);
        return editorialMapper.editorialToEditorialResponseDTO(editorialGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EditorialResponseDTO> listarEditoriales() {
        return editorialRepository.findAll().stream()
                .map(editorialMapper::editorialToEditorialResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialResponseDTO obtenerEditorialPorId(Long idEditorial) {
        Editorial editorial = editorialRepository.findById(idEditorial)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con ID: " + idEditorial));
        return editorialMapper.editorialToEditorialResponseDTO(editorial);
    }

    @Override
    @Transactional
    public EditorialResponseDTO actualizarEditorial(Long idEditorial, EditorialRequestDTO editorialRequestDTO) {
        Editorial editorialExistente = editorialRepository.findById(idEditorial)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con ID: " + idEditorial));

        editorialRepository.findByNombreEditorial(editorialRequestDTO.getNombreEditorial()).ifPresent(e -> {
            if (!e.getIdEditorial().equals(idEditorial)) {
                throw new BusinessRuleException("Ya existe otra editorial con el nombre: " + editorialRequestDTO.getNombreEditorial());
            }
        });

        editorialExistente.setNombreEditorial(editorialRequestDTO.getNombreEditorial());

        Editorial editorialActualizada = editorialRepository.save(editorialExistente);
        return editorialMapper.editorialToEditorialResponseDTO(editorialActualizada);
    }

    @Override
    @Transactional
    public void eliminarEditorial(Long idEditorial) {
        Editorial editorial = editorialRepository.findById(idEditorial)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con ID: " + idEditorial));
        try {
            editorialRepository.delete(editorial);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar la editorial. Puede estar asignada a libros.");
        }
    }
}
