package com.example.restaurant.servicio;

import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.*;
import com.example.restaurant.modelo.Item;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private DetallePedidoRepository detallePedidoRepo;

    @Autowired
    private ComidaRepository comidaRepo;

    @Autowired
    private BebidaRepository bebidaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    // Mock temporal (usuario y cliente por defecto)
    private static final int ID_USUARIO_DEFAULT = 1;
    private static final int ID_CLIENTE_DEFAULT = 1;

    private final List<Item> pedidoTemporal = new ArrayList<>();

    @Transactional
    public void agregarItem(Item item) {
        // Buscar en BD si es Comida o Bebida
        if (item.getCategoria().equals("COMIDA")) {
            Comida comida = comidaRepo.findByNombreComida(item.getNombre());
            if (comida != null) {
                item.setPrecioUnitario(comida.getPrecio());
            }
        } else if (item.getCategoria().equals("BEBIDA")) {
            Bebida bebida = bebidaRepo.findByNombreBebida(item.getNombre());
            if (bebida != null) {
                item.setPrecioUnitario(bebida.getPrecio());
            }
        }

        pedidoTemporal.add(item);
    }

    @Transactional
    public void guardarPedidoEnBD() {
        // Obtener usuario y cliente mock
        Usuario usuario = usuarioRepo.findById(ID_USUARIO_DEFAULT).orElseThrow();
        Cliente cliente = clienteRepo.findById(ID_CLIENTE_DEFAULT).orElseThrow();

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCliente(cliente);
        pedido.setTotal(BigDecimal.ZERO);
        pedidoRepo.save(pedido);

        // Guardar detalles
        for (Item item : pedidoTemporal) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
            detalle.setTipoItem(TipoItem.valueOf(item.getCategoria()));

            if (item.getCategoria().equals("COMIDA")) {
                Comida comida = comidaRepo.findByNombreComida(item.getNombre());
                detalle.setComida(comida);
            } else {
                Bebida bebida = bebidaRepo.findByNombreBebida(item.getNombre());
                detalle.setBebida(bebida);
            }

            detallePedidoRepo.save(detalle);
            pedido.setTotal(pedido.getTotal().add(detalle.getSubtotal()));
        }

        pedidoRepo.save(pedido);
        pedidoTemporal.clear();
    }

    public List<Item> obtenerPedido() {
        return pedidoTemporal;
    }

    public void limpiarPedido() {
        pedidoTemporal.clear();
    }
}
