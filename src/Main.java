import exceptions.NameOfCityIsBlankException;
import exceptions.NotValidNameOfCityException;

import menu.Menu;
import models.GeoLocApiInfo;
import models.WeatherApiInfo;
import api.Api;
import api.ApiUtils;

import static utils.Utils.*;

import java.io.IOException;

import java.util.InputMismatchException;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {

    checkPrerequisitesOrAbort();
    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");

    String cityName = "";
    int opt = 0;

    while(optionIsOutOfRange(opt)) {
      try {
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
      } catch (InputMismatchException e) {
        printErrorInConsole("Entrada no válida. Por favor, introduzca un número.");
        continue;
      } catch (IllegalArgumentException e) {
        printErrorInConsole("El número introducido está fuera del rango permitido!!");
        continue;
      }

      String cityNameFormatted = formatTextAndURLencode(cityName);

      Api<GeoLocApiInfo> geoLocApi = new Api<>(
              "http://api.openweathermap.org/geo/1.0/direct",
              "",
              "q=" + cityNameFormatted + "&limit=1",
              apiKey,
              ApiUtils::extractGeoLocData
      );
      geoLocApi.fetchAndExtractData();
      GeoLocApiInfo geoLocApiInfo = geoLocApi.getExtractedData();

      if (geoLocApiInfo == null) {
        System.err.println("No se encontró información con el nombre de ciudad introducido: " + cityName);
        opt = 0;
        cityName = "";
        continue;
      }

      Api<WeatherApiInfo> weatherApi = new Api<>(
              "https://api.openweathermap.org/data/2.5/weather",
              "",
              "lat=" + geoLocApiInfo.getLat() + "&lon=" + geoLocApiInfo.getLon() + "&lang=sp&units=metric",
              apiKey,
              response -> ApiUtils.extractWeatherData(response, geoLocApiInfo)
      );
      weatherApi.fetchAndExtractData();
      WeatherApiInfo weatherInfo = weatherApi.getExtractedData();

      weatherInfo.showInfo();

      System.out.println("\n");
      opt = 0;
      cityName = "";
    }
  }
}
