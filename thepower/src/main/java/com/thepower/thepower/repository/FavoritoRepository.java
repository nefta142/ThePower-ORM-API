package com.thepower.thepower.repository;

import com.thepower.thepower.entity.Favorito;
import com.thepower.thepower.entity.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

}

