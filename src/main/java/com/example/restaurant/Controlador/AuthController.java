package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Usuario;
import com.example.restaurant.repositorios.UsuarioRepository;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import com.example.restaurant.entidades.Rol;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario, HttpServletResponse response) {
        Usuario encontrado = usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario());
        
        if (encontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(encontrado.getRol());
    }
}