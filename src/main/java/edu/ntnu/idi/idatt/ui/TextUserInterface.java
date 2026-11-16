package edu.ntnu.idi.idatt.ui;

import edu.ntnu.idi.idatt.model.Activity;
import edu.ntnu.idi.idatt.model.Group;
import edu.ntnu.idi.idatt.model.Person;
import edu.ntnu.idi.idatt.registry.ActivityRegister;
import edu.ntnu.idi.idatt.registry.GroupRegister;
import edu.ntnu.idi.idatt.registry.PersonRegister;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Text based user interface of the trip and activity register.
 *
 * <p>Responsible for all interaction with the user: presenting information in the console and
 * reading input from the keyboard. The class holds no business logic of its own. It delegates
 * to the registers, which are created in {@link #init()}.
 */
public class TextUserInterface {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private PersonRegister personRegister;
  private GroupRegister groupRegister;
  private ActivityRegister activityRegister;

  /**
   * Prepares the application for use by creating the objects the user interface depends on.
   */
  public void init() {
    personRegister = new PersonRegister();
    groupRegister = new GroupRegister();
    activityRegister = new ActivityRegister();
  }

  /**
   * Runs the user interface until the user chooses to quit.
   */
  public void start() {
    System.out.println("Trip and activity register");
    System.out.println();

    addTestData();

    System.out.println("=== All activities ===");
    System.out.println();
    printActivities(activityRegister.getActivitiesSortedByDateTime());

    Person aisha = personRegister.findPersonByName("Aisha Khan").orElseThrow();
    System.out.println("=== Activities shared with " + aisha.getName() + " ===");
    System.out.println();
    printActivities(activityRegister.findActivitiesSharedWith(aisha));

    printStatistics();
  }

  // Temporary test data until persons, groups and activities can be registered from the menu
  // (part 3).
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

  private void printActivities(List<Activity> activities) {
    if (activities.isEmpty()) {
      System.out.println("No activities.");
      System.out.println();
    }
    for (Activity activity : activities) {
      printActivity(activity);
    }
  }

  private void printActivity(Activity activity) {
    System.out.println("Title:       " + activity.getTitle());
    System.out.println("Logged by:   " + activity.getLogger().getName());
    System.out.println("Registered:  "
        + activity.getRegistrationDateTime().format(DATE_TIME_FORMATTER));
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

  private void printStatistics() {
    Map<Person, Long> activitiesPerLogger = activityRegister.countActivitiesPerLogger();
    System.out.println("=== Number of activities per logger ===");
    System.out.println();
    System.out.printf("%-20s %10s%n", "Logger", "Activities");
    System.out.println("-".repeat(31));
    for (Person person : personRegister.getPersonsSortedByName()) {
      System.out.printf("%-20s %10d%n", person.getName(),
          activitiesPerLogger.getOrDefault(person, 0L));
    }
  }
}
