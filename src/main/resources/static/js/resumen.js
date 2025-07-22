let pedidoCompleto = {};
let nuevoClienteModal;

/**
 * Muestra los datos del usuario logueado o un mensaje de error.
 */
function cargarUsuarioLogueado() {
    const usuarioId = sessionStorage.getItem('usuarioId');
    const nombreUsuario = sessionStorage.getItem('nombreUsuario');
    const btnGenerar = document.getElementById('btn-generar-pedido');
    const usuarioInfoDiv = document.getElementById('usuarioInfo');
    const usuarioErrorAlert = document.getElementById('usuarioErrorAlert');

    if (usuarioId && nombreUsuario) {
        usuarioInfoDiv.textContent = `Empleado: ${nombreUsuario} (ID: ${usuarioId})`;
        btnGenerar.disabled = false;
        usuarioErrorAlert.classList.add('d-none');
        usuarioInfoDiv.classList.remove('d-none');
    } else {
        usuarioInfoDiv.classList.add('d-none');
        usuarioErrorAlert.classList.remove('d-none');
        btnGenerar.disabled = true;
    }
}

/**
 * Muestra el resumen del pedido actual en las tablas.
 */
function mostrarResumen() {
    const data = localStorage.getItem("pedidoSeleccion");
    if (!data || data === '{}') {
        document.getElementById("total-container").innerHTML = "<p>El carrito está vacío.</p>";
        document.getElementById('btn-generar-pedido').disabled = true;
        document.getElementById('btn-guardar-borrador').disabled = true;
        return;
    }
    try {
        pedidoCompleto = JSON.parse(data);
        const tbodyComidas = document.getElementById("detallesComidas");
        const tbodyBebidas = document.getElementById("detallesBebidas");
        tbodyComidas.innerHTML = "";
        tbodyBebidas.innerHTML = "";
        let totalGeneral = 0;

        // ✅ CORRECCIÓN: La función ahora usa el ID para el botón de eliminar
        const renderizarFila = (tbody, item, categoria, id) => {
            if (item.cantidad > 0) {
                const subtotal = item.cantidad * item.precio;
                totalGeneral += subtotal;
                const fila = document.createElement('tr');
                // Se usa item.nombre para mostrar el nombre del producto
                fila.innerHTML = `
                    <td>${item.nombre}</td>
                    <td>${item.cantidad}</td>
                    <td>S/. ${item.precio.toFixed(2)}</td>
                    <td>S/. ${subtotal.toFixed(2)}</td>
                    <td class="text-center">
                        <button class="btn btn-danger btn-sm" onclick="eliminarItem('${categoria}', '${id}')">Quitar</button>
                    </td>
                `;
                tbody.appendChild(fila);
            }
        };

        // ✅ CORRECCIÓN: Se itera sobre los IDs, no los nombres.
        ['entradas', 'platos'].forEach(cat => {
            for (const id in (pedidoCompleto[cat] || {})) {
                renderizarFila(tbodyComidas, pedidoCompleto[cat][id], cat, id);
            }
        });
        for (const id in (pedidoCompleto.bebidas || {})) {
            renderizarFila(tbodyBebidas, pedidoCompleto.bebidas[id], 'bebidas', id);
        }
        document.getElementById("total-pedido").textContent = `S/. ${totalGeneral.toFixed(2)}`;
    } catch (e) {
        console.error("Error al procesar los datos del pedido:", e);
    }
}

/**
 * Elimina un item del pedido actual usando su ID.
 */
function eliminarItem(categoria, id) {
    // ✅ CORRECCIÓN: La función ahora usa el ID para encontrar y eliminar el item.
    if (pedidoCompleto[categoria] && pedidoCompleto[categoria][id]) {
        delete pedidoCompleto[categoria][id];
        localStorage.setItem("pedidoSeleccion", JSON.stringify(pedidoCompleto));
        mostrarResumen(); // Vuelve a renderizar la tabla para reflejar el cambio
    }
}

/**
 * Finaliza y guarda el pedido en el backend.
 */
async function generarPedido() {
    document.getElementById('pedidoErrorContainer').classList.add('d-none');
    const clienteId = document.getElementById('selectedClienteId').value;
    const usuarioId = sessionStorage.getItem('usuarioId');
    if (!usuarioId) { alert("Error de sesión: No se ha detectado un usuario."); return; }
    if (!clienteId) { alert("Por favor, seleccione un cliente para el pedido."); return; }

    const detallesComida = [];
    const detallesBebida = [];

    // ✅✅✅ CORRECCIÓN PRINCIPAL: Se itera sobre los IDs y se usa el 'tipo' que ahora viene en el objeto 'item'
    ['entradas', 'platos'].forEach(categoria => {
        for (const id in (pedidoCompleto[categoria] || {})) {
            const item = pedidoCompleto[categoria][id];
            // La condición ahora funciona porque item.tipo existe y es 'comida'
            if (item.cantidad > 0 && item.tipo === 'comida') {
                detallesComida.push({ comidaId: item.id, cantidad: item.cantidad });
            }
        }
    });
    for (const id in (pedidoCompleto.bebidas || {})) {
        const item = pedidoCompleto.bebidas[id];
        // La condición ahora funciona porque item.tipo existe y es 'bebida'
        if (item.cantidad > 0 && item.tipo === 'bebida') {
            detallesBebida.push({ bebidaId: item.id, cantidad: item.cantidad });
        }
    }

    if (detallesComida.length === 0 && detallesBebida.length === 0) {
        alert("El carrito está vacío. No se puede generar el pedido.");
        return;
    }

    const pedidoParaGuardar = { clienteId: parseInt(clienteId), usuarioId: parseInt(usuarioId), detallesComida, detallesBebida };
    const btnGenerar = document.getElementById('btn-generar-pedido');
    btnGenerar.disabled = true;
    btnGenerar.textContent = 'Guardando...';

    try {
        const response = await fetch('/api/pedidos', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(pedidoParaGuardar) });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: 'No se pudo obtener detalle del error.' }));
            throw new Error(errorData.message || `Error del servidor: ${response.status}`);
        }
        const result = await response.json();
        sessionStorage.setItem('ultimoPedidoResumen', JSON.stringify(pedidoCompleto));
        sessionStorage.setItem('ultimoPedidoInfo', JSON.stringify(result));

        localStorage.removeItem("pedidoSeleccion");
        localStorage.removeItem("clienteSeleccionado");

        window.location.href = `ascii.html`;
    } catch (error) {
        const errorContainer = document.getElementById('pedidoErrorContainer');
        document.getElementById('pedidoErrorMessage').textContent = 'No se pudo guardar el pedido.';
        document.getElementById('pedidoErrorDetails').textContent = error.message;
        errorContainer.classList.remove('d-none');
        btnGenerar.disabled = false;
        btnGenerar.textContent = 'Finalizar y Guardar Pedido';
    }
}


// --- El resto de funciones no necesitan cambios y se mantienen igual ---

/**
 * Formatea el tipo de cliente para mostrarlo.
 */
function formatearTipoCliente(tipo) {
    if (!tipo) return 'N/A';
    const tipoLower = tipo.toLowerCase();
    return tipoLower.charAt(0).toUpperCase() + tipoLower.slice(1);
}

/**
 * Inicializa la funcionalidad de búsqueda de clientes.
 */
function initClienteSearch() {
    const searchInput = document.getElementById('clienteSearchInput');
    const resultsContainer = document.getElementById('clienteSearchResults');
    const btnNuevoCliente = document.getElementById('btnNuevoCliente');
    const btnGuardarNuevoCliente = document.getElementById('btnGuardarNuevoCliente');
    nuevoClienteModal = new bootstrap.Modal(document.getElementById('nuevoClienteModal'));
    let debounceTimer;
    searchInput.addEventListener('input', (e) => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(async () => {
            const termino = e.target.value.trim();
            if (termino.length < 2) {
                resultsContainer.innerHTML = '';
                resultsContainer.classList.add('d-none');
                return;
            }
            try {
                const response = await fetch(`/clientes/api/buscar/todos?termino=${encodeURIComponent(termino)}`);
                if (!response.ok) throw new Error('Error al buscar clientes');
                const pageData = await response.json();
                const clientes = pageData.content;
                resultsContainer.innerHTML = '';
                if (clientes.length === 0) {
                    resultsContainer.innerHTML = '<a href="#" class="list-group-item list-group-item-action disabled">No se encontraron clientes</a>';
                } else {
                    clientes.forEach(cliente => {
                        const item = document.createElement('a');
                        item.href = '#';
                        item.className = 'list-group-item list-group-item-action';
                        const nombreCompleto = cliente.nombreCliente || `${cliente.nombres || ''} ${cliente.apellidos || ''}`.trim();
                        item.textContent = `${nombreCompleto} (${formatearTipoCliente(cliente.tipoCliente)})`;
                        item.addEventListener('click', (evt) => {
                            evt.preventDefault();
                            selectCliente(cliente.idCliente, nombreCompleto);
                        });
                        resultsContainer.appendChild(item);
                    });
                }
                resultsContainer.classList.remove('d-none');
            } catch (error) {
                console.error('Error en búsqueda de cliente:', error);
            }
        }, 300);
    });
    document.addEventListener('click', (e) => {
        if (!searchInput.contains(e.target)) {
            resultsContainer.classList.add('d-none');
        }
    });
    btnNuevoCliente.addEventListener('click', () => {
        document.getElementById('modalClienteErrorAlert').classList.add('d-none');
        document.getElementById('nuevoClienteNombre').value = '';
        nuevoClienteModal.show();
    });
    btnGuardarNuevoCliente.addEventListener('click', guardarNuevoCliente);
}

/**
 * Selecciona un cliente, actualiza la UI y guarda la selección en localStorage.
 */
function selectCliente(id, nombre) {
    document.getElementById('clienteSearchInput').value = nombre;
    document.getElementById('selectedClienteId').value = id;
    document.getElementById('clienteSearchResults').classList.add('d-none');
    document.getElementById('clienteSearchResults').innerHTML = '';
    const clienteSeleccionado = { id, nombre };
    localStorage.setItem("clienteSeleccionado", JSON.stringify(clienteSeleccionado));
}

/**
 * Guarda un nuevo cliente particular.
 */
async function guardarNuevoCliente() {
    const nombreInput = document.getElementById('nuevoClienteNombre');
    const nombre = nombreInput.value.trim();
    const errorAlert = document.getElementById('modalClienteErrorAlert');
    if (!nombre) {
        errorAlert.textContent = 'El nombre del cliente no puede estar vacío.';
        errorAlert.classList.remove('d-none');
        return;
    }
    const clienteData = { tipoCliente: 'PARTICULAR', nombreCliente: nombre };
    const btnGuardar = document.getElementById('btnGuardarNuevoCliente');
    btnGuardar.disabled = true;
    btnGuardar.textContent = 'Guardando...';
    try {
        const saveResponse = await fetch('/clientes/guardar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(clienteData)
        });
        if (!saveResponse.ok) {
            const errorText = await saveResponse.text();
            throw new Error(errorText || 'Error del servidor al guardar');
        }
        const searchResponse = await fetch(`/clientes/api/buscar/todos?termino=${encodeURIComponent(nombre)}&size=1`);
        if (!searchResponse.ok) throw new Error('Cliente guardado, pero no se pudo encontrar para seleccionarlo.');
        const pageData = await searchResponse.json();
        if (!pageData.content || pageData.content.length === 0) throw new Error('Cliente guardado, pero no se encontró en la búsqueda posterior.');
        const clienteEncontrado = pageData.content[0];
        const nombreCompleto = clienteEncontrado.nombreCliente || `${clienteEncontrado.nombres || ''} ${clienteEncontrado.apellidos || ''}`.trim();
        nuevoClienteModal.hide();
        selectCliente(clienteEncontrado.idCliente, nombreCompleto);
    } catch (error) {
        console.error('Error al guardar y seleccionar nuevo cliente:', error);
        errorAlert.textContent = `Error: ${error.message}`;
        errorAlert.classList.remove('d-none');
    } finally {
        btnGuardar.disabled = false;
        btnGuardar.textContent = 'Guardar Cliente';
    }
}

/**
 * Guarda el pedido actual y el cliente seleccionado como un borrador.
 */
function guardarBorrador() {
    const pedidoActualStr = localStorage.getItem("pedidoSeleccion");
    if (!pedidoActualStr) {
        alert("No hay nada en el pedido actual para guardar como borrador.");
        return;
    }
    const clienteId = document.getElementById('selectedClienteId').value;
    const clienteNombre = document.getElementById('clienteSearchInput').value;
    const clienteSeleccionado = (clienteId && clienteNombre) ? { id: clienteId, nombre: clienteNombre } : null;
    try {
        const pedidoActual = JSON.parse(pedidoActualStr);
        const borradores = JSON.parse(localStorage.getItem("pedidosBorradores") || "[]");
        const nuevoBorrador = {
            id: Date.now(),
            fecha: new Date().toLocaleString('es-ES'),
            pedido: pedidoActual,
            cliente: clienteSeleccionado
        };
        borradores.push(nuevoBorrador);
        localStorage.setItem("pedidosBorradores", JSON.stringify(borradores));
        localStorage.removeItem("pedidoSeleccion");
        localStorage.removeItem("clienteSeleccionado");
        alert("Borrador guardado con éxito. Serás redirigido al panel principal.");
        window.location.href = 'main.html';
    } catch (e) {
        console.error("Error al guardar el borrador:", e);
        alert("Hubo un error al intentar guardar el borrador.");
    }
}

// --- Event Listeners y Carga Inicial ---
document.addEventListener('DOMContentLoaded', () => {
    cargarUsuarioLogueado();
    initClienteSearch();
    const clienteGuardadoStr = localStorage.getItem("clienteSeleccionado");
    if (clienteGuardadoStr) {
        try {
            const cliente = JSON.parse(clienteGuardadoStr);
            if (cliente && cliente.id && cliente.nombre) {
                selectCliente(cliente.id, cliente.nombre);
            }
        } catch (e) {
            console.error("Error al cargar cliente del borrador:", e);
            localStorage.removeItem("clienteSeleccionado");
        }
    }
    mostrarResumen();
    document.getElementById('btn-generar-pedido').addEventListener('click', generarPedido);
    document.getElementById('btn-guardar-borrador').addEventListener('click', guardarBorrador);
});