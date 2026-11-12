package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link Activity}.
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>The constructor stores all four values</li>
 *   <li>The constructor removes whitespace around the texts</li>
 *   <li>The constructor accepts an empty description</li>
 *   <li>{@code setTitle} and {@code setDescription} change the value</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>The constructor rejects {@code null} for each parameter</li>
 *   <li>The constructor rejects a blank logger name and a blank title</li>
 *   <li>The constructor rejects a time of registration in the future</li>
 *   <li>{@code setTitle} rejects {@code null} and blank, and leaves the title unchanged</li>
 *   <li>{@code setDescription} rejects {@code null}, and leaves the description unchanged</li>
 * </ul>
 */
class ActivityTest {

  private static final String LOGGER_NAME = "Kari Nordmann";
  private static final String TITLE = "Trip to Storheia";
  private static final String DESCRIPTION = "Clear weather and a great view from the top.";
  private static final LocalDateTime REGISTRATION_DATE_TIME = LocalDateTime.of(2026, 10, 3, 14, 30);

  private Activity activity;

  @BeforeEach
  void createValidActivity() {
    activity = new Activity(LOGGER_NAME, TITLE, DESCRIPTION, REGISTRATION_DATE_TIME);
  }

  // ---------- Positive tests ----------

  @Test
  void constructorStoresAllValues() {
    assertEquals(LOGGER_NAME, activity.getLoggerName());
    assertEquals(TITLE, activity.getTitle());
    assertEquals(DESCRIPTION, activity.getDescription());
    assertEquals(REGISTRATION_DATE_TIME, activity.getRegistrationDateTime());
  }

  @Test
  void constructorRemovesWhitespaceAroundTexts() {
    Activity activityWithPaddedTexts = new Activity(
        "  " + LOGGER_NAME + " ", " " + TITLE + "  ", "\t" + DESCRIPTION + "\n",
        REGISTRATION_DATE_TIME);

    assertEquals(LOGGER_NAME, activityWithPaddedTexts.getLoggerName());
    assertEquals(TITLE, activityWithPaddedTexts.getTitle());
    assertEquals(DESCRIPTION, activityWithPaddedTexts.getDescription());
  }

  @Test
  void constructorAcceptsEmptyDescription() {
    Activity activityWithoutDescription =
        new Activity(LOGGER_NAME, TITLE, "", REGISTRATION_DATE_TIME);

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

  // ---------- Negative tests ----------

  @Test
  void constructorThrowsNullPointerExceptionWhenLoggerNameIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(null, TITLE, DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenTitleIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(LOGGER_NAME, null, DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenDescriptionIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(LOGGER_NAME, TITLE, null, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenRegistrationDateTimeIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Activity(LOGGER_NAME, TITLE, DESCRIPTION, null));
  }

  @Test
  void constructorThrowsIllegalArgumentExceptionWhenLoggerNameIsBlank() {
    assertThrows(IllegalArgumentException.class,
        () -> new Activity("   ", TITLE, DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsIllegalArgumentExceptionWhenTitleIsBlank() {
    assertThrows(IllegalArgumentException.class,
        () -> new Activity(LOGGER_NAME, "", DESCRIPTION, REGISTRATION_DATE_TIME));
  }

  @Test
  void constructorThrowsDateTimeExceptionWhenRegistrationDateTimeIsInTheFuture() {
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    assertThrows(DateTimeException.class,
        () -> new Activity(LOGGER_NAME, TITLE, DESCRIPTION, tomorrow));
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
}
