package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.RolUsuarioRequestDTO;
import com.buhoristeca.lector.dtos.RolUsuarioResponseDTO;
import com.buhoristeca.lector.model.RolUsuario; // Paquete corregido
import com.buhoristeca.lector.repositories.RolUsuarioRepository;
import com.buhoristeca.lector.repositories.UsuarioRepository;
import com.buhoristeca.lector.service.RolUsuarioService;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.RolUsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolUsuarioServiceImpl implements RolUsuarioService {

    private final RolUsuarioRepository rolUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioMapper rolUsuarioMapper;

    @Autowired
    public RolUsuarioServiceImpl(RolUsuarioRepository rolUsuarioRepository,
                                 UsuarioRepository usuarioRepository,
                                 RolUsuarioMapper rolUsuarioMapper) {
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioMapper = rolUsuarioMapper;
    }

    @Override
    @Transactional
    public RolUsuarioResponseDTO crearRolUsuario(RolUsuarioRequestDTO rolUsuarioRequestDTO) {
        rolUsuarioRepository.findByNombreRol(rolUsuarioRequestDTO.getNombreRol()).ifPresent(r -> {
            throw new BusinessRuleException("Ya existe un rol con el nombre: " + rolUsuarioRequestDTO.getNombreRol());
        });
        RolUsuario rol = rolUsuarioMapper.rolUsuarioRequestDTOToRolUsuario(rolUsuarioRequestDTO);
        RolUsuario rolGuardado = rolUsuarioRepository.save(rol);
        return rolUsuarioMapper.rolUsuarioToRolUsuarioResponseDTO(rolGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolUsuarioResponseDTO> listarRolesUsuario() {
        return rolUsuarioRepository.findAll().stream()
                .map(rolUsuarioMapper::rolUsuarioToRolUsuarioResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolUsuarioResponseDTO obtenerRolUsuarioPorId(Long idRolUsuario) {
        RolUsuario rol = rolUsuarioRepository.findById(idRolUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Rol de usuario no encontrado con ID: " + idRolUsuario));
        return rolUsuarioMapper.rolUsuarioToRolUsuarioResponseDTO(rol);
    }

    @Override
    @Transactional
    public RolUsuarioResponseDTO actualizarRolUsuario(Long idRolUsuario, RolUsuarioRequestDTO rolUsuarioRequestDTO) {
        RolUsuario rolExistente = rolUsuarioRepository.findById(idRolUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Rol de usuario no encontrado con ID: " + idRolUsuario));

        rolUsuarioRepository.findByNombreRol(rolUsuarioRequestDTO.getNombreRol()).ifPresent(r -> {
            if (!r.getIdRolUsuario().equals(idRolUsuario)) {
                throw new BusinessRuleException("Ya existe otro rol de usuario con el nombre: " + rolUsuarioRequestDTO.getNombreRol());
            }
        });

        rolExistente.setNombreRol(rolUsuarioRequestDTO.getNombreRol());
        RolUsuario rolActualizado = rolUsuarioRepository.save(rolExistente);
        return rolUsuarioMapper.rolUsuarioToRolUsuarioResponseDTO(rolActualizado);
    }

    @Override
    @Transactional
    public void eliminarRolUsuario(Long idRolUsuario) {
        RolUsuario rol = rolUsuarioRepository.findById(idRolUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Rol de usuario no encontrado con ID: " + idRolUsuario));

        if (usuarioRepository.existsByRolUsuario(rol)) {
            throw new BusinessRuleException("No se puede eliminar el rol porque está asignado a uno o más usuarios.");
        }
        try {
            rolUsuarioRepository.delete(rol);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("No se puede eliminar el rol. Verifique que no esté en uso.");
        }
    }
}