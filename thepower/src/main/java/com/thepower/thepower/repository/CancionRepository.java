package com.thepower.thepower.repository;

import com.thepower.thepower.entity.Cancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CancionRepository
        extends JpaRepository<Cancion, Integer> {

    @Query("""
        SELECT DISTINCT c
        FROM Cancion c
        LEFT JOIN c.colaboraciones col
        LEFT JOIN col.artista a
        WHERE (
            :texto IS NULL
            OR LOWER(c.titulo) LIKE
                LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(a.nombreUsuario) LIKE
                LOWER(CONCAT('%', :texto, '%'))
        )
        AND (
            :genero IS NULL
            OR c.genero.idGenero = :genero
        )
        ORDER BY c.titulo ASC
    """)
    List<Cancion> buscar(
            @Param("texto") String texto,
            @Param("genero") Integer genero
    );
}

