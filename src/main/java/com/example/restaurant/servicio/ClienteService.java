package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.TipoCliente;
import com.example.restaurant.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    /**
     * Registra un nuevo cliente
     */
    public Cliente registrarCliente(String nombre, TipoCliente tipo) {
        Cliente cliente = new Cliente();
        cliente.setNombreCliente(nombre);
        cliente.setTipoCliente(tipo);
        return clienteRepository.save(cliente); // Suponiendo que tienes un JPA Repository
    }
    
    /**
     * Busca un cliente por ID
     */
    public Optional<Cliente> buscarClientePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que 0");
        }
        
        return clienteRepository.findById(id);
    }
    
    /**
     * Lista todos los clientes
     */
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
    
    /**
     * Actualiza un cliente existente
     */
    public Cliente actualizarCliente(Integer id, String nuevoNombre, TipoCliente nuevoTipo) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        cliente.setNombreCliente(nuevoNombre.trim());
        cliente.setTipoCliente(nuevoTipo);
        
        return clienteRepository.save(cliente);
    }
    
    /**
     * Elimina un cliente
     */
    public void eliminarCliente(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        
        clienteRepository.deleteById(id);
    }
    
    /**
     * Busca clientes por tipo
     */
    public List<Cliente> buscarClientesPorTipo(TipoCliente tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de cliente no puede ser nulo");
        }
        
        return clienteRepository.findByTipoCliente(tipo);
    }
    
    /**
     * Busca clientes por nombre
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        return clienteRepository.findByNombreClienteContainingIgnoreCase(nombre);
    }
    /**
     * Obtiene estadísticas básicas
     */
    public String obtenerEstadisticas() {
        List<Cliente> todos = clienteRepository.findAll();
        
        long pensionados = todos.stream()
                .filter(c -> c.getTipoCliente() == TipoCliente.PENSION)
                .count();
        
        long empresas = todos.stream()
                .filter(c -> c.getTipoCliente() == TipoCliente.EMPRESA)
                .count();
        
        long particulares = todos.stream()
                .filter(c -> c.getTipoCliente() == TipoCliente.PARTICULAR)
                .count();
        
        return String.format("Total: %d | Pensionados: %d | Empresas: %d | Particulares: %d",
                todos.size(), pensionados, empresas, particulares);
    }
    public void guardarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

}