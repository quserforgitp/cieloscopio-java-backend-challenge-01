package menu;

import exceptions.NameOfCityIsBlankException;
import exceptions.NotValidNameOfCityException;

import java.util.Scanner;

public class Menu {
  public static final int OP_INGRESAR_NOMBRE_CIUDAD = 6;
  public static final int OP_SALIR = 7;
  public static void promptUser(String msg, String promptChars) {
    System.out.println(msg);
    System.out.printf("%s",promptChars);
  }// utils
  public static int showAndPromptUser() {
    Scanner input = new Scanner(System.in);

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
    int opt = 0;
    try {
      opt = input.nextInt();
      if(optionIsOutOfRange(opt)) throw new IllegalArgumentException();
    }catch (IllegalArgumentException e) {
      throw e;
    } finally {
      input.nextLine();// Limpiar el buffer del scanner
    }
    if (opt == OP_SALIR) {
      System.out.println("Usted escogio salir, fin del programa...");
      System.exit(0);
    }
    return opt;
  }
  public static String promptForCityName() throws NameOfCityIsBlankException, NotValidNameOfCityException {
    Scanner input = new Scanner(System.in);
    String onlyNamesRgx = "[a-zA-Z\\s]+";
    String cityName = "";
    promptUser("Ingrese el nombre de la ciudad","> ");
    cityName = input.nextLine();

    if (cityName.isBlank()) throw new NameOfCityIsBlankException();
    else if (!cityName.matches(onlyNamesRgx)) throw new NotValidNameOfCityException();

    return cityName;
  }
  public static boolean optionIsOutOfRange (int selectedOption) {
    return selectedOption < 1 || selectedOption > 7;
  }// utils
}