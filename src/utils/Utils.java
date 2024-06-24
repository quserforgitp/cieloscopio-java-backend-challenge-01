package utils;

import constants.City;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class Utils {
  public static final String RED = "\033[0;31m";    // Texto rojo
  public static final String RESET = "\033[0m";     // Restablecer color

  public static void printErrorInConsole(String msg) {
    System.out.println(RED + msg + RESET);
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
  public static void checkPrerequisitesOrAbort() {
    String apiKey = System.getenv("OPENWEATHERMAP_API_KEY");
    if (!apiKeyExists(apiKey)) {
      System.err.println("API key not found. Please set the environment variable OPENWEATHERMAP_API_KEY.");
      System.exit(1);  // Aborts the program
    }
  }
  public static String formatTextAndURLencode (String text) {
    //==============================================================================================================
    // FORMATEAR EL NOMBRE DE CIUDAD VALIDO PARA LA REQUEST
    text = capitalizePhrase(text);
    return URLEncoder.encode(text, StandardCharsets.UTF_8);
    //==============================================================================================================
  }
  public static boolean isNotValidCityName(String cityName) {
    String onlyNamesRgx = "[a-zA-Z\\s]+";
    return cityName.isBlank() || !cityName.matches(onlyNamesRgx);
  }
  public static String getCityNameBasedOnOpt (int option) {
    return City.values()[option - 1].getName();
  }
  public static boolean optionIsOutOfRange (int selectedOption) {
    return selectedOption < 1 || selectedOption > 7;
  }
}