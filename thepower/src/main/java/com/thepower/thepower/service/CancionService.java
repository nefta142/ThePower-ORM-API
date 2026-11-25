package com.thepower.thepower.service;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.entity.*;
import com.thepower.thepower.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CancionService {

    private final CancionRepository cancionRepository;
    private final GeneroRepository generoRepository;
    private final ColaboracionRepository colaboracionRepository;
    private final ArtistaService artistaService;
    private final ArchivoService archivoService;

    public CancionService(
            CancionRepository cancionRepository,
            GeneroRepository generoRepository,
            ColaboracionRepository colaboracionRepository,
            ArtistaService artistaService,
            ArchivoService archivoService) {

        this.cancionRepository = cancionRepository;
        this.generoRepository = generoRepository;
        this.colaboracionRepository = colaboracionRepository;
        this.artistaService = artistaService;
        this.archivoService = archivoService;
    }

    @Transactional(readOnly = true)
    public List<CancionDTO> obtenerTodas() {
        return buscar(null, null);
    }

    @Transactional(readOnly = true)
    public CancionDTO obtenerPorId(int id) {
        return cancionRepository.findById(id)
                .map(this::convertirDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CancionDTO> buscar(
            String texto, Integer genero) {

        String busqueda = texto == null ||
                texto.isBlank()
                ? null
                : texto.trim();

        return cancionRepository
                .buscar(busqueda, genero)
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    @Transactional
    public CancionDTO subir(
            String titulo,
            Integer idGenero,
            MultipartFile audio,
            MultipartFile imagen,
            String email) {

        if (titulo == null || titulo.isBlank() ||
                titulo.trim().length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Título incorrecto");
        }

        Genero genero = generoRepository.findById(idGenero)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "El género no existe"));

        String rutaAudio = null;
        String rutaImagen = null;

        try {
            rutaAudio = archivoService.guardar(
                    audio, "audio");

            rutaImagen = archivoService.guardar(
                    imagen, "imagenes");

            Artista artista =
                    artistaService.obtenerOCrearArtista(email);

            Cancion cancion = new Cancion();
            cancion.setTitulo(titulo.trim());
            cancion.setGenero(genero);
            cancion.setAudio(rutaAudio);
            cancion.setImagen(rutaImagen);

            cancion = cancionRepository.saveAndFlush(cancion);

            Colaboracion colaboracion =
                    new Colaboracion(artista, cancion);

            colaboracionRepository.saveAndFlush(colaboracion);

            return convertirDTO(cancion);

        } catch (RuntimeException e) {
            if (rutaAudio != null) {
                archivoService.eliminar(rutaAudio);
            }

            if (rutaImagen != null) {
                archivoService.eliminar(rutaImagen);
            }

            throw e;
        }
    }

    private CancionDTO convertirDTO(Cancion cancion) {
        return new CancionDTO(
                cancion.getIdCancion(),
                cancion.getTitulo(),
                cancion.getImagen(),
                cancion.getAudio(),
                cancion.getGenero().getIdGenero(),
                cancion.getGenero().getTipo()
        );
    }
}
