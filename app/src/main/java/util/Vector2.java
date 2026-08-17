public class Vector2 {
  private int x;
  private int y;

  public Vector2(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // SETTERS //
  public void x(int v) { this.x = v; }
  public void y(int v) { this.y = v; }

  // GETTERS //
  public int x() { return this.x; }
  public int y() { return this.y; }
}
