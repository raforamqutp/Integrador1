
let clientesData = [];
let registrandoPension = false;
let timeoutBusqueda = null;

document.addEventListener('DOMContentLoaded', () => {
    cargarClientes();
    document.getElementById('btnGuardarCliente').addEventListener('click', guardarCambiosCliente);
    document.getElementById('btnGuardarEmpresa').addEventListener('click', guardarCambiosEmpresa);
    document.getElementById('btnGuardarPension').addEventListener('click', guardarCambiosPension);
});

function switchTab(tabName, event) {
    document.querySelectorAll('.tab-content, .tab').forEach(el => el.classList.remove('active'));
    document.getElementById(`tab-${tabName}`).classList.add('active');
    event.currentTarget.classList.add('active');
    if (tabName === 'pensiones') {
        cargarEmpresas();
        cargarPensiones();
        cargarClientesPensionados();
        cargarEmpresasParaDropdown();
    }
}

const API_CONFIG = {
    clientes: { endpoint: '/clientes/api/buscar/todos', listContainer: 'clientesLista', paginationContainer: 'paginacionClientes', renderFunction: mostrarClientes, loadFunction: 'cargarClientes' },
    empresas: { endpoint: '/empresas/api/listar', listContainer: 'empresasLista', paginationContainer: 'paginacionEmpresas', renderFunction: mostrarEmpresas, loadFunction: 'cargarEmpresas' },
    pensiones: { endpoint: '/pensiones/api/listar', listContainer: 'pensionesList', paginationContainer: 'paginacionPensiones', renderFunction: mostrarPensiones, loadFunction: 'cargarPensiones' }
};
function buscarDatos(tipo) {
    clearTimeout(timeoutBusqueda);
    timeoutBusqueda = setTimeout(() => {
        const termino = document.getElementById(`buscador${capitalize(tipo)}`).value;
        window[`cargar${capitalize(tipo)}`](0, API_CONFIG[tipo].size, termino);
    }, 300);
}


async function loadPaginatedData(type, page = 0, size = 5, termino = '') {
            const config = API_CONFIG[type];
            const container = document.getElementById(config.listContainer);
            container.innerHTML = `<div class="loading">Cargando...</div>`;
            try {
                const url = `${config.endpoint}?page=${page}&size=${size}&termino=${encodeURIComponent(termino)}`;
                const response = await fetch(url);
                if (!response.ok) throw new Error(`Error al cargar ${type}`);
                const pageData = await response.json();
                if (type === 'clientes') clientesData = pageData.content;
                config.renderFunction(pageData.content);
                renderizarPaginacion(config.paginationContainer, pageData, config.loadFunction, termino);
            } catch (error) {
                container.innerHTML = `<p class="text-center text-danger p-3">${error.message}</p>`;
            }
        }
function capitalize(s) {
                    return s.charAt(0).toUpperCase() + s.slice(1);
}
function cargarClientes(page = 0, size = 5, termino = '') { loadPaginatedData('clientes', page, size, termino); }
function cargarEmpresas(page = 0, size = 3, termino = '') { loadPaginatedData('empresas', page, size, termino); }
function cargarPensiones(page = 0, size = 4, termino = '') { loadPaginatedData('pensiones', page, size, termino); }

function mostrarClientes(clientes) {
    const container = document.getElementById('clientesLista');
    if (!clientes || clientes.length === 0) { container.innerHTML = '<p class="text-center p-3">No se encontraron clientes.</p>'; return; }
    container.innerHTML = clientes.map(c => {
        const nombre = c.tipoCliente === 'PENSION' ? `${c.nombres || ''} ${c.apellidos || ''}`.trim() : c.nombreCliente;
        const dniInfo = c.tipoCliente === 'PENSION' ? `<p><strong>DNI:</strong> ${c.dni || 'N/A'}</p>` : '';
        const botonVer = c.tipoCliente === 'PENSION' ? `<button class="btn btn-info btn-sm" onclick="verPensionesDeCliente(${c.idCliente}, '${nombre.replace(/'/g, "\\'")}')">Pensiones</button>` : '';
        return `<div class="cliente-item"><div class="cliente-info"><h4>${nombre}</h4><p><strong>ID:</strong> ${c.idCliente}</p>${dniInfo}</div><div>${botonVer}<button class="btn btn-secondary btn-sm" onclick="editarCliente(${c.idCliente})">Editar</button><button class="btn btn-danger btn-sm" onclick="eliminarCliente(${c.idCliente}, '${nombre.replace(/'/g, "\\'")}')">Eliminar</button></div></div>`;
    }).join('');
}
function mostrarEmpresas(empresas) {
    const container = document.getElementById('empresasLista');
    if (!empresas || empresas.length === 0) { container.innerHTML = '<p class="text-center p-3">No hay empresas para mostrar.</p>'; return; }
    container.innerHTML = empresas.map(e => `<div class="cliente-item"><div class="cliente-info"><h4>${e.razonSocial}</h4><p><strong>RUC:</strong> ${e.ruc}</p></div><div><button class="btn btn-info btn-sm" onclick="verPensionesDeEmpresa(${e.idEmpresa}, '${e.razonSocial.replace(/'/g, "\\'")}')">Pensiones</button><button class="btn btn-secondary btn-sm" onclick="editarEmpresa(${e.idEmpresa})">Editar</button><button class="btn btn-danger btn-sm" onclick="eliminarEmpresa(${e.idEmpresa}, '${e.razonSocial.replace(/'/g, "\\'")}')">Eliminar</button></div></div>`).join('');
}
function mostrarPensiones(pensiones) {
    const container = document.getElementById('pensionesList');
    if (!pensiones || pensiones.length === 0) { container.innerHTML = '<p class="text-center p-3">No se encontraron pensiones.</p>'; return; }
    container.innerHTML = pensiones.map(p => {
        const clienteNombre = p.clienteNombre || 'N/A';
        const empresaNombre = p.empresaRazonSocial || 'N/A';
        const estado = obtenerEstadoPension(p.fechaInicio, p.fechaFin);
        return `<div class="cliente-item"><div class="cliente-info"><h4>Pensión de ${clienteNombre}</h4><p><strong>Empresa:</strong> ${empresaNombre}</p><p><strong>Estado:</strong> <span style="font-weight:bold; color:${estado.color}">${estado.texto}</span></p></div><div><button class="btn btn-secondary btn-sm" onclick="editarPension(${p.idPension})">Editar</button><button class="btn btn-danger btn-sm" onclick="eliminarPension(${p.idPension}, '${clienteNombre.replace(/'/g, "\\'")}')">Eliminar</button></div></div>`;
    }).join('');
}
async function verPensionesDeEmpresa(empresaId, nombreEmpresa) {
            const modalTitulo = document.getElementById('modalPensionesTitulo');
            const modalContenido = document.getElementById('modalPensionesContenido');
            modalTitulo.textContent = `Pensiones de: ${nombreEmpresa}`;
            modalContenido.innerHTML = `<div class="loading">Cargando pensiones...</div>`;
            openModal('pensionesAsociadasModal');

            try {
                const response = await fetch(`/pensiones/api/por-empresa/${empresaId}`);
                if (!response.ok) throw new Error('Error al cargar las pensiones');
                const pensiones = await response.json();

                if (pensiones.length === 0) {
                    modalContenido.innerHTML = '<p class="text-center p-3">Esta empresa no tiene pensiones asociadas.</p>';
                    return;
                }

                modalContenido.innerHTML = pensiones.map(p => `
                    <div class="cliente-item">
                        <div class="cliente-info">
                            <h5>${p.clienteNombre}</h5>
                            <p>Del ${p.fechaInicio} al ${p.fechaFin} - S/ ${p.montoMensual.toFixed(2)}</p>
                        </div>
                        <button class="btn btn-danger btn-sm" onclick="eliminarPensionDesdeModal(${p.idPension}, 'empresa', ${empresaId}, '${nombreEmpresa}')">Eliminar</button>
                    </div>
                `).join('');

            } catch (error) {
                modalContenido.innerHTML = `<p class="text-danger text-center">${error.message}</p>`;
            }
}
async function verPensionesDeCliente(clienteId, nombreCliente) {
        const modalTitulo = document.getElementById('modalPensionesTitulo');
        const modalContenido = document.getElementById('modalPensionesContenido');
        modalTitulo.textContent = `Pensiones de: ${nombreCliente}`;
        modalContenido.innerHTML = `<div class="loading">Cargando pensiones...</div>`;
        openModal('pensionesAsociadasModal');
        try {
            const response = await fetch(`/pensiones/api/por-cliente/${clienteId}`);
            if (!response.ok) throw new Error('Error al cargar las pensiones del cliente');
            const pensiones = await response.json();
            if (pensiones.length === 0) {
                modalContenido.innerHTML = '<p class="text-center p-3">Este cliente no tiene pensiones asociadas.</p>';
                return;
            }
            modalContenido.innerHTML = pensiones.map(p => `
                <div class="cliente-item">
                    <div class="cliente-info">
                        <h5>Pensión con ${p.empresaRazonSocial}</h5>
                        <p>Del ${p.fechaInicio} al ${p.fechaFin} - S/ ${p.montoMensual.toFixed(2)}</p>
                    </div>
                    <button class="btn btn-danger btn-sm" onclick="eliminarPensionDesdeModal(${p.idPension}, 'cliente', ${clienteId}, '${nombreCliente.replace(/'/g, "\\'")}')">Eliminar</button>
                </div>
            `).join('');
        } catch (error) {
            modalContenido.innerHTML = `<p class="text-danger text-center">${error.message}</p>`;
        }
    }
async function eliminarPensionDesdeModal(pensionId, tipoOrigen, origenId, nombreOrigen) {
            if (!confirm(`¿Seguro que quieres eliminar esta pensión?`)) return;
            try {
                const response = await fetch(`/pensiones/eliminar/${pensionId}`, { method: 'DELETE' });
                if (!response.ok) throw new Error(await response.text());

                // Refrescar la vista del modal y la lista principal de pensiones
                if (tipoOrigen === 'empresa') {
                    verPensionesDeEmpresa(origenId, nombreOrigen);
                } else {
                    verPensionesDeCliente(origenId, nombreOrigen);
                }
                cargarPensiones(); // Recarga la lista principal en segundo plano

            } catch (error) {
                alert('Error al eliminar pensión: ' + error.message);
            }
}

function renderizarPaginacion(containerId, pageData, callbackFunctionName, termino = '') {
    const container = document.getElementById(containerId);
    container.innerHTML = '';
    if (pageData.totalPages <= 1) return;
    const { number: currentPage, totalPages, size } = pageData;
    const createLink = (page, text, disabled = false) => `<li class="page-item ${disabled ? 'disabled' : ''} ${page === currentPage ? 'active' : ''}"><a class="page-link" href="#" onclick="event.preventDefault(); window['${callbackFunctionName}'](${page}, ${size}, '${termino.replace(/'/g, "\\'")}')">${text}</a></li>`;
    container.innerHTML += createLink(currentPage - 1, '&laquo;', currentPage === 0);
    for (let i = 0; i < totalPages; i++) { container.innerHTML += createLink(i, i + 1); }
    container.innerHTML += createLink(currentPage + 1, '&raquo;', currentPage >= totalPages - 1);
}

function toggleClienteFields() {
    const tipo = document.getElementById('tipoCliente').value;
    document.getElementById('camposParticular').classList.toggle('show', tipo === 'PARTICULAR');
    document.getElementById('camposPensionado').classList.toggle('show', tipo === 'PENSION');
}

async function registrarCliente() {
    const tipo = document.getElementById('tipoCliente').value;
    if (!tipo) { mostrarAlerta('Seleccione un tipo de cliente', 'error', 1); return; }
    let clienteData = { tipoCliente: tipo };
    if (tipo === 'PARTICULAR') {
        clienteData.nombreCliente = document.getElementById('nombreParticular').value.trim();
    } else {
        clienteData.dni = document.getElementById('dniPensionado').value.trim();
        clienteData.nombres = document.getElementById('nombresPensionado').value.trim();
        clienteData.apellidos = document.getElementById('apellidosPensionado').value.trim();
    }
    try {
        const response = await fetch('/clientes/guardar', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(clienteData) });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Cliente registrado con éxito', 'success', 1);
        limpiarFormularioCliente();
        cargarClientes();
        if (tipo === 'PENSION') cargarClientesPensionados();
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 1); }
}

async function registrarEmpresa() {
    const empresaData = { ruc: document.getElementById('rucEmpresa').value.trim(), razonSocial: document.getElementById('razonSocial').value.trim() };
    try {
        const response = await fetch('/empresas/guardar', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(empresaData) });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Empresa registrada con éxito', 'success', 2);
        limpiarFormularioEmpresa();
        cargarEmpresas();
        cargarEmpresasParaDropdown();
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 2); }
}

async function registrarPension() {
    const pensionData = {
        empresaId: parseInt(document.getElementById('empresaPension').value),
        clienteId: parseInt(document.getElementById('clientePension').value),
        montoMensual: parseFloat(document.getElementById('montoMensual').value),
        fechaInicio: document.getElementById('fechaInicio').value,
        fechaFin: document.getElementById('fechaFin').value
    };
    try {
        const response = await fetch('/pensiones/guardar', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(pensionData) });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Pensión registrada con éxito', 'success', 2);
        limpiarFormularioPension();
        cargarPensiones();
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 2); }
}

async function cargarClientesPensionados() {
    try {
        const response = await fetch('/clientes/api/tipo/PENSION');
        if (!response.ok) return;
        const clientes = await response.json();
        const select = document.getElementById('clientePension');
        select.innerHTML = '<option value="">Seleccione un cliente...</option>';
        clientes.forEach(c => { select.innerHTML += `<option value="${c.idCliente}">${c.nombres} ${c.apellidos}</option>`; });
    } catch (error) { console.error(error); }
}

async function cargarEmpresasParaDropdown() {
    try {
        const response = await fetch('/empresas/api/listar?size=100');
        if (!response.ok) throw new Error('No se pudo cargar la lista de empresas');
        const paginaDeEmpresas = await response.json();
        const empresas = paginaDeEmpresas.content;
        const select = document.getElementById('empresaPension');
        select.innerHTML = '<option value="">Seleccione una empresa...</option>';
        empresas.forEach(empresa => { select.innerHTML += `<option value="${empresa.idEmpresa}">${empresa.razonSocial}</option>`; });
    } catch (error) {
        console.error('Error cargando empresas para el dropdown:', error);
        document.getElementById('empresaPension').innerHTML = '<option value="">Error al cargar</option>';
    }
}

function openModal(modalId) { document.getElementById(modalId).style.display = 'block'; }
function closeModal(modalId) { document.getElementById(modalId).style.display = 'none'; }

async function editarCliente(id) {
    try {
        const response = await fetch(`/clientes/api/buscar/${id}`);
        if (!response.ok) throw new Error('Cliente no encontrado');
        const cliente = await response.json();
        document.getElementById('editClienteId').value = cliente.idCliente;
        const esParticular = cliente.tipoCliente === 'PARTICULAR';
        document.getElementById('editCamposParticular').style.display = esParticular ? 'block' : 'none';
        document.getElementById('editCamposPensionado').style.display = !esParticular ? 'block' : 'none';
        if (esParticular) { document.getElementById('editNombreParticular').value = cliente.nombreCliente; }
        else { document.getElementById('editDniPensionado').value = cliente.dni; document.getElementById('editNombresPensionado').value = cliente.nombres; document.getElementById('editApellidosPensionado').value = cliente.apellidos; }
        openModal('editClienteModal');
    } catch (error) { mostrarAlerta(error.message, 'error', 1); }
}
async function guardarCambiosCliente() {
    const id = document.getElementById('editClienteId').value;
    const clienteOriginal = clientesData.find(c => c.idCliente == id);
    if (!clienteOriginal) { mostrarAlerta('Error: no se pudo encontrar el cliente original.', 'error', 1); return; }
    let data = { idCliente: parseInt(id), tipoCliente: clienteOriginal.tipoCliente };
    if (clienteOriginal.tipoCliente === 'PARTICULAR') { data.nombreCliente = document.getElementById('editNombreParticular').value; }
    else { data.dni = document.getElementById('editDniPensionado').value; data.nombres = document.getElementById('editNombresPensionado').value; data.apellidos = document.getElementById('editApellidosPensionado').value; }
    try {
        const response = await fetch('/clientes/actualizar', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Cliente actualizado', 'success', 1);
        closeModal('editClienteModal');
        cargarClientes(document.querySelector('#paginacionClientes .active a')?.textContent - 1 || 0);
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 1); }
}

async function editarEmpresa(id) {
    try {
        const response = await fetch(`/empresas/api/buscar/${id}`);
        if (!response.ok) throw new Error('Empresa no encontrada');
        const empresa = await response.json();
        document.getElementById('editEmpresaId').value = empresa.idEmpresa;
        document.getElementById('editRucEmpresa').value = empresa.ruc;
        document.getElementById('editRazonSocial').value = empresa.razonSocial;
        openModal('editEmpresaModal');
    } catch (error) { mostrarAlerta(error.message, 'error', 2); }
}
async function guardarCambiosEmpresa() {
     const data = { idEmpresa: parseInt(document.getElementById('editEmpresaId').value), razonSocial: document.getElementById('editRazonSocial').value };
    try {
        const response = await fetch('/empresas/actualizar', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Empresa actualizada', 'success', 2);
        closeModal('editEmpresaModal');
        cargarEmpresas(document.querySelector('#paginacionEmpresas .active a')?.textContent - 1 || 0);
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 2); }
}

async function editarPension(id) {
    try {
        const response = await fetch(`/pensiones/api/buscar/${id}`);
        if (!response.ok) throw new Error('Pensión no encontrada');
        const pension = await response.json();
        document.getElementById('editPensionId').value = pension.idPension;
        document.getElementById('editPensionCliente').value = pension.clienteNombre;
        document.getElementById('editPensionEmpresa').value = pension.empresaRazonSocial;
        document.getElementById('editMontoMensual').value = pension.montoMensual;
        document.getElementById('editFechaInicio').value = pension.fechaInicio;
        document.getElementById('editFechaFin').value = pension.fechaFin;
        openModal('editPensionModal');
    } catch (error) { mostrarAlerta(error.message, 'error', 2); }
}
async function guardarCambiosPension() {
    const data = { idPension: parseInt(document.getElementById('editPensionId').value), montoMensual: document.getElementById('editMontoMensual').value, fechaInicio: document.getElementById('editFechaInicio').value, fechaFin: document.getElementById('editFechaFin').value };
    try {
        const response = await fetch('/pensiones/actualizar', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Pensión actualizada', 'success', 2);
        closeModal('editPensionModal');
        cargarPensiones(document.querySelector('#paginacionPensiones .active a')?.textContent - 1 || 0);
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 2); }
}

async function eliminarCliente(id, nombre) {
    if (!confirm(`¿Seguro que quieres eliminar a ${nombre}?`)) return;
    try {
        const response = await fetch(`/clientes/eliminar/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Cliente eliminado', 'success', 1);
        cargarClientes();
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 1); }
}
async function eliminarEmpresa(id, nombre) {
    if (!confirm(`¿Seguro que quieres eliminar a ${nombre}?`)) return;
    try {
        const response = await fetch(`/empresas/eliminar/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Empresa eliminada', 'success', 2);
        cargarEmpresas();
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 2); }
}
async function eliminarPension(id, nombreCliente) {
    if (!confirm(`¿Seguro que quieres eliminar la pensión de ${nombreCliente}?`)) return;
    try {
        const response = await fetch(`/pensiones/eliminar/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error(await response.text());
        mostrarAlerta('Pensión eliminada', 'success', 2);
        cargarPensiones();
    } catch (error) { mostrarAlerta('Error: ' + error.message, 'error', 2); }
}

function mostrarAlerta(mensaje, tipo, containerId) {
    const container = document.getElementById(`alertContainer${containerId}`);
    if(container) {
        container.innerHTML = `<div class="alert alert-${tipo === 'success' ? 'alert-success' : 'alert-danger'}">${mensaje}</div>`;
        setTimeout(() => { container.innerHTML = ''; }, 4000);
    }
}
function limpiarFormularioCliente() { document.getElementById('clienteForm').reset(); toggleClienteFields(); }
function limpiarFormularioEmpresa() { document.getElementById('empresaForm').reset(); }
function limpiarFormularioPension() { document.getElementById('pensionForm').reset(); }
function obtenerEstadoPension(fechaInicio, fechaFin) {
    const hoy = new Date(); hoy.setHours(0,0,0,0);
    const inicio = new Date(fechaInicio + 'T00:00:00');
    const fin = new Date(fechaFin + 'T00:00:00');
    if (hoy < inicio) return { texto: 'Pendiente', color: '#ffc107' };
    if (hoy >= inicio && hoy <= fin) return { texto: 'Activa', color: '#28a745' };
    return { texto: 'Vencida', color: '#dc3545' };
}
