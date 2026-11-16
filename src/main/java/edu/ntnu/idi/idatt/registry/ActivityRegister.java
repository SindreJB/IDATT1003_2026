package edu.ntnu.idi.idatt.registry;

import edu.ntnu.idi.idatt.model.Activity;
import edu.ntnu.idi.idatt.model.Person;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A register holding all logged activities.
 *
 * <p>The register is responsible for storing activities and for finding them again. It does not
 * create activities and does not present them to the user.
 *
 * <p><b>Choice of collection</b><br>
 * The activities are kept in an {@link ArrayList}:
 * <ul>
 *   <li>An activity has no natural unique key, such as an id or a title that must be unique,
 *       so a {@code HashMap} would need an artificial key that nothing else uses.</li>
 *   <li>A {@code HashSet} relies on {@code equals} and {@code hashCode}, which
 *       {@code Activity} does not override because two different trips can have identical
 *       data. A set would add nothing over a list here.</li>
 *   <li>The most frequent operations are visiting all activities and filtering them, which a
 *       list handles well. Sorting is done on a copy when it is asked for, so the order of the
 *       internal list does not matter.</li>
 * </ul>
 *
 * <p><b>Encapsulation</b><br>
 * The internal list is never handed out. Methods that return several activities return a new
 * list, so changes to the returned list do not affect the register.
 */
public class ActivityRegister {

  private static final Comparator<Activity> BY_REGISTRATION_DATE_TIME =
      Comparator.comparing(Activity::getRegistrationDateTime);

  private final List<Activity> activities;

  /**
   * Creates an empty register.
   */
  public ActivityRegister() {
    this.activities = new ArrayList<>();
  }

  /**
   * Adds an activity to the register.
   *
   * @param activity the activity to add, not already in the register
   * @throws NullPointerException if {@code activity} is {@code null}
   * @throws IllegalArgumentException if this exact activity is already in the register
   */
  public void addActivity(Activity activity) {
    Objects.requireNonNull(activity, "activity must not be null");
    if (activities.contains(activity)) {
      throw new IllegalArgumentException("activity is already in the register");
    }
    activities.add(activity);
  }

  /**
   * Removes an activity from the register.
   *
   * @param activity the activity to remove
   * @throws NullPointerException if {@code activity} is {@code null}
   * @throws NoSuchElementException if {@code activity} is not in the register
   */
  public void removeActivity(Activity activity) {
    Objects.requireNonNull(activity, "activity must not be null");
    if (!activities.remove(activity)) {
      throw new NoSuchElementException("activity is not in the register");
    }
  }

  /**
   * Returns all activities, sorted by date and time of registration, oldest first.
   * Activities registered at the same time keep the order they were added in.
   *
   * @return a new list with all activities, empty if the register is empty
   */
  public List<Activity> getActivitiesSortedByDateTime() {
    return findActivitiesMatching(activity -> true);
  }

  /**
   * Finds the activities registered on a given date, sorted by time of registration.
   *
   * @param date the date to search for
   * @return a new list with the matching activities, empty if there are none
   * @throws NullPointerException if {@code date} is {@code null}
   */
  public List<Activity> findActivitiesOnDate(LocalDate date) {
    Objects.requireNonNull(date, "date must not be null");
    return findActivitiesMatching(
        activity -> activity.getRegistrationDateTime().toLocalDate().equals(date));
  }

  /**
   * Finds the activities registered in a period, sorted by date and time of registration.
   * Both dates are included in the period.
   *
   * @param fromDate the first date of the period
   * @param toDate the last date of the period, not before {@code fromDate}
   * @return a new list with the matching activities, empty if there are none
   * @throws NullPointerException if {@code fromDate} or {@code toDate} is {@code null}
   * @throws IllegalArgumentException if {@code toDate} is before {@code fromDate}
   */
  public List<Activity> findActivitiesInPeriod(LocalDate fromDate, LocalDate toDate) {
    Objects.requireNonNull(fromDate, "fromDate must not be null");
    Objects.requireNonNull(toDate, "toDate must not be null");
    if (toDate.isBefore(fromDate)) {
      throw new IllegalArgumentException(
          "toDate " + toDate + " must not be before fromDate " + fromDate);
    }
    return findActivitiesMatching(activity -> {
      LocalDate registrationDate = activity.getRegistrationDateTime().toLocalDate();
      return !registrationDate.isBefore(fromDate) && !registrationDate.isAfter(toDate);
    });
  }

  /**
   * Finds the activities logged by a given person, sorted by date and time of registration.
   *
   * @param logger the person who logged the activities
   * @return a new list with the matching activities, empty if there are none
   * @throws NullPointerException if {@code logger} is {@code null}
   */
  public List<Activity> findActivitiesByLogger(Person logger) {
    Objects.requireNonNull(logger, "logger must not be null");
    return findActivitiesMatching(activity -> activity.getLogger() == logger);
  }

  /**
   * Finds the activities whose title or description contains a word, ignoring case, sorted by
   * date and time of registration. The word also matches as part of a longer word, so
   * "tur" finds "Fjelltur".
   *
   * @param word the word to search for, not blank; whitespace around it is ignored
   * @return a new list with the matching activities, empty if there are none
   * @throws NullPointerException if {@code word} is {@code null}
   * @throws IllegalArgumentException if {@code word} is blank
   */
  public List<Activity> findActivitiesContainingWord(String word) {
    Objects.requireNonNull(word, "word must not be null");
    if (word.isBlank()) {
      throw new IllegalArgumentException("word must not be blank");
    }
    String lowerCaseWord = word.strip().toLowerCase(Locale.ROOT);
    return findActivitiesMatching(activity ->
        activity.getTitle().toLowerCase(Locale.ROOT).contains(lowerCaseWord)
            || activity.getDescription().toLowerCase(Locale.ROOT).contains(lowerCaseWord));
  }

  /**
   * Finds the activities shared with a given person, directly or through a group, sorted by
   * date and time of registration. Activities the person logged themself are not included.
   *
   * @param person the person the activities are shared with
   * @return a new list with the matching activities, empty if there are none
   * @throws NullPointerException if {@code person} is {@code null}
   */
  public List<Activity> findActivitiesSharedWith(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    return findActivitiesMatching(activity -> activity.isSharedWith(person));
  }

  /**
   * Counts the activities logged by each person. Persons without activities are not included.
   *
   * @return a new map from each logger to the number of activities they have logged, empty if
   *     the register is empty
   */
  public Map<Person, Long> countActivitiesPerLogger() {
    return activities.stream()
        .collect(Collectors.groupingBy(Activity::getLogger, Collectors.counting()));
  }

  private List<Activity> findActivitiesMatching(Predicate<Activity> condition) {
    return activities.stream()
        .filter(condition)
        .sorted(BY_REGISTRATION_DATE_TIME)
        .toList();
  }
}
