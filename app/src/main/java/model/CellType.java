package model;

public class CellType {
  public static final String GRAIN = "GRAIN";
  public static final String SOLID = "SOLID";
  public static final String LIQUID = "LIQUID";
  public static final String GAS = "GAS";

  private static final String[] all = new String[] {
    GRAIN,
    SOLID,
    LIQUID,
    GAS,
  };

  public static String[] getAll() {
    return all;
  }
}
