package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.UsuarioRequestDTO;
import com.buhoristeca.lector.dtos.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO usuarioRequestDTO);
    Page<UsuarioResponseDTO> consultarUsuarios(String dni, String email, Pageable pageable);
    UsuarioResponseDTO consultarUsuarioPorId(Long idUsuario);
    UsuarioResponseDTO modificarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO);
    void bajaLogicaUsuario(Long idUsuario);
}