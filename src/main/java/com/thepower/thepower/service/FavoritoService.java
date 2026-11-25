package com.thepower.thepower.service;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.entity.Cancion;
import com.thepower.thepower.entity.Favorito;
import com.thepower.thepower.entity.FavoritoId;
import com.thepower.thepower.entity.Usuario;
import com.thepower.thepower.repository.CancionRepository;
import com.thepower.thepower.repository.FavoritoRepository;
import com.thepower.thepower.repository.UsuarioRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CancionRepository cancionRepository;
    private final CancionService cancionService;

    public FavoritoService(
            FavoritoRepository favoritoRepository,
            UsuarioRepository usuarioRepository,
            CancionRepository cancionRepository,
            CancionService cancionService) {

        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cancionRepository = cancionRepository;
        this.cancionService = cancionService;
    }

    private Usuario obtenerUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Sesión no válida"
                        ));
    }

    @Transactional
    public void agregar(int idCancion, String email) {

        Usuario usuario = obtenerUsuario(email);

        Cancion cancion = cancionRepository.findById(idCancion)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Canción no encontrada"
                        ));

        FavoritoId id = new FavoritoId(
                usuario.getIdUsuario(),
                idCancion
        );

        if (favoritoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La canción ya está en favoritos"
            );
        }

        try {
            favoritoRepository.saveAndFlush(
                    new Favorito(usuario, cancion)
            );
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo añadir a favoritos",
                    e
            );
        }
    }

    @Transactional
    public void eliminar(int idCancion, String email) {

        Usuario usuario = obtenerUsuario(email);

        FavoritoId id = new FavoritoId(
                usuario.getIdUsuario(),
                idCancion
        );

        if (favoritoRepository.existsById(id)) {
            favoritoRepository.deleteById(id);
            favoritoRepository.flush();
        }
    }

    @Transactional(readOnly = true)
    public List<CancionDTO> listar(String email) {

        Usuario usuario = obtenerUsuario(email);

        return favoritoRepository
                .buscarCancionesFavoritas(usuario.getIdUsuario())
                .stream()
                .map(cancion -> cancionService.obtenerPorId(
                        cancion.getIdCancion()
                ))
                .toList();
    }
}
