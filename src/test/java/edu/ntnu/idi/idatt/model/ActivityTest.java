package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link Activity}.
 *
 * <p>Each test starts with a new private activity logged by Kari, and two other persons, Ola
 * and Aisha, who are not recipients.
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>The constructor stores all four values, and the new activity is private</li>
 *   <li>The constructor removes whitespace around the texts</li>
 *   <li>The constructor accepts an empty description</li>
 *   <li>{@code setTitle} and {@code setDescription} change the value</li>
 *   <li>{@code shareWithPerson} makes the activity shared with that person only</li>
 *   <li>{@code shareWithGroup} makes the activity shared with every member of the group,
 *       also a member who joins after the sharing</li>
 *   <li>{@code isSharedWith} is {@code false} for the logger, even when the logger is a
 *       member of a group the activity is shared with</li>
 *   <li>{@code markAsPrivate} removes all recipients</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>The constructor rejects {@code null} for each parameter</li>
 *   <li>The constructor rejects a blank title</li>
 *   <li>The constructor rejects a time of registration in the future</li>
 *   <li>{@code setTitle} rejects {@code null} and blank, and leaves the title unchanged</li>
 *   <li>{@code setDescription} rejects {@code null}, and leaves the description unchanged</li>
 *   <li>{@code shareWithPerson} rejects {@code null}, the logger, and a person already shared
 *       with</li>
 *   <li>{@code shareWithGroup} rejects {@code null} and a group already shared with</li>
 *   <li>{@code isSharedWith} rejects {@code null}</li>
 *   <li>A returned list of recipients cannot be used to change the activity</li>
 * </ul>
 */
class ActivityTest {

  private static final String TITLE = "Trip to Storheia";
  private static final String DESCRIPTION = "Clear weather and a great view from the top.";
  private static final LocalDateTime REGISTRATION_DATE_TIME = LocalDateTime.of(2026, 10, 3, 14, 30);

  private Person kari;
  private Person ola;
  private Person aisha;
  private Activity activity;

  @BeforeEach
  void createPrivateActivityLoggedByKari() {
    kari = new Person("Kari Nordmann");
    ola = new Person("Ola Hansen");
    aisha = new Person("Aisha Khan");
    activity = new Activity(kari, TITLE, DESCRIPTION, REGISTRATION_DATE_TIME);
  }

  // ---------- Positive tests ----------

  @Test
  void constructorStoresAllValuesAndCreatesPrivateActivity() {
    assertSame(kari, activity.getLogger());
    assertEquals(TITLE, activity.getTitle());
    assertEquals(DESCRIPTION, activity.getDescription());
    assertEquals(REGISTRATION_DATE_TIME, activity.getRegistrationDateTime());
    assertFalse(activity.isShared());
  }

  @Test
  void constructorRemovesWhitespaceAroundTexts() {
    Activity activityWithPaddedTexts = new Activity(
        kari, " " + TITLE + "  ", "\t" + DESCRIPTION + "\n", REGISTRATION_DATE_TIME);

    assertEquals(TITLE, activityWithPaddedTexts.getTitle());
    assertEquals(DESCRIPTION, activityWithPaddedTexts.getDescription());
  }

  @Test
  void constructorAcceptsEmptyDescription() {
    Activity activityWithoutDescription = new Activity(kari, TITLE, "", REGISTRATION_DATE_TIME);

    assertEquals("", activityWithoutDescription.getDescription());
  }

  @Test
  void setTitleChangesTitle() {
    activity.setTitle("Trip to Gråkallen");

    assertEquals("Trip to Gråkallen", activity.getTitle());
  }

  @Test
  void setDescriptionChangesDescription() {
    activity.setDescription("Foggy, turned around at the tree line.");

    assertEquals("Foggy, turned around at the tree line.", activity.getDescription());
  }

  @Test
  void shareWithPersonMakesActivitySharedWithThatPersonOnly() {
    activity.shareWithPerson(ola);

    assertTrue(activity.isShared());
    assertTrue(activity.isSharedWith(ola));
    assertFalse(activity.isSharedWith(aisha));
    assertEquals(List.of(ola), activity.getSharedWithPersons());
  }

  @Test
  void shareWithGroupMakesActivitySharedWithEveryMember() {
    Group hikingClub = new Group("Hiking club", ola);

    activity.shareWithGroup(hikingClub);

    assertTrue(activity.isShared());
    assertTrue(activity.isSharedWith(ola));
    assertFalse(activity.isSharedWith(aisha));
    assertEquals(List.of(hikingClub), activity.getSharedWithGroups());
  }

  @Test
  void shareWithGroupIncludesMemberWhoJoinsAfterSharing() {
    Group hikingClub = new Group("Hiking club", ola);
    activity.shareWithGroup(hikingClub);

    hikingClub.addMember(aisha);

    assertTrue(activity.isSharedWith(aisha));
  }

  @Test
  void isSharedWithIsFalseForLoggerWhoIsMemberOfSharedGroup() {
    Group hikingClub = new Group("Hiking club", ola);
    hikingClub.addMember(kari);

    activity.shareWithGroup(hikingClub);

    assertFalse(activity.isSharedWith(kari));
  }

  @Test
  void markAsPrivateRemovesAllRecipients() {
    activity.shareWithPerson(ola);
    activity.shareWithGroup(new Group("Hiking club", aisha));

    activity.markAsPrivate();

    assertFalse(activity.isShared());
    assertFalse(activity.isSharedWith(ola));
    assertFalse(activity.isSharedWith(aisha));
  }

  // ---------- Negative tests ----------

  @Test
  void constructorThrowsNullPointerExceptionWhenLoggerIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(null, TITLE, DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenTitleIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(kari, null, DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenDescriptionIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(kari, TITLE, null, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenRegistrationDateTimeIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(kari, TITLE, DESCRIPTION, null));
  }

  @Test
  void constructorThrowsIllegalArgumentExceptionWhenTitleIsBlank() {
    assertThrows(IllegalArgumentException.class,
        () -> new Activity(kari, "", DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsDateTimeExceptionWhenRegistrationDateTimeIsInTheFuture() {
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    assertThrows(DateTimeException.class,
        () -> new Activity(kari, TITLE, DESCRIPTION, tomorrow));
  }

  @Test
  void setTitleThrowsNullPointerExceptionAndKeepsTitleWhenTitleIsNull() {
    assertThrows(NullPointerException.class, () -> activity.setTitle(null));

    assertEquals(TITLE, activity.getTitle());
  }

  @Test
  void setTitleThrowsIllegalArgumentExceptionAndKeepsTitleWhenTitleIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> activity.setTitle(" \t "));

    assertEquals(TITLE, activity.getTitle());
  }

  @Test
  void setDescriptionThrowsNullPointerExceptionAndKeepsDescriptionWhenDescriptionIsNull() {
    assertThrows(NullPointerException.class, () -> activity.setDescription(null));

    assertEquals(DESCRIPTION, activity.getDescription());
  }

  @Test
  void shareWithPersonThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class, () -> activity.shareWithPerson(null));
  }

  @Test
  void shareWithPersonThrowsIllegalArgumentExceptionAndStaysPrivateWhenPersonIsLogger() {
    assertThrows(IllegalArgumentException.class, () -> activity.shareWithPerson(kari));

    assertFalse(activity.isShared());
  }

  @Test
  void shareWithPersonThrowsIllegalArgumentExceptionWhenAlreadySharedWithPerson() {
    activity.shareWithPerson(ola);

    assertThrows(IllegalArgumentException.class, () -> activity.shareWithPerson(ola));

    assertEquals(List.of(ola), activity.getSharedWithPersons());
  }

  @Test
  void shareWithGroupThrowsNullPointerExceptionWhenGroupIsNull() {
    assertThrows(NullPointerException.class, () -> activity.shareWithGroup(null));
  }

  @Test
  void shareWithGroupThrowsIllegalArgumentExceptionWhenAlreadySharedWithGroup() {
    Group hikingClub = new Group("Hiking club", ola);
    activity.shareWithGroup(hikingClub);

    assertThrows(IllegalArgumentException.class, () -> activity.shareWithGroup(hikingClub));

    assertEquals(List.of(hikingClub), activity.getSharedWithGroups());
  }

  @Test
  void isSharedWithThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class, () -> activity.isSharedWith(null));
  }

  @Test
  void returnedListOfRecipientsCannotChangeActivity() {
    List<Person> sharedWithPersons = activity.getSharedWithPersons();

    assertThrows(UnsupportedOperationException.class, () -> sharedWithPersons.add(ola));

    assertFalse(activity.isShared());
  }
}
