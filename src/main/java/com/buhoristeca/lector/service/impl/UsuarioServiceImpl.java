package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.dtos.UsuarioRequestDTO;
import com.buhoristeca.lector.dtos.UsuarioResponseDTO;
import com.buhoristeca.lector.model.RolUsuario;
import com.buhoristeca.lector.model.Usuario;
import com.buhoristeca.lector.repositories.RolUsuarioRepository;
import com.buhoristeca.lector.repositories.UsuarioRepository;
import com.buhoristeca.lector.service.UsuarioService;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolUsuarioRepository rolUsuarioRepository,
                              UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.existsByDni(usuarioRequestDTO.getDni())) {
            throw new BusinessRuleException("Ya existe un usuario con el DNI: " + usuarioRequestDTO.getDni());
        }
        if (usuarioRepository.existsByCorreoElectronico(usuarioRequestDTO.getCorreoElectronico())) {
            throw new BusinessRuleException("Ya existe un usuario con el correo electrónico: " + usuarioRequestDTO.getCorreoElectronico());
        }

        RolUsuario rol = rolUsuarioRepository.findById(usuarioRequestDTO.getIdRolUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Rol de usuario no encontrado con ID: " + usuarioRequestDTO.getIdRolUsuario()));

        Usuario usuario = usuarioMapper.usuarioRequestDTOToUsuario(usuarioRequestDTO, rol);
        usuario.setActivo(true);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> consultarUsuarios(String dni, String email, Pageable pageable) {
        Page<Usuario> usuariosPage;
        if (StringUtils.hasText(dni)) {
            usuariosPage = usuarioRepository.findByDniContainingAndActivoTrue(dni, pageable);
        } else if (StringUtils.hasText(email)) {
            usuariosPage = usuarioRepository.findByCorreoElectronicoContainingAndActivoTrue(email, pageable);
        } else {
            usuariosPage = usuarioRepository.findAllByActivoTrue(pageable);
        }
        return usuariosPage.map(usuarioMapper::usuarioToUsuarioResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO consultarUsuarioPorId(Long idUsuario) {
        Usuario usuario = usuarioRepository.findByIdUsuarioAndActivoTrue(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario activo no encontrado con ID: " + idUsuario));
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO modificarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        if (!usuarioExistente.getDni().equals(usuarioRequestDTO.getDni()) &&
                usuarioRepository.existsByDni(usuarioRequestDTO.getDni())) {
            throw new BusinessRuleException("El nuevo DNI ya está en uso por otro usuario.");
        }
        if (!usuarioExistente.getCorreoElectronico().equals(usuarioRequestDTO.getCorreoElectronico()) &&
                usuarioRepository.existsByCorreoElectronico(usuarioRequestDTO.getCorreoElectronico())) {
            throw new BusinessRuleException("El nuevo correo electrónico ya está en uso por otro usuario.");
        }

        RolUsuario rol = rolUsuarioRepository.findById(usuarioRequestDTO.getIdRolUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Rol de usuario no encontrado con ID: " + usuarioRequestDTO.getIdRolUsuario()));

        usuarioExistente.setNombre(usuarioRequestDTO.getNombre());
        usuarioExistente.setApellido(usuarioRequestDTO.getApellido());
        usuarioExistente.setDni(usuarioRequestDTO.getDni());
        usuarioExistente.setFechaNacimiento(usuarioRequestDTO.getFechaNacimiento());
        usuarioExistente.setDireccion(usuarioRequestDTO.getDireccion());
        usuarioExistente.setCorreoElectronico(usuarioRequestDTO.getCorreoElectronico());
        usuarioExistente.setRolUsuario(rol);

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void bajaLogicaUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}