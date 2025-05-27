package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.RolesUsuarioResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buhoristeca.lector.dtos.RolUsuarioRequestDTO;
import com.buhoristeca.lector.dtos.RolUsuarioResponseDTO;
import com.buhoristeca.lector.service.RolUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class RolUsuarioController implements RolesUsuarioResource {

    private final RolUsuarioService rolUsuarioService;
    private final NativeWebRequest request;

    @Autowired
    public RolUsuarioController(RolUsuarioService rolUsuarioService, Optional<NativeWebRequest> request) {
        this.rolUsuarioService = rolUsuarioService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public RolUsuarioResponseDTO actualizarRolUsuario(Long idRolUsuario, RolUsuarioRequestDTO rolUsuarioRequestDTO) {
        return rolUsuarioService.actualizarRolUsuario(idRolUsuario, rolUsuarioRequestDTO);
    }

    @Override
    public RolUsuarioResponseDTO crearRolUsuario(RolUsuarioRequestDTO rolUsuarioRequestDTO) {
        return rolUsuarioService.crearRolUsuario(rolUsuarioRequestDTO);
    }

    @Override
    public void eliminarRolUsuario(Long idRolUsuario) {
        rolUsuarioService.eliminarRolUsuario(idRolUsuario);
    }

    @Override
    public List<RolUsuarioResponseDTO> listarRolesUsuario() {
        return rolUsuarioService.listarRolesUsuario();
    }

    @Override
    public RolUsuarioResponseDTO obtenerRolUsuarioPorId(Long idRolUsuario) {
        return rolUsuarioService.obtenerRolUsuarioPorId(idRolUsuario);
    }
}
