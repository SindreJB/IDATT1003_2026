package edu.ntnu.idi.idatt.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.Activity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link ActivityRegister}.
 *
 * <p>Each test starts with a new register holding three activities, added out of
 * chronological order: one on 17 October and two on 3 October (14:30 and 09:00).
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
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>{@code addActivity} rejects {@code null}</li>
 *   <li>{@code addActivity} rejects an activity that is already in the register</li>
 *   <li>{@code removeActivity} rejects {@code null}</li>
 *   <li>{@code removeActivity} rejects an activity that is not in the register</li>
 *   <li>{@code findActivitiesOnDate} rejects {@code null}</li>
 *   <li>Changing a returned list does not change the register</li>
 * </ul>
 */
class ActivityRegisterTest {

  private ActivityRegister activityRegister;
  private Activity cyclingOnSeventeenthOctober;
  private Activity hikeOnThirdOctoberAfternoon;
  private Activity walkOnThirdOctoberMorning;

  @BeforeEach
  void createRegisterWithThreeActivities() {
    activityRegister = new ActivityRegister();
    cyclingOnSeventeenthOctober = new Activity("Ola Hansen", "Cycling around Jonsvatnet", "",
        LocalDateTime.of(2026, 10, 17, 18, 5));
    hikeOnThirdOctoberAfternoon = new Activity("Kari Nordmann", "Trip to Storheia", "",
        LocalDateTime.of(2026, 10, 3, 14, 30));
    walkOnThirdOctoberMorning = new Activity("Aisha Khan", "Walk along Nidelva", "",
        LocalDateTime.of(2026, 10, 3, 9, 0));
    activityRegister.addActivity(cyclingOnSeventeenthOctober);
    activityRegister.addActivity(hikeOnThirdOctoberAfternoon);
    activityRegister.addActivity(walkOnThirdOctoberMorning);
  }

  // ---------- Positive tests ----------

  @Test
  void newRegisterIsEmpty() {
    ActivityRegister emptyRegister = new ActivityRegister();

    assertTrue(emptyRegister.getActivitiesSortedByDateTime().isEmpty());
  }

  @Test
  void addActivityMakesActivityPartOfRegister() {
    Activity skiTrip = new Activity("Aisha Khan", "Ski trip to Skistua", "",
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
    Activity firstAdded = new Activity("Ola Hansen", "First", "", sameTime);
    Activity secondAdded = new Activity("Kari Nordmann", "Second", "", sameTime);
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
    Activity activityNotInRegister = new Activity("Ola Hansen", "Never added", "",
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
  void changingReturnedListDoesNotChangeRegister() {
    List<Activity> returnedActivities = activityRegister.getActivitiesSortedByDateTime();

    assertThrows(UnsupportedOperationException.class, returnedActivities::clear);

    assertEquals(3, activityRegister.getActivitiesSortedByDateTime().size());
  }
}
