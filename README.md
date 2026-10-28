# Cafetería Limón

Prototipo de escritorio en Java Swing con Maven (JDK 17 o posterior).

## Ejecutar

Desde esta carpeta:

```powershell
mvn clean package
java -jar target\CafeteriaLimon-1.0-SNAPSHOT.jar
```

El comprobante incluye un patrón visual decorativo estilo QR; no es un código escaneable.

## Flujo de cliente

- Entra como invitado sin crear una cuenta y consulta el menú, busca por nombre y filtra por categoría.
- Agrega productos con instrucciones opcionales; los artículos agotados no se pueden añadir.
- Los pedidos solo se aceptan durante el horario configurado. Selecciona efectivo, tarjeta o transferencia al confirmar.
- El pedido solicita un nombre, descuenta existencias y genera un folio único, una clave y un QR para guardar y presentar al recoger.
- Consulta el estado y los pedidos creados en la sesión actual. Puede cancelar únicamente mientras siga en «Por hacer»; la cancelación libera el inventario.
- Al cambiar a «Por recoger», la vista del cliente muestra un aviso de pedido listo.

## Funciones administrativas

- En el menú inicial, el acceso de administrador acepta cualquier contraseña de demostración.
- El administrador también puede armar pedidos de prueba desde el menú y consultar los propios.
- Tablero Kanban con «Por hacer», «En preparación» y «Por recoger»; permite iniciar preparación, marcar como listo y registrar la entrega.
- Alta y edición de productos, precios, descripciones, categorías y existencias.
- Configuración del horario y activación o pausa de recepción de pedidos.
- Corte diario por método de pago, tiempo promedio de preparación y exportación de las ventas del día a CSV.

## Reglas y alcance del prototipo

- Los roles mantienen vistas separadas: el invitado ve solo sus pedidos de la sesión; el administrador consulta el tablero y los datos agregados.
- El precio y el total de cada pedido se guardan como una instantánea al confirmar; los cambios posteriores al catálogo no alteran pedidos previos.
- El pedido requiere un método de pago, pero el prototipo no procesa pagos reales.
- Los productos, pedidos, horarios y estados se conservan solo en memoria; se reinician al cerrar la aplicación. La contraseña administrativa no autentica una cuenta real.
