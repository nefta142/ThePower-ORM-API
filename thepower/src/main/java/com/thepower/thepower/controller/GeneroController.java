
package com.thepower.thepower.controller;

import com.thepower.thepower.entity.Genero;
import com.thepower.thepower.service.GeneroService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }


    @GetMapping
    public List<Genero> obtenerTodos() {
        return generoService.obtenerTodos();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Genero> obtenerPorId(@PathVariable int id) {
        Genero genero = generoService.obtenerPorId(id);

        if (genero == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(genero);
    }
}

