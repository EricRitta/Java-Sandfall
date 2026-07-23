package model.abstracts;
import model.interfaces.MovementBehaviour;
import model.interfaces.ReactionBehaviour;

public abstract class Cell {
  private MovementBehaviour[] movements;
  private ReactionBehaviour[] reactions;
}
