package constants;

public enum City {
  MORELIA("Morelia"),
  CARACAS("Caracas"),
  BUENOS_AIRES("Buenos Aires"),
  BRASILIA("Brasilia"),
  QUITO("Quito");

  private final String name;

  City(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}