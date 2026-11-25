package com.thepower.thepower.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artista")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Artista extends Usuario {

    @Column(name = "descripcion", length = 250, nullable = false)
    private String descripcion;

    @OneToMany(mappedBy = "artista")
    private List<Colaboracion> colaboraciones = new ArrayList<>();

    public Artista() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Colaboracion> getColaboraciones() {
        return colaboraciones;
    }

    public void setColaboraciones(List<Colaboracion> colaboraciones) {
        this.colaboraciones = colaboraciones;
    }
}


