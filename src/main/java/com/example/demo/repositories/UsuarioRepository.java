package com.example.demo.repositories;
import com.example.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Query automática por nombre de método
    Usuario findByNombreUsuarioAndContraseña(String nombreUsuario, String contraseña);
}