package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.RolUsuarioRequestDTO;
import com.buhoristeca.lector.dtos.RolUsuarioResponseDTO;
import java.util.List;

public interface RolUsuarioService {
    RolUsuarioResponseDTO crearRolUsuario(RolUsuarioRequestDTO rolUsuarioRequestDTO);
    List<RolUsuarioResponseDTO> listarRolesUsuario();
    RolUsuarioResponseDTO obtenerRolUsuarioPorId(Long idRolUsuario);
    RolUsuarioResponseDTO actualizarRolUsuario(Long idRolUsuario, RolUsuarioRequestDTO rolUsuarioRequestDTO);
    void eliminarRolUsuario(Long idRolUsuario);
}