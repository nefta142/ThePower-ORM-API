package com.thepower.thepower.controller;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.service.GestionCancionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private final GestionCancionService gestionCancionService;

    public ArtistaController(
            GestionCancionService gestionCancionService) {
        this.gestionCancionService = gestionCancionService;
    }

    @GetMapping("/{id}/canciones")
    public List<CancionDTO> obtenerCanciones(
            @PathVariable int id) {

        return gestionCancionService.cancionesDeArtista(id);
    }
}

