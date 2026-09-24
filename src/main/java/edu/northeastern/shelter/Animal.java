package edu.northeastern.shelter;

import java.time.LocalDate;

/**
 * One animal in the shelter's care, as recorded at intake.
 *
 * <p>This is the class you build in this lab, and it is the class the rest of the term is built on.
 * Everything about it is deliberate:
 *
 * <ul>
 *   <li><strong>Every field is {@code private final}.</strong> Nothing outside this class can see
 *       them, and nothing — including this class — can change them after construction.
 *   <li><strong>There are no setters.</strong> An animal's intake record is a statement about what
 *       happened. If a fact changes, that is a new record, not an edit to history.
 *   <li><strong>The constructor validates.</strong> An {@code Animal} that exists is an {@code
 *       Animal} that makes sense. No method further down the line has to wonder whether the name is
 *       blank.
 * </ul>
 *
 * <p>Two things are <em>deliberately missing</em>, and you should not add them: this class has no
 * {@code equals}/{@code hashCode} and no ordering. Comparing animals turns out to be subtler than
 * it looks, and it gets a lab of its own. For now, two separately-constructed animals with identical
 * fields are simply two different objects.
 */
public class Animal {

  private final String name;
  private final Species species;
  private final AgeMonths age;
  private final LocalDate intakeDate;

  /**
   * Records an animal at intake.
   *
   * <p>Validate every argument before assigning any field, and throw {@link IntakeException} with a
   * message that says what was wrong. The rules:
   *
   * <ul>
   *   <li>{@code name} must be non-{@code null} and must contain at least one non-whitespace
   *       character. Store it with leading and trailing whitespace removed, so {@code "  Luna  "}
   *       is stored as {@code "Luna"}.
   *   <li>{@code species} must be non-{@code null}.
   *   <li>{@code age} must be non-{@code null}. It needs no range check — {@link AgeMonths} already
   *       guarantees it is sensible, which is the whole point of having made it a type.
   *   <li>{@code intakeDate} must be non-{@code null}.
   * </ul>
   *
   * @param name the animal's name
   * @param species the animal's species
   * @param age the animal's age at intake
   * @param intakeDate the date the animal arrived
   * @throws IntakeException if any argument is {@code null}, or if {@code name} is blank
   */
  public Animal(String name, Species species, AgeMonths age, LocalDate intakeDate) {
    throw new UnsupportedOperationException("TODO: validate the arguments and assign the fields");
  }

  /**
   * Returns this animal's name, with surrounding whitespace already removed.
   *
   * @return the name, never {@code null} and never blank
   */
  public String name() {
    throw new UnsupportedOperationException("TODO: implement name()");
  }

  /**
   * Returns this animal's species.
   *
   * @return the species, never {@code null}
   */
  public Species species() {
    throw new UnsupportedOperationException("TODO: implement species()");
  }

  /**
   * Returns this animal's age at intake.
   *
   * @return the age, never {@code null}
   */
  public AgeMonths age() {
    throw new UnsupportedOperationException("TODO: implement age()");
  }

  /**
   * Returns the date this animal arrived at the shelter.
   *
   * <p>{@link LocalDate} is itself immutable, so returning the field directly is safe — there is
   * nothing a caller could do to it that would affect this animal. That will not always be true of
   * the things you are asked to return, and knowing the difference matters.
   *
   * @return the intake date, never {@code null}
   */
  public LocalDate intakeDate() {
    throw new UnsupportedOperationException("TODO: implement intakeDate()");
  }

  /**
   * Returns a one-line description of this animal.
   *
   * <p>The format is exactly {@code "NAME (SPECIES, AGE, intake DATE)"}, where {@code SPECIES} is
   * the species label, {@code AGE} is the age's own description and {@code DATE} is the ISO form of
   * the intake date. For a cat called Luna, 23 months old, taken in on 21 September 2026:
   *
   * <pre>{@code Luna (Cat, 1 year, 11 months, intake 2026-09-21)}</pre>
   *
   * <p>Build this out of the parts you already have rather than re-deriving them. If {@link
   * AgeMonths#toString()} is right, this method should not contain the words "year" or "month" at
   * all — that is what delegation buys you.
   *
   * @return a description of this animal, never {@code null}
   */
  @Override
  public String toString() {
    throw new UnsupportedOperationException("TODO: implement toString()");
  }
}
