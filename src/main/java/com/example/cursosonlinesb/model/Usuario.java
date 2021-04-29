package com.example.cursosonlinesb.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Integer id;

    @Email
    @NotBlank
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Size(min = 6)
    @Transient
    private String password1;

    @NotNull
    @Transient
    private String password2;

    public enum Rol {
        ADMIN,
        ESTUDIANTE
    }
}
