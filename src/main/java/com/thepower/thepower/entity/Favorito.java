package com.thepower.thepower.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favoritos")
@IdClass(FavoritoId.class)
public class Favorito {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cancion", nullable = false)
    private Cancion cancion;

    public Favorito() {
    }

    public Favorito(Usuario usuario, Cancion cancion) {
        this.usuario = usuario;
        this.cancion = cancion;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cancion getCancion() {
        return cancion;
    }

    public void setCancion(Cancion cancion) {
        this.cancion = cancion;
    }
}


