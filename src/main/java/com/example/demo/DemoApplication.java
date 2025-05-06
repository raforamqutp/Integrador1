package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;

/**
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

**/


@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Ejemplo de prueba (opcional)
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Probar consulta
    public void probarConexion() {
        Usuario usuario = usuarioRepository.findByNombreUsuarioAndContraseña("admin", "admin123");
        System.out.println("Usuario encontrado: " + usuario.getNombreUsuario());
    }
}