
package com.thepower.thepower.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "colaboracion")
@IdClass(ColaboracionId.class)
public class Colaboracion {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Artista artista;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cancion", nullable = false)
    private Cancion cancion;

    public Colaboracion() {
    }

    public Colaboracion(Artista artista, Cancion cancion) {
        this.artista = artista;
        this.cancion = cancion;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public Cancion getCancion() {
        return cancion;
    }

    public void setCancion(Cancion cancion) {
        this.cancion = cancion;
    }
}
