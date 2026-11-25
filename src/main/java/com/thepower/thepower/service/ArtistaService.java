
package com.thepower.thepower.service;

import com.thepower.thepower.entity.Artista;
import com.thepower.thepower.entity.Usuario;
import com.thepower.thepower.repository.ArtistaRepository;
import com.thepower.thepower.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntityManager entityManager;

    public ArtistaService(
            ArtistaRepository artistaRepository,
            UsuarioRepository usuarioRepository,
            EntityManager entityManager) {

        this.artistaRepository = artistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Artista obtenerOCrearArtista(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        int idUsuario = usuario.getIdUsuario();

        Artista artista = artistaRepository.findById(idUsuario)
                .orElse(null);

        if (artista != null) {
            return artista;
        }

        entityManager.flush();

        artistaRepository.crearPerfilArtista(
                idUsuario,
                "Artista de The Power"
        );


        entityManager.clear();


        return artistaRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No se pudo crear el perfil de artista"
                ));
    }
}

