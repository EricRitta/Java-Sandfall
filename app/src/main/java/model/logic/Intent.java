package model.logic;
import model.logic.Chunk;

public class Intent {
  boolean ACTIVATION_ONLY = false;
  String WHERE = "to"; // to, from

  Chunk FROM_CHUNK;
  int FROM_X, FROM_Y;

  Chunk TO_CHUNK;
  int TO_X, TO_Y;

  int FROM_ID, FROM_DEADLINE;
  int TO_ID, TO_DEADLINE;
}
