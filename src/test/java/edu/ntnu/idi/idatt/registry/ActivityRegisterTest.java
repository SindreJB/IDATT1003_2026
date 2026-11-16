package edu.ntnu.idi.idatt.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.Activity;
import edu.ntnu.idi.idatt.model.Group;
import edu.ntnu.idi.idatt.model.Person;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link ActivityRegister}.
 *
 * <p>Each test starts with a new register holding three activities, added out of
 * chronological order:
 * <ul>
 *   <li>Ola, 17 October 18:05, "Cycling around Jonsvatnet", shared with Kari</li>
 *   <li>Kari, 3 October 14:30, "Trip to Storheia", shared with the group "Hiking club",
 *       whose only member is Aisha</li>
 *   <li>Kari, 3 October 09:00, "Walk along Nidelva", private</li>
 * </ul>
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>A new register is empty</li>
 *   <li>{@code addActivity} makes the activity part of the register</li>
 *   <li>{@code removeActivity} removes only the given activity</li>
 *   <li>{@code getActivitiesSortedByDateTime} returns all activities, oldest first</li>
 *   <li>{@code getActivitiesSortedByDateTime} keeps the order of addition for equal times</li>
 *   <li>{@code findActivitiesOnDate} returns only the activities on that date, sorted by time</li>
 *   <li>{@code findActivitiesOnDate} returns an empty list when no activity matches</li>
 *   <li>{@code findActivitiesInPeriod} includes both the first and the last date</li>
 *   <li>{@code findActivitiesInPeriod} accepts a period of a single day</li>
 *   <li>{@code findActivitiesByLogger} returns only that logger's activities</li>
 *   <li>{@code findActivitiesContainingWord} matches the title and the description, ignoring
 *       case, and also as part of a longer word</li>
 *   <li>{@code findActivitiesSharedWith} finds activities shared directly and through a
 *       group, but not the person's own or private ones</li>
 *   <li>{@code countActivitiesPerLogger} counts the activities of each logger</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>{@code addActivity} rejects {@code null}</li>
 *   <li>{@code addActivity} rejects an activity that is already in the register</li>
 *   <li>{@code removeActivity} rejects {@code null}</li>
 *   <li>{@code removeActivity} rejects an activity that is not in the register</li>
 *   <li>{@code findActivitiesOnDate} rejects {@code null}</li>
 *   <li>{@code findActivitiesInPeriod} rejects {@code null} dates and an end before the
 *       start</li>
 *   <li>{@code findActivitiesByLogger} rejects {@code null}</li>
 *   <li>{@code findActivitiesContainingWord} rejects {@code null} and a blank word</li>
 *   <li>{@code findActivitiesSharedWith} rejects {@code null}</li>
 *   <li>Changing a returned list does not change the register</li>
 * </ul>
 */
class ActivityRegisterTest {

  private ActivityRegister activityRegister;
  private Person kari;
  private Person ola;
  private Person aisha;
  private Activity cyclingOnSeventeenthOctober;
  private Activity hikeOnThirdOctoberAfternoon;
  private Activity walkOnThirdOctoberMorning;

  @BeforeEach
  void createRegisterWithThreeActivities() {
    kari = new Person("Kari Nordmann");
    ola = new Person("Ola Hansen");
    aisha = new Person("Aisha Khan");

    cyclingOnSeventeenthOctober = new Activity(ola, "Cycling around Jonsvatnet",
        "32 km, headwind on the way back.", LocalDateTime.of(2026, 10, 17, 18, 5));
    cyclingOnSeventeenthOctober.shareWithPerson(kari);
    hikeOnThirdOctoberAfternoon = new Activity(kari, "Trip to Storheia",
        "Clear weather from the top.", LocalDateTime.of(2026, 10, 3, 14, 30));
    hikeOnThirdOctoberAfternoon.shareWithGroup(new Group("Hiking club", aisha));
    walkOnThirdOctoberMorning = new Activity(kari, "Walk along Nidelva", "",
        LocalDateTime.of(2026, 10, 3, 9, 0));

    activityRegister = new ActivityRegister();
    activityRegister.addActivity(cyclingOnSeventeenthOctober);
    activityRegister.addActivity(hikeOnThirdOctoberAfternoon);
    activityRegister.addActivity(walkOnThirdOctoberMorning);
  }

  // ---------- Positive tests ----------

  @Test
  void newRegisterIsEmpty() {
    ActivityRegister emptyRegister = new ActivityRegister();

    assertTrue(emptyRegister.getActivitiesSortedByDateTime().isEmpty());
    assertTrue(emptyRegister.countActivitiesPerLogger().isEmpty());
  }

  @Test
  void addActivityMakesActivityPartOfRegister() {
    Activity skiTrip = new Activity(aisha, "Ski trip to Skistua", "",
        LocalDateTime.of(2026, 11, 8, 9, 45));

    activityRegister.addActivity(skiTrip);

    assertTrue(activityRegister.getActivitiesSortedByDateTime().contains(skiTrip));
  }

  @Test
  void removeActivityRemovesOnlyTheGivenActivity() {
    activityRegister.removeActivity(hikeOnThirdOctoberAfternoon);

    assertEquals(List.of(walkOnThirdOctoberMorning, cyclingOnSeventeenthOctober),
        activityRegister.getActivitiesSortedByDateTime());
  }

  @Test
  void getActivitiesSortedByDateTimeReturnsAllActivitiesOldestFirst() {
    List<Activity> sortedActivities = activityRegister.getActivitiesSortedByDateTime();

    assertEquals(List.of(walkOnThirdOctoberMorning, hikeOnThirdOctoberAfternoon,
        cyclingOnSeventeenthOctober), sortedActivities);
  }

  @Test
  void getActivitiesSortedByDateTimeKeepsOrderOfAdditionForEqualTimes() {
    LocalDateTime sameTime = LocalDateTime.of(2026, 9, 1, 12, 0);
    Activity firstAdded = new Activity(ola, "First", "", sameTime);
    Activity secondAdded = new Activity(kari, "Second", "", sameTime);
    ActivityRegister registerWithEqualTimes = new ActivityRegister();
    registerWithEqualTimes.addActivity(firstAdded);
    registerWithEqualTimes.addActivity(secondAdded);

    List<Activity> sortedActivities = registerWithEqualTimes.getActivitiesSortedByDateTime();

    assertEquals(List.of(firstAdded, secondAdded), sortedActivities);
  }

  @Test
  void findActivitiesOnDateReturnsOnlyMatchingActivitiesSortedByTime() {
    List<Activity> activitiesOnThirdOctober =
        activityRegister.findActivitiesOnDate(LocalDate.of(2026, 10, 3));

    assertEquals(List.of(walkOnThirdOctoberMorning, hikeOnThirdOctoberAfternoon),
        activitiesOnThirdOctober);
  }

  @Test
  void findActivitiesOnDateReturnsEmptyListWhenNoActivityMatches() {
    List<Activity> activitiesOnFirstOctober =
        activityRegister.findActivitiesOnDate(LocalDate.of(2026, 10, 1));

    assertTrue(activitiesOnFirstOctober.isEmpty());
  }

  @Test
  void findActivitiesInPeriodIncludesFirstAndLastDate() {
    List<Activity> activitiesInPeriod = activityRegister.findActivitiesInPeriod(
        LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 17));

    assertEquals(List.of(walkOnThirdOctoberMorning, hikeOnThirdOctoberAfternoon,
        cyclingOnSeventeenthOctober), activitiesInPeriod);
  }

  @Test
  void findActivitiesInPeriodAcceptsPeriodOfSingleDay() {
    LocalDate seventeenthOctober = LocalDate.of(2026, 10, 17);

    List<Activity> activitiesInPeriod =
        activityRegister.findActivitiesInPeriod(seventeenthOctober, seventeenthOctober);

    assertEquals(List.of(cyclingOnSeventeenthOctober), activitiesInPeriod);
  }

  @Test
  void findActivitiesByLoggerReturnsOnlyThatLoggersActivities() {
    List<Activity> activitiesByKari = activityRegister.findActivitiesByLogger(kari);

    assertEquals(List.of(walkOnThirdOctoberMorning, hikeOnThirdOctoberAfternoon),
        activitiesByKari);
  }

  @Test
  void findActivitiesContainingWordMatchesTitleIgnoringCase() {
    List<Activity> matchingActivities = activityRegister.findActivitiesContainingWord("NIDELVA");

    assertEquals(List.of(walkOnThirdOctoberMorning), matchingActivities);
  }

  @Test
  void findActivitiesContainingWordMatchesDescription() {
    List<Activity> matchingActivities = activityRegister.findActivitiesContainingWord("headwind");

    assertEquals(List.of(cyclingOnSeventeenthOctober), matchingActivities);
  }

  @Test
  void findActivitiesContainingWordMatchesPartOfLongerWord() {
    List<Activity> matchingActivities = activityRegister.findActivitiesContainingWord("storh");

    assertEquals(List.of(hikeOnThirdOctoberAfternoon), matchingActivities);
  }

  @Test
  void findActivitiesSharedWithFindsActivitySharedDirectly() {
    assertEquals(List.of(cyclingOnSeventeenthOctober),
        activityRegister.findActivitiesSharedWith(kari));
  }

  @Test
  void findActivitiesSharedWithFindsActivitySharedThroughGroup() {
    assertEquals(List.of(hikeOnThirdOctoberAfternoon),
        activityRegister.findActivitiesSharedWith(aisha));
  }

  @Test
  void countActivitiesPerLoggerCountsActivitiesOfEachLogger() {
    Map<Person, Long> activitiesPerLogger = activityRegister.countActivitiesPerLogger();

    assertEquals(Map.of(kari, 2L, ola, 1L), activitiesPerLogger);
  }

  // ---------- Negative tests ----------

  @Test
  void addActivityThrowsNullPointerExceptionWhenActivityIsNull() {
    assertThrows(NullPointerException.class, () -> activityRegister.addActivity(null));
  }

  @Test
  void addActivityThrowsIllegalArgumentExceptionWhenActivityIsAlreadyInRegister() {
    assertThrows(IllegalArgumentException.class,
        () -> activityRegister.addActivity(hikeOnThirdOctoberAfternoon));

    assertEquals(3, activityRegister.getActivitiesSortedByDateTime().size());
  }

  @Test
  void removeActivityThrowsNullPointerExceptionWhenActivityIsNull() {
    assertThrows(NullPointerException.class, () -> activityRegister.removeActivity(null));
  }

  @Test
  void removeActivityThrowsNoSuchElementExceptionWhenActivityIsNotInRegister() {
    Activity activityNotInRegister = new Activity(ola, "Never added", "",
        LocalDateTime.of(2026, 10, 20, 10, 0));

    assertThrows(NoSuchElementException.class,
        () -> activityRegister.removeActivity(activityNotInRegister));

    assertEquals(3, activityRegister.getActivitiesSortedByDateTime().size());
  }

  @Test
  void findActivitiesOnDateThrowsNullPointerExceptionWhenDateIsNull() {
    assertThrows(NullPointerException.class, () -> activityRegister.findActivitiesOnDate(null));
  }

  @Test
  void findActivitiesInPeriodThrowsNullPointerExceptionWhenFromDateIsNull() {
    assertThrows(NullPointerException.class,
        () -> activityRegister.findActivitiesInPeriod(null, LocalDate.of(2026, 10, 17)));
  }

  @Test
  void findActivitiesInPeriodThrowsNullPointerExceptionWhenToDateIsNull() {
    assertThrows(NullPointerException.class,
        () -> activityRegister.findActivitiesInPeriod(LocalDate.of(2026, 10, 3), null));
  }

  @Test
  void findActivitiesInPeriodThrowsIllegalArgumentExceptionWhenToDateIsBeforeFromDate() {
    assertThrows(IllegalArgumentException.class, () -> activityRegister.findActivitiesInPeriod(
        LocalDate.of(2026, 10, 17), LocalDate.of(2026, 10, 3)));
  }

  @Test
  void findActivitiesByLoggerThrowsNullPointerExceptionWhenLoggerIsNull() {
    assertThrows(NullPointerException.class,
        () -> activityRegister.findActivitiesByLogger(null));
  }

  @Test
  void findActivitiesContainingWordThrowsNullPointerExceptionWhenWordIsNull() {
    assertThrows(NullPointerException.class,
        () -> activityRegister.findActivitiesContainingWord(null));
  }

  @Test
  void findActivitiesContainingWordThrowsIllegalArgumentExceptionWhenWordIsBlank() {
    assertThrows(IllegalArgumentException.class,
        () -> activityRegister.findActivitiesContainingWord("  "));
  }

  @Test
  void findActivitiesSharedWithThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class,
        () -> activityRegister.findActivitiesSharedWith(null));
  }

  @Test
  void changingReturnedListDoesNotChangeRegister() {
    List<Activity> returnedActivities = activityRegister.getActivitiesSortedByDateTime();

    assertThrows(UnsupportedOperationException.class, returnedActivities::clear);

    assertEquals(3, activityRegister.getActivitiesSortedByDateTime().size());
  }
}
