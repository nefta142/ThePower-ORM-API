package com.thepower.thepower.controller;

import com.thepower.thepower.dto.AgregarColaboradorDTO;
import com.thepower.thepower.dto.ColaboradorRespuestaDTO;
import com.thepower.thepower.service.ColaboradorService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canciones/{idCancion}/colaboradores")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    public ColaboradorController(
            ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @GetMapping
    public List<ColaboradorRespuestaDTO> listar(
            @PathVariable int idCancion) {

        return colaboradorService.listar(idCancion);
    }

    @PostMapping
    public ResponseEntity<Void> agregar(
            @PathVariable int idCancion,
            @Valid @RequestBody AgregarColaboradorDTO datos,
            Authentication authentication) {

        colaboradorService.agregar(
                idCancion,
                datos.idArtista(),
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{idArtista}")
    public ResponseEntity<Void> eliminar(
            @PathVariable int idCancion,
            @PathVariable int idArtista,
            Authentication authentication) {

        colaboradorService.eliminar(
                idCancion,
                idArtista,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}

