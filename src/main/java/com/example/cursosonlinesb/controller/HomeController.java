package com.example.cursosonlinesb.controller;

import com.example.cursosonlinesb.model.Curso;
import com.example.cursosonlinesb.model.Inscripcion;
import com.example.cursosonlinesb.model.Usuario;
import com.example.cursosonlinesb.repo.CursoRepository;
import com.example.cursosonlinesb.repo.InscripcionRepository;
import com.example.cursosonlinesb.repo.UsuarioRepository;
import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class
HomeController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    ModelAndView index() {
//        if (true) throw new RuntimeException("Error interno del servidor.");

        List<Curso> cursosRecientes = cursoRepository
                .findAll(PageRequest.of(0, 8, Sort.by("fechaCreacion").descending()))
                .toList();

        return new ModelAndView("index")
                .addObject("cursosRecientes", cursosRecientes);
    }

    @GetMapping("/cursos")
    ModelAndView listaCursos(@PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Curso> cursos = cursoRepository
                .findAll(pageable);

        return new ModelAndView("lista-cursos")
                .addObject("cursos", cursos);
    }

    @GetMapping("/cursos/{id}")
    ModelAndView detallesCurso(@PathVariable Integer id) {
        Curso curso = cursoRepository.getOne(id);

        try {
            MercadoPago.SDK.setAccessToken("TEST-765229078052477-033000-f01fcd106c3580b6f4aec351fdad68cb-135203426");
            Preference mercadoPagoPreference = new Preference();

            Item item = new Item();
            item
                    .setTitle(curso.getTitulo())
                    .setQuantity(1)
                    .setUnitPrice(curso.getPrecio());

            mercadoPagoPreference.appendItem(item);

            BackUrls backUrls = new BackUrls(
                    "http://localhost:8080/inscribir?c=" + curso.getId(),
                    "http://localhost:8080/mercado-pago/pending",
                    "http://localhost:8080/mercado-pago/error"
            );

            mercadoPagoPreference.setBackUrls(backUrls);

            mercadoPagoPreference.save();

            return new ModelAndView("detalles-curso")
                    .addObject("curso", curso)
                    .addObject("mercadoPagoPreference", mercadoPagoPreference);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @GetMapping("/inscribir")
    String inscribir(
            @RequestParam("payment_id") String paymentId,
            @RequestParam String status,
            @RequestParam("merchant_order_id") String merchantOrderId,

            @RequestParam("c") Integer idCurso,

            Principal principal
    ) {
        Curso curso = cursoRepository
                .getOne(idCurso);

        Usuario usuario = usuarioRepository
                .findOneByEmail(principal.getName())
                .get();

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        inscripcionRepository.save(inscripcion);

        return "redirect:/mis-cursos";
    }

    @GetMapping("/mis-cursos")
    ModelAndView misCursos(@PageableDefault(sort = "fechaInscripcion", direction = Sort.Direction.DESC) Pageable pageable,
                           Principal principal) {

        Usuario usuario = usuarioRepository
                .findOneByEmail(principal.getName())
                .get();

        Page<Inscripcion> inscripciones = inscripcionRepository
                .findByUsuario(usuario, pageable);

        return new ModelAndView("mis-cursos")
                .addObject("inscripciones", inscripciones);
    }

    @GetMapping("/registro")
    ModelAndView registro() {
        return new ModelAndView("registro")
                .addObject("usuario", new Usuario());
    }

    @PostMapping("/registro")
    ModelAndView crearUsuario(@Validated Usuario usuario, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("registro")
                    .addObject("usuario", usuario);
        } else if (!usuario.getPassword1().equals(usuario.getPassword2())) {
            bindingResult.rejectValue("password2", "PasswordNotEquals");

            return new ModelAndView("registro")
                    .addObject("usuario", usuario);
        } else {
            boolean usuarioYaExiste = usuarioRepository.findOneByEmail(usuario.getEmail()).isPresent();

            if (usuarioYaExiste) {
                bindingResult.rejectValue("email", "EmailAlreadyExist");
                return new ModelAndView("registro")
                        .addObject("usuario", usuario);
            }
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword1()));
        usuario.setRol(Usuario.Rol.ESTUDIANTE);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("success", "Te has registrado correctamente por favor inicia sesión.");
        return new ModelAndView("redirect:/login");
    }

}
