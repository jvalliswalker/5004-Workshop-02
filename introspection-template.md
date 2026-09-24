# Design Introspection

Your own reflection on the design decisions you made this week. Written in your own words — this is
distinct from the LLM's assessment of your code, and distinct from your code-walk video.

Aim for a page. Cite your actual code: name the class and method you are talking about.

## 1. What you built

In two or three sentences: which types you wrote this week, and what each one is responsible for.

## 2. Design decisions

Pick the two or three decisions you actually had to think about, and for each one:

- **What was the choice?** What were the alternatives you considered?
- **What did you pick, and why?** What would have gone wrong with the other option?
- **What did it cost?** Every real design decision costs something.

Good candidates: where you put a piece of behaviour and why it belongs there rather than somewhere
else; how you represented something so that an invalid version could not be built; where you chose to
delegate to existing code rather than re-deriving an answer.

## 3. Invariants

What does your code guarantee about itself, and where is each guarantee enforced?

For each type that validates its input: what must always be true of an instance once it exists, and
which line makes that true? If a guarantee is enforced in more than one place, say why — and whether
that is deliberate or duplication.

## 4. Testing

- Which cases did you add beyond the provided tests, and what made you think of them?
- Which test was hardest to write, and what did writing it teach you about your own design?
- What is still untested, and how would you test it if you had another hour?

## 5. What you would change

Given another day, what would you do differently — and what stopped you this week? Be specific;
"write more tests" is not an answer.

## 6. What you found hard

The honest one. What took the longest, what did you get wrong first, and what finally made it click?
This is not marked on whether you struggled — everyone does — but on whether you can say clearly
where and why.
