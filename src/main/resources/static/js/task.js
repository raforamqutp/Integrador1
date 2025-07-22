// C:/Users/LENOVO/Documents/VS/Integrador1_4_2_1/Integrador1/src/main/resources/static/js/task.js

let selectedWorker = null;
let selectedPriority = null;
let allTasks = []; // Esta variable se llenará con los datos del backend

// Al cargar la página, obtenemos la lista de empleados y tareas
document.addEventListener('DOMContentLoaded', () => {
    loadWorkers();
    fetchAndLoadTasks(); // ✅ Cambiamos a una función que carga desde el backend

    // Evento para el buscador de trabajadores
    const workerSearchInput = document.getElementById('workerSearchInput');
    let workerSearchDebounceTimer;
    workerSearchInput.addEventListener('input', (e) => {
        clearTimeout(workerSearchDebounceTimer);
        workerSearchDebounceTimer = setTimeout(() => {
            loadWorkers(e.target.value.trim());
        }, 300);
    });

    // Evento para el buscador de tareas
    const taskSearchInput = document.getElementById('taskSearchInput');
    let searchDebounceTimer;
    taskSearchInput.addEventListener('input', (e) => {
        clearTimeout(searchDebounceTimer);
        searchDebounceTimer = setTimeout(() => {
            loadTasks(0, e.target.value.trim());
        }, 300);
    });
});

// ✅ FUNCIÓN CORREGIDA: Ahora envía la tarea al backend
async function assignTask() {
    if (!selectedWorker) {
        showToast('Error de Validación', 'Debes seleccionar un trabajador antes de asignar una tarea.', 'danger');
        return;
    }

    const taskData = {
        worker: selectedWorker,
        title: document.getElementById('taskTitle').value.trim(),
        type: document.getElementById('taskType').value,
        dueTime: document.getElementById('dueTime').value,
        priority: selectedPriority || 'Media',
        description: document.getElementById('taskDescription').value.trim()
    };

    if (!taskData.title || !taskData.type || !taskData.dueTime) {
        showToast('Error de Validación', 'Los campos Título, Tipo y Fecha Límite son obligatorios.', 'danger');
        return;
    }

    try {
        // ✅ ESTA ES LA PARTE CLAVE QUE FALTABA: La llamada al backend
        const response = await fetch('/api/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData),
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || 'Error del servidor');
        }

        showToast('Éxito', result.message, 'success');
        fetchAndLoadTasks(); // Recargamos las tareas desde el archivo para mostrar la nueva
        clearForm();

    } catch (error) {
        console.error('Error al asignar tarea:', error);
        showToast('Error', 'No se pudo guardar la tarea. ' + error.message, 'danger');
    }
}

// ✅ NUEVA FUNCIÓN: Carga las tareas desde el backend
async function fetchAndLoadTasks() {
    try {
        const response = await fetch('/api/tasks/all'); // Usaremos un nuevo endpoint para obtener todas las tareas
        if (!response.ok) {
            throw new Error('No se pudieron cargar las tareas desde el servidor.');
        }
        allTasks = await response.json();
        loadTasks(); // Llama a la función que renderiza la tabla
    } catch (error) {
        console.error(error);
        const taskListBody = document.getElementById('taskListBody');
        taskListBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">${error.message}</td></tr>`;
    }
}


// --- El resto de funciones se mantienen igual, pero las incluyo para que el archivo esté completo ---

// El resto de funciones (loadTasks, renderTasks, etc.) se mantienen igual
function loadTasks(page = 0, searchTerm = '') {
    const taskListBody = document.getElementById('taskListBody');
    const searchTermLower = searchTerm.toLowerCase();

    const filteredTasks = allTasks.filter(task =>
        (task.title || '').toLowerCase().includes(searchTermLower) ||
        (task.worker.name || '').toLowerCase().includes(searchTermLower) ||
        (task.type || '').replace(/_/g, ' ').toLowerCase().includes(searchTermLower)
    );

    const itemsPerPage = 5;
    const totalPages = Math.ceil(filteredTasks.length / itemsPerPage);
    const start = page * itemsPerPage;
    const end = start + itemsPerPage;
    const paginatedTasks = filteredTasks.slice(start, end);

    renderTasks(paginatedTasks);
    renderPagination(totalPages, page, searchTerm);
}

function renderTasks(tasks) {
    const taskListBody = document.getElementById('taskListBody');
    taskListBody.innerHTML = '';

    if (tasks.length === 0) {
        taskListBody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No se encontraron tareas.</td></tr>';
        return;
    }

    tasks.forEach(task => {
        const priorityColors = { 'Alta': 'danger', 'Media': 'warning', 'Baja': 'success' };
        const statusColors = { 'Pendiente': 'secondary', 'En Progreso': 'primary', 'Completada': 'success' };
        const formattedDate = new Date(task.dueTime).toLocaleString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });

        const row = `
            <tr>
                <td><strong>${task.title}</strong><br><small class="text-muted">${task.description || ''}</small></td>
                <td>${task.worker.name}</td>
                <td><span class="badge bg-${priorityColors[task.priority]}">${task.priority}</span></td>
                <td>${task.type.replace(/_/g, ' ')}</td>
                <td>${formattedDate}</td>
                <td><span class="badge rounded-pill bg-${statusColors[task.status || 'Pendiente']}">${task.status || 'Pendiente'}</span></td>
                <td class="text-center">
                    <button class="btn btn-sm btn-outline-success" title="Marcar como completada"><i class="fas fa-check"></i></button>
                    <button class="btn btn-sm btn-outline-danger" title="Eliminar tarea"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `;
        taskListBody.innerHTML += row;
    });
}

function renderPagination(totalPages, currentPage, searchTerm) {
    const paginationContainer = document.getElementById('taskPagination');
    paginationContainer.innerHTML = '';

    if (totalPages <= 1) return;

    const createPageItem = (page, text, isDisabled = false, isActive = false) => {
        const li = document.createElement('li');
        li.className = `page-item ${isDisabled ? 'disabled' : ''} ${isActive ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); loadTasks(${page}, '${searchTerm.replace(/'/g, "\\'")}')">${text}</a>`;
        return li;
    };

    paginationContainer.appendChild(createPageItem(currentPage - 1, 'Anterior', currentPage === 0));

    for (let i = 0; i < totalPages; i++) {
        paginationContainer.appendChild(createPageItem(i, i + 1, false, i === currentPage));
    }

    paginationContainer.appendChild(createPageItem(currentPage + 1, 'Siguiente', currentPage === totalPages - 1));
}

async function loadWorkers(searchTerm = '') {
    const workerListContainer = document.getElementById('workerList');
    try {
        const response = await fetch('/empleados/api/listar');
        if (!response.ok) {
            throw new Error(`Error del servidor: ${response.status}`);
        }
        let workers = await response.json();
        workerListContainer.innerHTML = '';

        const searchTermLower = searchTerm.toLowerCase();
        if (searchTermLower) {
            workers = workers.filter(worker =>
                (worker.nombreUsuario || '').toLowerCase().includes(searchTermLower) ||
                (worker.rol || '').toLowerCase().includes(searchTermLower)
            );
        }

        if (workers.length === 0) {
            workerListContainer.innerHTML = '<p class="text-center text-muted">No se encontraron empleados.</p>';
            return;
        }

        workers.forEach(worker => {
            const nombreUsuario = worker.nombreUsuario || 'Sin Nombre';
            const rol = worker.rol || 'Sin Cargo';
            const initials = nombreUsuario.split(' ').map(n => n[0]).join('').toUpperCase();
            const statusClass = 'success';
            const statusText = 'Disponible';
            const turno = 'General';

            const card = `
                <a href="#" class="list-group-item list-group-item-action worker-card"
                   onclick="selectWorker(this, '${nombreUsuario.replace(/'/g, "\\'")}', '${rol}')">
                    <div class="d-flex align-items-center">
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 50px; height: 50px;">
                            ${initials}
                        </div>
                        <div>
                            <h5 class="mb-1">${nombreUsuario}</h5>
                            <span class="badge bg-light text-primary border border-primary">${rol}</span>
                            <div class="d-flex align-items-center mt-1">
                                <span class="badge bg-${statusClass} rounded-circle me-2" style="width: 10px; height: 10px;"></span>
                                <small class="text-muted">${statusText} - Turno ${turno}</small>
                            </div>
                        </div>
                    </div>
                </a>`;
            workerListContainer.innerHTML += card;
        });

    } catch (error) {
        console.error('Error al cargar empleados:', error);
        workerListContainer.innerHTML = `<div class="alert alert-danger">No se pudo cargar la lista de empleados. ${error.message}</div>`;
    }
}

function showToast(title, message, type = 'success') {
    const toastEl = document.getElementById('notificationToast');
    const toast = new bootstrap.Toast(toastEl);
    document.getElementById('toastTitle').textContent = title;
    document.getElementById('toastMessage').textContent = message;

    const toastHeader = toastEl.querySelector('.toast-header');
    toastHeader.className = 'toast-header text-white';
    toastHeader.classList.add(`bg-${type}`);

    toast.show();
}

function selectWorker(element, name, role) {
    document.querySelectorAll('.worker-card').forEach(card => card.classList.remove('active'));
    element.classList.add('active');
    selectedWorker = { name, role };

    document.getElementById('currentAssignment').classList.remove('d-none');
    document.getElementById('selectedWorkerInfo').textContent = `${name} - ${role}`;

    showToast('Trabajador seleccionado', `Has seleccionado a ${name}`, 'info');
}

function clearForm() {
    document.getElementById('taskForm').reset();
    document.getElementById('taskPriority').value = '';
    selectedPriority = null;

    document.querySelectorAll('.priority-badge').forEach(btn => {
        btn.className = 'btn priority-badge';
        if (btn.textContent === 'Baja') btn.classList.add('btn-outline-success');
        if (btn.textContent === 'Media') btn.classList.add('btn-outline-warning');
        if (btn.textContent === 'Alta') btn.classList.add('btn-outline-danger');
    });

    showToast('Formulario limpiado', 'Todos los campos han sido restablecidos.', 'secondary');
}

function selectPriority(element, priority) {
    selectedPriority = priority;
    document.getElementById('taskPriority').value = priority;

    document.querySelectorAll('.priority-badge').forEach(btn => {
        btn.className = 'btn priority-badge';
        if (btn.textContent === 'Baja') btn.classList.add('btn-outline-success');
        if (btn.textContent === 'Media') btn.classList.add('btn-outline-warning');
        if (btn.textContent === 'Alta') btn.classList.add('btn-outline-danger');
    });

    const color = priority === 'Alta' ? 'danger' : priority === 'Media' ? 'warning' : 'success';
    element.classList.remove(`btn-outline-${color}`);
    element.classList.add(`btn-${color}`);
}

function logout() {
    window.location.href = 'login.html';
}