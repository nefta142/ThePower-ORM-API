package com.thepower.thepower.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cancion")
public class Cancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancion", nullable = false)
    private int idCancion;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "imagen", length = 255, nullable = false)
    private String imagen;

    @Column(name = "audio", length = 2048, nullable = false)
    private String audio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero", nullable = false)
    private Genero genero;

    @OneToMany(mappedBy = "cancion")
    private List<Favorito> favoritos = new ArrayList<>();

    @OneToMany(mappedBy = "cancion")
    private List<Reproduccion> reproducciones = new ArrayList<>();

    @OneToMany(mappedBy = "cancion")
    private List<Colaboracion> colaboraciones = new ArrayList<>();

    public Cancion() {
    }

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public List<Favorito> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Favorito> favoritos) {
        this.favoritos = favoritos;
    }

    public List<Reproduccion> getReproducciones() {
        return reproducciones;
    }

    public void setReproducciones(
            List<Reproduccion> reproducciones) {
        this.reproducciones = reproducciones;
    }

    public List<Colaboracion> getColaboraciones() {
        return colaboraciones;
    }

    public void setColaboraciones(
            List<Colaboracion> colaboraciones) {
        this.colaboraciones = colaboraciones;
    }
}
