# Cafetería Limón

Prototipo de punto de venta de escritorio desarrollado en Java Swing y Maven.

## Requisitos

- JDK 17 o posterior
- Apache Maven 3.6 o posterior

## Ejecutar

Desde esta carpeta:

```powershell
mvn clean package
java -jar target\CafeteriaLimon-1.0-SNAPSHOT.jar
```

## Funcionalidades

- Panel de inicio con resumen de ventas y actividad de la sesión.
- Catálogo de cafés, bebidas y repostería con filtros y búsqueda.
- Carrito con controles para aumentar o quitar productos.
- Cobro en efectivo o tarjeta; valida el efectivo y calcula el cambio.
- Historial de pedidos y total de venta.

Los productos de demostración y los pedidos se mantienen en memoria y se reinician al cerrar la aplicación.
