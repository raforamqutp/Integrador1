package com.example.restaurant.Controlador;

import com.example.restaurant.dto.PensionDTO;
import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.entidades.Pension;
import com.example.restaurant.repositorios.PensionRepository;
import com.example.restaurant.servicio.ClienteService;
import com.example.restaurant.servicio.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
     * Lista las pensiones con paginación y búsqueda.
     */
    @GetMapping("/api/listar")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PensionDTO>> listarPensiones(
            @RequestParam(value = "termino", required = false, defaultValue = "") String termino,
            Pageable pageable) {

        Page<Pension> pensionesEntidad;
        if (termino != null && !termino.isEmpty()) {
            pensionesEntidad = pensionRepository.findByNombreEmpresa(termino, pageable);
        } else {
            pensionesEntidad = pensionRepository.findAll(pageable);
        }

        Page<PensionDTO> pensionesDTO = pensionesEntidad.map(PensionDTO::new);
        return ResponseEntity.ok(pensionesDTO);
    }

    /**
     * Registra una nueva pensión con validaciones específicas y mensajes de error claros.
     */
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarPension(@RequestBody Map<String, Object> pensionData) {
        try {
            // 1. Extracción de datos
            Object empresaIdObj = pensionData.get("empresaId");
            Object clienteIdObj = pensionData.get("clienteId");
            Object montoObj = pensionData.get("montoMensual");
            String fechaInicioStr = (String) pensionData.get("fechaInicio");
            String fechaFinStr = (String) pensionData.get("fechaFin");

            // 2. Validaciones específicas para cada campo
            if (empresaIdObj == null) {
                return ResponseEntity.badRequest().body("Error de validación: Debe seleccionar una empresa.");
            }
            if (clienteIdObj == null) {
                return ResponseEntity.badRequest().body("Error de validación: Debe seleccionar un cliente.");
            }
            if (montoObj == null || montoObj.toString().isEmpty()) {
                return ResponseEntity.badRequest().body("Error de validación: El monto mensual es obligatorio.");
            }
            if (fechaInicioStr == null || fechaInicioStr.isEmpty()) {
                return ResponseEntity.badRequest().body("Error de validación: La fecha de inicio es obligatoria.");
            }
            if (fechaFinStr == null || fechaFinStr.isEmpty()) {
                return ResponseEntity.badRequest().body("Error de validación: La fecha de fin es obligatoria.");
            }

            // 3. Conversión de tipos segura
            Integer empresaId = Integer.parseInt(empresaIdObj.toString());
            Integer clienteId = Integer.parseInt(clienteIdObj.toString());
            BigDecimal montoMensual = new BigDecimal(montoObj.toString());
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);

            // 4. Validaciones de lógica de negocio
            if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)) {
                return ResponseEntity.badRequest().body("La fecha de fin debe ser posterior a la fecha de inicio.");
            }
            if (montoMensual.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El monto mensual debe ser mayor a cero.");
            }

            // 5. Búsqueda de entidades relacionadas
            Empresa empresa = empresaService.buscarPorId(empresaId)
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + empresaId));

            Cliente cliente = clienteService.buscarClientePorId(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

            if (!cliente.getTipoCliente().equals(com.example.restaurant.entidades.TipoCliente.PENSION)) {
                return ResponseEntity.badRequest().body("El cliente seleccionado no es de tipo PENSIONADO.");
            }

            // 6. Creación y guardado de la pensión
            Pension pension = new Pension(cliente, empresa, fechaInicio, fechaFin, montoMensual);
            pensionRepository.save(pension);

            return ResponseEntity.ok("Pensión registrada exitosamente.");

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("El formato del monto o de los IDs es inválido.");
        } catch (IllegalArgumentException e) {
            // Errores de negocio (ej. "Cliente no encontrado")
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("El formato de fecha es inválido. Use YYYY-MM-DD.");
        } catch (Exception e) {
            // Errores inesperados
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno al registrar la pensión: " + e.getMessage());
        }
    }

    /**
     * Elimina una pensión por su ID.
     */
    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarPension(@PathVariable("id") Integer id) {
        try {
            if (!pensionRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pensión no encontrada con ID: " + id);
            }
            pensionRepository.deleteById(id);
            return ResponseEntity.ok("Pensión eliminada exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la pensión: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de una pensión existente.
     */
    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarPension(@RequestBody Map<String, Object> payload) {
        try {
            Integer idPension = Integer.parseInt(payload.get("idPension").toString());
            BigDecimal montoMensual = new BigDecimal(payload.get("montoMensual").toString());
            LocalDate fechaInicio = LocalDate.parse(payload.get("fechaInicio").toString());
            LocalDate fechaFin = LocalDate.parse(payload.get("fechaFin").toString());

            Pension existente = pensionRepository.findById(idPension)
                    .orElseThrow(() -> new IllegalArgumentException("Pensión no encontrada con ID: " + idPension));

            if (fechaFin.isBefore(fechaInicio)) {
                return ResponseEntity.badRequest().body("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }
            if (montoMensual.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El monto mensual debe ser un valor positivo.");
            }

            existente.setMontoMensual(montoMensual);
            existente.setFechaInicio(fechaInicio);
            existente.setFechaFin(fechaFin);

            pensionRepository.save(existente);
            return ResponseEntity.ok("Pensión actualizada exitosamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar la pensión.");
        }
    }

    /**
     * Busca una pensión por ID para cargar sus datos en el modal de edición.
     */
    @GetMapping("/api/buscar/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> buscarPensionParaEditar(@PathVariable("id") Integer id) {
        return pensionRepository.findById(id)
                .map(pension -> ResponseEntity.ok(new PensionDTO(pension)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lista todas las pensiones asociadas a una empresa específica.
     */
    @GetMapping("/api/por-empresa/{empresaId}")
    @ResponseBody
    public ResponseEntity<List<PensionDTO>> getPensionesPorEmpresa(@PathVariable("empresaId") Integer empresaId) {
        List<PensionDTO> pensiones = pensionRepository.findPensionsByEmpresaId(empresaId);
        return ResponseEntity.ok(pensiones);
    }

    /**
     * Lista todas las pensiones asociadas a un cliente específico.
     */
    @GetMapping("/api/por-cliente/{clienteId}")
    @ResponseBody
    public ResponseEntity<List<PensionDTO>> getPensionesPorCliente(@PathVariable("clienteId") Integer clienteId) {
        List<PensionDTO> pensiones = pensionRepository.findPensionsByClienteId(clienteId);
        return ResponseEntity.ok(pensiones);
    }
}