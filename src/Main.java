import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {

    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");

    // INPUT del nombre de la ciudad=========================================
    Scanner input = new Scanner(System.in);

    String cityName = "";
    String onlyNamesRgx = "[a-zA-Z\\s]+";

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


    //============== GEO LOC REQ (get LAT and LONG by city name)
    String URLgeoLocReq = "http://api.openweathermap.org/geo/1.0/direct?q="+
            cityNameFormatted+
            "&limit=1&appid="+apiKey;
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest geoLocRequest = HttpRequest.newBuilder()
            .uri(URI.create(URLgeoLocReq))
            .build();

    HttpResponse<String> geoLocResponse = client.send(geoLocRequest, HttpResponse.BodyHandlers.ofString());

    String geoLocData = geoLocResponse.body();

    //System.out.println("\n======================GEO LOC DATA LAT & LONG==========================\n" + geoLocData + "\n==============================================");

    //==========EXTRAER DATOS DE LA RESPUESTA
    // parsear a jsonElement (manipulable, se puede convertir a tipos de datos json)
    JsonElement jsonElement = JsonParser.parseString(geoLocData);
    //System.out.println("jsonElementGeoLoc======>"+ jsonElement);

    // parsear a array tipo json
    JsonArray jsonArray = jsonElement.getAsJsonArray();

    // obtener el objeto tipo json del array tipo json
    JsonObject jsonObj = jsonArray.get(0).getAsJsonObject();

    //System.out.println("jsonObject======================>"+jsonObj);

    // Extraer datos del objeto tipo json como String's
    String lat = jsonObj.get("lat").getAsString();
    String lon = jsonObj.get("lon").getAsString();
    String nameInSpanish = jsonObj.get("local_names").getAsJsonObject().get("es").getAsString();
    //System.out.println("NAME IN SPANISH ========>======>"+nameInSpanish);
    //System.out.println("===============\nlat = "+lat+"\n"+"lon = "+lon+"\n=================");

    //============== WEATHER REQ (get WEATHER by LAT and LONG)
    HttpRequest weatherRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat="+lat+
                    "&lon="+lon+
                    "&appid="+apiKey+
                    "&lang=sp&units=metric"))
            .build();

    HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

    String weatherData = weatherResponse.body();

    //System.out.println("\n=========================WEATHER DATA ==========================\n" + weatherData + "\n========================================");

    System.out.printf("=========== DATOS DEL CLIMA ===========\n" +
            "Nombre de la ciudad:%s\n" +
            "Latitud:%s\n" +
            "Longitud:%s\n=====================================",nameInSpanish,lat,lon);

  }
}