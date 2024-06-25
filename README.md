# 🌤️ Cieloscopio APP 🌍

<p align="center">
  <img src="https://github.com/quserforgitp/cieloscopio-java-backend-challenge-01/assets/67709631/40a1ee01-e8d5-4a4b-9f89-92dc34dd3111" width=100% height=800 />
</p>

Este proyecto es una aplicación de consola en Java que permite obtener información meteorológica de varias ciudades utilizando la API de OpenWeatherMap.
<p align="center">
  <a href="https://github.com/quserforgitp/cieloscopio-java-backend-challenge-01/releases/tag/v1.0.2">
    <img src="https://img.shields.io/badge/STATUS-v1.0.2-green" width=20%>
  </a>
</p>

## 🚀  Requisitos

- Java 11 o superior
- API Key de OpenWeatherMap

## 🛠️ Configuración

1. Clona este repositorio en tu máquina local.
2. Configura la variable de entorno `OPENWEATHERMAP_API_KEY` con tu API Key de OpenWeatherMap.

## 🏃‍♂️ Uso

1. Descarga la <a href="https://github.com/quserforgitp/cieloscopio-java-backend-challenge-01/releases/download/v1.0.2/cieloscopio-java_v1.0.2.jar" target="_blank">ultima version de la app</a>
2. Ejecutala en tu linea de comandos

```sh
java -jar cieloscopio-java_v1.0.2.jar
```
## 📜 Menu de la aplicacion
```sh
========================= MENÚ =========================
| 1. Morelia (mx)                                     |
| 2. Caracas (vnz)                                    |
| 3. Buenos Aires (arg)                               |
| 4. Brasilia (br)                                    |
| 5. Quito (ec)                                       |
| 6. Introducir nombre manualmente                    |
| 7. Salir                                            |
========================================================
> |
```
- Selecciona una opción introduciendo el número correspondiente y presionando Enter.

## 🌟 Funcionalidades
- Mostrar el clima de ciudades predefinidas: Selecciona una ciudad del menú para obtener información meteorológica.
- Introducir manualmente el nombre de una ciudad: Selecciona la opción 6 y escribe el nombre de la ciudad deseada.
- Salir de la aplicación: Selecciona la opción 7 para salir del programa.

## 📦 Dependencias
- Gson para el parseo de JSON
- OpenWeatherMap API para obtener la información del clima

# 👤 Autor
Helios Barrera Hernández X Alura Latam challenge 01 bootcamp backend Java
