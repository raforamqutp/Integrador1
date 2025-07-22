package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.servicio.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/empresas") // La ruta base que espera tu frontend
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    /**
     * Endpoint para listar empresas con paginación y búsqueda.
     * Responde a: /empresas/api/listar
     */
    @GetMapping("/api/listar")
    public ResponseEntity<Page<Empresa>> listarEmpresas(
            @RequestParam(value = "termino", required = false, defaultValue = "") String termino,
            Pageable pageable) {
        // Reutilizamos el método de búsqueda del repositorio que ya tienes
        Page<Empresa> empresas = empresaService.buscarEmpresasPaginado(termino, pageable);
        return ResponseEntity.ok(empresas);
    }

    /**
     * Endpoint para guardar una nueva empresa.
     * Responde a: /empresas/guardar
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarEmpresa(@RequestBody Map<String, String> payload) {
        try {
            String ruc = payload.get("ruc");
            String razonSocial = payload.get("razonSocial");
            Empresa nuevaEmpresa = empresaService.registrarEmpresa(ruc, razonSocial);
            return ResponseEntity.ok(nuevaEmpresa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error inesperado al guardar la empresa.");
        }
    }

    /**
     * Endpoint para buscar una empresa por su ID.
     * Responde a: /empresas/api/buscar/{id}
     */
    @GetMapping("/api/buscar/{id}")
    public ResponseEntity<Empresa> buscarEmpresaPorId(@PathVariable Integer id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para actualizar una empresa existente.
     * Responde a: /empresas/actualizar
     */
    @PostMapping("/actualizar")
    public ResponseEntity<?> actualizarEmpresa(@RequestBody Map<String, Object> payload) {
        try {
            Integer id = (Integer) payload.get("idEmpresa");
            String razonSocial = (String) payload.get("razonSocial");
            Empresa empresaActualizada = empresaService.actualizarEmpresa(id, razonSocial);
            return ResponseEntity.ok(empresaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error inesperado al actualizar la empresa.");
        }
    }

    /**
     * Endpoint para eliminar una empresa.
     * Responde a: /empresas/eliminar/{id}
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarEmpresa(@PathVariable Integer id) {
        try {
            empresaService.eliminarEmpresa(id);
            return ResponseEntity.ok("Empresa eliminada correctamente.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Captura errores de negocio (ej. no se puede eliminar si tiene pensiones)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error inesperado al eliminar la empresa.");
        }
    }
}