package com.thepower.thepower.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reproducciones")
public class Reproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reproduccion", nullable = false)
    private int idReproduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cancion", nullable = false)
    private Cancion cancion;

    @Column(name = "fecha_reproduccion", nullable = false)
    private LocalDateTime fechaReproduccion;

    public Reproduccion() {
    }

    public Reproduccion(Usuario usuario, Cancion cancion) {
        this.usuario = usuario;
        this.cancion = cancion;
        this.fechaReproduccion = LocalDateTime.now();
    }

    @PrePersist
    public void asignarFecha() {
        if (fechaReproduccion == null) {
            fechaReproduccion = LocalDateTime.now();
        }
    }

    public int getIdReproduccion() {
        return idReproduccion;
    }

    public void setIdReproduccion(int idReproduccion) {
        this.idReproduccion = idReproduccion;
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

    public LocalDateTime getFechaReproduccion() {
        return fechaReproduccion;
    }

    public void setFechaReproduccion(LocalDateTime fechaReproduccion) {
        this.fechaReproduccion = fechaReproduccion;
    }
}
