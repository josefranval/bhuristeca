package com.buhoristeca.lector.controllers;

import com.buhoristeca.lector.controllers.resources.IdiomasResource;
import com.buhoristeca.lector.controllers.resources.UsuariosResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buhoristeca.lector.controllers.resources.UsuariosResource;
import com.buhoristeca.lector.dtos.UsuarioRequestDTO;
import com.buhoristeca.lector.dtos.UsuarioResponseDTO;
import com.buhoristeca.lector.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UsuarioController implements UsuariosResource {

    private final UsuarioService usuarioService;
    private final NativeWebRequest request;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, Optional<NativeWebRequest> request) {
        this.usuarioService = usuarioService;
        this.request = request.orElse(null);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public void bajaLogicaUsuario(Long idUsuario) {
        usuarioService.bajaLogicaUsuario(idUsuario);
    }

    @Override
    public UsuarioResponseDTO consultarUsuarioPorId(Long idUsuario) {
        return usuarioService.consultarUsuarioPorId(idUsuario);
    }

    @Override
    public List<UsuarioResponseDTO> consultarUsuarios(String dni, String email, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioService.consultarUsuarios(dni, email, pageable).getContent();
    }

    @Override
    public UsuarioResponseDTO modificarUsuario(Long idUsuario, UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioService.modificarUsuario(idUsuario, usuarioRequestDTO);
    }

    @Override
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioService.registrarUsuario(usuarioRequestDTO);
    }
}