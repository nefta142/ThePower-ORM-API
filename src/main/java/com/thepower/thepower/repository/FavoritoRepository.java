package com.thepower.thepower.repository;

import com.thepower.thepower.entity.Cancion;
import com.thepower.thepower.entity.Favorito;
import com.thepower.thepower.entity.FavoritoId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritoRepository
        extends JpaRepository<Favorito, FavoritoId> {

    @Query("""
        SELECT f.cancion
        FROM Favorito f
        WHERE f.usuario.idUsuario = :idUsuario
        ORDER BY f.cancion.titulo
    """)
    List<Cancion> buscarCancionesFavoritas(
            @Param("idUsuario") int idUsuario
    );
}

