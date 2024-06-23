public class GeoLocApiInfo {
  private String lat;
  private String lon;
  private String cityNameInSpanish;

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public String getCityNameInSpanish() {
    return cityNameInSpanish;
  }

  public void setCityNameInSpanish(String cityNameInSpanish) {
    this.cityNameInSpanish = cityNameInSpanish;
  }

  public GeoLocApiInfo(String lat, String lon, String cityNameInSpanish) {
    this.lat = lat;
    this.lon = lon;
    this.cityNameInSpanish = cityNameInSpanish;
  }

}
