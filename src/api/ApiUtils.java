// archivo: api/ApiUtils.java
package api;

import com.google.gson.*;
import models.GeoLocApiInfo;
import models.WeatherApiInfo;

public class ApiUtils {

  public static GeoLocApiInfo extractGeoLocData(String geolocBodyResponse) {
    JsonElement jsonElement = JsonParser.parseString(geolocBodyResponse);
    JsonArray jsonArray = jsonElement.getAsJsonArray();

    if (jsonArray.isEmpty())
      return null;

    JsonObject jsonObj = jsonArray.get(0).getAsJsonObject();

    String lat = jsonObj.get("lat").getAsString();
    String lon = jsonObj.get("lon").getAsString();
    String nameInSpanish = jsonObj.get("name").getAsString();

    if (jsonObj.has("local_names") && jsonObj.get("local_names").getAsJsonObject().has("es"))
      nameInSpanish = jsonObj.get("local_names").getAsJsonObject().get("es").getAsString();

    return new GeoLocApiInfo(lat, lon, nameInSpanish);
  }

  public static WeatherApiInfo extractWeatherData(String weatherBodyResponse, GeoLocApiInfo geoLocApiInfoObj) {
    JsonObject weatherJsonObj = JsonParser.parseString(weatherBodyResponse).getAsJsonObject();
    JsonObject condClimJsonObj = weatherJsonObj.get("weather").getAsJsonArray().get(0).getAsJsonObject();

    String condClimMain = condClimJsonObj.get("main").getAsString();
    String condClimDesc = condClimJsonObj.get("description").getAsString();
    String volumenLluvia = "...";

    if (weatherJsonObj.has("rain")) {
      JsonObject lluviaJsonObj = weatherJsonObj.get("rain").getAsJsonObject();
      volumenLluvia = lluviaJsonObj.has("1h") ? lluviaJsonObj.get("1h").getAsString() : lluviaJsonObj.get("3h").getAsString();
      volumenLluvia += " Lts por metro cúbico";
    }

    JsonObject tempJsonObj = weatherJsonObj.get("main").getAsJsonObject();
    String temp = tempJsonObj.get("temp").getAsString();
    String minTemp = tempJsonObj.get("temp_min").getAsString();
    String maxTemp = tempJsonObj.get("temp_max").getAsString();

    return new WeatherApiInfo(geoLocApiInfoObj.getCityNameInSpanish(), condClimMain, condClimDesc, volumenLluvia, temp, minTemp, maxTemp);
  }
}
