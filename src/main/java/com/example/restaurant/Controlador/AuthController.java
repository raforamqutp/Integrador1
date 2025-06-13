package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Usuario;
import com.example.restaurant.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/login")
    public String login(@RequestBody Usuario usuario) {
        Usuario encontrado = usuarioRepository.findByNombreUsuarioAndContrasena(
            usuario.getNombreUsuario(), usuario.getContrasena()
        );
        if (encontrado != null) {
            return encontrado.getRol(); 
        } else {
            return "ERROR";
        }
    }
}