# LLM Evaluation

Produced with an LLM using the prompt in `LLM-Evaluation-prompt.md`, then reviewed and answered by
you. **Both halves are required** — an unread LLM assessment pasted in whole is worth nothing.

---

## Assessment

Paste the LLM's assessment here, in full and unedited.

Its scoring categories are the ones in `LLM-Evaluation-prompt.md`, which are the same categories and
the same weights as the rubric in `how-to-submit.md`. If the LLM invents different categories or
weights, say so below rather than silently accepting them.

**Coverage reported:** 100% line coverage (49/49). Also 100% branch (28/28), 100% instruction
(223/223), 100% method (18/18), 4/4 classes — from `build/reports/jacoco/test/html/index.html`.
32 tests, 0 failures. Checkstyle `src/main`: 0 warnings.

---

> The assessment below was produced from all four classes in `src/main`, all three test classes in
> `src/test`, the git history (used to separate provided scaffolding from my own work), and the
> build reports already on disk.

**Hard data:** 32 tests, 0 failures. JaCoCo: 100% line (49/49), 100% branch (28/28), 100% method
(18/18). Checkstyle main: zero warnings. No `throw new UnsupportedOperationException` remains in
`src/main/java`.

### Total — 82/100

| Category | Score |
|---|---|
| 1. Immutability & encapsulation | 24/25 |
| 2. Constructor validation | 21/25 |
| 3. Correctness | 14/15 |
| 4. Testing & coverage | 11/15 |
| 5. Code quality & style | 7/10 |
| 6. Scope discipline & code walk | 5/10 |
| **Total** | **82/100** |

*Category note:* these weights (25/25/15/15/10/10) match `how-to-submit.md` lines 78-85 exactly. One
name differs: the rubric's category 6 is "Introspection, LLM eval & code walk," which grades two
written deliverables; the prompt's version is "Scope discipline and code walk," which grades a thing
the rubric handles as a blanket deduction at `how-to-submit.md:87`. Both were graded, and they pull
in opposite directions — see §6.

---

### 1. Immutability and encapsulation — 24/25

Correct on every substantive point.

- All fields `private final`: `Animal.java:39-42` and `AgeMonths.java:26`.
- No setters, and no method assigns to a field outside a constructor.
- **No accessor leaks a mutable reference.** Every return type is deeply immutable: `String`, an enum
  constant, `AgeMonths` (itself `private final int` with a private constructor), and `LocalDate`.
  Returning `this.intakeDate` directly at `Animal.java:131` is safe, and it is safe for the reason
  the Javadoc gives, not by accident.
- `public static final int MAX_MONTHS = 480` at `AgeMonths.java:24` is the only public field, and it
  is a primitive constant the tests legitimately reference. Nothing else is public that need not be.
- `AgeMonths` keeps the private constructor + static factory intact (`AgeMonths.java:40`, `:57`)
  rather than widening the constructor for convenience — a common shortcut not taken.

**−1:** `public class Animal` at `Animal.java:37` is not `final`, while `AgeMonths.java:21` is
`public final class`. A subclass could override `name()` to return something other than the validated
field, and the class-level Javadoc's promise that "nothing — including this class — can change them
after construction" would no longer hold for instances of that subclass. In fairness, that
declaration shipped that way and the lab excludes inheritance, so changing it is arguably out of
scope — the deduction is for not noticing the asymmetry, which is exactly the question a reviewer
asks about a value object. This is the kind of thing to raise in the code walk rather than edit.

Minor, not scored: `return name;` at `Animal.java:97` but `return this.species;` at `:106`,
`this.age` at `:115`, `this.intakeDate` at `:131`. Pick one.

### 2. Constructor validation — 21/25

Taking the five required rejections individually against `Animal.java:72-89`:

| Rule | Present? | Line |
|---|---|---|
| null name | ✅ | `name == null` (73) |
| empty or whitespace-only name | ⚠️ partial | `name.trim().isEmpty()` (73) |
| null species | ✅ | 76 |
| null age | ✅ | 79 |
| null intake date | ✅ | 82 |
| name stored stripped | ⚠️ partial | `this.name = name.trim();` (85) |

**Validate-before-assign: yes, correctly.** All four `throw` statements precede all four assignments
— no field is written before every check has run. That ordering is the whole point of the category,
and it is right.

**Messages identify the problem: yes.** `"Species cannot be null"`, `"Age cannot be null"`,
`"Intake date cannot be null"` each name the offending argument. Nothing generic, nothing bare, and
`IntakeException` is thrown in all four cases.

**−2, the real defect: `trim()` is the wrong method on Java 21.** `String.trim()` removes only
characters with code point ≤ `U+0020`. It does not recognise Unicode whitespace. So:

```java
new Animal(" ", Species.CAT, AgeMonths.of(1), INTAKE)   // U+2003 EM SPACE
```

is **accepted**. The name passes `trim().isEmpty()` (false), and is stored unchanged. The animal's
`name()` then returns a string that renders as blank, directly violating the contract left in place
at `Animal.java:94` — *"@return the name, never `null` and never blank."* `isBlank()` and `strip()`
(Java 11+) use `Character.isWhitespace` and reject it. This is a live hole, not a hypothetical: it is
exactly the class of input a hidden test would probe.

**−1: null and blank share one branch and one message.** `Animal.java:73-75` collapses two distinct
faults into `"Name cannot be null or blank"`. The rubric asks for a message that names *what was
wrong*; "null or blank" tells the caller it was one of two things without saying which. Every other
argument got its own check — the name deserved two.

**−1: the messages don't name the offending value.** Contrast the worked example the assignment says
to read first, `AgeMonths.java:59`:

```java
throw new IntakeException("age in months cannot be negative, was " + months);
```

That `, was -1` is the part that makes a message useful in a stack trace. The name check could have
echoed the rejected value the same way. There is even a test asserting that `AgeMonths` does this
(`AgeMonthsTest.java:131-136`, "the message should name the value that was rejected") — and then
`Animal` is not held to the same standard.

Not scored: `name.trim()` is computed twice (73, 85); and these messages are capitalized sentences
while the shipped ones are lowercase fragments. Cosmetic, but a grader reading both files notices.

### 3. Correctness — 14/15

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

**`Animal.toString()` — exactly right in shape.** `Animal.java:158` produces
`Luna (Cat, 1 year, 11 months, intake 2026-09-21)`. `%s` on `intakeDate` yields `LocalDate`'s ISO
form; `species.label()` yields `Cat`.

**−1: field shadowing at `AgeMonths.java:138`.**

```java
int years = years();
int months = remainderMonths();   // shadows the field `months`
```

The local `months` means *remainder months*; the field `months` means *total months*. Inside this
method the field is now unreachable by simple name, and the same identifier means two different
quantities depending on where you stand in the file. The code is correct today, but any later edit
that writes `months` expecting the field silently gets the remainder. `int rem = remainderMonths();`
costs nothing.

Not scored, but worth a sentence in the introspection: `AgeMonths.java:86` uses
`Math.floorDiv(months, 12)` while `:98` uses plain `months % 12` and `:107` uses plain `months / 12`.
`floorDiv` differs from `/` only for negative operands, which `of()` has already made impossible. So
one method defends against a case the invariant rules out, and its two neighbours don't. Either the
invariant is trusted or it isn't — inconsistency is the thing to avoid. (Trust it; that's what the
value object is *for*.)

### 4. Testing and coverage — 11/15

**Coverage is 100% on every counter — and that is the least interesting fact here.**
`how-to-submit.md:10-14` warns that the provided suite already covers almost every line, so the
percentage cannot tell you whether you added anything worth having. It can't, and the gaps below are
all invisible to it.

**Boundaries: covered.** 0 (`AgeMonthsTest.java:31`), 11/12 (`:104-106`, `:112-119`), the maximum
(`:36`), one past it (`:68`), and −1 (`:53`).

**No test asserts a weaker exception type.** Every `assertThrows` names `IntakeException.class`; not
one says `Exception.class` or `RuntimeException.class`. The specific trap in this category, avoided.

**The best thing in this submission is in this file.** All six provided `assertThrows` calls in
`AnimalTest` — which asserted only *that* something was thrown — were upgraded to capture the
exception and assert its exact message (`AnimalTest.java:56-113`). That is precisely what
`how-to-submit.md:13` asks for, and it is the difference between a test that pins behaviour down and
one that just watches it fail.

**−2: a real assertion was replaced with a tautology.** `SpeciesTest.java:26-31`:

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
loops 479 times, and the `try`/`catch`/`fail` wrapper is redundant — an uncaught `IntakeException`
already fails a JUnit test, with a better stack trace than the string it builds. Iterating every
value between two boundaries tests nothing that testing the boundaries doesn't. Same idiom problem at
`:58-65` and `:73-82`: the better tool is already in use at `:132`, where `assertThrows` *returns*
the exception to assert on. Also, `AgeMonths.of(-1).toString()` at `:60` and `:75` — the `.toString()`
is dead; `of()` throws before it can be called.

**−1: a string-concatenation bug in a failure message.** `AgeMonthsTest.java:76`:

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
it, and the reformatting discussed in §5 makes that harder than it should be.

#### Three specific untested cases

1. **`AgeMonths.of(AgeMonths.MAX_MONTHS).toString()`.** 480 is tested as *accepted* (`:36`) but never
   as *rendered*. Every `toString` assertion uses 0–25, so the whole-years branch is only ever
   exercised at one and two years. Nothing pins `"40 years"`, and nothing checks `isUnderOneYear()`,
   `years()` or `remainderMonths()` at the top boundary.

2. **A name whose whitespace isn't ASCII.** `new Animal(" ", Species.CAT, AgeMonths.of(1),
   INTAKE)` — the live bug from §2. Nothing in `AnimalTest` uses anything but spaces: `:42` uses
   `"  Luna  "`, `:78` uses `"   "`. A tab-and-newline case (`"\t\n Luna \t\n"`) is untested too, and
   it would have passed — which is why the Unicode one is the test worth writing.

3. **Which check wins when more than one argument is bad.** `new Animal(null, null, null, null)`
   throws *something*, but no test says which message. The order of the four checks is currently free
   to change without a single test noticing. Related and equally cheap: `assertSame(INTAKE,
   luna.intakeDate())` at `:129` has no counterpart for `age()` — the accessor most likely to be
   "helpfully" rewritten to return a copy later.

### 5. Code quality and style — 7/10

**Delegation — the question asked: `Animal.toString()` delegates, correctly and completely.**
`Animal.java:158` passes `age` straight into `%s` and lets `AgeMonths.toString()` do the work. The
words "year" and "month" appear nowhere in `Animal.java`, which is the test the Javadoc at `:148-152`
sets. Change the age format — to `"1y 11m"`, or to add weeks — and `Animal` needs no edit and its
tests stay green. Had it been re-derived, every format change would mean editing two files, and they
would drift: `AgeMonths.toString()` would say one thing and `Animal.toString()` another, with no test
able to tell you which was intended. `species.label()` is also called explicitly rather than leaning
on `Species.toString()` — the better choice, since it doesn't depend on `toString()` continuing to
return the label.

**But the same principle is missed one level down.** `AgeMonths.java:107`:

```java
public boolean isUnderOneYear() {
  return months / 12 < 1;
}
```

`years()` is defined immediately above and computes exactly this. `return years() < 1;` — or plainly
`return months < 12;` — says what the method means. The concept is clearly held; apply it
consistently.

**−2: the Javadoc is still addressed to the student.** Every public member carries a purpose
statement, and several say things a reader could not guess — but not one sentence of it is the
student's; the git history confirms only method bodies changed. That's fine in itself; the problem is
what the inherited text still says in a file being submitted as finished work:

- `AgeMonths.java:19`: *"...ship complete as your worked example of the pattern; **you implement the
  rest**."*
- `AgeMonths.java:48`: *"**Ships complete as the worked example. Read it before you write anything
  else**..."*
- `AgeMonths.java:114`: *"The exact format is specified by the provided tests, and it is fussy on
  purpose — **reading a specification precisely is part of the exercise**."*
- `Animal.java:48`: *"**Validate every argument before assigning any field, and throw**
  `IntakeException`..."* — an instruction in the imperative, where the API contract should be.

These were assignment prompts. In a submission they are stale: they describe work to be done rather
than what the code does, and `Animal`'s constructor Javadoc now tells a caller what the *implementer*
must do. Rewriting them into descriptions is part of finishing.

**−1: a formatter ran over the whole repository.** Compared against the initial commit, roughly 200
lines of diff are pure re-wrapping of provided Javadoc into ragged lines — e.g. `AgeMonths.java:7-12`,
where "it wraps it in / a way that makes / an invalid age impossible to construct" now breaks
mid-phrase. The actual contribution — four method bodies and a constructor — is buried in it. Two
practical costs: a grader can't see what was written, and when Lab 2's starter arrives containing
this lab's solution, merging will be worse than it needed to be. `.vscode/settings.json` and a second
`test { }` block in `build.gradle` also went in (commit `7b4e072`); the duplicate block is harmless to
Gradle but it's editor configuration in a graded repo.

Credit where due: **Checkstyle reports zero warnings on `src/main`** — `build/reports/checkstyle/main.xml`
lists all four files with no violations, which satisfies `how-to-submit.md:52`. `AgeMonths.java:145`
runs to 116 characters, under the 120 limit, but it is the one line the formatter left alone and the
least readable in the file.

### 6. Scope discipline and code walk — 5/10

**Scope discipline: spotless — full marks on this half.**

- **Inheritance:** none added. `Animal` declares no `extends`; `IntakeException extends
  IllegalArgumentException` shipped that way and was left alone.
- **Collections:** none. The only import across both classes written is `java.time.LocalDate`
  (`Animal.java:3`).
- **`equals`/`hashCode`:** not added — and this is the one worth naming, because it is the tempting
  one. `AnimalTest.java:133-140` asserts their *absence*, and it passes. Plenty of students "fix"
  that test. This one read the comment and left the boundary where the lab put it.

**Introspection: −5, because there is nothing to read.**

`submission/` contains only `.gitkeep`. There is **no `introspection.md`** and **no
`LLM-Evaluation.md`** — both required by `how-to-submit.md:18-22`. `introspection-template.md` is
untouched at the repo root, still carrying its unanswered prompts.

So there is no evidence either way about `Species`. The three questions `code-walk.md:20-24` requires
are unanswered anywhere in the repository:

- Why an `enum` rather than three `String` constants or 1/2/3?
- What is `label()` for, and why does `toString()` return it?
- `Species` has a **field** (`Species.java:19`) and a **method** (`:30`) — what does that tell you
  about what an enum actually *is*?

The video is graded separately on Canvas and cannot be assessed here, so this deduction is for the
missing written deliverable only. It is also the cheapest five marks in the entire rubric to recover:
the file does not exist yet, and writing it costs an hour.

One thing to have ready for that third question, since it's the one people fumble: the submitted code
already answers it. `species.label()` at `Animal.java:158` calls a *method* on a *constant*. Three
`String` constants could not have done that. An enum constant is an object: `DOG`, `CAT` and `BIRD`
are three instances of a class, each constructed with its own `label`, which is why `Species` can
carry behaviour and per-constant state while still being a closed, exhaustive set the compiler can
check.

---

### The one thing to do differently next time

**Write the test that could fail, not the test that will pass.** Coverage is 100% on every counter —
lines, branches, methods — and there is still a live bug in `Animal`'s constructor that a hidden test
can find in one line. The three places this shows up are the same mistake wearing different clothes:
a literal assertion replaced with one that compares the implementation to itself
(`SpeciesTest.java:27-31`); a loop over 479 values that all behave identically instead of the two that
don't (`AgeMonthsTest.java:41-50`); and no attempt at a name made of whitespace `trim()` doesn't
recognise. Before writing an assertion, ask the one question that matters: *what would have to be
broken for this to fail?* If the honest answer is "nothing plausible," the test is decoration. That
habit would have caught the `trim()` bug on its own.

### The one thing done genuinely well

**Delegation where it counts, and the handed-down tests were strengthened.** `Animal.toString()`
contains neither "year" nor "month" (`Animal.java:158`) — the age formats itself, so the format lives
in exactly one place and `Animal` is immune to changing it. That is the design lesson the whole lab is
built around, and plenty of otherwise-correct submissions re-derive `months / 12` inside `Animal` and
never notice the duplication until it bites. Separately, all six of the provided bare `assertThrows`
calls in `AnimalTest` were rewritten to assert exact message content (`AnimalTest.java:56-113`) — the
recognition that "it threw" is a weaker claim than "it threw *this*." That instinct is the right one;
§4 is about pointing it at the cases where it would have found something.

---

## Your response

The part that is actually marked. For each point below, a few sentences.

### Where it is right

Which criticisms do you accept? For each, say what you would change and why you agree.

<!--
Candidates, strongest first:
  - The trim() / U+2003 hole (§2). Do you accept that isBlank()/strip() is the right fix, and can you
    say why trim() exists at all?
  - The tautological SpeciesTest loop (§4). What would you restore, and what would you keep?
  - The fail() concatenation bug at AgeMonthsTest.java:76 (§4).
  - Field shadowing at AgeMonths.java:138 (§3).
-->

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
  - §2 asks the name check to echo the rejected value, but the provided test at AnimalTest.java:60
    asserts the exact string "Name cannot be null or blank". Splitting null from blank, or appending
    the value, would break a test that ships as the specification. Does that make the deduction wrong,
    or does it just mean the provided test is the weaker one?
  - §6 deducts 5 for a missing introspection.md, which is a separate deliverable with its own place in
    the rubric. Is that double-counting?
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
