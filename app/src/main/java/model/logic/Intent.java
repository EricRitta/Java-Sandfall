package model.logic;
import model.logic.Chunk;

public class Intent {
  boolean ACTIVATION_ONLY = false;

  Chunk FROM_CHUNK;
  int FROM_CX, FROM_CY;

  Chunk TO_CHUNK;
  int TO_CX, TO_CY;

  int FROM_ID, FROM_DEADLINE;
  int TO_ID, TO_DEADLINE;
}
