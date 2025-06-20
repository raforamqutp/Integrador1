<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestión de Menú</title>
    <link rel="stylesheet" href="/css/menu-crud.css">
</head>
<body>
<div class="crud-container">
    <h1>Gestión de Menú</h1>

    <!-- Mensajes de éxito/error -->
    <c:if test="${not empty message}">
        <div class="alert alert-${messageType}">${message}</div>
    </c:if>

    <!-- Pestañas -->
    <div class="tabs">
        <a href="?tab=comidas" class="tab-button ${param.tab == 'comidas' or empty param.tab ? 'active' : ''}">Comidas</a>
        <a href="?tab=bebidas" class="tab-button ${param.tab == 'bebidas' ? 'active' : ''}">Bebidas</a>
        <a href="?tab=entradas" class="tab-button ${param.tab == 'entradas' ? 'active' : ''}">Entradas</a>
    </div>

    <!-- Contenido de las pestañas -->
    <c:choose>
        <c:when test="${param.tab == 'comidas' or empty param.tab}">
            <div class="tab-content active" id="comidas">
                <h2>Gestión de Platos Principales</h2>
                <div class="crud-actions">
                    <a href="/admin/menu/comida/nuevo" class="btn-add">Agregar Plato</a>
                    <form action="/admin/menu/comida/buscar" method="get" class="search-form">
                        <input type="text" name="query" class="search-input" placeholder="Buscar plato...">
                        <button type="submit" class="btn-search">Buscar</button>
                    </form>
                </div>
                <table class="crud-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Tipo</th>
                        <th>Precio</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${comidas}" var="comida">
                        <tr>
                            <td>${comida.idComida}</td>
                            <td>${comida.nombreComida}</td>
                            <td>${comida.tipoComida}</td>
                            <td>S/${comida.precio}</td>
                            <td>
                                <a href="/admin/menu/comida/editar/${comida.idComida}" class="btn-edit">Editar</a>
                                <form action="/admin/menu/comida/eliminar/${comida.idComida}" method="post" style="display:inline;">
                                    <button type="submit" class="btn-delete" onclick="return confirm('¿Está seguro?')">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>

        <c:when test="${param.tab == 'bebidas'}">
            <!-- Similar estructura para bebidas -->
        </c:when>

        <c:when test="${param.tab == 'entradas'}">
            <!-- Similar estructura para entradas -->
        </c:when>
    </c:choose>

    <!-- Formulario modal ahora será una página separada -->
</div>
</body>
</html>