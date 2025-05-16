CREATE TABLE Cliente (
    ID_Cliente INT AUTO_INCREMENT PRIMARY KEY,
    Nombre_Cliente VARCHAR(100) NOT NULL,
    Tipo_Cliente VARCHAR(50) NOT NULL
);

CREATE TABLE Usuario (
    ID_Usuario INT AUTO_INCREMENT PRIMARY KEY,
    Nombre_Usuario VARCHAR(100) NOT NULL,
    Contrasena VARCHAR(100) NOT NULL,
    Rol VARCHAR(50) NOT NULL
);

CREATE TABLE Insumo (
    ID_Insumo INT AUTO_INCREMENT PRIMARY KEY,
    Nombre_Insumo VARCHAR(100) NOT NULL,
    Stock INT NOT NULL
);

CREATE TABLE Item (
    ID_Item INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Tipo_Item VARCHAR(20) NOT NULL,
    Tipo_Comida VARCHAR(50),
    Precio DECIMAL(10,2) NOT NULL
);

CREATE TABLE Compra (
    ID_Compra INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Compra DATE NOT NULL
);

CREATE TABLE DetalleCompra (
    ID_DetalleCompra INT AUTO_INCREMENT PRIMARY KEY,
    ID_Compra INT,
    ID_Insumo INT,
    Cantidad INT NOT NULL,
    Precio_Compra DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2),
    FOREIGN KEY (ID_Compra) REFERENCES Compra(ID_Compra),
    FOREIGN KEY (ID_Insumo) REFERENCES Insumo(ID_Insumo)
);

CREATE TABLE Pedido (
    ID_Pedido INT AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INT,
    ID_Usuario INT,
    Fecha_Pedido DATE NOT NULL,
    Total DECIMAL(10,2),
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(ID_Usuario)
);

CREATE TABLE DetallePedido (
    ID_DetallePedido INT AUTO_INCREMENT PRIMARY KEY,
    ID_Pedido INT,
    ID_Item INT,
    Cantidad INT NOT NULL,
    Precio_Unitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2),
    FOREIGN KEY (ID_Pedido) REFERENCES Pedido(ID_Pedido),
    FOREIGN KEY (ID_Item) REFERENCES Item(ID_Item)
);

CREATE TABLE Pension (
    ID_Pension INT AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INT,
    Fecha_Inicio DATE NOT NULL,
    Fecha_Fin DATE NOT NULL,
    Monto_Mensual DECIMAL(10,2),
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente)
);
