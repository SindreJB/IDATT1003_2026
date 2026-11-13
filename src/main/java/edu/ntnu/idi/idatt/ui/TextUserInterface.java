package edu.ntnu.idi.idatt.ui;

import edu.ntnu.idi.idatt.model.Activity;
import edu.ntnu.idi.idatt.registry.ActivityRegister;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

  private ActivityRegister activityRegister;

  /**
   * Prepares the application for use by creating the objects the user interface depends on.
   */
  public void init() {
    activityRegister = new ActivityRegister();
  }

  /**
   * Runs the user interface until the user chooses to quit.
   */
  public void start() {
    System.out.println("Trip and activity register");
    System.out.println();

    addTestData();
    printAllActivities();
  }

  // Temporary test data until activities can be registered from the menu (part 3).
  private void addTestData() {
    activityRegister.addActivity(new Activity("Aisha Khan", "Ski trip to Skistua",
        "First snow of the season.", LocalDateTime.of(2026, 11, 8, 9, 45)));
    activityRegister.addActivity(new Activity("Kari Nordmann", "Trip to Storheia",
        "Clear weather and a great view from the top.", LocalDateTime.of(2026, 10, 3, 14, 30)));
    activityRegister.addActivity(new Activity("Kari Nordmann", "City walk along Nidelva",
        "", LocalDateTime.of(2026, 10, 25, 11, 0)));
    activityRegister.addActivity(new Activity("Ola Hansen", "Cycling around Jonsvatnet",
        "32 km, headwind on the way back.", LocalDateTime.of(2026, 10, 17, 18, 5)));
  }

  private void printAllActivities() {
    for (Activity activity : activityRegister.getActivitiesSortedByDateTime()) {
      printActivity(activity);
    }
  }

  private void printActivity(Activity activity) {
    System.out.println("Title:       " + activity.getTitle());
    System.out.println("Logged by:   " + activity.getLoggerName());
    System.out.println("Registered:  "
        + activity.getRegistrationDateTime().format(DATE_TIME_FORMATTER));
    if (!activity.getDescription().isEmpty()) {
      System.out.println("Description: " + activity.getDescription());
    }
    System.out.println();
  }
}
