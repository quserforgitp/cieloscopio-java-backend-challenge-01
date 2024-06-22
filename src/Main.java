import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {

    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");
    if (apiKey == null || apiKey.isEmpty()) {
      System.err.println("No se encontró la API key. Asegúrate de definir la variable de entorno OPENWEATHERMAP_API_KEY.");
      return;
    }

    // INPUT del nombre de la ciudad=========================================
    Scanner input = new Scanner(System.in);

    String cityName = "";
    String onlyNamesRgx = "[a-zA-Z\\s]+";

    // WHILE DE LA APP

      //1. mostrar menu (5 opciones => cd mex, morelia,caracas,buenos aires,brasilia,quito)
      showMenu();

      // pedir numero de opcion (while)

      // pedir nombre de ciudad (validar con while)

      // mostrar informacion

      // goto 1.

    // WHILE PARA INPUT DEL NOMBRE DE LA CIUDAD
    while (cityName.isBlank() || !cityName.matches(onlyNamesRgx)) {
      System.out.println("Ingrese el nombre de la ciudad");
      System.out.printf("> ");
      cityName = input.nextLine();
      if (cityName.isBlank()) {
        System.err.println("Es necesario que se ingrese el nombre de una ciudad...");
      } else if (!cityName.matches(onlyNamesRgx)) {
        System.err.println("No se aceptan caracteres extraños, ni numeros...");
      }
    }

    String cityNameFormatted = cityName.substring(0,1).toUpperCase() + cityName.substring(1).toLowerCase();

    //FIN - INPUT del nombre de la ciudad=========================================


    //============== GEO LOC REQ (get LAT and LONG by city name)=======================================================
    String URLgeoLocReq = "http://api.openweathermap.org/geo/1.0/direct?q="+
            cityNameFormatted+
            "&limit=1&appid="+apiKey;
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest geoLocRequest = HttpRequest.newBuilder()
            .uri(URI.create(URLgeoLocReq))
            .build();

    HttpResponse<String> geoLocResponse = client.send(geoLocRequest, HttpResponse.BodyHandlers.ofString());

    String geoLocData = geoLocResponse.body();

    //==========EXTRAER DATOS DE LA RESPUESTA
    // parsear a jsonElement (manipulable, se puede convertir a tipos de datos json)
    JsonElement jsonElement = JsonParser.parseString(geoLocData);

    // parsear a array tipo json
    JsonArray jsonArray = jsonElement.getAsJsonArray();

    // obtener el objeto tipo json del array tipo json
    if (jsonArray.isEmpty()) {
      System.out.println("No se encontró información con el nombre de ciudad introducido: " + cityName);
      System.err.println("abortando...");
      return;
    }

    JsonObject jsonObj = jsonArray.get(0).getAsJsonObject();

    System.out.println("objeto json geo loc====================" + jsonObj);

    // Extraer datos del objeto tipo json como String's
    String lat = jsonObj.get("lat").getAsString();
    String lon = jsonObj.get("lon").getAsString();

    String nameInSpanish = jsonObj.get("name").getAsString();;
    if (jsonObj.has("local_names"))
      nameInSpanish = jsonObj.get("local_names").getAsJsonObject().get("es").getAsString();



    //============== WEATHER REQ (get WEATHER by LAT and LONG)=========================================================
    HttpRequest weatherRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat="+lat+
                    "&lon="+lon+
                    "&appid="+apiKey+
                    "&lang=sp&units=metric"))
            .build();

    HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

    String weatherData = weatherResponse.body();

    JsonObject weatherJsonObj = JsonParser
            .parseString(weatherData)
            .getAsJsonObject();

    //System.out.println("\n=========================WEATHER DATA ==========================\n" + weatherJsonObj + "\n========================================");

    //CONDICION CLIMATICA
    JsonObject condClimJsonObj = weatherJsonObj
            .get("weather")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject();

    String condClimMain = condClimJsonObj
            .get("main")
            .getAsString();
    String condClimDesc = condClimJsonObj
            .get("description")
            .getAsString();

    String volumenLluvia = "...";

    // si hay informacion de la lluvia, entonces setear a variable
    if (weatherJsonObj.has("rain")) {
      JsonObject lluviaJsonObj = weatherJsonObj.get("rain").getAsJsonObject();

      volumenLluvia = lluviaJsonObj.has("1h") ?
              lluviaJsonObj.get("1h").getAsString() :
              lluviaJsonObj.get("3h").getAsString();
      volumenLluvia += " Lts por metro cúbico";
    }

    // TEMPERATURAS
    JsonObject tempJsonObj = weatherJsonObj
            .get("main")
            .getAsJsonObject();
    String temp = tempJsonObj
            .get("temp")
            .getAsString();
    String minTemp = tempJsonObj
            .get("temp_min")
                    .getAsString();
    String maxTemp = tempJsonObj
            .get("temp_max")
                    .getAsString();

    System.out.printf("""
            =============== RETRIEVED INFORMATION ===============
            ciudad: %s
            condicion: %s
            descripción: %s
            precipitación: %s
            temperatura actual: %s
            temperatura maxima: %s
            temperatura minima: %s""",nameInSpanish,condClimMain,condClimDesc,volumenLluvia,temp,maxTemp,minTemp);
  }
  public static void showMenu () {
    System.out.println("""
            ========================= MENÚ =========================
            | 1. Morelia (mx)                                     |
            | 2. Caracas (vnz)                                    |
            | 3. Buenos Aires (arg)                               |
            | 4. Brasilia (br)                                    |
            | 5. Quito (ec)                                       |
            | 6. Introducir nombre manualmente                    |
            | 7. Salir                                            |
            ========================================================
            """);

  }
}