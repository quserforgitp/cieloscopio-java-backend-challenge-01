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

    // COMPROBACIONES antes de iniciar el programa
    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");
    if (!apiKeyExists(apiKey)) {
      System.err.println("No se encontró la API key. Asegúrate de definir la variable de entorno OPENWEATHERMAP_API_KEY.");
      return;
    }

    // DEFINICION DE NOMBRES DE CIUDADES HARDCODEADOS
    ArrayList<String> cityNames = new ArrayList<>();
    cityNames.add("Morelia");
    cityNames.add("Caracas");
    cityNames.add("Buenos Aires");
    cityNames.add("Brasilia");
    cityNames.add("Quito");

    // VARIABLES/OBJS de la app
    Scanner input = new Scanner(System.in);
    String cityName = "";
    String onlyNamesRgx = "[a-zA-Z\\s]+";
    int opt = 0;

    // BUCLE APP
    while(optionIsOutOfRange(opt)) {
    // MOSTRAR MENU
      showMenu();
    // OBTENER OPCION INGRESADA POR teclado
      try {
        opt = input.nextInt();
        input.nextLine();// Limpriar el buffer del scanner
        if (optionIsOutOfRange(opt)) {// numero fuera de rango
          System.err.println("El número introducido está fuera del rango permitido!!");
          continue;
        }
      } catch (InputMismatchException e) {// se introdujo un numero o caracter extranio
        System.err.println("Entrada no válida. Por favor, introduzca un número.");
        input.next(); // Limpiar el buffer del scanner
        continue;
      }
      // Se selecciono, introducir manualmente el nombre de la ciudad
      if (opt == 6) {
        // While pedir nombre de ciudad valido
        while (isNotValidCityName(cityName)) {
          promptUser("Ingrese el nombre de la ciudad","> ");
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

      //==============================================================================================================
      // FORMATEAR EL NOMBRE DE CIUDAD VALIDO PARA LA REQUEST
      String cityNameFormatted = formatTextAndURLencode(cityName);
      //==============================================================================================================

    // TRAER DATOS DE LA API

    // REQUEST PARA TRAER LAS COORDENADAS DE LA CIUDAD
      String URLgeoLocReq = "http://api.openweathermap.org/geo/1.0/direct?q="+
              cityNameFormatted+
              "&limit=1&appid="+apiKey;

      String geoLocData = fetchCityCoordinates(URLgeoLocReq);// => response.body()

    // EXTRAER DATOS DE COORDENADAS Y NOMBRE EN ESPANIOL DE LA CIUDAD (API GEO LOC)

      GeoLocApiInfo geoLocApiInfo = extractGeoLocData(geoLocData);// => {lat,lon,cityNameInSpanish}
      // si la ciudad no existe
      if (geoLocApiInfo == null) {
        System.err.println("No se encontró información con el nombre de ciudad introducido: " + cityName);
        opt = 0;
        cityName = "";
        continue;
      }

    // REQUEST PARA OBTENER DATOS DEL CLIMA (API WEATHER)
      String weatherURL = "https://api.openweathermap.org/data/2.5/weather?lat="+geoLocApiInfo.getLat()+
              "&lon="+geoLocApiInfo.getLon()+
              "&appid="+apiKey+
              "&lang=sp&units=metric";

      String weatherData = fetchWeatherConditions(weatherURL);// => response.body()

      WeatherInfo weatherInfo = extractWeatherData(weatherData, geoLocApiInfo);
                  // => {nameInSpanish,condClimMain,condClimDesc,volumenLluvia,temp,minTemp,maxTemp}

      // MOSTRAR INFORMACION DEL CLIMA AL USUARIO
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
    promptUser("Ingrese una opción", "> ");

  }
  public static String capitalizePhrase (String phrase) {

    phrase = phrase.trim();
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
  public static GeoLocApiInfo extractGeoLocData (String geolocBodyResponse) {

    // parsear a jsonElement (manipulable, se puede convertir a tipos de datos json)
    JsonElement jsonElement = JsonParser.parseString(geolocBodyResponse);

    // parsear a array tipo json
    JsonArray jsonArray = jsonElement.getAsJsonArray();

    // si no hay informacion (no existe esa ciudad)
    if (jsonArray.isEmpty())
      return null;

    JsonObject jsonObj = jsonArray.get(0).getAsJsonObject();

    // Extraer datos del objeto tipo json como String's
    String lat = jsonObj.get("lat").getAsString();
    String lon = jsonObj.get("lon").getAsString();

    String nameInSpanish = jsonObj.get("name").getAsString();
    if (jsonObj.has("local_names"))
      if(jsonObj.get("local_names").getAsJsonObject().has("es"))
        nameInSpanish = jsonObj.get("local_names").getAsJsonObject().get("es").getAsString();

    return new GeoLocApiInfo(lat,lon,nameInSpanish);
  }
  public static WeatherInfo extractWeatherData(String weatherBodyResponse, GeoLocApiInfo geoLocApiInfoObj) {

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

    return new WeatherInfo(geoLocApiInfoObj.getCityNameInSpanish(),condClimMain,condClimDesc,volumenLluvia,temp,minTemp,maxTemp);
  }
  public static String fetchCityCoordinates (String apiURL) throws IOException, InterruptedException {

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest geoLocRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiURL))
            .build();

    HttpResponse<String> geoLocResponse = client.send(geoLocRequest, HttpResponse.BodyHandlers.ofString());
    return geoLocResponse.body();
  }
  public static String fetchWeatherConditions (String apiURL) throws IOException, InterruptedException {
    HttpRequest weatherRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiURL))
            .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
    return weatherResponse.body();
  }
  public static String formatTextAndURLencode (String text) {
    //==============================================================================================================
    // FORMATEAR EL NOMBRE DE CIUDAD VALIDO PARA LA REQUEST
    text = capitalizePhrase(text);
    return URLEncoder.encode(text, StandardCharsets.UTF_8);
    //==============================================================================================================
  }
  public static boolean optionIsOutOfRange (int selectedOption) {
    return selectedOption < 1 || selectedOption > 7;
  }
  public static boolean isNotValidCityName(String cityName) {
    String onlyNamesRgx = "[a-zA-Z\\s]+";
    return cityName.isBlank() || !cityName.matches(onlyNamesRgx);
  }
  public static void promptUser(String msg, String promptChars) {
    System.out.println(msg);
    System.out.printf("%s",promptChars);
  }
}

