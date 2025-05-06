package com.example.demo.services;


import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario autenticar(String nombreUsuario, String contraseña) {
        return usuarioRepository.findByNombreUsuarioAndContraseña(nombreUsuario, contraseña);
    }
}