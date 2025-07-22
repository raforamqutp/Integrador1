package com.example.restaurant.servicio;

import com.example.restaurant.dto.TaskRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
// import java.nio.file.Paths; // Ya no es necesario
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    // ✅ El archivo ahora se buscará en el directorio raíz del proyecto (donde se ejecuta la app)
    private static final String TASKS_FILE_PATH = "tareas_asignadas.json";
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final Object lock = new Object(); // Objeto para sincronización y evitar problemas de concurrencia

    /**
     * Obtiene el archivo de tareas.
     * Ahora busca el archivo en el directorio de trabajo de la aplicación.
     * @return El objeto File apuntando a tareas_asignadas.json
     */
    private File getTasksFile() {
        // Este enfoque es más robusto y funciona tanto en desarrollo (desde el IDE)
        // como en producción (al ejecutar el JAR).
        return new File(TASKS_FILE_PATH);
    }

    /**
     * Guarda una nueva tarea en el archivo JSON.
     * Lee el archivo, añade la nueva tarea al principio de la lista y reescribe el archivo.
     * @param taskRequest El objeto con los datos de la tarea enviados desde el frontend.
     * @throws IOException si ocurre un error de lectura/escritura.
     */
    public void saveTask(TaskRequest taskRequest) throws IOException {
        // 'synchronized' para evitar que múltiples hilos escriban en el archivo al mismo tiempo
        synchronized (lock) {
            File tasksFile = getTasksFile();
            List<Map<String, Object>> tasks;

            // Si el archivo existe y no está vacío, lee las tareas existentes
            if (tasksFile.exists() && tasksFile.length() > 0) {
                tasks = objectMapper.readValue(tasksFile, new TypeReference<List<Map<String, Object>>>() {});
            } else {
                // Si no, crea una nueva lista
                tasks = new ArrayList<>();
            }

            // Convertimos el DTO a un Map para añadir los campos que faltan y tener control
            Map<String, Object> newTask = new LinkedHashMap<>();
            newTask.put("id", System.currentTimeMillis()); // ID único basado en el tiempo
            newTask.put("worker", taskRequest.getWorker());
            newTask.put("title", taskRequest.getTitle());
            newTask.put("description", taskRequest.getDescription());
            newTask.put("type", taskRequest.getType());
            newTask.put("dueTime", taskRequest.getDueTime());
            newTask.put("priority", taskRequest.getPriority());
            newTask.put("status", "Pendiente"); // Estado por defecto al crear

            tasks.add(0, newTask); // Añadimos la nueva tarea al principio de la lista para que aparezca primero

            // Escribimos la lista completa de vuelta al archivo
            objectMapper.writeValue(tasksFile, tasks);
        }
    }

    /**
     * Lee y devuelve todas las tareas del archivo JSON.
     * @return Una lista de tareas.
     * @throws IOException si ocurre un error de lectura.
     */
    public List<Map<String, Object>> getAllTasks() throws IOException {
        synchronized (lock) {
            File tasksFile = getTasksFile();
            if (tasksFile.exists() && tasksFile.length() > 0) {
                return objectMapper.readValue(tasksFile, new TypeReference<List<Map<String, Object>>>() {});
            }
            // Si el archivo no existe o está vacío, devuelve una lista vacía para evitar errores
            return new ArrayList<>();
        }
    }
}