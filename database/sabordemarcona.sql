CREATE DATABASE IF NOT EXISTS restaurante_db;
USE restaurante_db;

-- Tabla Usuario
CREATE TABLE Usuario (
    ID_Usuario INT PRIMARY KEY AUTO_INCREMENT,
    Nombre_Usuario VARCHAR(100) NOT NULL,
    Contraseña VARCHAR(100) NOT NULL,
    Rol VARCHAR(50) NOT NULL
);

-- Tabla Cliente
CREATE TABLE Cliente (
    ID_Cliente INT PRIMARY KEY AUTO_INCREMENT,
    Nombre_Cliente VARCHAR(100) NOT NULL,
    Tipo_Cliente VARCHAR(50) NOT NULL
);

-- Tabla Pedido
CREATE TABLE Pedido (
    ID_Pedido INT PRIMARY KEY AUTO_INCREMENT,
    ID_Cliente INT,
    ID_Usuario INT,
    Fecha_Pedido DATE NOT NULL,
    Total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(ID_Usuario)
);

-- Tabla Item
CREATE TABLE Item (
    ID_Item INT PRIMARY KEY AUTO_INCREMENT,
    Nombre VARCHAR(100) NOT NULL,
    Tipo_Item VARCHAR(20) NOT NULL,         -- 'comida' o 'bebida'
    Tipo_Comida VARCHAR(50),
    Precio DECIMAL(10,2) NOT NULL
);

-- Tabla DetallePedido
CREATE TABLE DetallePedido (
    ID_DetallePedido INT PRIMARY KEY AUTO_INCREMENT,
    ID_Pedido INT,
    ID_Item INT,
    Cantidad INT NOT NULL,
    Precio_Unitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ID_Pedido) REFERENCES Pedido(ID_Pedido),
    FOREIGN KEY (ID_Item) REFERENCES Item(ID_Item)
);

-- Tabla Pension
CREATE TABLE Pension (
    ID_Pension INT PRIMARY KEY AUTO_INCREMENT,
    ID_Cliente INT,
    Fecha_Inicio DATE NOT NULL,
    Fecha_Fin DATE NOT NULL,
    Monto_Mensual DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente)
);

-- Tabla Insumo
CREATE TABLE Insumo (
    ID_Insumo INT PRIMARY KEY AUTO_INCREMENT,
    Nombre_Insumo VARCHAR(100) NOT NULL,
    Stock INT NOT NULL
);

-- Tabla Compra
CREATE TABLE Compra (
    ID_Compra INT PRIMARY KEY AUTO_INCREMENT,
    Fecha_Compra DATE NOT NULL
);

-- Tabla DetalleCompra
CREATE TABLE DetalleCompra (
    ID_DetalleCompra INT PRIMARY KEY AUTO_INCREMENT,
    ID_Compra INT,
    ID_Insumo INT,
    Cantidad INT NOT NULL,
    Precio_Compra DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ID_Compra) REFERENCES Compra(ID_Compra),
    FOREIGN KEY (ID_Insumo) REFERENCES Insumo(ID_Insumo)
);
