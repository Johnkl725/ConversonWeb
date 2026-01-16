# Ejecutar con Gradle

## 🚀 Opciones para Ejecutar

### Opción 1: Desde IntelliJ IDEA (Más Fácil)

1. **Abrir el proyecto** en IntelliJ IDEA
2. **Esperar** a que Gradle sincronice las dependencias
3. **Buscar** la clase `ConversonWebApplication.java`
4. **Click derecho** → **Run 'ConversonWebApplication'**

O usar el panel de Gradle:
1. **Abrir panel Gradle** (botón en la barra lateral derecha)
2. **Expandir**: ConversonWeb → Tasks → application
3. **Doble-click** en `bootRun`

### Opción 2: Terminal con Gradle Wrapper

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### Opción 3: Script de ejecución

```bash
# Ejecutar el script
run-gradle.bat
```

### Opción 4: Compilar JAR y ejecutar

```bash
# Compilar
.\gradlew.bat build

# Ejecutar JAR
java -jar build\libs\converson-web-1.0.0.jar
```

## 🔧 Tareas Gradle Útiles

```bash
# Limpiar proyecto
.\gradlew.bat clean

# Compilar
.\gradlew.bat build

# Ejecutar tests
.\gradlew.bat test

# Ejecutar aplicación
.\gradlew.bat bootRun

# Ver dependencias
.\gradlew.bat dependencies
```

## ✅ Verificar Instalación

La aplicación estará lista cuando veas:

```
========================================
  Conversor de Archivos a PDF - Web
  Aplicación iniciada exitosamente
  URL: http://localhost:8080
========================================
```

Luego abre tu navegador en: **http://localhost:8080**

## 🎯 Recomendación

**Usa IntelliJ IDEA directamente:**
- Click derecho en `ConversonWebApplication.java`
- **Run 'ConversonWebApplication'**
- ¡Listo! 🚀

Es la forma más rápida y con hot reload automático.
