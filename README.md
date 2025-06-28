# STORE
STORE GROCERY
📱 Project: Grocery Store App
Platform: Android Studio
Language: Kotlin
UI Framework: Jetpack Compose
Local Database: SQLite
Remote Database: Firebase (Firestore + Storage)
External API: Open Food Data
Dynamic UI: LazyColumn / LazyRow
API:

🧱 1. App Architecture

💾Databases
📍 SQLite (Offline) OR ROOM
• Inventory
• Customers
• Suppliers
• Recent Sales
• Local Orders
☁️ Firebase Firestore / Storage
• Full Sales History
• Data Sync Across Devices
• PDF Receipts
• Authentication (Multi-User, Optional)
🔍 Public API
• Open Food Facts:
https://world.openfoodfacts.org/api/v0/product/[barcode].json

🧩 2. Main Features

📦 Inventory
• Product scanning by barcode (device camera) or manual entry
• Recording of inputs (purchases) and outputs (sales)
• Stock control with alerts for:
• Minimum quantity
• Expiration date
• Classification by category
• Location system within the business (e.g., $aisle, $shelf, $level)
🧾 Invoicing and Accounting Management
• Receipt issuance (onscreen or PDF)
• Recording of fixed/operating expenses
• Viewing historical purchases and sales
👤 People Management
Customers:
• Name, phone number, address, email
• Purchase and order history
Suppliers:
• Name, phone number, address, email
• Supplied products, purchase history
🛒 Orders
• Create custom orders for customers
• Statuses: Pending, Delivered, Cancelled, Paid
• Association with customer history
• Automatic customer notifications
📊 Reports and Analytics
• Sales charts by:
• Product
• Customer
• Category
• Time (weekly, monthly, quarterly, annually)
• Best-selling products
• Replenishment suggestions based on sales and trends
________________________________________
________________________________________
📱 3. Initial Views and Navigation
🧭 Menu or Bottom Navigation:

• Presentation Screen
• Home / Dashboard
• Inventory
• Sales
• Purchases
• Orders
• Reports
• Customers
• Suppliers
• Configuration
🔁 Lists with LazyColumn / LazyRow:
• Products
• Customers
• Suppliers
• Orders
________________________________________
📚 4. Relational Data Model (Tables)
🗃️ Products
• ID, Name, Barcode, Purchase Price, Sale Price
• Category, Stock, Supplier ID
🧾 Sales and SalesDetail
• Sales: ID, Date, Customer ID, Total
• Detail: ID, Sale ID, Product ID, Quantity, Unit Price
📥 Purchases and PurchaseDetail
• Purchases: ID, Date, Supplier ID, Total
• Detail: ID, Purchase ID, Product ID, Quantity, Unit Price
👤 Customers
• ID, Name, Phone, Address, Email
• Payment Preferences
🚚 Suppliers
• ID, Name, Phone, Address, Email
🧾 Orders
• ID, Client ID, Date, Status, Total
💸Services and Expenses
• ID, Type, Amount, Date, Description
________________________________________
🧠 5. Intelligence and Statistics
• Ranking of best-selling products
• Replenishment recommendations: Based on volume, frequency, and season
• Comparisons by period:
• Timeline, bars
• Week vs. week, month vs. month, year vs. year

📝 Important notes about Hilt
• AppModule.kt: Room, DAO, Repositories, Retrofit, Firebase
• FirebaseModule.kt: Firebase Providers
• ViewModelModule.kt: (optional) for ViewModels
________________________________________
✅ Suggested workflow
1. Create domain/models with clean classes
2. Implement data/local (entities + DAOs + DB)
3. Configure data/remote/Firebase and API (MiniStore “Project number”) 604998840585”) and (Open Food Facts https://world.openfoodfacts.org/api/v0/product/[barcode].json)
4. Create repository/ and define interfaces
5. Configure di/ with Hilt
6. Create use cases/ with business logic
7. Start the UI (presentation/screens/, navigation/, etc.)
8. Use @HiltViewModel and hiltViewModel() to inject dependencies
🌐Internationalization Module
1. **Default language:** English
**Additional languages:**
- Moroccan Arabic
- English
- Chinese
🌙 Activate Dark Theme
Implement dark theme support using Jetpack Compose
Configure colors and styles in the theme/ folder
Allow users to switch between light and dark themes from settings / Allow users to switch between light and dark themes from settings
🚀 Splash Screen Presentation
Create a splash screen for the app presentation
Show the store logo and a welcome message
Set navigation to go to the dashboard after a few seconds

📊 Dashboard Adapted to the Project

📱 Proyecto: Aplicación para Tienda de Abarrotes
Plataforma: Android Studio
Lenguaje: Kotlin
UI Framework: Jetpack Compose
Base de Datos Local: SQLite
Base de Datos Remota: Firebase (Firestore + Storage)
API externa: Open Food Facts
UI dinámica: LazyColumn / LazyRow
API :

🧱 1. Arquitectura de la Aplicación

💾 Bases de Datos
📍 SQLite (Offline) O ROOM
•	Inventario
•	Clientes
•	Proveedores
•	Ventas recientes
•	Pedidos locales
☁️ Firebase Firestore / Storage
•	Historial de ventas completo
•	Datos sincronizados entre dispositivos
•	Recibos en PDF
•	Autenticación (multiusuario, opcional)
🔍 API Pública
•	Open Food Facts:
https://world.openfoodfacts.org/api/v0/product/[barcode].json

🧩 2. Funcionalidades Principales

📦 Inventario
•	Escaneo de productos por código de barras (cámara de dispositivo) o ingreso manual
•	Registro de entradas (compras) y salidas (ventas)
•	Control de stock con alertas por:
•	Cantidad mínima
•	Vencimiento
•	Clasificación por categorías
•	Sistema de ubicación dentro del negocio (ej. $pasillo, $estante, $nivel)
🧾 Facturación y Gestión Contable
•	Emisión de recibos (en pantalla o PDF)
•	Registro de gastos fijos/operativos
•	Consulta histórica de compras y ventas
👤 Gestión de Personas
Clientes:
•	Nombre, teléfono, dirección, correo electrónico
•	Historial de compras y pedidos
Proveedores:
•	Nombre, teléfono, dirección, correo electrónico
•	Productos suministrados, historial de compras
🛒 Pedidos
•	Crear órdenes personalizadas para clientes
•	Estados: Pendiente, Entregado, Cancelado, Pagado
•	Asociación al historial del cliente
•	Notificaciones automáticas para clientes
📊 Reportes y Análisis
•	Gráficas de ventas por:
•	Producto
•	Cliente
•	Categoría
•	Tiempo (semanal, mensual, trimestral, anual)
•	Productos más vendidos
•	Sugerencias de reposición según ventas y tendencias
________________________________________
________________________________________
📱 3. Vistas Iniciales y Navegación
🧭 Menú o Bottom Navigation:

•	Pantalla splash
•	Inicio / Dashboard
•	Inventario
•	Ventas
•	Compras
•	Pedidos
•	Reportes
•	Clientes
•	Proveedores
•	Configuración
🔁 Listas con LazyColumn / LazyRow:
•	Productos
•	Clientes
•	Proveedores
•	Pedidos
________________________________________
📚 4. Modelo de Datos Relacional (Tablas)
🗃️ Productos
•	ID, Nombre, Código de barras, Precio compra, Precio venta
•	Categoría, Stock, ProveedorID
🧾 Ventas y DetalleVenta
•	Ventas: ID, Fecha, ClienteID, Total
•	Detalle: ID, VentaID, ProductoID, Cantidad, Precio unitario
📥 Compras y DetalleCompra
•	Compras: ID, Fecha, ProveedorID, Total
•	Detalle: ID, CompraID, ProductoID, Cantidad, Precio unitario
👤 Clientes
•	ID, Nombre, Teléfono, Dirección, Email
•	Preferencias de pago
🚚 Proveedores
•	ID, Nombre, Teléfono, Dirección, Email
🧾 Pedidos
•	ID, ClienteID, Fecha, Estado, Total
💸 Servicios y Gastos
•	ID, Tipo, Monto, Fecha, Descripción
________________________________________
🧠 5. Inteligencia y Estadísticas
•	Ranking de productos más vendidos
•	Recomendaciones de reposición:
•	Basadas en volumen, frecuencia, y temporada
•	Comparativas por períodos:
•	Línea de tiempo, barras
•	Semana vs semana, mes vs mes, año vs año



📝 Notas importantes sobre Hilt
•	AppModule.kt: Room, DAOs, Repositorios, Retrofit, Firebase
•	FirebaseModule.kt: Proveedores de Firebase
•	ViewModelModule.kt: (opcional) para ViewModels
________________________________________
✅ Secuencia de trabajo sugerida
1.	Crear domain/models con clases limpias
2.	Implementar data/local (entities + DAOs + DB)
3.	Configurar data/remote/firebase y api (MiniStore “Número del proyecto 604998840585”) y (Open Food Facts https://world.openfoodfacts.org/api/v0/product/[barcode].json)
4.	Crear repository/ y definir interfaces
5.	Configurar di/ con Hilt
6.	Crear usecases/ con lógica de negocio
7.	Comenzar la UI (presentation/screens/, navigation/, etc.)
8.	Usar @HiltViewModel y hiltViewModel() para inyectar dependencias
🌐 Módulo de Internacionalización
1.	**Idioma por defecto:** Inglés
**Idiomas adicionales:**
- Árabe marroquí
- Español
- Chino
🌙 Activación de Tema Oscuro / Dark Theme Activation
Implementar soporte para tema oscuro utilizando Jetpack Compose / Implement dark theme support using Jetpack Compose
Configurar colores y estilos en la carpeta theme/ / Configure colors and styles in the theme/ folder
Permitir a los usuarios cambiar entre tema claro y oscuro desde la configuración / Allow users to switch between light and dark themes from settings
🚀 Pantalla Splash de Presentación / Splash Screen Presentation
Crear una pantalla splash para la presentación de la aplicación / Create a splash screen for the application presentation
Mostrar el logo de la tienda y un mensaje de bienvenida / Display the store logo and a welcome message
Configurar la navegación para ir al dashboard después de unos segundos / Configure navigation to go to the dashboard after a few seconds.


📊 Dashboard Adaptado al Proyecto
El objetivo del Dashboard es ofrecer una vista rápida del estado actual del negocio, con métricas clave y accesos directos a las funcionalidades más usadas.

✅ Funcionalidades sugeridas del Dashboard
1.	Resumen de inventario
o	Total de productos
o	Productos con stock bajo
o	Productos próximos a vencerse
2.	Resumen de ventas
o	Total vendido hoy
o	Ventas de la semana
o	Producto más vendido (hoy o semana)
3.	Pedidos
o	Pedidos pendientes
o	Entregas agendadas para hoy
4.	Gráficas rápidas
o	Ventas por día (últimos 7 días)
o	Categorías más vendidas (gráfico de pastel)
5.	Accesos rápidos
o	Botones o chips para: Añadir venta, Añadir compra, Escanear producto


- Usuarios registrados en AuthRepositoryImpl. Por ejemplo:
- Usuario: admin@store.com
- Contraseña: admin123
- o
- Usuario: user@store.com
- Contraseña: user123
- 






