package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.entidades.Pension;
import com.example.restaurant.servicio.ClienteService;
import com.example.restaurant.servicio.EmpresaService;
import com.example.restaurant.repositorios.PensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pensiones")
@CrossOrigin(origins = "*")
public class PensionController {
    
    @Autowired
    private PensionRepository pensionRepository;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmpresaService empresaService;
    
    /**
     * ✅ LISTAR PENSIONES - VERSIÓN SIMPLIFICADA Y ROBUSTA
     */
    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarPensiones() {
        try {
            System.out.println("🔄 [PENSION] Iniciando carga de pensiones...");
            
            // Usar método con EAGER loading
            List<Pension> pensiones = pensionRepository.findAllWithDetails();
            
            System.out.println("📊 [PENSION] Pensiones encontradas: " + pensiones.size());
            
            // Log de cada pensión para debugging
            for (int i = 0; i < Math.min(pensiones.size(), 3); i++) {
                Pension p = pensiones.get(i);
                System.out.println("📝 [PENSION] Pensión " + (i+1) + ": ID=" + p.getIdPension() + 
                                 ", Cliente=" + (p.getCliente() != null ? p.getCliente().getNombreCompleto() : "null") +
                                 ", Empresa=" + (p.getEmpresa() != null ? p.getEmpresa().getRazonSocial() : "null"));
            }
            
            return ResponseEntity.ok(pensiones);
            
        } catch (Exception e) {
            System.err.println("❌ [PENSION] Error al cargar pensiones: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar pensiones: " + e.getMessage());
        }
    }
    
    /**
     * ✅ REGISTRAR PENSIÓN - CON LOGS MEJORADOS
     */
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarPension(@RequestBody Map<String, Object> pensionData) {
        try {
            System.out.println("🔄 [PENSION] Registrando nueva pensión...");
            System.out.println("📊 [PENSION] Datos recibidos: " + pensionData);
            
            // Extraer y validar datos
            Integer empresaId = (Integer) pensionData.get("empresaId");
            Integer clienteId = (Integer) pensionData.get("clienteId");
            
            BigDecimal montoMensual;
            Object montoObj = pensionData.get("montoMensual");
            if (montoObj instanceof Number) {
                montoMensual = BigDecimal.valueOf(((Number) montoObj).doubleValue());
            } else {
                montoMensual = new BigDecimal(montoObj.toString());
            }
            
            String fechaInicioStr = (String) pensionData.get("fechaInicio");
            String fechaFinStr = (String) pensionData.get("fechaFin");
            
            // Validaciones
            if (empresaId == null || clienteId == null || montoMensual == null || 
                fechaInicioStr == null || fechaFinStr == null) {
                return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
            }
            
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);
            
            if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)) {
                return ResponseEntity.badRequest().body("La fecha de fin debe ser posterior a la fecha de inicio");
            }
            
            if (montoMensual.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El monto mensual debe ser mayor a 0");
            }
            
            // Buscar entidades
            Optional<Empresa> empresaOpt = empresaService.buscarPorId(empresaId);
            Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(clienteId);
            
            if (!empresaOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Empresa no encontrada con ID: " + empresaId);
            }
            
            if (!clienteOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + clienteId);
            }
            
            Cliente cliente = clienteOpt.get();
            if (!cliente.getTipoCliente().equals(com.example.restaurant.entidades.TipoCliente.PENSION)) {
                return ResponseEntity.badRequest().body("El cliente debe ser de tipo PENSIONADO");
            }
            
            // Crear y guardar pensión
            Pension pension = new Pension();
            pension.setEmpresa(empresaOpt.get());
            pension.setCliente(cliente);
            pension.setMontoMensual(montoMensual);
            pension.setFechaInicio(fechaInicio);
            pension.setFechaFin(fechaFin);
            
            Pension pensionGuardada = pensionRepository.save(pension);
            
            System.out.println("✅ [PENSION] Pensión registrada exitosamente con ID: " + pensionGuardada.getIdPension());
            
            return ResponseEntity.ok("Pensión registrada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ [PENSION] Error al registrar pensión: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
    
    /**
     * ✅ ELIMINAR PENSIÓN - SIMPLIFICADO
     */
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarPension(@PathVariable Integer id) {
        try {
            System.out.println("🔄 [PENSION] Eliminando pensión con ID: " + id);
            
            if (!pensionRepository.existsById(id)) {
                System.out.println("❌ [PENSION] Pensión no encontrada con ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            pensionRepository.deleteById(id);
            
            System.out.println("✅ [PENSION] Pensión eliminada exitosamente");
            
            return ResponseEntity.ok("Pensión eliminada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ [PENSION] Error al eliminar pensión: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la pensión: " + e.getMessage());
        }
    }
    
    // Los demás métodos se mantienen igual...
    @GetMapping("/api/listar/activas")
    @ResponseBody
    public ResponseEntity<List<Pension>> listarPensionesActivas() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Pension> pensiones = pensionRepository.findPensionesActivas(hoy);
            return ResponseEntity.ok(pensiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/api/empresa/{empresaId}")
    @ResponseBody
    public ResponseEntity<List<Pension>> listarPensionesPorEmpresa(@PathVariable Integer empresaId) {
        try {
            List<Pension> pensiones = pensionRepository.findByEmpresa_IdEmpresa(empresaId);
            return ResponseEntity.ok(pensiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/api/cliente/{clienteId}")
    @ResponseBody
    public ResponseEntity<List<Pension> > listarPensionesPorCliente(@PathVariable Integer clienteId) {
        try {
            List<Pension> pensiones = pensionRepository.findByCliente_IdCliente(clienteId);
            return ResponseEntity.ok(pensiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}