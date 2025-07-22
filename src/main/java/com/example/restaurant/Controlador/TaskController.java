// C:/Users/LENOVO/Documents/VS/Integrador1_4_2_1/Integrador1/src/main/java/com/example/restaurant/Controlador/TaskController.java
package com.example.restaurant.Controlador;

import com.example.restaurant.dto.TaskRequest;
import com.example.restaurant.servicio.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Recibe una nueva tarea desde el frontend y la guarda.
     * @param taskRequest Un objeto TaskRequest con los datos de la nueva tarea.
     * @return Una respuesta JSON indicando éxito o error.
     */
    @PostMapping
    public ResponseEntity<?> assignTask(@RequestBody TaskRequest taskRequest) {
        try {
            // Validación básica en el controlador
            if (taskRequest.getWorker() == null || taskRequest.getTitle() == null || taskRequest.getDueTime() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Faltan datos esenciales (trabajador, título o fecha límite)."));
            }

            taskService.saveTask(taskRequest);

            return ResponseEntity.ok(Map.of("message", "Tarea asignada y guardada correctamente."));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Error al escribir la tarea en el archivo."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Ocurrió un error inesperado en el servidor."));
        }
    }

    /**
     * Devuelve todas las tareas almacenadas en el archivo JSON.
     * @return Una lista de tareas.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllTasks() {
        try {
            List<Map<String, Object>> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "No se pudieron leer las tareas."));
        }
    }
}