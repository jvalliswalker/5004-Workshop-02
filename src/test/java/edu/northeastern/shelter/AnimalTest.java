package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The specification for {@link Animal}, written as tests.
 *
 * <p>Note {@link #twoIdenticalAnimalsAreDifferentObjects()} at the bottom: it asserts the
 * <em>absence</em> of value equality. That is not an oversight to be fixed — it is this lab's
 * boundary, and a later lab moves it.
 */
@Tag("current")
class AnimalTest {

  private static final LocalDate INTAKE = LocalDate.of(2026, 9, 21);

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
    assertEquals("Luna", new Animal("  Luna  ", Species.CAT, AgeMonths.of(1), INTAKE).name());
  }

  @Test
  void anInteriorSpaceIsNotWhitespaceToBeStripped() {
    assertEquals(
        "Mr Bigglesworth",
        new Animal(" Mr Bigglesworth ", Species.CAT, AgeMonths.of(1), INTAKE).name());
  }

  @Test
  void aNullNameIsRefused() {
    assertThrows(
        IntakeException.class, () -> new Animal(null, Species.DOG, AgeMonths.of(1), INTAKE));
  }

  @Test
  void anEmptyNameIsRefused() {
    assertThrows(IntakeException.class, () -> new Animal("", Species.DOG, AgeMonths.of(1), INTAKE));
  }

  @Test
  void aNameOfOnlyWhitespaceIsRefused() {
    assertThrows(
        IntakeException.class, () -> new Animal("   ", Species.DOG, AgeMonths.of(1), INTAKE));
  }

  @Test
  void aNullSpeciesIsRefused() {
    assertThrows(IntakeException.class, () -> new Animal("Rex", null, AgeMonths.of(1), INTAKE));
  }

  @Test
  void aNullAgeIsRefused() {
    assertThrows(IntakeException.class, () -> new Animal("Rex", Species.DOG, null, INTAKE));
  }

  @Test
  void aNullIntakeDateIsRefused() {
    assertThrows(
        IntakeException.class, () -> new Animal("Rex", Species.DOG, AgeMonths.of(1), null));
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
    // Deliberate: Animal has no value equality in this lab, so the default identity comparison
    // applies. A later lab gives the Animal family a proper equals/hashCode, and this test moves.
    assertNotSame(luna(), luna());
    assertEquals(false, luna().equals(luna()));
  }

  @Test
  void theRefusalSaysWhichArgumentWasWrong() {
    // The rubric grades messages that name the problem. "invalid" tells a reader nothing.
    IntakeException blankName =
        assertThrows(
            IntakeException.class, () -> new Animal("  ", Species.DOG, AgeMonths.of(1), INTAKE));
    assertTrue(
        blankName.getMessage().toLowerCase().contains("name"),
        "the message should name the offending argument");
  }
}
