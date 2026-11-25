package com.thepower.thepower.service;

import com.thepower.thepower.dto.ActualizarUsuarioDTO;
import com.thepower.thepower.dto.LoginDTO;
import com.thepower.thepower.dto.RegistroDTO;
import com.thepower.thepower.dto.UsuarioRespuestaDTO;
import com.thepower.thepower.entity.Usuario;
import com.thepower.thepower.repository.UsuarioRepository;
import com.thepower.thepower.dto.CambiarContrasenaDTO;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Transactional
    public void registrar(RegistroDTO datos) {

        String email = datos.email().trim()
                .toLowerCase(Locale.ROOT);
        String nombreUsuario = datos.nombreUsuario().trim();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado");
        }

        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya existe");
        }

        Usuario usuario = new Usuario();

        usuario.setNombreUsuario(nombreUsuario);
        usuario.setEmail(email);
        usuario.setContrasena(
                encoder.encode(datos.contrasena()));
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellido1(datos.apellido1().trim());
        usuario.setApellido2(
                datos.apellido2() == null ||
                        datos.apellido2().isBlank()
                        ? null
                        : datos.apellido2().trim());
        usuario.setAvatar(null);

        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Usuario o correo ya existente", e);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO login(LoginDTO datos) {

        String email = datos.email().trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Correo o contraseña incorrectos"));

        if (!encoder.matches(
                datos.contrasena(),
                usuario.getContrasena())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos");
        }

        return respuesta(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO buscarPorEmail(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Sesión no válida"));

        return respuesta(usuario);
    }

    @Transactional
    public UsuarioRespuestaDTO actualizar(
            String emailActual,
            ActualizarUsuarioDTO datos) {

        Usuario usuario = usuarioRepository
                .findByEmail(emailActual)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Sesión no válida"));

        String nuevoEmail = datos.email().trim()
                .toLowerCase(Locale.ROOT);

        String nuevoUsuario = datos.nombreUsuario().trim();

        if (!usuario.getEmail().equals(nuevoEmail) &&
                usuarioRepository.existsByEmail(nuevoEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado");
        }

        if (!usuario.getNombreUsuario().equals(nuevoUsuario) &&
                usuarioRepository.existsByNombreUsuario(nuevoUsuario)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya existe");
        }

        usuario.setNombreUsuario(nuevoUsuario);
        usuario.setEmail(nuevoEmail);
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellido1(datos.apellido1().trim());

        usuario.setApellido2(
                datos.apellido2() == null ||
                        datos.apellido2().isBlank()
                        ? null
                        : datos.apellido2().trim());

        try {
            return respuesta(
                    usuarioRepository.saveAndFlush(usuario));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo o usuario ya existe", e);
        }
    }

    private UsuarioRespuestaDTO respuesta(Usuario usuario) {

        return new UsuarioRespuestaDTO(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido1(),
                usuario.getApellido2(),
                usuario.getAvatar()
        );
    }

    @Transactional
    public void cambiarContrasena(
            String email,
            CambiarContrasenaDTO datos) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Sesión no válida"
                        ));

        if (!encoder.matches(
                datos.contrasenaActual(),
                usuario.getContrasena())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña actual es incorrecta"
            );
        }

        if (encoder.matches(
                datos.contrasenaNueva(),
                usuario.getContrasena())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva contraseña debe ser diferente"
            );
        }

        usuario.setContrasena(
                encoder.encode(datos.contrasenaNueva())
        );

        usuarioRepository.saveAndFlush(usuario);
    }

}
