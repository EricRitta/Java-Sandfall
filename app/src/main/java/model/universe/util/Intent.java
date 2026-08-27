package model.universe.util;
import model.universe.Chunk;

public class Intent {
  public boolean ACTIVATION_ONLY = false;

  public Chunk SENDER_CHUNK;
  public int SENDER_X, SENDER_Y;
  public int SENDER_ID, SENDER_DEADLINE;

  public Chunk RECEIVER_CHUNK;
  public int RECEIVER_X, RECEIVER_Y;
  public int RECEIVER_ID, RECEIVER_DEADLINE;

  public void clear() {
    ACTIVATION_ONLY = false;

    SENDER_CHUNK = null;
    SENDER_X = 0;
    SENDER_Y = 0;
    SENDER_ID = 0;
    SENDER_DEADLINE = 0;

    RECEIVER_CHUNK = null;
    RECEIVER_X = 0;
    RECEIVER_X = 0;
    RECEIVER_ID = 0;
    RECEIVER_DEADLINE = 0;
  }
}
