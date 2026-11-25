
package com.thepower.thepower.service;

import com.thepower.thepower.entity.Genero;
import com.thepower.thepower.repository.GeneroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;

    public GeneroService(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    @Transactional(readOnly = true)
    public List<Genero> obtenerTodos() {
        return generoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Genero obtenerPorId(int id) {
        return generoRepository.findById(id).orElse(null);
    }
}

