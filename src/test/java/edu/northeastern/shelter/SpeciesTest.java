package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Species}, which ships complete.
 *
 * <p>These pass the moment you clone the repo. That is on purpose: run them first, confirm your
 * toolchain works, and only then start on the code that does not compile yet.
 */
@Tag("current")
class SpeciesTest {

  @Test
  void everySpeciesHasADisplayLabel() {
    assertEquals("Dog", Species.DOG.label());
    assertEquals("Cat", Species.CAT.label());
    assertEquals("Bird", Species.BIRD.label());
  }

  @Test
  void toStringIsTheLabel() {
    assertEquals("Cat", Species.CAT.toString());
  }

  @Test
  void thereAreExactlyThreeSpecies() {
    assertEquals(3, Species.values().length);
  }
}
