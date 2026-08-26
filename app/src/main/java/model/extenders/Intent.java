package model.extenders;
import model.universe.Chunk;

public class Intent {
  boolean ACTIVATION_ONLY = false;

  Chunk SENDER_CHUNK;
  int SENDER_X, SENDER_Y;
  int SENDER_ID, SENDER_DEADLINE;

  Chunk RECEIVER_CHUNK;
  int RECEIVER_X, RECEIVER_Y;
  int RECEIVER_ID, RECEIVER_DEADLINE;
}
