package com.example.restaurant.servicio;

import com.example.restaurant.entidades.*;
import com.example.restaurant.modelo.Item;
import com.example.restaurant.modelo.PedidoSession;
import com.example.restaurant.repositorios.*;
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
    
    @Autowired
    private PedidoSession pedidoSession;

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

    /* OLD CODE:
    @Transactional
    public void guardarPedidoEnBD() {
        // Obtener usuario y cliente mock
        Usuario usuario = usuarioRepo.findById(ID_USUARIO_DEFAULT).orElseThrow();
        Cliente cliente = clienteRepo.findById(ID_CLIENTE_DEFAULT).orElseThrow();

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCliente(cliente);
        pedido.setTotal(0.0);         // previously `pedido.setTotal(BigDecimal.ZERO);`
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
            double newTotal = pedido.getTotal() + detalle.getSubtotal().doubleValue(); // new line added
            pedido.setTotal(newTotal); // previously `pedido.setTotal(pedido.getTotal().add(detalle.getSubtotal()));`
        }

        pedidoRepo.save(pedido);
        pedidoTemporal.clear();
    }
     * */
    @Transactional
    public void guardarPedidoEnBD(int usuarioId, int clienteId) {
        // Obtener usuario y cliente de la base de datos
    	Usuario usuario = usuarioRepo.findById(usuarioId).orElseThrow();
        Cliente cliente = clienteRepo.findById(clienteId).orElseThrow();

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCliente(cliente);
        pedido.setTotal(BigDecimal.ZERO.doubleValue());		// Set initial 'total' to BigDecimal.ZERO
        pedidoRepo.save(pedido);
        
        pedido.setTotal(0.0);

        BigDecimal total = BigDecimal.ZERO;
		// Guardar detalles
        for (Item item : pedidoSession.getItems()) {
        	DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            
            BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            detalle.setSubtotal(subtotal);
            total = total.add(subtotal);
            
            if (item.getCategoria().equals("COMIDA")) {
                Comida comida = comidaRepo.findByNombreComida(item.getNombre());
                detalle.setComida(comida);
                detalle.setTipoItem(comida.getTipoComida() == TipoComida.entrada ? TipoItem.ENTRADA : TipoItem.COMIDA);
            } else {
                Bebida bebida = bebidaRepo.findByNombreBebida(item.getNombre());
                detalle.setBebida(bebida);
                detalle.setTipoItem(TipoItem.BEBIDA);
            }
            detallePedidoRepo.save(detalle);
        	
        }
        double newTotal = total.doubleValue();  // Convert BigDecimal to double
        pedido.setTotal(total.doubleValue());       //intended original way ->   pedido.setTotal(total);
        pedidoRepo.save(pedido);
        pedidoSession.clear();
    }
    

    public List<Item> obtenerPedido() {
        return pedidoTemporal;
    }

    public void limpiarPedido() {
        pedidoTemporal.clear();
    }


}
