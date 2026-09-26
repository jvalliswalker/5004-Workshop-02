# Introspection

## What I built

This week I build out the method functionality of the `AgeMonths` and `Animal` classes, and completed or expanded upon the test functionality for those classes, as well as the tests in `SpeciesTest.java`.  

## Design Decisions

Some design decisions I made included how to handle argument validation for the `Animal` class, how to effectively expand the existing tests to increase reliability of coverage.

For the `Animal` class argument validation, I opted to encapsulate this somewhat complex process to the private helper method `validateConstructorArguments()`, where I loop through the arguments to check for null or blank values. This improves readability in the `Animal` constructor and separates the cognitive load of understanding how validation works to the new method. If the validation remained in the constructor, understanding what happens during initialization would be more cumbersome for future readers. The thoroughness of the `validateConstructorArguments()` is valuable, but was more time consuming than a simple `if(argument == null)` check.

I also added `normalizeWhitespace()` method that replaces non-standard whitespace characters with the common space `" "` character. While this scenario is unlikely to occur within the scope of this lab, I believe that this sort of data sanitization increases the robustness of the program. It required me to find a list of alternate unicode "space" characters, as well as implementing a string replacement with a regex formula that could correctly identify the non-standard spaces.

## Invariants

The `Animal` class stores all of it's fields as invariants. Each field will be populated with a non-null, non-blank string value due to the argument validation during construction. Similarly, their private nature along with a lack of setters ensures that the values will remain unchanged throughout the object's lifecycle. As such, all method outputs are invariants as well; `myAnimal.getString()` will always return the same value after its instantiation; An animal's age will never change, as `AgeMonths` uses a static number of months, rather than a birthdate compared against the current data-time.

## Testing

For testing in the `AnimalTest` and `AgeMonths` class, I opted to add an "error message confirmation" method to confirm tests were throwing the expected messages for each exception. 

In `AnimalTest` I also added a check of non-standard whitespace characters for the purposes of valdating names were not blank and that the use of `trim()` on names always functioned as expected. 

The non-standard whitespace testing was the most difficult to implement, due to the need to identify and collect the non-standard characters and learn how HashMaps work in Java. As far as I am aware my tests are quite thorough, so I'm not clear on what additional tests I would add if given additional time.

## What you would change?

With additional time, I would likely separate the whitespace standardization functionality and its testing to a standard "input validation" utility class. I would likewise add the `confirmErrorMessage` validation method in `AnimalTest` to a generic "testing utility" class so that it could be used across test classes.

## What you found hard?

I found the seeking out of specific scenarios I had not tested for to be the more challenging part of this assignment. Identifying where gaps in the testing existed was time consuming and unintuitive, especially given that many test methods were already constructed.