package com.example.cursosonlinesb.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcurso")
    private Integer id;

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotNull
    private Float precio;

    private String rutaImagen;

    private LocalDateTime fechaCreacion;

    @Transient
    private MultipartFile imagen;
}
