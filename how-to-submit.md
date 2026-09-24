# How to Submit — Lab 1: Intake: Value Objects

## Deliverables

1. **Implementation** — `AgeMonths` and `Animal` in `edu.northeastern.shelter`, compiling and passing
   the provided test suite.
2. **Tests** — your own JUnit tests. Tag each of your test classes `@Tag("current")`: the graders
   filter by tag, and an untagged test may not be run. (You will see `@Tag("inherited")` on the
   provided suites carried over from earlier assignments — that tag marks work that is already
   done and green. Yours are `current`.) The provided suite is the
   **specification**, and it already covers almost every line, so a coverage percentage will not
   tell you whether you have added anything worth having. Write tests for what the provided suite
   does *not* pin down: the **content** of exception messages, boundary values either side of a
   documented limit, and any behaviour you had to infer from Javadoc rather than read off a test.

   Your submission is also run against **hidden tests** at grading. Passing the provided suite is
   the floor, not the finish line.
3. **LLM Evaluation** — **`submission/LLM-Evaluation.md`**, a self-assessment of *your own* code
   produced with an LLM (start from [LLM-Evaluation-Template.md](LLM-Evaluation-Template.md)).
4. **Design introspection** — **`submission/introspection.md`**, your own reflection on your design
   decisions, invariants, testing and struggles (from
   [introspection-template.md](introspection-template.md)).
5. **Code-walk video** — to Canvas by **Tue Oct 6, 5:00 PM PT**. **Worth 10 points**,
   and recording it earns all 10 — the written feedback you get back is the real output.
   **[code-walk.md](code-walk.md) is the requirement**: it names the code you must walk in
   each of the two parts, including **code you did not write**. A video that skips the
   second part does not meet the requirement.

Written deliverables go in the **`submission/`** folder.

## Steps

### 1. Implement and test locally
```bash
./gradlew test jacocoTestReport checkstyleMain
```

**You are done when no `UnsupportedOperationException` remains.** Every stub in this repository is
exercised by at least one provided test, so a leftover stub always shows up as a failure — but check
directly anyway, because it is the clearest statement of "finished" there is:

```bash
grep -rn "throw new UnsupportedOperationException" src/main/java   # should print nothing
```

(Match `throw new`, not the bare class name — some Javadoc legitimately mentions the exception when
documenting a method that throws it, and grepping for the name alone reports those as if they were
unfinished work.)
- All tests green.
- Coverage: `build/reports/jacoco/test/html/index.html` — read it to find code you have *not*
  exercised. A diagnostic, not a target; the provided tests already push it high.
- Style: `build/reports/checkstyle/` — clear the warnings.

### 2. Write your LLM evaluation
Give the prompt in `LLM-Evaluation-prompt.md` to your LLM along with your source, your tests and your
coverage number. Save the assessment as **`submission/LLM-Evaluation.md`** using
`LLM-Evaluation-Template.md`, then add your own response to it.

### 3. Write your design introspection
Copy `introspection-template.md` to **`submission/introspection.md`** and answer it in your own
words, citing your actual code.

### 4. Commit and push
```bash
git add -A
git commit -m "Lab 1: AgeMonths + Animal"
git push
```

> ⏰ **Due Fri Oct 2, 5:00 PM PT.** Late: **Sat Oct 3, 5:00 PM → 80%**; **Sun Oct 4, 5:00 PM → 60%**;
> **zero after that.** The deadline closes hard because Lab 2's starter — which contains this lab's
> finished solution — is published Mon Oct 5 at 5:00 PM. Both late days are automatic; just push. If
> something serious happens, talk to the instructor *before* Sunday, because an extension past the
> deadline is not something we can give.

## Rubric (100 pts)

| Category | Pts | What we look for |
|---|---|---|
| Immutability & encapsulation | 25 | `private final` fields, no setters, nothing leaks a mutable reference |
| Constructor validation | 25 | Every documented rule enforced, `IntakeException` with a message that names the problem, validate-then-assign |
| Correctness | 15 | The `toString` formats exactly as specified, including singular/plural and the whole-year case |
| Testing | 15 | Cases the provided suite does **not** check — exception message content, boundaries, anything inferred from Javadoc rather than pinned by a test |
| Code quality & style | 10 | Javadoc purpose statements on every public member, Checkstyle clean, delegation over duplication |
| Introspection, LLM eval & code walk | 10 | Honest introspection + LLM self-assessment; you can explain every line you submitted **and can explain `Species`, which you did not write** |

**Marks are deducted** for adding what this lab excludes: inheritance, collections, `equals`/`hashCode`.
Those arrive in Labs 2 and 3, built on what you write here.

---

## Where the code walk is graded

Two Canvas entries, **10 points each — the code and the code walk carry equal weight.**

- **The assignment entry: 10 points**, marked from the 100-point rubric above and divided by ten.
  The rubric stays out of 100 because that is the granularity marking needs; the entry is out of 10
  because that is the weight the work carries.
- **The code-walk video: 10 points**, and recording it earns all 10. It is not marked for polish.
  [code-walk.md](code-walk.md) is the requirement.

Read that twice, because it is unusual: **explaining the work is worth as much as doing it.** That
is deliberate. You can submit code you do not understand; you cannot talk through it. The ten marks
are close to free, and the written feedback you get back is the part actually worth your time.
