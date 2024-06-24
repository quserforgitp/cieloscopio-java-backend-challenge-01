import com.google.gson.*;
import exceptions.NameOfCityIsBlankException;
import exceptions.NotValidNameOfCityException;
import models.GeoLocApiInfo;
import models.WeatherApiInfo;

import static utils.Utils.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.InputMismatchException;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {

    // COMPROBACIONES antes de iniciar el programa
    checkPrerequisitesOrAbort();
    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");

    // VARIABLES/OBJS de la app
    String cityName = "";
    int opt = 0;

    // BUCLE APP
    while(optionIsOutOfRange(opt)) {
      try {
    // MOSTRAR MENU Y OBTENER OPCION INGRESADA POR TECLADO
        opt = Menu.showAndPromptUser();

        switch (opt) {
          case Menu.OP_INGRESAR_NOMBRE_CIUDAD:
            while (isNotValidCityName(cityName)) {
              try {
                cityName = Menu.promptForCityName();
              } catch (NameOfCityIsBlankException e) {
                printErrorInConsole("Es necesario que se ingrese el nombre de una ciudad...");
              } catch (NotValidNameOfCityException e) {
                printErrorInConsole("No se aceptan caracteres extraños, ni numeros...");
              }
            }
            break;
          default:
            cityName = getCityNameBasedOnOpt(opt);
        }
      } catch (InputMismatchException e) {// se introdujo un numero o caracter extranio
        printErrorInConsole("Entrada no válida. Por favor, introduzca un número.");
        continue;
      } catch (IllegalArgumentException e) {
        printErrorInConsole("El número introducido está fuera del rango permitido!!");
        continue;
      }

      // FORMATEAR EL NOMBRE DE CIUDAD VALIDO PARA LA REQUEST
      String cityNameFormatted = formatTextAndURLencode(cityName);

    // TRAER DATOS DE LA API

    // REQUEST PARA TRAER LAS COORDENADAS DE LA CIUDAD
      String URLgeoLocReq = "http://api.openweathermap.org/geo/1.0/direct?q="+
              cityNameFormatted+
              "&limit=1&appid="+apiKey;

      GeoLocApiInfo geoLocApiInfo = getGeoLocData(URLgeoLocReq);
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

      WeatherApiInfo weatherInfo = getWeatherData(weatherURL,geoLocApiInfo);

      // MOSTRAR INFORMACION DEL CLIMA AL USUARIO
      weatherInfo.showInfo();

      // VARIABLES DE CONTROL DE FLUJO RESETEADAS
      System.out.println("\n");
      opt = 0;
      cityName = "";
    }
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
  } // retrieve data
  public static WeatherApiInfo extractWeatherData(String weatherBodyResponse, GeoLocApiInfo geoLocApiInfoObj) {

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

    return new WeatherApiInfo(geoLocApiInfoObj.getCityNameInSpanish(),condClimMain,condClimDesc,volumenLluvia,temp,minTemp,maxTemp);
  } // retrieve data
  public static String fetchCityCoordinates (String apiURL) throws IOException, InterruptedException {

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest geoLocRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiURL))
            .build();

    HttpResponse<String> geoLocResponse = client.send(geoLocRequest, HttpResponse.BodyHandlers.ofString());
    return geoLocResponse.body();
  } // retrieve data
  public static String fetchWeatherConditions (String apiURL) throws IOException, InterruptedException {
    HttpRequest weatherRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiURL))
            .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
    return weatherResponse.body();
  } // retrieve data
  public static GeoLocApiInfo getGeoLocData(String apiURL) throws IOException, InterruptedException {
    return extractGeoLocData(fetchCityCoordinates(apiURL));
  }
  public static WeatherApiInfo getWeatherData(String apiURL, GeoLocApiInfo geoLocData) throws IOException, InterruptedException {
    return extractWeatherData(fetchWeatherConditions(apiURL),geoLocData);
  }
}

