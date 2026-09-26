# LLM Evaluation

Produced with an LLM using the prompt in `LLM-Evaluation-prompt.md`, then reviewed and answered by
you. **Both halves are required** — an unread LLM assessment pasted in whole is worth nothing.

---

## Assessment

**Materials reviewed:** `Animal.java`, `AgeMonths.java`, `IntakeException.java`, `Species.java`,
`AnimalTest.java`, `AgeMonthsTest.java`, `SpeciesTest.java`, the JaCoCo coverage report
(`build/reports/jacoco/test/html/index.html`, regenerated from the current working tree), and
`submission/introspection.md`.

### 1. Immutability and encapsulation (25/25)

All four fields (`name`, `species`, `age`, `intakeDate`, `Animal.java:41-44`) are `private final`.
There are no setters anywhere in `Animal`. Every accessor either returns an immutable type directly
(`species()` → `Species` enum, `age()` → `AgeMonths`, which is itself `private final` with no
mutators, `intakeDate()` → `LocalDate`) or a `String`. The Javadoc on `intakeDate()`
(`Animal.java:114-119`) even states the reasoning for why returning the field directly is safe —
that's correct and shows the student understands *why*, not just the rule. Nothing is public that
doesn't need to be: `validateConstructorArguments` and `normalizeWhitespace` are both `private`. No
deductions.

### 2. Constructor validation (22/25)

Checking each required rejection individually, all five are present and each is tested:

- Null name → rejected, message "Name cannot be null" (`Animal.java:174-176`, tested
  `AnimalTest.aNullNameIsRefused`).
- Empty/whitespace-only name → rejected via `normalizeWhitespace(name).isEmpty()`
  (`Animal.java:179-181`), tested for plain empty string and for a dozen individual Unicode
  whitespace code points (`AnimalTest.anEmptyNameIsRefused`, `aNameOfOnlyWhitespaceIsRefused`).
- Null species → rejected, "Species cannot be null" (tested `aNullSpeciesIsRefused`).
- Null age → rejected, "Age cannot be null" (tested `aNullAgeIsRefused`).
- Null intake date → rejected, "Intake date cannot be null" (tested `aNullIntakeDateIsRefused`).
- Name is stored trimmed: confirmed (`normalizeWhitespace`, `Animal.java:193-195`). It actually does
  more than the spec asks — it also collapses non-standard interior whitespace to a plain space —
  but that's a superset of the required behavior, not a violation of it, and the extra behavior is
  deliberately tested (`nonstandardInteriorWhiteSpaceIsConverted`).
- Validation happens before any field is assigned: `validateConstructorArguments(...)` is called at
  `Animal.java:76`, before any `this.field = ...` assignment at lines 78-81. Correct order.
- Throws `IntakeException` (not a bare/generic exception) with a message naming the specific problem
  in every single-error case above.

The deduction is for `Animal.java:171-188`. When more than one argument is invalid at once, the
messages are collected into a `HashMap<String, String>` and joined with `String.join("; ",
invalidArguments.values())`. `Map` does not guarantee iteration order — nothing enforces which
invalid-argument message comes first when two or more fields are bad simultaneously. In practice
this is stable on a given JDK because `String.hashCode()` is specified by the JLS, but the code is
relying on that as an implementation detail rather than declaring it, and a `LinkedHashMap` or a
plain ordered check would have made the message order an explicit, tested guarantee instead of an
accident of hashing. This is exactly the one branch JaCoCo reports as uncovered in the whole
codebase — see Testing, below — so the gap was never exercised by a test either.

### 3. Correctness (15/15)

Traced `AgeMonths.toString()` (`AgeMonths.java:135-148`) by hand against all seven required inputs:

| months | years() | remainderMonths() | branch taken | output |
|---|---|---|---|---|
| 0 | 0 | 0 | `years==0` | "0 months" |
| 1 | 0 | 1 | `years==0` | "1 month" |
| 11 | 0 | 11 | `years==0` | "11 months" |
| 12 | 1 | 0 | `remainingMonths==0` | "1 year" |
| 23 | 1 | 11 | else | "1 year, 11 months" |
| 24 | 2 | 0 | `remainingMonths==0` | "2 years" |
| 25 | 2 | 1 | else | "2 years, 1 month" |

All seven match the spec, singular/plural agreement is correct in both the years and months
positions, and whole years correctly omit the months clause. `AgeMonthsTest` asserts all seven
cases directly plus the 40-year maximum.

`Animal.toString()` (`Animal.java:150-157`) builds `"%s (%s, %s, intake %s)"` from `name`,
`species.label()`, `age` (delegated `AgeMonths.toString()` via `%s`), and `intakeDate` (ISO via
`LocalDate.toString()`). For Luna (Cat, 23 months, 2026-09-21) this produces exactly
`"Luna (Cat, 1 year, 11 months, intake 2026-09-21)"`, matching the spec and
`AnimalTest.toStringFollowsTheSpecifiedFormat`. No deductions.

### 4. Testing and coverage (13/15)

**Coverage reported:** Total line coverage 100% (0 of 63 missed), instruction coverage 99% (2 of
297 missed), branch coverage 96% (1 of 30 missed) — `edu.northeastern.shelter/index.html`,
regenerated via `gradlew test jacocoTestReport` against the current working tree. Per class:
`AgeMonths` 100%/100% (instruction/branch), `Animal` 98%/91%.

Boundaries are well covered on the `AgeMonths` side: 0, 1, 11, 12, 23, 24, 25, the maximum (480),
and one past the maximum (481) are all directly asserted. Every documented exception case for
`Animal`'s constructor has its own test, and `assertThrows(IntakeException.class, ...)` is used
throughout (never a bare `Exception.class` or `RuntimeException.class` that would also pass for the
wrong exception type), and message content is checked with `assertEquals`/`assertTrue` rather than
just "it threw something" — this satisfies the rubric's concern about hollow assertions.

The one missed branch is `Animal.java:184`
(`invalidArguments.size() > 1 ? "Invalid arguments found: " : ""`) — nothing constructs an `Animal`
with two or more invalid arguments at once, so that branch's "more than one" side never runs.

Three specific untested cases:
1. Multiple simultaneous invalid constructor arguments (e.g., `name` null **and** `species` null in
   the same call) — the exact branch JaCoCo flags as missed, and the one place the HashMap-ordering
   issue above would actually surface.
2. An `Animal` built with `AgeMonths.of(AgeMonths.MAX_MONTHS)` — every `AnimalTest` case uses a
   small age (1 or 23 months), so `Animal.toString()`'s delegation is never checked at the "40
   years" edge, only `AgeMonths.toString()` is (in isolation).
3. No test checks that `years()`, `remainderMonths()`, and `months()` stay mutually consistent for
   the same instance (e.g., `years()*12 + remainderMonths() == months()`) across a range of values —
   each is currently tested against hand-picked expected numbers rather than against each other.

### 5. Code quality and style (9/10)

Every public member carries a Javadoc statement, and most say something a reader could not have
guessed from the name alone: the constructor's Javadoc spells out the validation order and the
exception contract, and `intakeDate()`'s explains *why* returning the reference is safe. `species()`
and `age()`'s Javadoc bodies are thinner — close to restating the method name plus a null
guarantee — which is defensible but the least informative of the set.

`Animal.toString()` delegates to `AgeMonths.toString()` rather than re-deriving years/months itself
— confirmed by inspection (`Animal.java:150-157` contains no arithmetic on months, and no re-derived
"year"/"month" text). If `AgeMonths`'s format ever changed, `Animal.toString()` would pick it up
automatically instead of drifting out of sync — this is exactly right and was called out as the
thing to check.

Minor deduction: `validateConstructorArguments` (`Animal.java:159-191`) builds two `HashMap`s and
loops over keys to do what is fundamentally four independent null checks plus one blank check. It's
not incorrect, and the student's own introspection explains the reasoning (see below), but it's more
machinery than four `if` statements would need, and it's what produces the ordering issue in
category 2.

### 6. Scope discipline and code walk (7/10)

No inheritance was added. `Animal` has no `equals`/`hashCode` override, and `AnimalTest`'s
`twoIdenticalAnimalsAreDifferentObjects()` explicitly locks in default identity semantics — exactly
what this lab wants. The `HashMap` usage is confined to a private validation helper, not exposed as
part of the object model, so it doesn't cross the "no collections in the domain" line the lab is
drawing.

However, `submission/introspection.md` was read in full, and it never mentions `Species`, enums, or
`label()`/`toString()` — it covers `Animal`'s constructor validation design, the whitespace-
normalization decision, invariants, and testing difficulty, but nothing about the one class the
student didn't write. The prompt specifically asks for evidence of understanding `Species` in this
document, and none is present, so no credit is available for that half of the category on the
material provided. (There is a separate `code-walk.md` video assignment that covers this same
question, but a video isn't part of what was reviewed here, and the introspection document is what
the prompt points at.)

### Total: 91/100 (25 + 22 + 15 + 13 + 9 + 7)

### The single most important thing to do differently next time

Stop relying on `Map` iteration order for anything a person reads — the multi-invalid-argument
message in `Animal.java:171-188` is the one piece of unexercised, order-dependent code in an
otherwise fully-covered class. A fixed sequence of checks (or a `LinkedHashMap`/`List<String>`)
would make the message order a guarantee instead of an accident, and writing the test that JaCoCo
says is missing would have caught this before it shipped.

### One thing done genuinely well

The whitespace testing goes well past what the spec literally asks for ("strip surrounding
whitespace"): it tests a dozen distinct Unicode space characters individually, both at the edges and
inside a name, and that testing is what surfaced that `String.trim()` alone doesn't catch
non-breaking or other Unicode spaces — motivating the `normalizeWhitespace` helper in the first
place. That's the kind of edge-case hunting the "testing and coverage" category is designed to
reward.

---

## Your response

*(Complete this section yourself: where you agree, where you don't, and what you would change given
another day. See `LLM-Evaluation-prompt.md` and `how-to-submit.md` for what's expected here.)*

### Where it is right

### Where it is wrong

### What it missed

### What you changed

---

## Declaration

- Which LLM and version you used: Claude Sonnet 5 (claude-sonnet-5), via Claude Code
- Confirm you understand every line you submitted, regardless of who or what wrote it: [yes/no]
