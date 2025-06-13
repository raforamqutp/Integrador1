package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.TipoCliente;
import com.example.restaurant.repositorios.ClienteRepository;
import com.example.restaurant.servicio.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller

@RequestMapping("/clientes")
public class ClienteController {
	
    @Autowired
    private ClienteService clienteService;
    private ClienteRepository clienteRepository;
    
    /**
     * Muestra la página de gestión de clientes
     */
    @GetMapping
    public String mostrarGestionClientes() {
        return "gestion-clientes";
    }
    
    /**
     * Procesa el registro de un nuevo cliente
     */
    // Guardar cliente
    
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCliente(@RequestBody Cliente cliente) {
        try {
            System.out.println("Entrando a guardarCliente...");

            String nombreCliente = cliente.getNombreCliente();
            TipoCliente tipo = cliente.getTipoCliente();

            if (nombreCliente == null || nombreCliente.length() < 3 || nombreCliente.length() > 100) {
                return ResponseEntity.badRequest().body("Nombre inválido");
            }

            if (tipo == null) {
                return ResponseEntity.badRequest().body("Tipo de cliente inválido");
            }

            clienteService.guardarCliente(cliente);
            return ResponseEntity.ok("Cliente guardado");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno");
        }
    }


    


    
    /**
     * Actualiza un cliente existente
     */
    @PostMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarCliente(@PathVariable Integer id,
                                             @RequestParam String nombreCliente,
                                             @RequestParam String tipoCliente) {
        try {
            TipoCliente tipo = TipoCliente.valueOf(tipoCliente.toLowerCase());
            clienteService.actualizarCliente(id, nombreCliente, tipo);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el cliente");
        }
    }
    
    /**
     * Elimina un cliente
     */
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarCliente(@PathVariable Integer id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el cliente");
        }
    }
    
    /**
     * Busca un cliente por ID (API REST)
     */
    @GetMapping("/api/buscar/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarClienteApi(@PathVariable Integer id) {
        try {
            Optional<Cliente> cliente = clienteService.buscarClientePorId(id);
            
            if (cliente.isPresent()) {
                return ResponseEntity.ok(cliente.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar el cliente");
        }
    }
    
    /**
     * Obtiene un cliente para editar
     */
    @GetMapping("/api/editar/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerClienteParaEditar(@PathVariable Integer id) {
        try {
            Optional<Cliente> cliente = clienteService.buscarClientePorId(id);
            
            if (cliente.isPresent()) {
                return ResponseEntity.ok(cliente.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el cliente");
        }
    }
    
    /**
     * Busca clientes por tipo (API REST)
     */
    @GetMapping("/api/tipo/{tipo}")
    @ResponseBody
    public ResponseEntity<List<Cliente>> buscarPorTipo(@PathVariable String tipo) {
        try {
            TipoCliente tipoCliente = TipoCliente.valueOf(tipo.toLowerCase());
            List<Cliente> clientes = clienteService.buscarClientesPorTipo(tipoCliente);
            
            return ResponseEntity.ok(clientes);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene todos los clientes (API REST)
     */
    @GetMapping("/api/buscar/todos")
    @ResponseBody
    public ResponseEntity<List<Cliente>> obtenerTodosClientes() {
        try {
            List<Cliente> clientes = clienteService.listarClientes();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Cliente>> buscarPorNombre(@RequestParam(required = false) String nombre) {
        try {
            List<Cliente> clientes;

            if (nombre == null || nombre.trim().isEmpty()) {
                clientes = clienteService.listarClientes();
            } else {
                clientes = clienteService.buscarClientesPorNombre(nombre);
            }

            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cajero/busqueda-rapida")
    @ResponseBody
    public ResponseEntity<List<Cliente>> busquedaRapida(@RequestParam("termino") String termino) {
        try {
            if (termino == null || termino.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Cliente> clientes = clienteService.buscarClientesPorNombre(termino.trim());
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    

 
}