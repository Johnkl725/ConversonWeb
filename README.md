# Conversor de Archivos a PDF - Versión Web 🌐

Aplicación web profesional para convertir documentos Word e imágenes a PDF con procesamiento paralelo y progreso en tiempo real.

## 🚀 Características

- **Conversión Individual**: Un PDF por cada archivo Word o imagen
- **Combinación de Archivos**: Múltiples archivos en un solo PDF
- **Procesamiento Paralelo**: Utiliza todos los núcleos de la CPU para máxima velocidad
- **Progreso en Tiempo Real**: WebSocket para actualizaciones instantáneas
- **Drag & Drop**: Interfaz moderna con arrastrar y soltar
- **Diseño Profesional**: Glassmorphism y gradientes modernos
- **Responsive**: Funciona en desktop, tablet y móvil

---

## 📋 Requisitos

- **Java**: 17 o superior
- **Maven**: 3.6 o superior
- **RAM**: 512 MB mínimo, 1 GB recomendado
- **Espacio en Disco**: 500 MB

---

## 🛠️ Instalación y Ejecución

### Opción 1: Usando Maven Wrapper (Recomendado)

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Opción 2: Usando Maven

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

### Opción 3: JAR Ejecutable

```bash
# Compilar JAR
mvn clean package

# Ejecutar
java -jar target/converson-web-1.0.0.jar
```

La aplicación estará disponible en: **http://localhost:8080**

---

## 🎯 Tipos de Conversión Soportados

### Conversión Individual

| Tipo | Extensiones | Descripción |
|------|-------------|-------------|
| **Word a PDF** | `.doc`, `.docx` | Un PDF por cada archivo Word |
| **Imagen a PDF** | `.jpg`, `.png`, `.bmp`, `.gif`, `.tiff` | Un PDF por cada imagen |

### Combinar en un Solo PDF (⚡ Paralelo)

| Tipo | Extensiones | Descripción |
|------|-------------|-------------|
| **Combinar Words** | `.doc`, `.docx` | Múltiples Words → 1 PDF |
| **Combinar Imágenes** | `.jpg`, `.png`, `.bmp`, `.gif`, `.tiff` | Múltiples imágenes → 1 PDF |

---

## 📖 Cómo Usar

### 1. Seleccionar Tipo de Conversión
- Elegir entre conversión individual o combinada
- Los tipos combinados usan procesamiento paralelo para máxima velocidad

### 2. Subir Archivos
- **Método 1**: Arrastrar archivos al área de drop zone
- **Método 2**: Click en "Seleccionar Archivos"

### 3. Convertir
- Click en "Convertir a PDF"
- Ver progreso en tiempo real
- Descargar PDFs generados

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────┐
│   Frontend (HTML/CSS/JavaScript)    │
│   - Drag & Drop                     │
│   - WebSocket para progreso         │
└──────────────┬──────────────────────┘
               │ REST API
┌──────────────▼──────────────────────┐
│   Presentation Layer (Controllers)   │
│   - FileUploadController            │
│   - ConversionController            │
│   - DownloadController              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Application Layer (Services)      │
│   - ConversionService (@Async)      │
│   - FileStorageService              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Domain Layer (Business Logic)     │
│   - ConversionType, ConversionResult│
│   - FileConverter (interface)       │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Infrastructure Layer              │
│   - WordToPdfConverter              │
│   - ImageToPdfConverter             │
│   - ConverterFactory                │
└─────────────────────────────────────┘
```

### Clean Architecture

- **Domain**: Modelos de negocio (reutilizados de app desktop)
- **Application**: Servicios y DTOs
- **Infrastructure**: Implementaciones concretas (converters)
- **Presentation**: Controllers REST y WebSocket

---

## 🌐 API REST Endpoints

### Upload de Archivos
```http
POST /api/files/upload
Content-Type: multipart/form-data

Response:
{
  "success": true,
  "uploadedFiles": [...],
  "count": 3
}
```

### Iniciar Conversión
```http
POST /api/conversion/start
Content-Type: application/json

Request:
{
  "fileIds": ["uuid-1", "uuid-2"],
  "conversionType": "WORD_TO_PDF"
}

Response:
{
  "jobId": "job-uuid",
  "status": "PROCESSING",
  "totalFiles": 2
}
```

### Consultar Estado
```http
GET /api/conversion/status/{jobId}

Response:
{
  "jobId": "job-uuid",
  "status": "COMPLETED",
  "successCount": 2,
  "failureCount": 0,
  "message": "Conversión completada"
}
```

### Descargar PDF
```http
GET /api/download/{jobId}/{filename}

Response: PDF file (application/pdf)
```

### WebSocket
```javascript
// Conectar a:
ws://localhost:8080/ws

// Suscribirse a:
/topic/progress/{jobId}
/topic/completion/{jobId}
```

---

## ⚙️ Configuración

Editar `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# File Upload
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=1GB

# Storage
app.storage.upload-dir=./uploads
app.storage.output-dir=./converted

# Async
spring.task.execution.pool.core-size=4
spring.task.execution.pool.max-size=8
```

---

## 🚀 Optimizaciones de Rendimiento

### 1. Procesamiento Asíncrono
```java
@Async
public CompletableFuture<ConversionResult> convertFilesAsync(...)
```

### 2. Thread Pool Optimizado
- Core pool: Número de CPUs
- Max pool: CPUs × 2
- Prioridad ajustada para UI responsiva

### 3. WebSocket
- Actualizaciones en tiempo real
- Sin polling innecesario

### 4. Streaming de Archivos
- PDFs servidos como stream, no cargados en memoria

---

## 📦 Estructura del Proyecto

```
ConversonWeb/
├── src/main/
│   ├── java/com/example/conversonweb/
│   │   ├── domain/              # Modelos de negocio
│   │   ├── application/         # Servicios y DTOs
│   │   ├── infrastructure/      # Converters
│   │   ├── presentation/        # Controllers
│   │   ├── config/              # Configuraciones
│   │   └── ConversonWebApplication.java
│   │
│   └── resources/
│       ├── templates/
│       │   └── index.html       # Frontend
│       ├── static/
│       │   ├── css/styles.css
│       │   └── js/app.js
│       └── application.properties
│
└── pom.xml
```

---

## 🧪 Testing

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Con coverage
mvn test jacoco:report
```

---

## 📊 Métricas de Rendimiento

| Operación | Tiempo Estimado |
|-----------|----------------|
| Upload 10 archivos (10MB c/u) | < 5 segundos |
| Convertir 1 Word a PDF | 1-3 segundos |
| Convertir 1 Imagen a PDF | < 1 segundo |
| Combinar 10 imágenes (paralelo) | 3-5 segundos |
| Combinar 5 Words (paralelo) | 8-12 segundos |

---

## 🐛 Solución de Problemas

### Error: "Puerto 8080 ya en uso"
```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error: "No se pueden subir archivos"
```bash
# Verificar límites en application.properties
spring.servlet.multipart.max-file-size=100MB
```

### WebSocket no conecta
- Verificar que no haya proxy/firewall bloqueando
- Verificar SockJS está cargado en el frontend

---

## 📝 Diferencias vs Aplicación Desktop

| Aspecto | Desktop | Web |
|---------|---------|-----|
| **UI** | Swing/JTable | HTML/CSS/JavaScript |
| **Acceso** | Local | Multi-usuario remoto |
| **Instalación** | JAR en cada PC | Deploy en servidor |
| **Progreso** | SwingWorker | WebSocket |
| **Archivos** | Filesystem directo | Upload/Download HTTP |
| **Estado** | En memoria | Session + caché |

**Ventajas de la versión Web:**
- ✅ Acceso desde cualquier navegador
- ✅ Sin instalación en clientes
- ✅ Actualizaciones centralizadas
- ✅ Multi-usuario simultáneo
- ✅ Logs centralizados

---

## 🔐 Seguridad

- Validación de tipos de archivo (MIME type + extensión)
- Límite de tamaño de archivos (100MB por defecto)
- Límite de cantidad de archivos (100 por job)
- Sanitización de nombres de archivo
- CORS configurado (actualizar para producción)

---

## 📄 Licencia

Este proyecto es de código libre para uso educativo y comercial.

---

## 👨‍💻 Desarrollo

Desarrollado con:
- **Spring Boot 3.2.1**
- **Java 17**
- **PDFBox 2.0.29** (conversión imágenes)
- **docx4j 11.4.9** (conversión Word)
- **WebSocket** (STOMP)
- **Modern CSS** (Glassmorphism)

---

## 🎉 ¡Listo para Usar!

1. `mvn spring-boot:run`
2. Abrir http://localhost:8080
3. ¡Convertir archivos a PDF!

**¡Disfruta la velocidad del procesamiento paralelo! ⚡**
