package com.thepower.thepower.entity;

import java.io.Serializable;
import java.util.Objects;

public class FavoritoId implements Serializable {

    private int usuario;
    private int cancion;

    public FavoritoId() {
    }

    public FavoritoId(int usuario, int cancion) {
        this.usuario = usuario;
        this.cancion = cancion;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
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
        if (!(o instanceof FavoritoId that)) return false;

        return usuario == that.usuario
                && cancion == that.cancion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, cancion);
    }
}
