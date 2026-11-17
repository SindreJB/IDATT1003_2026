package edu.ntnu.idi.idatt.ui;

import edu.ntnu.idi.idatt.model.Activity;
import edu.ntnu.idi.idatt.model.Group;
import edu.ntnu.idi.idatt.model.Person;
import edu.ntnu.idi.idatt.registry.ActivityRegister;
import edu.ntnu.idi.idatt.registry.GroupRegister;
import edu.ntnu.idi.idatt.registry.PersonRegister;
import java.io.UncheckedIOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Text based user interface of the trip and activity register.
 *
 * <p>Responsible for all interaction with the user: showing a menu, carrying out the chosen
 * action and presenting the result in the console. The class holds no business logic of its
 * own. It delegates to the registers and the entity classes, and leaves reading and checking
 * of input to an {@link InputReader}.
 *
 * <p><b>Robustness</b><br>
 * The {@code InputReader} only hands over valid values, and the lists offered to choose from
 * leave out choices the model would refuse. Should a lower layer still reject an action, its
 * exception is caught here and shown as a message, and the user is returned to the menu.
 *
 * <p>The menu and the actions were written with help from Claude Code (AI), see the AI log in
 * the README.
 */
public class TextUserInterface {

  private static final int QUIT = 0;
  private static final int REGISTER_ACTIVITY = 1;
  private static final int SHOW_ALL_ACTIVITIES = 2;
  private static final int DELETE_ACTIVITY = 3;
  private static final int SEARCH_ON_DATE = 4;
  private static final int SEARCH_IN_PERIOD = 5;
  private static final int SEARCH_BY_LOGGER = 6;
  private static final int SEARCH_BY_WORD = 7;
  private static final int SHOW_SHARED_WITH_PERSON = 8;
  private static final int SHARE_ACTIVITY = 9;
  private static final int MAKE_ACTIVITY_PRIVATE = 10;
  private static final int REGISTER_PERSON = 11;
  private static final int REGISTER_GROUP = 12;
  private static final int ADD_GROUP_MEMBER = 13;
  private static final int REMOVE_GROUP_MEMBER = 14;
  private static final int SHOW_PERSONS_AND_GROUPS = 15;
  private static final int SHOW_STATISTICS = 16;

  private static final int SHARE_WITH_PERSON = 1;
  private static final int SHARE_WITH_GROUP = 2;

  private PersonRegister personRegister;
  private GroupRegister groupRegister;
  private ActivityRegister activityRegister;
  private InputReader inputReader;

  /**
   * Prepares the application for use by creating the objects the user interface depends on.
   */
  public void init() {
    personRegister = new PersonRegister();
    groupRegister = new GroupRegister();
    activityRegister = new ActivityRegister();
    inputReader = new InputReader(new Scanner(System.in));
  }

  /**
   * Fills the registers with test data and runs the menu until the user chooses to quit or
   * the input ends.
   */
  public void start() {
    addTestData();
    System.out.println("Trip and activity register");
    try {
      boolean quitChosen = false;
      while (!quitChosen) {
        printMenu();
        int choice = inputReader.readIntInRange("Choose an option: ", QUIT, SHOW_STATISTICS);
        System.out.println();
        quitChosen = choice == QUIT;
        if (!quitChosen) {
          runMenuChoice(choice);
        }
      }
    } catch (UncheckedIOException endOfInput) {
      System.out.println();
      System.out.println("No more input.");
    }
    System.out.println("Goodbye.");
  }

  private void printMenu() {
    System.out.println();
    System.out.println("=== Main menu ===");
    System.out.println("Activities");
    System.out.println("   1. Register an activity");
    System.out.println("   2. Show all activities");
    System.out.println("   3. Delete an activity");
    System.out.println("Search");
    System.out.println("   4. Activities on a date");
    System.out.println("   5. Activities in a period");
    System.out.println("   6. Activities by a logger");
    System.out.println("   7. Activities containing a word");
    System.out.println("   8. Activities shared with a person");
    System.out.println("Sharing");
    System.out.println("   9. Share an activity");
    System.out.println("  10. Make an activity private");
    System.out.println("Persons and groups");
    System.out.println("  11. Register a person");
    System.out.println("  12. Register a group");
    System.out.println("  13. Add a member to a group");
    System.out.println("  14. Remove a member from a group");
    System.out.println("  15. Show all persons and groups");
    System.out.println("Statistics");
    System.out.println("  16. Number of activities per logger");
    System.out.println();
    System.out.println("   0. Quit");
  }

  private void runMenuChoice(int choice) {
    try {
      switch (choice) {
        case REGISTER_ACTIVITY -> registerActivity();
        case SHOW_ALL_ACTIVITIES ->
            printActivities(activityRegister.getActivitiesSortedByDateTime());
        case DELETE_ACTIVITY -> deleteActivity();
        case SEARCH_ON_DATE -> searchOnDate();
        case SEARCH_IN_PERIOD -> searchInPeriod();
        case SEARCH_BY_LOGGER -> searchByLogger();
        case SEARCH_BY_WORD -> searchByWord();
        case SHOW_SHARED_WITH_PERSON -> showActivitiesSharedWithPerson();
        case SHARE_ACTIVITY -> shareActivity();
        case MAKE_ACTIVITY_PRIVATE -> makeActivityPrivate();
        case REGISTER_PERSON -> registerPerson();
        case REGISTER_GROUP -> registerGroup();
        case ADD_GROUP_MEMBER -> addGroupMember();
        case REMOVE_GROUP_MEMBER -> removeGroupMember();
        case SHOW_PERSONS_AND_GROUPS -> printPersonsAndGroups();
        case SHOW_STATISTICS -> printStatistics();
        default -> throw new IllegalArgumentException("unknown menu choice " + choice);
      }
    } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException
        | DateTimeException rejectedByModel) {
      System.out.println("Could not complete the action: " + rejectedByModel.getMessage());
    }
  }

  // ----- Activities -----

  private void registerActivity() {
    Person logger = chooseOrRegisterLogger();
    String title = inputReader.readNonBlankText("Title: ");
    String description = inputReader.readText("Description (may be empty): ");
    LocalDateTime registrationDateTime = inputReader.readPastDateTimeOrNow(
        "Date and time (dd.mm.yyyy hh:mm, empty for now): ");
    activityRegister.addActivity(
        new Activity(logger, title, description, registrationDateTime));
    System.out.println("Activity registered. It is private until you share it (option "
        + SHARE_ACTIVITY + ").");
  }

  private Person chooseOrRegisterLogger() {
    List<Person> persons = personRegister.getPersonsSortedByName();
    if (persons.isEmpty()) {
      System.out.println("No loggers are registered yet, so a new one is registered first.");
      return registerPerson();
    }
    return inputReader.chooseFromList(
            "Who is logging the activity?", persons, Person::getName, "Someone new")
        .orElseGet(this::registerPerson);
  }

  private void deleteActivity() {
    Optional<Activity> activity = chooseActivity(activityRegister.getActivitiesSortedByDateTime());
    if (activity.isEmpty()) {
      return;
    }
    if (inputReader.readYesNo("Delete \"" + activity.get().getTitle() + "\"? (y/n): ")) {
      activityRegister.removeActivity(activity.get());
      System.out.println("Activity deleted.");
    } else {
      System.out.println("Nothing deleted.");
    }
  }

  // ----- Search -----

  private void searchOnDate() {
    LocalDate date = inputReader.readDate("Date (dd.mm.yyyy): ");
    printActivities(activityRegister.findActivitiesOnDate(date));
  }

  private void searchInPeriod() {
    LocalDate fromDate = inputReader.readDate("From date (dd.mm.yyyy): ");
    LocalDate toDate = inputReader.readDate("To date (dd.mm.yyyy): ");
    if (toDate.isBefore(fromDate)) {
      System.out.println("The to date cannot be before the from date.");
      return;
    }
    printActivities(activityRegister.findActivitiesInPeriod(fromDate, toDate));
  }

  private void searchByLogger() {
    inputReader.chooseFromList("Whose activities?", personRegister.getPersonsSortedByName(),
            Person::getName, "Cancel")
        .ifPresent(logger -> printActivities(activityRegister.findActivitiesByLogger(logger)));
  }

  private void searchByWord() {
    String word = inputReader.readNonBlankText("Word to search for: ");
    printActivities(activityRegister.findActivitiesContainingWord(word));
  }

  private void showActivitiesSharedWithPerson() {
    inputReader.chooseFromList("Shared with whom?", personRegister.getPersonsSortedByName(),
            Person::getName, "Cancel")
        .ifPresent(person -> printActivities(activityRegister.findActivitiesSharedWith(person)));
  }

  // ----- Sharing -----

  private void shareActivity() {
    Optional<Activity> activity = chooseActivity(activityRegister.getActivitiesSortedByDateTime());
    if (activity.isEmpty()) {
      return;
    }
    boolean doneSharing = false;
    while (!doneSharing) {
      System.out.println("Shared with: " + describeRecipients(activity.get()));
      int choice = inputReader.readIntInRange(
          "Share with 1) a person, 2) a group, or 0) done: ", 0, SHARE_WITH_GROUP);
      if (choice == SHARE_WITH_PERSON) {
        shareWithPerson(activity.get());
      } else if (choice == SHARE_WITH_GROUP) {
        shareWithGroup(activity.get());
      } else {
        doneSharing = true;
      }
    }
  }

  private void shareWithPerson(Activity activity) {
    List<Person> possibleRecipients = personRegister.getPersonsSortedByName().stream()
        .filter(person -> person != activity.getLogger())
        .filter(person -> !activity.getSharedWithPersons().contains(person))
        .toList();
    inputReader.chooseFromList("Share with which person?", possibleRecipients,
        Person::getName, "Cancel").ifPresent(activity::shareWithPerson);
  }

  private void shareWithGroup(Activity activity) {
    List<Group> possibleRecipients = groupRegister.getGroupsSortedByName().stream()
        .filter(group -> !activity.getSharedWithGroups().contains(group))
        .toList();
    inputReader.chooseFromList("Share with which group?", possibleRecipients,
        Group::getName, "Cancel").ifPresent(activity::shareWithGroup);
  }

  private void makeActivityPrivate() {
    List<Activity> sharedActivities = activityRegister.getActivitiesSortedByDateTime().stream()
        .filter(Activity::isShared)
        .toList();
    chooseActivity(sharedActivities).ifPresent(activity -> {
      activity.markAsPrivate();
      System.out.println("\"" + activity.getTitle() + "\" is now private.");
    });
  }

  // ----- Persons and groups -----

  private Person registerPerson() {
    Person person = new Person(inputReader.readNonBlankText("Full name: "));
    personRegister.addPerson(person);
    System.out.println(person.getName() + " registered.");
    return person;
  }

  private void registerGroup() {
    String name = inputReader.readNonBlankText("Name of the group: ");
    if (groupRegister.findGroupByName(name).isPresent()) {
      System.out.println("There is already a group named " + name + ".");
      return;
    }
    Optional<Person> firstMember = inputReader.chooseFromList("First member of the group:",
        personRegister.getPersonsSortedByName(), Person::getName, "Cancel");
    if (firstMember.isEmpty()) {
      System.out.println("No group registered.");
      return;
    }
    Group group = new Group(name, firstMember.get());
    groupRegister.addGroup(group);
    System.out.println("Group " + group.getName() + " registered.");
    addMembersUntilDone(group);
  }

  private void addGroupMember() {
    inputReader.chooseFromList("Which group?", groupRegister.getGroupsSortedByName(),
        this::describeGroup, "Cancel").ifPresent(this::addMembersUntilDone);
  }

  private void addMembersUntilDone(Group group) {
    Optional<Person> newMember = chooseNonMember(group);
    while (newMember.isPresent()) {
      group.addMember(newMember.get());
      System.out.println(newMember.get().getName() + " added to " + group.getName() + ".");
      newMember = chooseNonMember(group);
    }
  }

  private Optional<Person> chooseNonMember(Group group) {
    List<Person> nonMembers = personRegister.getPersonsSortedByName().stream()
        .filter(person -> !group.isMember(person))
        .toList();
    return inputReader.chooseFromList("Add a member to " + group.getName() + ":", nonMembers,
        Person::getName, "Done");
  }

  private void removeGroupMember() {
    Optional<Group> group = inputReader.chooseFromList("Which group?",
        groupRegister.getGroupsSortedByName(), this::describeGroup, "Cancel");
    if (group.isEmpty()) {
      return;
    }
    if (group.get().getMembers().size() == 1) {
      System.out.println(group.get().getName()
          + " has only one member, and a group cannot be left empty.");
      return;
    }
    inputReader.chooseFromList("Remove which member?", group.get().getMembers(),
        Person::getName, "Cancel").ifPresent(member -> {
          group.get().removeMember(member);
          System.out.println(member.getName() + " removed from " + group.get().getName() + ".");
        });
  }

  // ----- Printing -----

  private Optional<Activity> chooseActivity(List<Activity> activities) {
    return inputReader.chooseFromList("Which activity?", activities, this::summarizeActivity,
        "Cancel");
  }

  private String summarizeActivity(Activity activity) {
    return activity.getRegistrationDateTime().format(InputReader.DATE_TIME_FORMATTER)
        + "  " + activity.getTitle() + " (" + activity.getLogger().getName() + ")";
  }

  private void printActivities(List<Activity> activities) {
    if (activities.isEmpty()) {
      System.out.println("No activities found.");
      return;
    }
    System.out.println(activities.size() + " activities found:");
    System.out.println();
    for (Activity activity : activities) {
      printActivity(activity);
    }
  }

  private void printActivity(Activity activity) {
    System.out.println("Title:       " + activity.getTitle());
    System.out.println("Logged by:   " + activity.getLogger().getName());
    System.out.println("Registered:  "
        + activity.getRegistrationDateTime().format(InputReader.DATE_TIME_FORMATTER));
    if (!activity.getDescription().isEmpty()) {
      System.out.println("Description: " + activity.getDescription());
    }
    System.out.println("Shared with: " + describeRecipients(activity));
    System.out.println();
  }

  private String describeRecipients(Activity activity) {
    if (!activity.isShared()) {
      return "nobody (private)";
    }
    List<String> recipientNames = new ArrayList<>();
    for (Person person : activity.getSharedWithPersons()) {
      recipientNames.add(person.getName());
    }
    for (Group group : activity.getSharedWithGroups()) {
      recipientNames.add(group.getName() + " (group)");
    }
    return String.join(", ", recipientNames);
  }

  private String describeGroup(Group group) {
    List<String> memberNames = group.getMembers().stream().map(Person::getName).toList();
    return group.getName() + ": " + String.join(", ", memberNames);
  }

  private void printPersonsAndGroups() {
    System.out.println("Persons:");
    for (Person person : personRegister.getPersonsSortedByName()) {
      System.out.println("  " + person.getName());
    }
    System.out.println();
    System.out.println("Groups:");
    for (Group group : groupRegister.getGroupsSortedByName()) {
      System.out.println("  " + describeGroup(group));
    }
  }

  private void printStatistics() {
    Map<Person, Long> activitiesPerLogger = activityRegister.countActivitiesPerLogger();
    System.out.printf("%-20s %10s%n", "Logger", "Activities");
    System.out.println("-".repeat(31));
    for (Person person : personRegister.getPersonsSortedByName()) {
      System.out.printf("%-20s %10d%n", person.getName(),
          activitiesPerLogger.getOrDefault(person, 0L));
    }
  }

  // Test data, so the program has something to show and search in from the start.
  private void addTestData() {
    Person kari = new Person("Kari Nordmann");
    Person ola = new Person("Ola Hansen");
    Person aisha = new Person("Aisha Khan");
    Person jonas = new Person("Jonas Berg");
    personRegister.addPerson(kari);
    personRegister.addPerson(ola);
    personRegister.addPerson(aisha);
    personRegister.addPerson(jonas);

    Group hikingClub = new Group("Hiking club", kari);
    hikingClub.addMember(aisha);
    groupRegister.addGroup(hikingClub);

    Activity skiTrip = new Activity(aisha, "Ski trip to Skistua",
        "First snow of the season.", LocalDateTime.of(2026, 11, 8, 9, 45));
    skiTrip.shareWithPerson(ola);
    Activity storheiaTrip = new Activity(kari, "Trip to Storheia",
        "Clear weather and a great view from the top.", LocalDateTime.of(2026, 10, 3, 14, 30));
    storheiaTrip.shareWithGroup(hikingClub);
    Activity cityWalk = new Activity(kari, "City walk along Nidelva",
        "", LocalDateTime.of(2026, 10, 25, 11, 0));
    Activity cyclingTrip = new Activity(ola, "Cycling around Jonsvatnet",
        "32 km, headwind on the way back.", LocalDateTime.of(2026, 10, 17, 18, 5));
    cyclingTrip.shareWithPerson(aisha);
    cyclingTrip.shareWithGroup(hikingClub);

    activityRegister.addActivity(skiTrip);
    activityRegister.addActivity(storheiaTrip);
    activityRegister.addActivity(cityWalk);
    activityRegister.addActivity(cyclingTrip);
  }
}
