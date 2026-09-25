# LLM Evaluation

Produced with an LLM using the prompt in `LLM-Evaluation-prompt.md`, then reviewed and answered by
you. **Both halves are required** — an unread LLM assessment pasted in whole is worth nothing.

---

## Assessment

Paste the LLM's assessment here, in full and unedited.

Its scoring categories are the ones in `LLM-Evaluation-prompt.md`, which are the same categories and
the same weights as the rubric in `how-to-submit.md`. If the LLM invents different categories or
weights, say so below rather than silently accepting them.

**Coverage reported:** 100% line coverage (51/51). Also 100% branch (28/28), 100% instruction
(231/231), 100% method (19/19), 4/4 classes — from `build/reports/jacoco/test/html/index.html`.
33 tests, 0 failures. Checkstyle `src/main`: **1 warning** (regression — see §5). Checkstyle
`src/test`: 2 warnings.

---

> This is a rerun of the same assessment against the current `AgeMonths.java`, `Animal.java` and
> `AnimalTest.java` (uncommitted changes on top of the version this document previously scored).
> `AgeMonthsTest.java` and `SpeciesTest.java` are byte-for-byte unchanged, so every finding against
> them below is unchanged too. Two of the three previously-open defects in the changed files are
> fixed; one new defect and one new regression were introduced in fixing them. Net change: **82 → 80
> (−2)**.

**Hard data:** 33 tests (was 32), 0 failures. JaCoCo: 100% line (51/51), 100% branch (28/28), 100%
method (19/19). Checkstyle main: 1 warning (was 0). No `throw new UnsupportedOperationException`
remains in `src/main/java`.

### Total — 80/100

| Category | Score | Was | Δ |
|---|---|---|---|
| 1. Immutability & encapsulation | 24/25 | 24/25 | — |
| 2. Constructor validation | 22/25 | 21/25 | +1 |
| 3. Correctness | 15/15 | 14/15 | +1 |
| 4. Testing & coverage | 8/15 | 11/15 | −3 |
| 5. Code quality & style | 6/10 | 7/10 | −1 |
| 6. Scope discipline & code walk | 5/10 | 5/10 | — |
| **Total** | **80/100** | **82/100** | **−2** |

*Category note:* these weights (25/25/15/15/10/10) match `how-to-submit.md` lines 78-85 exactly. One
name differs: the rubric's category 6 is "Introspection, LLM eval & code walk," which grades two
written deliverables; the prompt's version is "Scope discipline and code walk," which grades a thing
the rubric handles as a blanket deduction at `how-to-submit.md:87`. Both were graded, and they pull
in opposite directions — see §6.

---

### 1. Immutability and encapsulation — 24/25

Correct on every substantive point.

- All fields `private final`: `Animal.java:40-43` and `AgeMonths.java:26`.
- No setters, and no method assigns to a field outside a constructor.
- **No accessor leaks a mutable reference.** Every return type is deeply immutable: `String`, an enum
  constant, `AgeMonths` (itself `private final int` with a private constructor), and `LocalDate`.
  Returning `this.intakeDate` directly at `Animal.java:132` is safe, and it is safe for the reason
  the Javadoc gives, not by accident.
- `public static final int MAX_MONTHS = 480` at `AgeMonths.java:24` is the only public field, and it
  is a primitive constant the tests legitimately reference. Nothing else is public that need not be.
- `AgeMonths` keeps the private constructor + static factory intact (`AgeMonths.java:40`, `:57`)
  rather than widening the constructor for convenience — a common shortcut not taken.

**−1:** `public class Animal` at `Animal.java:38` is not `final`, while `AgeMonths.java:21` is
`public final class`. A subclass could override `name()` to return something other than the validated
field, and the class-level Javadoc's promise that "nothing — including this class — can change them
after construction" would no longer hold for instances of that subclass. In fairness, that
declaration shipped that way and the lab excludes inheritance, so changing it is arguably out of
scope — the deduction is for not noticing the asymmetry, which is exactly the question a reviewer
asks about a value object. This is the kind of thing to raise in the code walk rather than edit.

Minor, not scored, and mostly cleaned up since the last review: accessors now consistently use
`this.name` / `this.species` / `this.age` / `this.intakeDate` (`Animal.java:98,107,116,132`). One
inconsistency remains: `toString()` at `Animal.java:159` still reads the bare field names
(`name`, `species`, `age`, `intakeDate`) rather than `this.x`. Pick one, still.

### 2. Constructor validation — 22/25 (was 21/25)

Taking the five required rejections individually against `Animal.java:73-90`:

| Rule | Present? | Line |
|---|---|---|
| null name | ✅ | `name == null` (74) |
| empty or whitespace-only name | ✅ | `normalizeWhitespace(name).isEmpty()` (74) — fixed, see below |
| null species | ✅ | 77 |
| null age | ✅ | 80 |
| null intake date | ✅ | 83 |
| name stored stripped | ⚠️ partial | `this.name = normalizeWhitespace(name);` (86) — does more than "stripped," see below |

**Validate-before-assign: yes, correctly.** All four `throw` statements precede all four assignments
— no field is written before every check has run. That ordering is the whole point of the category,
and it is right.

**Messages identify the problem: yes.** `"Species cannot be null"`, `"Age cannot be null"`,
`"Intake date cannot be null"` each name the offending argument. Nothing generic, nothing bare, and
`IntakeException` is thrown in all four cases.

**Fixed since the last review: the `trim()` / Unicode-whitespace hole is closed.**
`Animal.java:162-164` now runs:

```java
private String normalizeWhitespace(String value) {
  return value.replaceAll("\\p{Zs}+", " ").trim();
}
```

`\p{Zs}` is the Unicode "space separator" category — it covers `U+2003` EM SPACE, `U+00A0` NBSP,
`U+3000` IDEOGRAPHIC SPACE, and the rest of the family that plain `trim()` (code point ≤ `U+0020`
only) missed. I compiled and ran this directly to confirm it:

```java
"\u2003Luna\u2003".replaceAll("\\p{Zs}+", " ").trim()  // -> "Luna"
```

`new Animal("\u2003", Species.CAT, AgeMonths.of(1), INTAKE)` is now correctly refused. This closes
the previous −2 finding, and it is now backed by a real test (`AnimalTest.java:64-84`, see §4).

**−1 (new): the fix does more than the contract asks, silently.** The Javadoc's rule is "store it
with leading and trailing whitespace removed" (`Animal.java:56-58`) — nothing about interior
whitespace. But `replaceAll("\\p{Zs}+", " ")` runs over the *whole* string, not just the ends, so it
also collapses interior runs of ordinary spaces. I confirmed this too:

```java
"John  Smith".replaceAll("\\p{Zs}+", " ").trim()  // -> "John Smith" -- the second space is gone
```

A name typed with two spaces between words is silently rewritten to one. That may well be desirable,
but it isn't what was asked for, it isn't documented anywhere (`Animal.java:56-58` still says only
"leading and trailing"), and — see §4 — nothing tests it either way, so this is a behavior change
resting entirely on nobody having written a counter-example yet. If a hidden test constructs a name
with deliberate internal double-spacing and expects it preserved, this fails it.

**−1: null and blank still share one branch and one message.** `Animal.java:74-76` collapses two
distinct faults into `"Name cannot be null or blank"`. Unchanged from the last review.

**−1: the messages still don't name the offending value.** Contrast `AgeMonths.java:59`:
`"age in months cannot be negative, was " + months`. The name check could echo the rejected value
the same way (there's precedent for it — `AgeMonthsTest.java:131-136` grades exactly this — and
`Animal` still isn't held to it). Unchanged from the last review.

Not scored: `normalizeWhitespace(name)` is computed twice (74, 86), same as `trim()` was before; and
these messages are capitalized sentences while the shipped ones are lowercase fragments.

### 3. Correctness — 15/15 (was 14/15)

**`AgeMonths.toString()` — all seven cases correct.** Traced through `AgeMonths.java:136-147`:

| months | years / rem | branch | output | ✓ |
|---|---|---|---|---|
| 0 | 0 / 0 | 140 | `0 months` | ✓ |
| 1 | 0 / 1 | 140 | `1 month` | ✓ |
| 11 | 0 / 11 | 140 | `11 months` | ✓ |
| 12 | 1 / 0 | 142 | `1 year` | ✓ |
| 23 | 1 / 11 | 144 | `1 year, 11 months` | ✓ |
| 24 | 2 / 0 | 142 | `2 years` | ✓ |
| 25 | 2 / 1 | 144 | `2 years, 1 month` | ✓ |

Singular/plural agreement is right in all four positions, including the easy-to-miss `" year, "` vs
`" years, "` on line 145. The whole-year case correctly omits the months part via the `months == 0`
branch rather than by emitting `"1 year, 0 months"`. The three-branch structure is the right
decomposition.

**`Animal.toString()` — exactly right in shape.** `Animal.java:159` produces
`Luna (Cat, 1 year, 11 months, intake 2026-09-21)`. `%s` on `intakeDate` yields `LocalDate`'s ISO
form; `species.label()` yields `Cat`.

**Fixed since the last review: the field-shadowing bug at `AgeMonths.java:138` is gone.** The local
variable was renamed from `months` to `remainingMonths`:

```java
int years = years();
int remainingMonths = remainderMonths();
```

The field `months` (total months) and the local (months left after whole years) no longer share a
name, so the field is reachable by simple name again inside this method. This closes the previous
−1 finding cleanly — nothing else in the method changed, and all seven `toString()` cases above
still trace correctly. Full marks.

Not scored, but still worth a sentence in the introspection: `AgeMonths.java:86` uses
`Math.floorDiv(months, 12)` while `:98` uses plain `months % 12` and `:107` uses plain `months / 12`.
`floorDiv` differs from `/` only for negative operands, which `of()` has already made impossible. So
one method defends against a case the invariant rules out, and its two neighbours don't. Either the
invariant is trusted or it isn't — inconsistency is the thing to avoid. (Trust it; that's what the
value object is *for*.)

### 4. Testing and coverage — 8/15 (was 11/15)

**Coverage is 100% on every counter — and that is the least interesting fact here.**
`how-to-submit.md:10-14` warns that the provided suite already covers almost every line, so the
percentage cannot tell you whether you added anything worth having. It can't, and the gaps below are
all invisible to it.

**Boundaries: covered.** 0 (`AgeMonthsTest.java:31`), 11/12 (`:104-106`, `:112-119`), the maximum
(`:36`), one past it (`:68`), and −1 (`:53`).

**No test asserts a weaker exception type.** Every `assertThrows` names `IntakeException.class`; not
one says `Exception.class` or `RuntimeException.class`. The specific trap in this category, avoided.

**Real progress: the Unicode-whitespace gap from the last review is now mostly tested.**
`AnimalTest.java:64-84` (`theNameIsTrimmed`) and `:127-141` (`aNameOfOnlyWhitespaceIsRefused`) now loop
over twelve non-ASCII space characters — `U+2002` through `U+200A`, `U+202F`, `U+205F`, `U+3000` —
both leading/trailing and, in the new `nonstandardInteriorWhiteSpaceIsConverted` (`:93-106`), interior.
This is exactly the "untested case" flagged last time, and it is now backed by real assertions with
per-case failure messages naming the code point. Genuine credit.

**−1 (new): one entry in that new map is mislabeled and tests nothing.**
`AnimalTest.java:33`:

```java
returnMap.put("U+00A0", " ");
```

The value here is a literal `U+0020` regular space, not `U+00A0` NBSP — I decoded the actual bytes in
the file to confirm this (`ord(ch) == 0x20`, not `0xa0`). Every other entry in the map (`:34-45`) is
correctly the code point it claims to be. So the suite reads as if it tests thirteen distinct
whitespace characters including NBSP, but it actually tests twelve real ones plus a second, redundant
copy of a regular space. NBSP — probably the single most common "invisible" whitespace character a
real intake form would produce by copy-paste — is claimed as covered and is not.

**−2 (new, and the most important finding in this document): a real assertion was deleted and
replaced with nothing.** `AnimalTest.java:187-193`:

```java
@Test
void twoIdenticalAnimalsAreDifferentObjects() {
  // Deliberate: Animal has no value equality in this lab, so the default identity
  // comparison
  // applies. A later lab gives the Animal family a proper equals/hashCode, and
  // this test moves.{
}
```

Compare this against what the same method asserted before this round's edits:
`assertNotSame(luna(), luna()); assertEquals(false, luna().equals(luna()));`. Both lines are gone; the
method body is now empty. It still compiles (the stray `{` sits inside a `//` comment, harmless), it
still passes (an empty `@Test` method always passes), and the class-level Javadoc at
`AnimalTest.java:20-24` still tells a reader *"Note {@link #twoIdenticalAnimalsAreDifferentObjects()}
at the bottom: it asserts the absence of value equality"* — which is no longer true. The `assertNotSame`
import is now unused (`AnimalTest.java:4`; Checkstyle flags this, see §5), which is the physical
trace of the deletion. This is worse than the `SpeciesTest` tautology below: a tautology at least
executes an assertion that happens to be too weak. This test asserts nothing at all, while the comment
above it and the class Javadoc both actively claim it does. It was also the concrete evidence cited in
§6 last time for "the student read the comment and left the `equals`/`hashCode` boundary alone" —
that evidence no longer exists in the test suite (the production code still has no `equals`/`hashCode`,
so the boundary itself is intact; only the proof of it is gone).

**−2: a real assertion was replaced with a tautology.** `SpeciesTest.java:26-31` (unchanged since the
last review):

```java
for (Species s : Species.values()) {
  assertEquals(s.label(), s.toString());
}
```

The provided version asserted `assertEquals("Cat", Species.CAT.toString())` — anchored to a literal.
This one compares the implementation to itself. If `label()` and `toString()` both returned `name()`,
or both returned `""`, this test still passes. **This is the exact failure mode the rubric names: an
assertion that would still pass if the implementation were wrong.** The loop is a good idea *in
addition to* the literal — it proves the label/toString relation holds for all three values — but it
cannot replace it, because nothing in it knows what a `Cat` is called.

**−1: `positiveMonthsWithinMaximumAreAllowed` is volume, not coverage.** `AgeMonthsTest.java:41-50`
(unchanged) loops 479 times, and the `try`/`catch`/`fail` wrapper is redundant — an uncaught
`IntakeException` already fails a JUnit test, with a better stack trace than the string it builds.
Iterating every value between two boundaries tests nothing that testing the boundaries doesn't. Same
idiom problem at `:58-65` and `:73-82`: the better tool is already in use at `:132`, where
`assertThrows` *returns* the exception to assert on. Also, `AgeMonths.of(-1).toString()` at `:60` and
`:75` — the `.toString()` is dead; `of()` throws before it can be called.

**−1: a string-concatenation bug in a failure message.** `AgeMonthsTest.java:76` (unchanged):

```java
fail("AgeMonths.of(" + AgeMonths.MAX_MONTHS + 1 + ") should have thrown an IntakeException");
```

`+` is left-associative and the left operand is already a `String`, so this builds
`"AgeMonths.of(4801)"`, not `481`. Four lines later at `:79` it is parenthesised correctly —
`(AgeMonths.MAX_MONTHS + 1)` — so the rule is understood; it was missed one line up. Harmless today
because the `fail()` never fires, which is precisely why nobody catches this kind of thing.

Procedural note: `how-to-submit.md:7` asks for *your own* test classes tagged `@Tag("current")`.
These tests were added inside the three provided classes instead. They will run (those classes
already carry the tag), so no marks lost — but a grader looking for the contribution has to diff for
it.

#### Three specific untested cases

1. **`AgeMonths.of(AgeMonths.MAX_MONTHS).toString()`.** 480 is tested as *accepted* (`:36`) but never
   as *rendered*. Every `toString` assertion uses 0—25, so the whole-years branch is only ever
   exercised at one and two years. Nothing pins `"40 years"`, and nothing checks `isUnderOneYear()`,
   `years()` or `remainderMonths()` at the top boundary. (Unchanged since the last review.)

2. **A genuinely-tested NBSP case.** As found above, the new whitespace suite claims `U+00A0` and
   actually exercises `U+0020`. Nothing in the repository currently proves `new Animal("\u00A0",
   ...)` is refused, or that `"\u00A0Luna\u00A0"` is trimmed to `"Luna"` — one real Unicode
   character away from what the test file claims to cover.

3. **Which check wins when more than one argument is bad.** `new Animal(null, null, null, null)`
   throws *something*, but no test says which message. The order of the four checks is currently free
   to change without a single test noticing. Related and equally cheap: `assertSame(INTAKE,
   luna.intakeDate())` at `:184` has no counterpart for `age()` — the accessor most likely to be
   "helpfully" rewritten to return a copy later. (Unchanged since the last review.)

### 5. Code quality and style — 6/10 (was 7/10)

**Delegation — the question asked: `Animal.toString()` delegates, correctly and completely.**
`Animal.java:159` passes `age` straight into `%s` and lets `AgeMonths.toString()` do the work. The
words "year" and "month" appear nowhere in `Animal.java`, which is the test the Javadoc at `:139-153`
sets. Change the age format — to `"1y 11m"`, or to add weeks — and `Animal` needs no edit and its
tests stay green. `species.label()` is also called explicitly rather than leaning on
`Species.toString()` — the better choice, since it doesn't depend on `toString()` continuing to
return the label. Unchanged and still correct.

**But the same principle is missed one level down.** `AgeMonths.java:106-108` (unchanged):

```java
public boolean isUnderOneYear() {
  return months / 12 < 1;
}
```

`years()` is defined immediately above and computes exactly this. `return years() < 1;` — or plainly
`return months < 12;` — says what the method means. The concept is clearly held; apply it
consistently.

**−2: the Javadoc is still addressed to the student.** Unchanged since the last review — every
public member carries a purpose statement, but the inherited text still reads as an assignment prompt
rather than a finished API description:

- `AgeMonths.java:19`: *"...ship complete as your worked example of the pattern; **you implement the
  rest**."*
- `AgeMonths.java:48`: *"**Ships complete as the worked example. Read it before you write anything
  else**..."*
- `AgeMonths.java:114`: *"...**reading a specification precisely is part of the exercise**."*
- `Animal.java:49`: *"**Validate every argument before assigning any field, and throw**
  `IntakeException`..."* — an instruction in the imperative, where the API contract should be.

**−1: a formatter ran over the whole repository.** Unchanged since the last review — roughly 200
lines of diff against the initial commit are pure re-wrapping of provided Javadoc into ragged lines,
and the actual contribution is buried in it.

**−1 (new): the Checkstyle-clean claim from the last review no longer holds.**
`build/reports/checkstyle/main.xml` now lists one warning:

```
Animal.java:4:8: Unused import - java.util.List. [UnusedImports]
```

`java.util.List` was added to `Animal.java`'s imports (`:4`) as part of this round's edit but is never
referenced anywhere in the file — `normalizeWhitespace` uses only `String.replaceAll`. This is a
plain regression against `how-to-submit.md:52` ("Style: clear the warnings"), and it is the kind of
thing `./gradlew checkstyleMain` catches immediately, so it should not have shipped. `src/test` also
picked up two new warnings this round: the unused `assertNotSame` import already noted in §4
above, and `AnimalTest.java:31` (`NONSTANDARD_WHITESPACE_CHARACTERS` violates the `MethodName` pattern
— it's named like a constant but it's a method). Test code isn't gated by `checkstyleMain`
specifically, but `how-to-submit.md:52` doesn't limit "clear the warnings" to `src/main` either.
Correction to the last review: `AgeMonths.java:145`, previously cited as a 116-character line the
formatter left alone, no longer exists in that form. The field-shadowing fix in §3 split it into two
shorter lines (`:145-146`) as a side effect of renaming `months` to `remainingMonths` and rewrapping
the concatenation; I checked the current line and it's 76 characters. That specific complaint is
retired along with the bug that caused it.

### 6. Scope discipline and code walk — 5/10 (unchanged)

**Scope discipline: still spotless on the production code — full marks on this half.**

- **Inheritance:** none added. `Animal` declares no `extends`; `IntakeException extends
  IllegalArgumentException` shipped that way and was left alone.
- **Collections:** none *used*. `Animal.java:4` now imports `java.util.List`, but nothing in the file
  constructs, accepts or returns one — it's dead weight (flagged for style in §5), not a scope
  violation. Worth watching: an unused collection import is the kind of thing that shows up right
  before a collection actually gets used.
- **`equals`/`hashCode`:** still not added to `Animal` itself, so the boundary this lab draws is still
  respected in the code that's graded here. The *evidence* for that in the test suite is weaker than
  last time, though: `AnimalTest.java:187-193` used to assert the absence of value equality directly
  and now asserts nothing (see §4's −2 finding) — the deduction for that lives in Testing, not
  here, but it means the code-walk claim "the student read the comment and left the boundary alone"
  now rests on the comment text and the unchanged production code, not on a passing test that proves it.

**Introspection: −5, because there is still nothing to read.**

`submission/introspection.md` now exists — that's new since the last review — but every one of its
six sections is still the unmodified template text (*"In two or three sentences: which types you
wrote this week..."*, *"Pick the two or three decisions you actually had to think about..."*, and so
on through section 6). Not one prompt has an actual answer. Functionally this is the same finding as
before: there is a file at the required path, but no introspection has been written into it.

So there is still no evidence either way about `Species`. The three questions `code-walk.md:20-24`
requires are unanswered anywhere in the repository:

- Why an `enum` rather than three `String` constants or 1/2/3?
- What is `label()` for, and why does `toString()` return it?
- `Species` has a **field** (`Species.java:19`) and a **method** (`:30`) — what does that tell you
  about what an enum actually *is*?

The video is graded separately on Canvas and cannot be assessed here, so this deduction is for the
missing written content only, and it remains the cheapest five marks in the entire rubric to recover:
the template is already sitting at `submission/introspection.md`, so this is answering questions, not
starting from a blank file.

One thing to have ready for that third question, since it's the one people fumble: the submitted code
already answers it. `species.label()` at `Animal.java:159` calls a *method* on a *constant*. Three
`String` constants could not have done that. An enum constant is an object: `DOG`, `CAT` and `BIRD`
are three instances of a class, each constructed with its own `label`, which is why `Species` can
carry behaviour and per-constant state while still being a closed, exhaustive set the compiler can
check.

---

### The one thing to do differently next time

**Write the test that could fail, not the test that will pass — and don't delete one that already
could.** This is the second time this exact failure mode has shown up, and this round makes it worse,
not better. Last time it was a tautology (`SpeciesTest.java:27-31`) and a 479-iteration loop that all
behave identically. This time, in the process of *fixing* the `trim()` bug, a test that used to assert
something real (`assertNotSame(luna(), luna())`, `assertEquals(false, luna().equals(luna()))`) was
reduced to an empty method body that still says, in its own comment, that it asserts the thing it no
longer asserts (`AnimalTest.java:187-193`). Separately, a whitespace-coverage table that reads as
thorough has one entry (`U+00A0`) that silently tests a different character than the one it's labeled
as. Before treating a test file as finished, diff it against what it asserted before, and read every
literal it constructs against the label attached to it — both of this round's new findings would have
been caught by exactly that habit, and neither is visible from the coverage percentage.

### The one thing done genuinely well

**The `trim()` fix is real, and it's backed by a real test.** `Animal.java:162-164` replaces
`name.trim()` with `value.replaceAll("\\p{Zs}+", " ").trim()`, and I compiled and ran it directly to
confirm `"\u2003Luna\u2003"` now normalizes to `"Luna"` where the old code left it unrecognized as
whitespace. That's the exact defect flagged in the previous review, fixed correctly, and pinned down by
new assertions (`AnimalTest.java:64-84`) rather than left to trust. Separately, and still true from
before: `Animal.toString()` contains neither "year" nor "month" (`Animal.java:159`) — the age formats
itself, so the format lives in exactly one place. That design instinct hasn't moved. What has moved,
for the worse, is discussed in §4 and §5 above — the fix that closed one gap opened two smaller
ones right next to it.

## Your response

The part that is actually marked. For each point below, a few sentences.

### Where it is right

Which criticisms do you accept? For each, say what you would change and why you agree.

<!--
Candidates, strongest first (updated for this rerun):
  - The gutted twoIdenticalAnimalsAreDifferentObjects() test (§4). It used to assert real behavior;
    now it asserts nothing, while its own comment and the class Javadoc still claim it does. Do you
    accept this is worse than a weak assertion, and would you restore the two deleted lines?
  - The mislabeled U+00A0 entry in the new whitespace map (§4). One line of code (AnimalTest.java:33)
    versus one line of intent — do you agree the fix is trivial and worth making regardless of marks?
  - The interior-whitespace-collapsing side effect of the trim() fix (§2). Is silently collapsing
    "John  Smith" to "John Smith" a bug, a reasonable interpretation of "stripped," or something that
    should at least have been documented and tested either way?
  - The tautological SpeciesTest loop (§4, unchanged from last time). What would you restore, and
    what would you keep?
  - The fail() concatenation bug at AgeMonthsTest.java:76 (§4, unchanged from last time).
-->

*Note: the field-shadowing bug at AgeMonths.java:138 and the trim()/U+2003 whitespace hole from the
previous review are both fixed in this version — nothing to accept or reject there anymore, but it's
worth saying in your response how you found and fixed each one.*

### Where it is wrong

Which criticisms do you reject, and on what grounds? LLMs confidently misread code, invent requirements that are not in the specification, and flag correct code as broken. Disagreeing with a specific reason is worth more marks here than agreeing with everything.

<!--
At least one deduction above is genuinely arguable. Things worth pushing back on:
  - The -1 in §1 for `Animal` not being final: that declaration shipped in the starter, and this lab
    excludes inheritance. Is it fair to deduct for not changing provided code you were told to leave
    alone?
  - The -2 in §5 for the Javadoc: the assignment never said to rewrite the provided Javadoc, and
    editing it would have added to the diff noise the same section criticises. Are those two
    deductions consistent with each other?
  - §2 asks the name check to echo the rejected value, but the provided tests at AnimalTest.java:114
    and :123 assert the exact string "Name cannot be null or blank". Splitting null from blank, or appending
    the value, would break a test that ships as the specification. Does that make the deduction wrong,
    or does it just mean the provided test is the weaker one?
  - §6 deducts 5 because introspection.md exists but is unanswered, calling that "functionally the
    same as missing." Is a blank template really equivalent to no file at all, or does the file's mere
    existence deserve partial credit — and is this deduction, being a separate deliverable with its own
    place in the rubric, double-counting regardless?
  - §5's new −1 for the unused `java.util.List` import: is one dead import from an abandoned attempt
    really equivalent in severity to the Javadoc or formatter deductions it's grouped with, or is this
    the LLM padding out a category?
Do not reject something merely because rejecting scores well — say what you actually think, with the
line number.
-->

### What it missed

What do you know is weak in your submission that the assessment did not mention? Volunteering this costs you nothing and demonstrates you understand your own code.

### What you changed

If you changed anything as a result, say what and why. If you changed nothing, say that and defend it.

---

## Declaration

- Which LLM and version you used: Claude Opus 5 (`claude-opus-5`), via Claude Code

- Confirm you understand every line you submitted, regardless of who or what wrote it: yes
