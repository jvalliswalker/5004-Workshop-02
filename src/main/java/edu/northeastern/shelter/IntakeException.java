package edu.northeastern.shelter;

/**
 * Thrown when the data offered at intake cannot describe a real animal — a blank name, a negative
 * age, a missing intake date.
 *
 * <p>This ships complete. It extends {@link IllegalArgumentException}, so callers are not forced to
 * wrap every {@code new Animal(...)} in a try/catch. That is deliberate: a blank name is a bug in the
 * calling code, not a condition a correct caller should be expected to recover from, and demanding a
 * try/catch around every construction would drown the code that matters.
 *
 * <p>That reasoning — is this a bug in the caller, or something the caller can sensibly recover
 * from? — is the checked-versus-unchecked decision from <em>Module 1, Lesson 11</em>. This class is
 * the unchecked side of it. You write exception classes of your own in a later assignment.
 */
public class IntakeException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs an exception describing why intake was refused.
   *
   * @param message the reason intake was refused
   */
  public IntakeException(String message) {
    super(message);
  }
}
