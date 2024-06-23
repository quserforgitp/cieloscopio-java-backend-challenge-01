import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {

    // 1.-- COMPROBACIONES antes de iniciar el programa
    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");
    if (!apiKeyExists(apiKey)) {
      System.err.println("No se encontró la API key. Asegúrate de definir la variable de entorno OPENWEATHERMAP_API_KEY.");
      return;
    }

    // 2.-- DEFINICION DE NOMBRES DE CIUDADES HARDCODEADOS
    ArrayList<String> cityNames = new ArrayList<>();
    cityNames.add("Morelia");
    cityNames.add("Caracas");
    cityNames.add("Buenos Aires");
    cityNames.add("Brasilia");
    cityNames.add("Quito");

    // 3.-- VARIABLES/OBJS de la app
    Scanner input = new Scanner(System.in);
    String cityName = "";
    String onlyNamesRgx = "[a-zA-Z\\s]+";
    int opt = 0;
    // 4.-- BUCLE APP
    while(opt < 1 || opt > 7) {
      // 5.-- MOSTRAR MENU
      showMenu();
      // 6.-- OBTENER OPCION INGRESADA POR teclado
      try {
        opt = input.nextInt();
        input.nextLine();// Limpriar el buffer del scanner
        // 7.-- TRAER DATOS DE LA API
        if (opt < 1 || opt > 7) {// numero fuera de rango
          System.err.println("El número introducido está fuera del rango permitido!!");
          continue;
        }
      } catch (InputMismatchException e) {// se introdujo un numero o caracter extranio
        System.err.println("Entrada no válida. Por favor, introduzca un número.");
        input.next(); // Limpiar el buffer del scanner
        continue;
      }
      // 7.1.--- Se selecciono, introducir manualmente el nombre de la ciudad
      if (opt == 6) {
        // While pedir nombre de ciudad valido
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
      } else if(opt == 7) {// se selecciono SAlir de la app
        System.out.println("Usted escogio salir, fin del programa...");
        return;
      } else {// se selecciono un nombre de ciudad hardcodeado
        cityName = cityNames.get(opt - 1);
      }

      // 8.-- FORMATEAR EL NOMBRE DE CIUDAD VALIDO PARA LA REQUEST
      cityName = capitalizePhrase(cityName);
      String cityNameFormatted = URLEncoder.encode(cityName, StandardCharsets.UTF_8);



      // 9.-- REQUEST PARA TRAER LAS COORDENADAS DE LA CIUDAD
      //=====================================================
      String URLgeoLocReq = "http://api.openweathermap.org/geo/1.0/direct?q="+
              cityNameFormatted+
              "&limit=1&appid="+apiKey;

      String geoLocData =  fetchCityCoordinates(URLgeoLocReq);
      //=======================================================================================================

      // 10.-- EXTRAER COORDENADAS DE LA RESPUESTA (API GEO LOC)

      String[] geoLocInfo = extractGeoLocData(geoLocData);//[lat,lon,nameInSpanish]

      // obtener el objeto tipo json del array tipo json
      if (geoLocInfo.length == 0) {
        System.err.println("No se encontró información con el nombre de ciudad introducido: " + cityName);
        opt = 0;
        cityName = "";
        continue;
      }
      String lat = geoLocInfo[0],
              lon = geoLocInfo[1],
              nameInSpanish = geoLocInfo[2];

      // 11.-- REQUEST PARA OBTENER DATOS DEL CLIMA (API WEATHER)
      HttpRequest weatherRequest = HttpRequest.newBuilder()
              .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat="+lat+
                      "&lon="+lon+
                      "&appid="+apiKey+
                      "&lang=sp&units=metric"))
              .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

      String weatherData = weatherResponse.body();

      // 13.-- MOSTRAR INFORMACION DEL CLIMA AL USUARIO
      WeatherInfo weatherInfo = extractWeatherData(weatherData, nameInSpanish);

      weatherInfo.showInfo();

      // VARIABLES DE CONTROL DEL FLUJO RESETEADAS
      System.out.println("\n");
      opt = 0;
      cityName = "";
    }
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
    System.out.println("ingrese una opción");
    System.out.printf("> ");

  }
  public static String capitalizePhrase (String phrase) {

    String mutablePhrase = phrase.substring(0);

    String splittedPhrase[] = mutablePhrase.split(" ");
    String reJoinedPhrase = "";

    for (String word : splittedPhrase) {
      word = word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
      reJoinedPhrase += word + " ";
    }
    reJoinedPhrase = reJoinedPhrase.substring(0,reJoinedPhrase.length());

    return reJoinedPhrase;
  }
  public static boolean apiKeyExists (String apiKeyString) {
    return !(apiKeyString == null || apiKeyString.isEmpty());
  }
  public static String[] extractGeoLocData (String geolocBodyResponse) {
    // parsear a jsonElement (manipulable, se puede convertir a tipos de datos json)
    JsonElement jsonElement = JsonParser.parseString(geolocBodyResponse);

    // parsear a array tipo json
    JsonArray jsonArray = jsonElement.getAsJsonArray();

    // si no hay informacion (no existe esa ciudad)
    if (jsonArray.isEmpty())
      return new String[]{};

    JsonObject jsonObj = jsonArray.get(0).getAsJsonObject();

    // Extraer datos del objeto tipo json como String's
    String lat = jsonObj.get("lat").getAsString();
    String lon = jsonObj.get("lon").getAsString();

    String nameInSpanish = jsonObj.get("name").getAsString();
    if (jsonObj.has("local_names"))
      nameInSpanish = jsonObj.get("local_names").getAsJsonObject().get("es").getAsString();

    return new String[]{lat,lon,nameInSpanish};
  }
  public static WeatherInfo extractWeatherData(String weatherBodyResponse, String cityName) {

    JsonObject weatherJsonObj = JsonParser
            .parseString(weatherBodyResponse)
            .getAsJsonObject();

    JsonObject condClimJsonObj = weatherJsonObj
            .get("weather")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject();

    // EXTRAER CONDICIONES DEL CLIMA
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

    // EXTRAER TEMPERATURAS
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

    return new WeatherInfo(cityName,condClimMain,condClimDesc,volumenLluvia,temp,minTemp,maxTemp);
  }
  public static String fetchCityCoordinates (String apiURL) throws IOException, InterruptedException {

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest geoLocRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiURL))
            .build();

    HttpResponse<String> geoLocResponse = client.send(geoLocRequest, HttpResponse.BodyHandlers.ofString());
    return geoLocResponse.body();
  }
}

