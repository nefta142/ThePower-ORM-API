package com.thepower.thepower.controller;

import com.thepower.thepower.dto.*;
import com.thepower.thepower.service.EliminarCuentaService;
import com.thepower.thepower.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SecurityContextRepository securityContextRepository;

    public UsuarioController(
            UsuarioService usuarioService,
            SecurityContextRepository securityContextRepository) {
        this.usuarioService = usuarioService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "token", csrfToken.getToken(),
                "headerName", csrfToken.getHeaderName()
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registrar(
            @Valid @RequestBody RegistroDTO datos) {

        usuarioService.registrar(datos);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensaje",
                        "Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioRespuestaDTO> login(
            @Valid @RequestBody LoginDTO datos,
            HttpServletRequest request,
            HttpServletResponse response) {

        UsuarioRespuestaDTO usuario = usuarioService.login(datos);

        if (request.getSession(false) != null) {
            request.changeSessionId();
        } else {
            request.getSession(true);
        }

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        usuario.email(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context, request, response);

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/me")
    public UsuarioRespuestaDTO me(Authentication authentication) {
        return usuarioService.buscarPorEmail(
                authentication.getName());
    }

    @PutMapping("/me")
    public UsuarioRespuestaDTO actualizar(
            @Valid @RequestBody ActualizarUsuarioDTO datos,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        UsuarioRespuestaDTO usuario = usuarioService.actualizar(
                authentication.getName(), datos);

        Authentication nuevaAutenticacion =
                new UsernamePasswordAuthenticationToken(
                        usuario.email(),
                        null,
                        authentication.getAuthorities()
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(nuevaAutenticacion);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context, request, response);

        return usuario;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        new SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/me/contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @Valid @RequestBody CambiarContrasenaDTO datos,
            Authentication authentication) {

        usuarioService.cambiarContrasena(
                authentication.getName(),
                datos
        );

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Contraseña actualizada correctamente"
                )
        );
    }

    @Autowired
    private EliminarCuentaService eliminarCuentaService;


    @DeleteMapping("/me")
    public ResponseEntity<Void> eliminarCuenta(
            @Valid @RequestBody EliminarCuentaDTO datos,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        eliminarCuentaService.eliminar(
                authentication.getName(),
                datos.contrasena()
        );

        new SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        return ResponseEntity.noContent().build();
    }

}
