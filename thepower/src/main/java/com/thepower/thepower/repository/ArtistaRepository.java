package com.thepower.thepower.repository;

import com.thepower.thepower.entity.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistaRepository
        extends JpaRepository<Artista, Integer> {

    @Modifying
    @Query(value = """
        INSERT INTO artista (id_usuario, descripcion)
        VALUES (:idUsuario, :descripcion)
        """, nativeQuery = true)
    void crearPerfilArtista(
            @Param("idUsuario") int idUsuario,
            @Param("descripcion") String descripcion
    );
}
