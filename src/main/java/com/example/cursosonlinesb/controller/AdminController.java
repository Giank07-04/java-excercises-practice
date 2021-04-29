package com.example.cursosonlinesb.controller;

import com.example.cursosonlinesb.model.Curso;
import com.example.cursosonlinesb.repo.CursoRepository;
import com.example.cursosonlinesb.service.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/cursos")
public class AdminController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @GetMapping("")
    ModelAndView index(@PageableDefault(sort = "titulo", size = 5) Pageable pageable) {
        Page<Curso> cursos = cursoRepository.findAll(pageable);

        return new ModelAndView("admin/index")
                .addObject("cursos", cursos);
    }

    @GetMapping("/nuevo")
    ModelAndView nuevoCurso() {
        return new ModelAndView("admin/nuevo-curso")
                .addObject("curso", new Curso());
    }

    @PostMapping("/nuevo")
    ModelAndView crearCurso(@Validated Curso curso, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors() || curso.getImagen().isEmpty()) {
            if (curso.getImagen().isEmpty()) {
                bindingResult.rejectValue("imagen", "MultipartNotEmpty");
            }
            return new ModelAndView(("admin/nuevo-curso"))
                    .addObject("curso", curso);
        }
        String rutaImagen = fileSystemStorageService.store(curso.getImagen());
        curso.setRutaImagen(rutaImagen);
        curso.setFechaCreacion(LocalDateTime.now());

        cursoRepository.save(curso);
        ra.addFlashAttribute("success", "El curso ha sido creado.");
        return new ModelAndView("redirect:/admin/cursos");
    }

    @GetMapping("/{id}/editar")
    ModelAndView editarCurso(@PathVariable Integer id) {
        Curso curso = cursoRepository.getOne(id);

        return new ModelAndView("admin/editar-curso")
                .addObject("curso", curso);
    }

    @PostMapping("/{id}/editar")
    ModelAndView actualizarCurso(@PathVariable Integer id,
                                 @Validated Curso curso, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("admin/editar-curso")
                    .addObject("curso", curso);
        }
        Curso cursoFromDB = cursoRepository.getOne(id);
        cursoFromDB.setTitulo(curso.getTitulo());
        cursoFromDB.setDescripcion(curso.getDescripcion());
        cursoFromDB.setPrecio(curso.getPrecio());

        if (!curso.getImagen().isEmpty()) {
            String rutaImagen = fileSystemStorageService.store(curso.getImagen());
            cursoFromDB.setRutaImagen(rutaImagen);
        }
        cursoRepository.save(cursoFromDB);

        ra.addFlashAttribute("success", "El curso ha sido actualizado.");
        return new ModelAndView("redirect:/admin/cursos");
    }

    @PostMapping("/{id}/eliminar")
    String eliminarCurso(@PathVariable Integer id, RedirectAttributes ra) {
        Curso curso = cursoRepository.getOne(id);
        cursoRepository.delete(curso);
        fileSystemStorageService.delete(curso.getRutaImagen());

        ra.addFlashAttribute("success", "El curso ha sido eliminado.");
        return "redirect:/admin/cursos";
    }

}
