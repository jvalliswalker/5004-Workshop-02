package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The specification for {@link Animal}, written as tests.
 *
 * <p>
 * Note {@link #twoIdenticalAnimalsAreDifferentObjects()} at the bottom: it
 * asserts the
 * <em>absence</em> of value equality. That is not an oversight to be fixed — it
 * is this lab's
 * boundary, and a later lab moves it.
 */
@Tag("current")
class AnimalTest {

  private static final LocalDate INTAKE = LocalDate.of(2026, 9, 21);

  private static final Map<String, String> NONSTANDARD_WHITESPACE_CHARACTERS() {
    Map<String, String> returnMap = new HashMap<>();
    returnMap.put("U+00A0", " ");
    returnMap.put("U+2002", " ");
    returnMap.put("U+2003", " ");
    returnMap.put("U+2004", " ");
    returnMap.put("U+2005", " ");
    returnMap.put("U+2006", " ");
    returnMap.put("U+2007", " ");
    returnMap.put("U+2008", " ");
    returnMap.put("U+2009", " ");
    returnMap.put("U+200A", " ");
    returnMap.put("U+202F", " ");
    returnMap.put("U+205F", " ");
    returnMap.put("U+3000", "　");

    return returnMap;
  }

  private static Animal luna() {
    return new Animal("Luna", Species.CAT, AgeMonths.of(23), INTAKE);
  }

  @Test
  void accessorsReturnWhatWasPassedIn() {
    Animal luna = luna();
    assertEquals("Luna", luna.name());
    assertEquals(Species.CAT, luna.species());
    assertEquals(23, luna.age().months());
    assertEquals(INTAKE, luna.intakeDate());
  }

  @Test
  void theNameIsTrimmed() {

    assertEquals(
        "Luna",
        new Animal("  Luna  ", Species.CAT, AgeMonths.of(1), INTAKE).name());

    Map<String, String> hashMap = NONSTANDARD_WHITESPACE_CHARACTERS();

    for (String whitespaceName : hashMap.keySet()) {
      String whitespace = hashMap.get(whitespaceName);

      assertEquals(
          "Luna",
          new Animal(
              whitespace + "Luna" + whitespace,
              Species.CAT,
              AgeMonths.of(1),
              INTAKE).name(),
          "Whitespace type " + whitespaceName + " not converted to standard whitespace");
    }
  }

  @Test
  void anInteriorSpaceIsNotWhitespaceToBeStripped() {
    assertEquals(
        "Mr Bigglesworth",
        new Animal(" Mr Bigglesworth ", Species.CAT, AgeMonths.of(1), INTAKE).name());
  }

  @Test
  void nonstandardInteriorWhiteSpaceIsConverted() {

    Map<String, String> hashMap = NONSTANDARD_WHITESPACE_CHARACTERS();

    for (String whitespaceName : hashMap.keySet()) {
      String whitespace = hashMap.get(whitespaceName);

      assertEquals(
          "Mr Bigglesworth",
          new Animal("Mr" + whitespace + "Bigglesworth", Species.CAT, AgeMonths.of(1), INTAKE).name(),
          "Whitespace type " + whitespaceName + " not converted to standard whitespace");
    }
  }

  @Test
  void aNullNameIsRefused() {
    IntakeException e = assertThrows(
        IntakeException.class,
        () -> new Animal(null, Species.DOG, AgeMonths.of(1), INTAKE));

    this.confirmErrorMessage(e, "Name cannot be null");
  }

  @Test
  void anEmptyNameIsRefused() {
    IntakeException e = assertThrows(
        IntakeException.class,
        () -> new Animal("", Species.DOG, AgeMonths.of(1), INTAKE));

    this.confirmErrorMessage(e, "Name cannot be blank or all whitespace characters");
  }

  @Test
  void aNameOfOnlyWhitespaceIsRefused() {

    Map<String, String> hashMap = NONSTANDARD_WHITESPACE_CHARACTERS();

    for (String whitespaceName : hashMap.keySet()) {
      String whitespace = hashMap.get(whitespaceName);

      IntakeException e = assertThrows(
          IntakeException.class,
          () -> new Animal(whitespace, Species.DOG, AgeMonths.of(1), INTAKE),
          "Name of whitespace type " + whitespaceName + " did not throw IntakeException");

      this.confirmErrorMessage(
          e,
          "Name cannot be blank or all whitespace characters");
    }
  }

  @Test
  void aNullSpeciesIsRefused() {
    IntakeException e = assertThrows(
        IntakeException.class,
        () -> new Animal("Rex", null, AgeMonths.of(1), INTAKE));

    this.confirmErrorMessage(e, "Species cannot be null");
  }

  @Test
  void aNullAgeIsRefused() {
    IntakeException e = assertThrows(
        IntakeException.class,
        () -> new Animal("Rex", Species.DOG, null, INTAKE));

    this.confirmErrorMessage(e, "Age cannot be null");
  }

  @Test
  void aNullIntakeDateIsRefused() {
    IntakeException e = assertThrows(
        IntakeException.class,
        () -> new Animal("Rex", Species.DOG, AgeMonths.of(1), null));

    this.confirmErrorMessage(e, "Intake date cannot be null");
  }

  @Test
  void toStringFollowsTheSpecifiedFormat() {
    assertEquals("Luna (Cat, 1 year, 11 months, intake 2026-09-21)", luna().toString());
  }

  @Test
  void toStringDelegatesToTheAgeDescription() {
    Animal pup = new Animal("Pip", Species.DOG, AgeMonths.of(1), INTAKE);
    assertEquals("Pip (Dog, 1 month, intake 2026-09-21)", pup.toString());
  }

  @Test
  void theIntakeDateSurvivesTheRoundTrip() {
    Animal luna = luna();
    assertSame(INTAKE, luna.intakeDate());
  }

  @Test
  void twoIdenticalAnimalsAreDifferentObjects() {
    // Deliberate: Animal has no value equality in this lab, so the default identity
    // comparison
    // applies. A later lab gives the Animal family a proper equals/hashCode, and
    // this test moves.
    assertNotSame(luna(), luna());
    assertEquals(false, luna().equals(luna()));
  }

  @Test
  void theRefusalSaysWhichArgumentWasWrong() {
    // The rubric grades messages that name the problem. "invalid" tells a reader
    // nothing.
    IntakeException blankName = assertThrows(
        IntakeException.class,
        () -> new Animal("  ", Species.DOG, AgeMonths.of(1), INTAKE));
    assertTrue(
        blankName.getMessage().toLowerCase().contains("name"),
        "the message should name the offending argument");
  }

  private void confirmErrorMessage(Exception e, String expectedMessage) {
    assertEquals(
        expectedMessage,
        e.getMessage(),
        "Unexpected error message");
  }
}
