package edu.northeastern.shelter;

/**
 * An animal's age, measured in whole months.
 *
 * <p>This is a <em>value object</em>: it wraps a single number, but it wraps it in a way that makes
 * an invalid age impossible to construct. Once you have an {@code AgeMonths} in your hand, you know
 * it is non-negative and within a plausible range — you never have to check again.
 *
 * <p>The class is <strong>immutable</strong>: the field is {@code private final}, there is no
 * setter, and no method changes the object. {@link #of(int)} and {@link #months()} ship complete as
 * your worked example of the pattern; you implement the rest.
 */
public final class AgeMonths {

  /** The oldest age this shelter will record: 40 years. */
  public static final int MAX_MONTHS = 480;

  private final int months;

  /**
   * Private constructor — callers go through {@link #of(int)}.
   *
   * <p>Making the constructor private and offering a named factory method is a habit worth
   * acquiring: the factory can validate, can be given a meaningful name, and could one day return a
   * cached instance without any caller noticing.
   *
   * @param months the already-validated age in months
   */
  private AgeMonths(int months) {
    this.months = months;
  }

  /**
   * Returns an age of the given number of months.
   *
   * <p>Ships complete as the worked example. Read it before you write anything else: validate
   * first, assign second, and refuse to build an object that does not make sense.
   *
   * @param months the age in whole months
   * @return an age of {@code months} months
   * @throws IntakeException if {@code months} is negative or exceeds {@link #MAX_MONTHS}
   */
  public static AgeMonths of(int months) {
    if (months < 0) {
      throw new IntakeException("age in months cannot be negative, was " + months);
    }
    if (months > MAX_MONTHS) {
      throw new IntakeException("age in months cannot exceed " + MAX_MONTHS + ", was " + months);
    }
    return new AgeMonths(months);
  }

  /**
   * Returns this age as a whole number of months.
   *
   * @return the age in months, never negative
   */
  public int months() {
    return months;
  }

  /**
   * Returns the number of <em>complete</em> years in this age.
   *
   * <p>An age of 23 months is one complete year, not two: partial years are discarded.
   *
   * @return the number of complete years, never negative
   */
  public int years() {
    throw new UnsupportedOperationException("TODO: implement years()");
  }

  /**
   * Returns the number of months left over after the complete years.
   *
   * <p>An age of 23 months is one year and eleven months, so this returns 11.
   *
   * @return a value in the range 0 to 11 inclusive
   */
  public int remainderMonths() {
    throw new UnsupportedOperationException("TODO: implement remainderMonths()");
  }

  /**
   * Returns whether this age is under one year.
   *
   * @return {@code true} if this age is less than twelve months
   */
  public boolean isUnderOneYear() {
    throw new UnsupportedOperationException("TODO: implement isUnderOneYear()");
  }

  /**
   * Returns a human-readable description of this age.
   *
   * <p>The exact format is specified by the provided tests, and it is fussy on purpose — reading a
   * specification precisely is part of the exercise. Examples:
   *
   * <ul>
   *   <li>0 months → {@code "0 months"}
   *   <li>1 month → {@code "1 month"}
   *   <li>11 months → {@code "11 months"}
   *   <li>12 months → {@code "1 year"}
   *   <li>23 months → {@code "1 year, 11 months"}
   *   <li>24 months → {@code "2 years"}
   *   <li>25 months → {@code "2 years, 1 month"}
   * </ul>
   *
   * <p>Note the singular/plural agreement, and that a whole number of years omits the months part
   * entirely.
   *
   * @return a description of this age, never {@code null}
   */
  @Override
  public String toString() {
    throw new UnsupportedOperationException("TODO: implement toString()");
  }
}
