package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Usuario;
import com.example.restaurant.repositorios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @PostMapping("/login")
    public String login(@RequestParam String nombreUsuario, 
                       @RequestParam String contraseña, 
                       HttpSession session) {
        Usuario usuario = usuarioRepo.findByNombreUsuarioAndContraseña(nombreUsuario, contraseña);
        if (usuario != null) {
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            return "redirect:/main.html";
        }
        return "redirect:/login.html?error";
    }
}