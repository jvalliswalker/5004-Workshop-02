package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The specification for {@link AgeMonths}, written as tests.
 *
 * <p>These tests ship with the assignment and are part of the spec — read them as the definition of
 * "correct". They do not, however, exhaust it: your own tests are graded too, and there are cases
 * here that are deliberately left for you to think of.
 */
@Tag("current")
class AgeMonthsTest {

  @Test
  void ofStoresTheGivenNumberOfMonths() {
    assertEquals(7, AgeMonths.of(7).months());
  }

  @Test
  void zeroMonthsIsAllowed() {
    assertEquals(0, AgeMonths.of(0).months());
  }

  @Test
  void theMaximumIsAllowed() {
    assertEquals(AgeMonths.MAX_MONTHS, AgeMonths.of(AgeMonths.MAX_MONTHS).months());
  }

  @Test
  void negativeMonthsIsRefused() {
    assertThrows(IntakeException.class, () -> AgeMonths.of(-1));
  }

  @Test
  void aboveTheMaximumIsRefused() {
    assertThrows(IntakeException.class, () -> AgeMonths.of(AgeMonths.MAX_MONTHS + 1));
  }

  @Test
  void yearsDiscardsPartialYears() {
    assertEquals(0, AgeMonths.of(0).years());
    assertEquals(0, AgeMonths.of(11).years());
    assertEquals(1, AgeMonths.of(12).years());
    assertEquals(1, AgeMonths.of(23).years());
    assertEquals(2, AgeMonths.of(24).years());
  }

  @Test
  void remainderMonthsIsWhatIsLeftAfterTheWholeYears() {
    assertEquals(0, AgeMonths.of(0).remainderMonths());
    assertEquals(11, AgeMonths.of(11).remainderMonths());
    assertEquals(0, AgeMonths.of(12).remainderMonths());
    assertEquals(11, AgeMonths.of(23).remainderMonths());
    assertEquals(1, AgeMonths.of(25).remainderMonths());
  }

  @Test
  void isUnderOneYearIsTrueBelowTwelveMonths() {
    assertTrue(AgeMonths.of(0).isUnderOneYear());
    assertTrue(AgeMonths.of(11).isUnderOneYear());
    assertFalse(AgeMonths.of(12).isUnderOneYear());
    assertFalse(AgeMonths.of(13).isUnderOneYear());
  }

  @Test
  void toStringUsesMonthsAloneUnderOneYear() {
    assertEquals("0 months", AgeMonths.of(0).toString());
    assertEquals("1 month", AgeMonths.of(1).toString());
    assertEquals("11 months", AgeMonths.of(11).toString());
  }

  @Test
  void toStringOmitsTheMonthsPartForWholeYears() {
    assertEquals("1 year", AgeMonths.of(12).toString());
    assertEquals("2 years", AgeMonths.of(24).toString());
  }

  @Test
  void toStringCombinesYearsAndMonths() {
    assertEquals("1 year, 1 month", AgeMonths.of(13).toString());
    assertEquals("1 year, 11 months", AgeMonths.of(23).toString());
    assertEquals("2 years, 1 month", AgeMonths.of(25).toString());
  }

  @Test
  void theRefusalNamesTheOffendingValue() {
    IntakeException tooOld =
        assertThrows(IntakeException.class, () -> AgeMonths.of(AgeMonths.MAX_MONTHS + 1));
    assertTrue(
        tooOld.getMessage().contains(String.valueOf(AgeMonths.MAX_MONTHS + 1)),
        "the message should name the value that was rejected");
  }
}
