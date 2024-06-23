public class WeatherInfo {
  private String cityName;
  private String conditionMain;
  private String conditionDescription;
  private String precipitationVolume;
  private String temperature;
  private String maxTemperature;
  private String minTemperature;
  public WeatherInfo(String cityName, String conditionMain, String conditionDescription, String precipitationVolume, String temperature, String maxTemperature, String minTemperature) {
    this.cityName = cityName;
    this.conditionMain = conditionMain;
    this.conditionDescription = conditionDescription;
    this.precipitationVolume = precipitationVolume;
    this.temperature = temperature;
    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
  }
  public String getCityName() {
    return this.cityName;
  }

  public String getConditionMain() {
    return this.conditionMain;
  }

  public String getConditionDescription() {
    return this.conditionDescription;
  }

  public String getPrecipitationVolume() {
    return this.precipitationVolume;
  }

  public String getTemperature() {
    return this.temperature;
  }

  public String getMaxTemperature() {
    return this.maxTemperature;
  }

  public String getMinTemperature() {
    return this.minTemperature;
  }

  public void showInfo () {
    System.out.printf("""
            =============== RETRIEVED INFORMATION ===============
            ciudad: %s
            condicion: %s
            descripción: %s
            precipitación: %s
            temperatura actual: %s
            temperatura maxima: %s
            temperatura minima: %s""",
            this.getCityName(),
            this.getConditionMain(),
            this.getConditionDescription(),
            this.getPrecipitationVolume(),
            this.getTemperature(),
            this.getMaxTemperature(),
            this.getMinTemperature());
  }
}