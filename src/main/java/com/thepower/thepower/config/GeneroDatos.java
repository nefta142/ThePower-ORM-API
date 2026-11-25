package com.thepower.thepower.config;

import com.thepower.thepower.entity.Genero;
import com.thepower.thepower.repository.GeneroRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GeneroDatos {

    @Bean
    CommandLineRunner cargarGeneros(GeneroRepository generoRepository) {

        return args -> {

            List<String> generos = List.of(
                    "Pop",
                    "Rock",
                    "Rap",
                    "Hip-Hop",
                    "Reggaeton",
                    "Trap",
                    "Electrónica",
                    "Techno",
                    "House",
                    "Jazz",
                    "Blues",
                    "R&B",
                    "Soul",
                    "Metal",
                    "Punk",
                    "Indie",
                    "Clásica",
                    "Country",
                    "Folk",
                    "Latina",
                    "Salsa",
                    "Bachata",
                    "Merengue",
                    "K-Pop",
                    "Otros"
            );

            List<String> existentes = generoRepository.findAll()
                    .stream()
                    .map(Genero::getTipo)
                    .map(String::toLowerCase)
                    .toList();

            for (String tipo : generos) {

                if (!existentes.contains(tipo.toLowerCase())) {

                    Genero genero = new Genero();
                    genero.setTipo(tipo);

                    generoRepository.save(genero);
                }
            }
        };
    }
}

