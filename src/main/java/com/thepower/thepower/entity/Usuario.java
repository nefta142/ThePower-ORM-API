package com.thepower.thepower.entity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "nombre_user", length = 25,
            nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "contrasena", length = 255,
            nullable = false)
    private String contrasena;

    @Column(name = "email", length = 50,
            nullable = false, unique = true)
    private String email;

    @Column(name = "nombre", length = 50,
            nullable = false)
    private String nombre;

    @Column(name = "apellido1", length = 50,
            nullable = false)
    private String apellido1;

    @Column(name = "apellido2", length = 50)
    private String apellido2;

    @Column(name = "avatar", length = 255)
    private String avatar;

    public Usuario() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    @OneToMany(mappedBy = "usuario")
    private List<Favorito> favoritos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Reproduccion> reproducciones = new ArrayList<>();


    public List<Favorito> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Favorito> favoritos) {
        this.favoritos = favoritos;
    }

    public List<Reproduccion> getReproducciones() {
        return reproducciones;
    }

    public void setReproducciones(List<Reproduccion> reproducciones) {
        this.reproducciones = reproducciones;
    }


}
