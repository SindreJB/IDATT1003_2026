# Trip and activity register

Portfolio project in IDATT1003 Programming 1 (NTNU, autumn 2026): a text based application
for logging trips and activities, with a register of loggers, groups, and local sharing of
activities.

**Status:** part 2, level 1 done. `Activity` and `ActivityRegister` exist with unit tests,
and the application fills the register with test data and prints it sorted by date and
time. There are no loggers, groups or menu yet.

## Build and run

Requires JDK 25 and Maven 3.9.

```
mvn compile                                              # compile
mvn test                                                 # run the unit tests
java -cp target/classes edu.ntnu.idi.idatt.ActivityRegisterApp   # run from the command line
```

## Project structure

```
pom.xml                          Maven setup with the versions required by the assignment
src/main/java/edu/ntnu/idi/idatt
  ActivityRegisterApp.java       main(): creates the UI, calls init() and start()
  model/Activity.java            entity class: one logged trip or activity
  registry/ActivityRegister.java holds all activities: add, remove, sorted list, search by date
  ui/TextUserInterface.java      all user interaction (start() prints test data, no menu yet)
src/test/java/edu/ntnu/idi/idatt   unit tests, mirroring the package structure
  model/ActivityTest.java        positive and negative tests of Activity
  registry/ActivityRegisterTest.java  positive and negative tests of ActivityRegister
src/doc/plantuml/                PlantUML sources, *.puml (not created yet)
docs/uml/                        generated PNG diagrams for the report (not created yet)
```

Planned packages, to keep the layers apart (UI → registers → entities, never the other way):

| Package    | Contents                                                    | Unit tested |
|------------|-------------------------------------------------------------|-------------|
| `model`    | Entity classes: `Activity`, `Person`, `Group`               | Yes         |
| `registry` | Register classes holding and searching collections of entities | Yes      |
| `ui`       | Menu/commands, input reading, printing                      | No          |

Class names other than `Activity`, `Person` and `Group` (which the assignment suggests) are
my own choice, and so is the theme of the register (mountain trips, cycling, city walks ...).
Decide the theme before writing `Activity`, because it determines the extra fields.

## Requirements checklist

### Level 1 (MVP)

- [ ] Create an activity with: name of the logger, title, description, date and time of registration
- [ ] Print all activities in a structured, readable way
- [ ] Search for activities on a given date
- [ ] Delete activities

### Level 2 (needed for A/B)

- [ ] Own register of loggers; choose among earlier loggers when creating an activity
- [ ] Groups of loggers: name, one or more members, add and remove members
- [ ] Share an activity with selected loggers or with a group
- [ ] Mark an activity as private or shared
- [ ] Show activities shared with a given logger
- [ ] Search by period (from date, to date)
- [ ] Search by logger
- [ ] Search by a word in title or description
- [ ] Statistics: number of activities per logger, shown as a table

### Own extensions (count positively)

Pick a few that fit the theme, for example: activity type, duration or distance, rating 1–5,
location, unshare an activity, shared vs. private counts in the statistics.

### Limitations set by the assignment

Text based only, runs from IDE and command line, single machine, no network, no file
handling, no inheritance/interfaces, no self-made exception classes.

## Plan

The plan follows the three parts of the assignment. Commit after every step, with a message
that says what changed. Push often.

### Step 0 – Repository

- [ ] Create the repository from the course template on NTNU GitHub (see Canvas, "Opprette
      Mappe-prosjektet fra GitHub") and connect this project to it
- [ ] Install JDK 25 (the machine currently has JDK 21, which cannot build with `release 25`)
- [ ] Install the CheckStyle (Google checks) and SonarQube plugins in the IDE
- [ ] First commit and push

### Step 1 – Entity class `Activity` (part 1)

- [x] Choose fields and data types; `LocalDateTime` for date and time
- [x] Decide which fields are fixed at creation and which can change afterwards
- [x] Set-methods for all fields, validating their parameter, but only the ones that should
      be changeable are `public`; the constructor uses the set-methods
- [x] Throw fitting exceptions on invalid data, not `IllegalArgumentException` everywhere
- [x] Class Javadoc: role, fields with reasons for the data types, mutability, strategy for invalid data
- [x] `ActivityTest` with positive and negative tests
- [x] `start()` creates 3–4 activities and prints them through a separate print method
- [ ] Tag `v0.1.0`

### Step 2 – Activity register (part 2, level 1)

- [x] Choose the collection class (`ArrayList`, `HashMap`, `HashSet` ...) and write down why
- [x] Add, delete, search by date, and access to all activities sorted by date/time
- [x] Do not hand out the internal collection itself (return an iterator or a copy)
- [x] Write the test plan in the Javadoc of the test class first, then the tests
- [x] `init()` creates the register, `start()` fills it with test data and prints an overview
- [ ] Tag `v0.2.0`

### Step 3 – Loggers, groups and sharing (part 2, level 2)

- [ ] `Person` and a person register; `Activity` refers to a `Person` object instead of a name
- [ ] `Group` with name and members, add/remove member; a group register
- [ ] Model sharing: private/shared, and who it is shared with (persons and/or groups)
- [ ] Remaining searches (period, logger, word) and statistics per logger
- [ ] Tests for every new class, positive and negative
- [ ] Tag `v0.3.0`

### Step 4 – User interface and own touch (part 3)

- [ ] Choose menu or commands; build it in `TextUserInterface`
- [ ] Every level 1 and 2 function reachable from the UI
- [ ] Robust input: wrong menu choice, text where a number is expected, invalid date, empty
      input. The program never crashes on user input, and exceptions from the lower layers
      are caught here and turned into understandable messages
- [ ] Own extensions
- [ ] Refactor for coupling and cohesion; note what the code looked like before and why it
      changed (needed in the report)
- [ ] Let someone else try the program and note what they struggled with (user test for the report)

### Step 5 – Finish

- [ ] CheckStyle reports no (or minimal) violations
- [ ] `mvn compile` and `mvn test` pass on a fresh clone
- [ ] Javadoc on every public class and method, including get- and set-methods
- [ ] Version in `pom.xml` set to `1.0.0`, tag `v1.0.0`, push with tags
- [ ] ZIP file and report delivered in Inspera (week 50)

## What the examiners look for in the code

Taken from the grading guide, column B/A.

| Area | Aim for |
|------|---------|
| Classes | Robust constructors, good encapsulation, `final`/`static` where suitable, own classes as data types where sensible |
| Get/set | Set-methods for all fields with validation, few of them public, used by the constructor |
| Methods | Parameters validated first (guard conditions); return real types, not `String`; `toString()` is not used for text shown to the user |
| Collections | Own register classes (no collection directly in the UI or main), several registers, the choice of collection is argued for |
| Loops | for-each only when the whole collection is visited, without `break`/`return` inside; `while` with `Iterator` when searching; streams and lambdas for filtering |
| Javadoc | All classes and public methods; describes what, not how; no "This method ..." or "Gets the ..."; consistent `@param`, `@return`, `@throws`; some HTML formatting |
| Robustness | All parameters validated; sensible, varied exception types |
| Design | One clear responsibility per class, one task per method, delegation to helper methods, loose coupling, UI separated from logic |
| Tests | Descriptive names, Arrange-Act-Assert, negative tests in every test class, exceptions tested, `@BeforeEach` where useful, high coverage, no dependence on test order |
| Git | Frequent small commits, good messages, central repository, tags that match the version in `pom.xml` |
| Usability | Intuitive interaction, understandable output, wrong input handled well |

## Report

About 2500–3000 words plus figures and small code snippets, Norwegian or English, written in
the Word template from the course. It counts 30 %. Most of the text belongs in Results and
Discussion; the first chapters are short. Write it alongside the code.

| Chapter | What to include |
|---------|-----------------|
| 1 Introduction | Short description of the project. Requirement specification: functional requirements for level 1 and 2, non-functional requirements, limitations. Use case diagram |
| 2 Theory | Only theory that is referred to later: coupling, cohesion, responsibility driven design, layered architecture, encapsulation, robust code and exceptions, unit testing (positive/negative, AAA), code style (Google, CheckStyle), good user interaction. Cite the sources (textbook, course wiki, Javadoc) |
| 3 Method | Tools (IDE, Maven, JUnit, Git/GitHub, CheckStyle, SonarQube, PlantUML, AI tools). Way of working: three parts with feedback meetings, small steps, test alongside code, version control, refactoring |
| 4 Results | The final solution. Class diagram, possibly sequence diagram of one flow (e.g. sharing an activity), screenshots of the UI. Role and responsibility of each class, not every method. Design choices with reference to chapter 2. Original vs. refactored classes/methods and why. Own extensions. Result of the user test. No discussion here |
| 5 Discussion | The result measured against the requirements and the theory, proven with concrete examples from the code: how the UI is separated from the logic, how sharing and groups are modelled, where coupling is low and cohesion high, and where it is not. Weaknesses and known limitations |
| 6 Conclusion | Half a page at most. Is the project in line with the principles of the course, what was achieved, what would be done differently next time |
| Appendix | AI log (table below) and the AI declaration form |

Questions the assignment explicitly wants answered in the report:

- Why this collection class for the register?
- How is the sorted list of activities returned, how was the solution found, and what does
  the code do, explained in own words?
- How does an activity refer to its logger, and how is sharing modelled? Why?
- Which changes were made to the original specification and design, and why do they give a
  better design?
- How does the solution meet the principle of layered architecture?
- Which parts of the code had AI help, why does that code solve the problem, and what does it do?

Extras that lift the report: cross-references to figures and chapters, a bibliography, and a
clear line from theory through results to discussion.

## AI log

Required as an appendix to the report: at least one row per part, and at least one case per
part where an AI suggestion was corrected or rejected. Keep it up to date here and copy it
into the report at the end. Code with substantial AI help must also be marked with a comment.

| Part | Purpose/role | Prompt (short) | Central suggestion from AI | Accepted? | Reason |
|------|--------------|----------------|----------------------------|-----------|--------|
| Setup | Project setup (Claude Code) | "I have attached PDFs of the project plan and criteria. Help me create a readme with a project plan containing the AI log." | Maven setup, `.gitignore`, empty `ActivityRegisterApp` and `TextUserInterface`, this README | | |