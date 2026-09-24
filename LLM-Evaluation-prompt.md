# LLM Evaluation Prompt — Lab 1

Paste the prompt below into your LLM together with **every source file you wrote or were asked to
read for the categories below**, your test files, your coverage figure, and any written
deliverable the prompt asks it to assess — here that is `submission/introspection.md`.

If the prompt scores a category you have not given it the material for, it will invent an
assessment rather than say so. Check the categories below against what you actually pasted.

Save the result as `submission/LLM-Evaluation.md`, then **add your own response** — where you agree,
where you don't, and what you would change given another day.

---

> You are reviewing a first-year graduate student's Java submission for a lab whose entire subject is
> **immutable value objects with constructor validation**. Assess only what is in front of you; do not
> rewrite the code for me.
>
> Score each category out of the marks shown and justify every deduction by quoting the specific line
> or the specific missing case.
>
> **1. Immutability and encapsulation (25).** Are all fields `private final`? Are there setters, or
> any method that mutates state? Does any accessor hand back a reference through which a caller could
> change this object? Is anything `public` that did not need to be?
>
> **2. Constructor validation (25).** `Animal`'s constructor must reject: a null name; a name that is
> empty or only whitespace; a null species; a null age; a null intake date. It must store the name
> with surrounding whitespace stripped. Check each of these individually and say which are missing.
> Does it validate *before* assigning any field? Does it throw `IntakeException` with a message that
> identifies what was wrong, rather than a bare or generic message?
>
> **3. Correctness (15).** `AgeMonths.toString()` must produce `"0 months"`, `"1 month"`,
> `"11 months"`, `"1 year"`, `"1 year, 11 months"`, `"2 years"`, `"2 years, 1 month"` for 0, 1, 11,
> 12, 23, 24, 25 months. Check singular/plural agreement and that whole years omit the months part.
> `Animal.toString()` must be exactly `"Luna (Cat, 1 year, 11 months, intake 2026-09-21)"` in shape.
>
> **4. Testing and coverage (15).** Do the tests cover boundaries (0 months, 11/12, the maximum, one
> past the maximum) and every exception case? Are there assertions that would still pass if the
> implementation were wrong — for example asserting a method throws *something* rather than
> `IntakeException`? Name three specific untested cases.
>
> **5. Code quality and style (10).** Does every public member carry a Javadoc purpose statement that says
> something a reader could not have guessed from the name? Does `Animal.toString()` **delegate** to
> `AgeMonths.toString()`, or does it re-derive years and months itself — and if it re-derives them,
> what breaks when the age format changes?
>
> **6. Scope discipline and code walk (10).** This lab deliberately excludes inheritance, collections
> and `equals`/`hashCode`. Did the student add any of them? Flag it if so. Then: the student must be
> able to explain `Species`, which ships complete and which they did not write — read their
> `submission/introspection.md` for evidence they understand why it is an `enum` with a field and a
> method rather than three constants.
>
> Finish with: the single most important thing this student should do differently next time, and one
> thing they did genuinely well.
