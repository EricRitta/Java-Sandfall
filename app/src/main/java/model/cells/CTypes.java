package model.cells;

public final class CTypes {
  public static final String POWDER = "POWDER";
  public static final String SOLID = "SOLID";
  public static final String LIQUID = "LIQUID";
  public static final String GAS = "GAS";

  private static final String[] all = new String[] {
    POWDER,
    SOLID,
    LIQUID,
    GAS,
  };

  public static String[] getAll() {
    return all;
  }
}
