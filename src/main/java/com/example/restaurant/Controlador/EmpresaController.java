package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.servicio.EmpresaService;
import com.example.restaurant.repositorios.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {
    
    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * ✅ GUARDAR EMPRESA - CORREGIDO
     */
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarEmpresa(@RequestBody Empresa empresa) {
        try {
            System.out.println("🔄 Guardando empresa...");
            System.out.println("📊 Datos recibidos: " + empresa.toString());
            
            String ruc = empresa.getRuc();
            String razonSocial = empresa.getRazonSocial();
            
            if (ruc == null || !ruc.matches("\\d{11}")) {
                return ResponseEntity.badRequest().body("RUC debe tener exactamente 11 dígitos");
            }
            
            if (razonSocial == null || razonSocial.trim().length() < 3) {
                return ResponseEntity.badRequest().body("Razón social debe tener al menos 3 caracteres");
            }
            
            // Verificar si ya existe el RUC
            if (empresaRepository.existsByRuc(ruc)) {
                return ResponseEntity.badRequest().body("Ya existe una empresa con el RUC: " + ruc);
            }
            
            // Limpiar datos
            empresa.setRuc(ruc.trim());
            empresa.setRazonSocial(razonSocial.trim());
            
            Empresa empresaGuardada = empresaRepository.save(empresa);
            
            System.out.println("✅ Empresa guardada exitosamente con ID: " + empresaGuardada.getIdEmpresa());
            
            return ResponseEntity.ok("Empresa registrada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar empresa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
    
    /**
     * ✅ LISTAR EMPRESAS ACTIVAS - CORREGIDO
     */
    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarEmpresas() {
        try {
            System.out.println("🔄 Cargando empresas activas...");
            
            List<Empresa> empresas = empresaRepository.findByActivoTrue();
            
            // Evitar lazy loading de pensiones
            for (Empresa empresa : empresas) {
                // Solo acceder a propiedades básicas, no a las colecciones
                empresa.getRazonSocial();
                empresa.getRuc();
            }
            
            System.out.println("📊 Empresas activas encontradas: " + empresas.size());
            
            return ResponseEntity.ok(empresas);
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar empresas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar empresas: " + e.getMessage());
        }
    }
    
    /**
     * Buscar empresa por ID
     */
    @GetMapping("/api/buscar/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarEmpresaPorId(@PathVariable Integer id) {
        try {
            Optional<Empresa> empresa = empresaService.buscarPorId(id);
            
            if (empresa.isPresent()) {
                return ResponseEntity.ok(empresa.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar la empresa");
        }
    }
    
    /**
     * Buscar empresas por término (RUC o razón social)
     */
    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Empresa>> buscarEmpresas(@RequestParam(required = false) String termino) {
        try {
            List<Empresa> empresas = empresaService.buscarEmpresas(termino);
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Actualizar empresa
     */
    @PostMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarEmpresa(@PathVariable Integer id,
                                             @RequestParam String razonSocial) {
        try {
            Empresa empresaActualizada = empresaService.actualizarEmpresa(id, razonSocial);
            return ResponseEntity.ok("Empresa actualizada exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la empresa: " + e.getMessage());
        }
    }
    
    /**
     * ✅ ELIMINAR EMPRESA - CORREGIDO
     */
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarEmpresa(@PathVariable Integer id) {
        try {
            System.out.println("🔄 Eliminando empresa con ID: " + id);
            
            Optional<Empresa> empresaOpt = empresaRepository.findById(id);
            if (!empresaOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Empresa empresa = empresaOpt.get();
            
            // Verificar si tiene pensiones asociadas (sin cargar la colección completa)
            Long cantidadPensiones = empresaRepository.contarPensionesPorEmpresa(id);
            if (cantidadPensiones > 0) {
                return ResponseEntity.badRequest()
                    .body("No se puede eliminar la empresa porque tiene " + cantidadPensiones + " pensión(es) asociada(s)");
            }
            
            empresaRepository.delete(empresa);
            
            System.out.println("✅ Empresa eliminada exitosamente");
            
            return ResponseEntity.ok("Empresa eliminada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar empresa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la empresa: " + e.getMessage());
        }
    }
    
    /**
     * Obtener empresas con pensiones activas
     */
    @GetMapping("/api/con-pensiones")
    @ResponseBody
    public ResponseEntity<List<Empresa>> obtenerEmpresasConPensiones() {
        try {
            List<Empresa> empresas = empresaService.obtenerEmpresasConPensionesActivas();
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}