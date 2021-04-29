package com.example.cursosonlinesb.repo;

import com.example.cursosonlinesb.model.Inscripcion;
import com.example.cursosonlinesb.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    Page<Inscripcion> findByUsuario(Usuario usuario, Pageable pageable);
}
