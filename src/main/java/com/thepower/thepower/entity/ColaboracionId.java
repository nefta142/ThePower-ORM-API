package com.thepower.thepower.entity;

import java.io.Serializable;
import java.util.Objects;

public class ColaboracionId implements Serializable {

    private int artista;
    private int cancion;

    public ColaboracionId() {
    }

    public ColaboracionId(int artista, int cancion) {
        this.artista = artista;
        this.cancion = cancion;
    }

    public int getArtista() {
        return artista;
    }

    public void setArtista(int artista) {
        this.artista = artista;
    }

    public int getCancion() {
        return cancion;
    }

    public void setCancion(int cancion) {
        this.cancion = cancion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColaboracionId that)) return false;

        return artista == that.artista
                && cancion == that.cancion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(artista, cancion);
    }
}
