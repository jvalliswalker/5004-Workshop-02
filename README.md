# Assignment 01 — Track A 1: Intake: Value Objects

> **Released** Mon Sep 21, 5:00 PM PT · **Due** Fri Oct 2, 5:00 PM PT (an 11-day window)
> Late: Sat 5:00 PM → 80%, Sun 5:00 PM → 60%, **0 after Sun Oct 4, 5:00 PM PT**.
> Code-walk **video** to Canvas by Tue Oct 6, 5:00 PM PT.

> **Track A (Animal Shelter) — deliverable 1 of 5.** Track A and Track B alternate in one
> numbered sequence, so the next release is **Assignment 02**, which is Track B and shares no
> code with this. Nothing here continues from anything; this is where Track A starts.

This is the first of **five Labs** that build one program together: a model of an animal shelter.
Each lab starts from the previous lab's finished, correct code and adds one design idea. By Lab 5 you
will have a small, well-structured system you wrote yourself.

This lab is about the smallest and most important idea in the whole sequence: **make invalid states
impossible to construct.**

## What you are building

An animal's intake record. Four types, in package `edu.northeastern.shelter`:

| Type | Status | What it is |
|---|---|---|
| `Species` | **ships complete** | An enum: `DOG`, `CAT`, `BIRD`. Read it properly — your code-walk video has to explain it. |
| `IntakeException` | **ships complete** | Thrown when offered data can't describe a real animal. |
| `AgeMonths` | **partly yours** | An age in whole months. `of` and `months()` are done for you; you write the rest. |
| `Animal` | **yours** | Name, species, age, intake date — immutable, validated at construction. |

Four classes, and none of them do anything clever. That is the point. The work is in getting the
*guarantees* right, not in writing a lot of code.

## The idea: a type that cannot be wrong

Compare two ways to write the same thing:

```java
int ageMonths = -7;                    // compiles fine. is it valid? who knows.
AgeMonths age = AgeMonths.of(-7);      // throws immediately. it never gets to be wrong.
```

The second version pushes the check to the *one* place where an age comes into existence. After
that, every method that receives an `AgeMonths` gets a guarantee for free — no defensive checks, no
`if (age < 0)` scattered through the codebase, no possibility that someone forgot one.

`Animal` does the same thing for a whole record. Once you hold an `Animal`, you know its name isn't
blank and its species isn't null, because there is no way to have built one otherwise.

Three habits make that work, and all three are graded:

1. **`private final` fields, no setters.** State is fixed at construction. An intake record is a
   statement about something that happened; if a fact changes, that is a new record.
2. **Validate before you assign.** Check every argument, then assign. Throw `IntakeException` with a
   message that names what was wrong.
3. **Delegate rather than re-derive.** `Animal.toString()` should ask `AgeMonths` how to describe an
   age. If you find yourself writing the word `"month"` in `Animal`, something has gone wrong.

## What is deliberately absent

Do **not** add these. Each is coming in a later lab, and adding it now will cost you marks:

- **No inheritance.** No `Dog`, `Cat` or `Bird` classes — `Species` is an enum for now. (Lab 2)
- **No collections.** Nothing holds a list of animals yet. (Lab 2)
- **No `equals` or `hashCode`.** Two identical animals are two different objects in this lab, and
  `AnimalTest` asserts exactly that. Equality is subtler than it looks. (Lab 3)

If a design decision feels prematurely limited, that is the sequencing doing its job. Note it in your
introspection instead of building it.

## Getting started

```bash
./gradlew test          # SpeciesTest passes; the rest fail. That is the correct starting state.
```

The provided tests **are the specification** — particularly `AgeMonthsTest.toStringCombinesYearsAndMonths`,
which is fussy about singular vs plural on purpose. Reading a spec precisely is part of the exercise.

Work in this order:

1. Run the tests. Confirm `SpeciesTest` is green — that tells you your toolchain works before you've
   written a line.
2. Read `Species` and `AgeMonths.of`. They are the pattern you are about to copy — and `Species` is
   the class you will be explaining in your code-walk video, so read it like you mean it. It is nine
   lines and there is more in it than you would guess: an enum with a field and a method is a *class*,
   not a list of names.
3. Finish `AgeMonths`: `years()`, `remainderMonths()`, `isUnderOneYear()`, `toString()`.
4. Write `Animal`: constructor validation first, then the accessors, then `toString()`.
5. Add your own tests. **The provided tests already cover every line**, so the coverage number will
   not tell you whether you have added anything worth having — do not use it as your target. Write
   tests for what the provided suite does *not* check: the **content** of the exception messages, the
   boundary at `MAX_MONTHS - 1`, and any behaviour you had to infer from the Javadoc rather than from
   a test. Your work is marked on those, and on the hidden tests used at grading.

Java **21** is required (`./gradlew` handles Gradle itself). You installed it in **Assignment 00**;
that repo's `installing-java.md` is the install guide if you need it again. Check with
`java -version` — if it does not say 21, fix that before anything else.

## Deliverables and grading

See [how-to-submit.md](how-to-submit.md) for the full list, the rubric, and the exact steps.
