# 🚀 Guía de Deployment en la Nube

Despliega tu aplicación para **acceso desde cualquier lugar** con opciones gratuitas.

---

## 🎯 Opciones Recomendadas (De mejor a más compleja)

| Plataforma | Gratis | Fácil | Tiempo | Recomendado |
|------------|--------|-------|--------|-------------|
| **Railway** | ✅ 5GB | ⭐⭐⭐⭐⭐ | 5 min | 🏆 **MEJOR** |
| **Render** | ✅ 750h | ⭐⭐⭐⭐ | 10 min | ⭐ Buena |
| **Railway + GitHub** | ✅ | ⭐⭐⭐⭐⭐ | 8 min | ⭐ Auto-deploy |
| **Fly.io** | ✅ 3 VMs | ⭐⭐⭐ | 15 min | Avanzado |
| **AWS/Azure** | ❌ $$ | ⭐⭐ | 30 min | Empresarial |

---

# 🏆 OPCIÓN 1: Railway (MÁS FÁCIL - RECOMENDADO)

**✅ Ventajas:**
- Gratis hasta 5GB de RAM
- Deploy en 5 minutos
- URL gratuita: `https://tu-app.up.railway.app`
- No requiere tarjeta de crédito (inicialmente)
- Auto-scaling
- Logs en tiempo real

## 📋 Pasos:

### 1. Preparar tu proyecto

Ya está listo! Solo asegúrate de tener `build.gradle` correcto.

### 2. Crear cuenta en Railway

1. Ve a: https://railway.app
2. Click en **"Start a New Project"**
3. Login con GitHub (recomendado) o Email

### 3. Deploy

**Opción A: Deploy directo (MÁS RÁPIDO)**

```bash
# Instalar Railway CLI
npm i -g @railway/cli

# O en PowerShell:
iwr https://railway.app/install.ps1 | iex

# Login
railway login

# Inicializar en tu proyecto
cd C:\Users\John\IdeaProjects\ConversonWeb
railway init

# Deploy!
railway up
```

**Opción B: Desde GitHub (AUTO-DEPLOY)**

1. Sube tu proyecto a GitHub
2. En Railway: **"Deploy from GitHub repo"**
3. Selecciona tu repositorio
4. Railway detecta Spring Boot automáticamente
5. ¡Ya está!

### 4. Configurar Variables de Entorno (Opcional)

En Railway dashboard:
- `SPRING_PROFILES_ACTIVE=prod`
- `SERVER_PORT=8080`

### 5. Obtener URL

Railway te dará una URL como:
```
https://conversonweb-production.up.railway.app
```

---

# ⭐ OPCIÓN 2: Render (También muy fácil)

**✅ Ventajas:**
- Gratis 750 horas/mes
- Deploy automático desde GitHub
- SSL gratis
- URL: `https://tu-app.onrender.com`

## 📋 Pasos:

### 1. Crear cuenta

Ve a: https://render.com
Login con GitHub

### 2. Crear archivo de configuración

Crea `render.yaml` en la raíz del proyecto:

```yaml
services:
  - type: web
    name: conversor-pdf
    env: java
    buildCommand: ./gradlew build -x test
    startCommand: java -jar build/libs/converson-web-1.0.0.jar
    envVars:
      - key: JAVA_OPTS
        value: -Xmx512m
```

### 3. Deploy

1. En Render: **"New Web Service"**
2. Conecta tu repositorio de GitHub
3. Render detecta el `render.yaml`
4. Click **"Create Web Service"**

### 4. Esperar deploy (5-10 min)

Render compilará y desplegará automáticamente.

### 5. Acceder

```
https://conversor-pdf.onrender.com
```

**⚠️ Nota:** Render FREE se "duerme" después de 15 min sin uso. Primera carga puede tardar 30 segundos.

---

# 🐳 OPCIÓN 3: Docker + cualquier plataforma

Si quieres máxima portabilidad, usa Docker.

## Crear Dockerfile

```dockerfile
# Etapa 1: Build
FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Etapa 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/converson-web-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Crear .dockerignore

```
.gradle
build
.idea
*.log
uploads
converted
```

## Build y Deploy

```bash
# Build local
docker build -t conversor-pdf .

# Run local
docker run -p 8080:8080 conversor-pdf

# Deploy a Railway/Render/Fly.io
# (Detectan Dockerfile automáticamente)
```

---

# ☁️ OPCIÓN 4: Fly.io (Para avanzados)

**✅ Ventajas:**
- 3 VMs gratis
- Deploy global (edge locations)
- Muy rápido

## Pasos rápidos:

```bash
# Instalar Fly CLI
iwr https://fly.io/install.ps1 -useb | iex

# Login
flyctl auth login

# Inicializar
cd C:\Users\John\IdeaProjects\ConversonWeb
flyctl launch

# Deploy
flyctl deploy
```

---

# 📊 Comparativa Detallada

## Railway 🏆
- ✅ **Pros**: Más fácil, rápido, UI excelente
- ❌ **Contras**: 5GB límite (suficiente para esto)
- 💰 **Costo**: Gratis / $5/mes después

## Render ⭐
- ✅ **Pros**: 750h gratis, auto-deploy
- ❌ **Contras**: Se duerme después de 15 min inactividad
- 💰 **Costo**: Gratis / $7/mes para siempre activo

## Fly.io
- ✅ **Pros**: 3 VMs, muy rápido, global
- ❌ **Contras**: Curva de aprendizaje
- 💰 **Costo**: Gratis / Pay as you go

## Heroku
- ❌ **YA NO ES GRATIS** (desde Nov 2022)
- 💰 **Costo**: $5-7/mes mínimo

---

# 🎯 Mi Recomendación

## Para ti (uso personal/familiar):
👉 **Railway** - La más fácil y rápida

## Si planeas compartir públicamente:
👉 **Render** - Más escalable, límites generosos

## Si quieres aprender Docker:
👉 **Fly.io con Docker** - Mejor experiencia técnica

---

# 🚀 Inicio Rápido: Railway (5 minutos)

```bash
# 1. Instalar CLI
npm i -g @railway/cli

# 2. Login
railway login

# 3. Ir a tu proyecto
cd C:\Users\John\IdeaProjects\ConversonWeb

# 4. Inicializar
railway init

# 5. Deploy!
railway up

# 6. Ver en navegador
railway open
```

**¡Ya está online!** 🎉

---

# 📝 Configuraciones Importantes

## application.properties para producción

Crea `application-prod.properties`:

```properties
# Server
server.port=${PORT:8080}
server.address=0.0.0.0

# File Upload (ajusta para la nube)
spring.servlet.multipart.max-file-size=25MB
spring.servlet.multipart.max-request-size=250MB

# Storage (usar directorios temporales)
app.storage.upload-dir=/tmp/uploads
app.storage.output-dir=/tmp/converted

# Logging para producción
logging.level.root=INFO
logging.level.com.example.conversonweb=INFO
```

## Variables de entorno

En Railway/Render/Fly, configurar:

```bash
SPRING_PROFILES_ACTIVE=prod
PORT=8080  # Railway/Render lo asignan automáticamente
```

---

# 🔒 Seguridad en Producción

## Lo que está OK:
✅ Límites de tamaño de archivo
✅ Validación de tipos de archivo
✅ CORS configurado

## Mejoras opcionales:

### 1. Rate Limiting

En `pom.xml` o `build.gradle`:
```groovy
implementation 'com.bucket4j:bucket4j-core:8.1.0'
```

### 2. Autenticación básica (opcional)

```properties
spring.security.user.name=admin
spring.security.user.password=tu-password-seguro
```

### 3. HTTPS

Railway/Render/Fly ya incluyen SSL gratis ✅

---

# 📊 Monitoreo

## Railway
- Dashboard con métricas en tiempo real
- Logs en vivo
- Uso de CPU/RAM

## Render
- Dashboard similar
- Build logs
- Deploy history

## Ambos tienen:
- ✅ Health checks automáticos
- ✅ Auto-restart si falla
- ✅ Rollback a versión anterior

---

# 💡 Consejos Finales

1. **Empieza con Railway** - Es gratis y muy fácil
2. **Sube a GitHub** - Para auto-deploy en el futuro
3. **Comparte la URL** - Funciona en cualquier dispositivo
4. **Monitorea recursos** - Railway muestra uso en dashboard
5. **Haz backup** del código regularmente

---

# 🎉 Resultado Final

Después del deploy tendrás:

```
✅ URL pública: https://tu-app.up.railway.app
✅ Acceso desde cualquier lugar
✅ HTTPS gratis y automático
✅ Auto-scaling
✅ 99.9% uptime
✅ Sin configurar servidores
```

**Cualquier persona con la URL puede usar tu conversor PDF!** 🚀

---

¿Quieres que te ayude con el deploy paso a paso en Railway? 🏆
