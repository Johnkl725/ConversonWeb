# 🌐 Guía: Compartir la Aplicación en Red Local (LAN)

Tu aplicación Spring Boot está lista para ser usada en tu red local. Sigue estos pasos:

---

## ✅ Paso 1: Verificar Configuración

La configuración ya está lista en `application.properties`:

```properties
server.address=0.0.0.0  # Acepta conexiones de cualquier IP
server.port=8080        # Puerto de la aplicación
```

✅ **Ya configurado correctamente**

---

## 📋 Paso 2: Obtener tu IP Local

Necesitas saber la IP de tu PC para que otras computadoras se conecten.

### En Windows (Tu PC):

```bash
# Abrir PowerShell o CMD y ejecutar:
ipconfig

# Buscar "Adaptador de LAN inalámbrica Wi-Fi" o "Adaptador de Ethernet"
# Encontrar la línea que dice:
# Dirección IPv4. . . . . . . . . : 192.168.X.X
```

**Ejemplo de salida:**
```
Adaptador de LAN inalámbrica Wi-Fi:
   Dirección IPv4. . . . . . . . . : 192.168.1.100
   Máscara de subred . . . . . . . : 255.255.255.0
   Puerta de enlace predeterminada: 192.168.1.1
```

📝 **Anota esta IP:** Por ejemplo `192.168.1.100`

---

## 🔥 Paso 3: Configurar Firewall de Windows

Debes permitir que el puerto 8080 acepte conexiones.

### Opción A: Desde Interfaz Gráfica (Recomendado)

1. **Abrir Firewall**:
   - Presiona `Windows + R`
   - Escribe: `wf.msc`
   - Presiona Enter

2. **Crear Regla de Entrada**:
   - Click en "Reglas de entrada" (panel izquierdo)
   - Click en "Nueva regla..." (panel derecho)

3. **Configurar Regla**:
   - Tipo de regla: **Puerto** → Siguiente
   - Protocolo: **TCP**
   - Puerto local específico: **8080** → Siguiente
   - Acción: **Permitir la conexión** → Siguiente
   - Perfiles: Marcar **Privado** y **Público** → Siguiente
   - Nombre: `ConversorPDF-8080` → Finalizar

### Opción B: Desde PowerShell (Más Rápido)

Abre PowerShell **como Administrador** y ejecuta:

```powershell
New-NetFirewallRule -DisplayName "ConversorPDF-8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow
```

✅ **Verás:** "Regla creada correctamente"

---

## 🚀 Paso 4: Ejecutar la Aplicación

Inicia tu aplicación Spring Boot:

1. **Desde IntelliJ**:
   - Click derecho en `ConversonWebApplication.java`
   - Run 'ConversonWebApplication'

2. **O desde terminal**:
   ```bash
   .\gradlew.bat bootRun
   ```

3. **Esperar a ver**:
   ```
   ========================================
     Conversor de Archivos a PDF - Web
     Aplicación iniciada exitosamente
     URL: http://localhost:8080
   ========================================
   ```

---

## 💻 Paso 5: Acceder desde Otras PCs

Desde cualquier computadora en tu red local:

### URL de Acceso

Reemplaza `192.168.1.100` con TU IP:

```
http://192.168.1.100:8080
```

### En cada PC/dispositivo:

1. **Abrir navegador** (Chrome, Firefox, Edge, Safari)
2. **Escribir la URL** con tu IP
3. **¡Listo!** Verás la aplicación funcionando

---

## 📱 Paso 6: Acceso desde Móviles

También funciona en tablets y smartphones conectados a la misma WiFi:

1. Abrir navegador en el móvil
2. Ir a: `http://192.168.1.100:8080`
3. Usar la aplicación normalmente

---

## 🔍 Verificación y Troubleshooting

### ✅ Verificar que funciona desde tu PC

Primero asegúrate que funcione localmente:

```
http://localhost:8080
http://127.0.0.1:8080
```

### ✅ Verificar con tu IP local en tu PC

```
http://192.168.1.100:8080  (usa TU IP)
```

Si esto funciona, otras PCs también podrán acceder.

---

## ❌ Problemas Comunes

### 1. No carga la página desde otra PC

**Posibles causas:**

#### A) Firewall bloqueando
Verifica la regla del firewall:
```powershell
Get-NetFirewallRule -DisplayName "ConversorPDF-8080"
```

Si no existe, créala (Paso 3).

#### B) IP incorrecta
Verifica tu IP actual:
```bash
ipconfig
```
Las IPs pueden cambiar si tu router usa DHCP.

#### C) No están en la misma red
Ambas PCs deben estar conectadas al **mismo router/WiFi**.

### 2. La conexión se rechaza

Asegúrate que:
- ✅ La aplicación esté corriendo
- ✅ El puerto sea 8080
- ✅ La IP sea la correcta

### 3. WebSocket no funciona

Si el progreso no se actualiza:
- Verifica que el firewall permita conexiones WebSocket
- Asegúrate de usar `http://` (no `https://`)

---

## 🎯 Script de Verificación Rápida

Guarda esto como `verificar-red.bat`:

```batch
@echo off
echo ==============================================
echo  Verificacion de Red - Conversor PDF
echo ==============================================
echo.
echo TU IP LOCAL:
ipconfig | findstr /i "IPv4"
echo.
echo PUERTOS ESCUCHANDO (debe aparecer 8080):
netstat -an | findstr :8080
echo.
echo REGLA DE FIREWALL:
netsh advfirewall firewall show rule name="ConversorPDF-8080"
echo.
pause
```

Ejecuta este script para verificar todo.

---

## 📊 Resumen Visual

```
┌─────────────────────────────────────────┐
│   TU PC (192.168.1.100:8080)           │
│   ↑ Aplicación corriendo               │
│   ↑ Firewall: Puerto 8080 abierto      │
└─────────────────┬───────────────────────┘
                  │
         Red Local WiFi/Ethernet
                  │
      ┌───────────┴───────────┐
      │                       │
┌─────▼─────┐          ┌──────▼────┐
│  PC #2    │          │  PC #3    │
│192.168.1.X│          │192.168.1.Y│
└───────────┘          └───────────┘

Todas acceden a:
http://192.168.1.100:8080
```

---

## 🔒 Seguridad en Red Local

### Lo que SÍ es seguro:
✅ Usar solo en tu red local (WiFi de casa)
✅ Confiar en los usuarios de tu red

### Lo que NO debes hacer:
❌ NO expongas el puerto 8080 a internet público
❌ NO uses sin autenticación en redes públicas
❌ NO configures forwarding del router sin protección

---

## 💡 Consejos Adicionales

### 1. IP Estática (Opcional)

Para que la IP no cambie:

**En Windows:**
1. Panel de Control → Redes
2. Adaptador → Propiedades → IPv4
3. Configurar IP manualmente (ej: 192.168.1.100)

**O en el Router:**
1. Acceder al router (192.168.1.1)
2. DHCP → Reservación de IP
3. Asignar IP fija a tu PC (por MAC address)

### 2. Crear Acceso Directo

En otras PCs, crea un acceso directo en el escritorio:
- Nombre: `Conversor PDF`
- URL: `http://192.168.1.100:8080`

### 3. Mantener PC Encendida

La aplicación solo está disponible cuando:
- ✅ Tu PC está encendida
- ✅ La aplicación está corriendo
- ✅ Conectada a la red

---

## 📝 Checklist Final

Antes de compartir con otras PCs:

- [ ] Obtuve mi IP local (ej: 192.168.1.100)
- [ ] Configuré el firewall (puerto 8080 abierto)
- [ ] La aplicación está corriendo
- [ ] Probé desde mi PC: `http://MI-IP:8080`
- [ ] Comprobé que funciona
- [ ] Compartí la URL con otros: `http://192.168.1.100:8080`

---

## 🎉 ¡Listo!

Ahora cualquier persona en tu red local puede:
1. Abrir navegador
2. Ir a `http://TU-IP:8080`
3. Usar el conversor de PDF

**Notas:**
- No necesitan instalar nada
- Funciona en Windows, Mac, Linux, móviles
- Todo se procesa en tu PC (la que corre el servidor)
- Los archivos se suben a tu PC, se convierten, y se descargan

---

¿Necesitas ayuda específica con algún paso? ¡Pregunta! 🚀
