package edu.northeastern.shelter;

/**
 * The kinds of animal this shelter accepts at intake.
 *
 * <p>This enum ships complete as a worked example. Notice what it does <em>not</em> have: no
 * setters, no mutable state, and no behaviour that depends on anything outside itself. An enum is
 * the right choice here because the set of values is small, fixed, and known when the program is
 * written.
 */
public enum Species {
  /** A dog. */
  DOG("Dog"),
  /** A cat. */
  CAT("Cat"),
  /** A bird. */
  BIRD("Bird");

  private final String label;

  Species(String label) {
    this.label = label;
  }

  /**
   * Returns the human-readable name of this species, suitable for display.
   *
   * @return the display label, never {@code null}
   */
  public String label() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }
}
