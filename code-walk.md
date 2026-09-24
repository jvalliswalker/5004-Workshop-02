# Code Walk — Assignment 01: Intake: Value Objects

**Due Tue Oct 6, 5:00 PM PT · upload to Canvas · 10 points**

Two parts. Both are required, and the second one is the one people forget.

## Part 1 — Your own code

**Walk through `Animal`'s constructor and explain what it refuses to build.**

Take the checks in order and say, for each, what invalid animal it makes impossible. Then answer the
question the whole lab turns on: once you are holding an `Animal`, what do you *know* about it
without checking anything?

## Part 2 — Code you did not write

**Walk through `Species`.** It ships complete, it is nine lines, and there is more in it than you
would guess. Answer:

- Why is it an `enum` rather than three `String` constants or the numbers 1, 2, 3?
- What is `label()` for, and why does `toString()` return it?
- And the one worth pausing on: `Species` has a **field** and a **method**. What does that tell you
  about what an enum actually *is*?

Explaining code you did not write is a habit this course builds deliberately, and it starts here.
Every later assignment asks more of it.

## How this is graded

**10 points, and the whole 10 is yours for doing it.** Record the walk, cover both parts, upload it.
There is no polish score, no time limit to hit, and no penalty for restarting a sentence or for
saying "I got this bit wrong, and here is what I now think it should be" — that last one is one of
the better things you can say on a code walk.

Marks come off in one case only: **no video, or a video that does not walk any code.** Late follows
the standard 10%/day.

**The grade is not the point — the feedback is.** This is the one place in the course where someone
responds to how you *explain* a design rather than to whether your tests went green. You will get
written comments back. Read them; they are worth more than the ten points.

## Practical

- **Share your screen with the code visible** and talk over it. Slides are not wanted.
- **Five to ten minutes is plenty.** Better to cover both parts than one part exhaustively.
- Any recording tool is fine (Zoom, QuickTime, Panopto). Upload to Canvas — a link is fine if the
  file is large.
- Say your name at the start.
