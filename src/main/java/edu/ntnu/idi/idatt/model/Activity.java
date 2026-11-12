package edu.ntnu.idi.idatt.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A single trip or activity logged by one person.
 *
 * <p>The class is an entity class: it holds the data of one activity and guarantees that the
 * data is valid, but it knows nothing about how activities are stored or shown to the user.
 *
 * <p><b>Fields and data types</b>
 * <ul>
 *   <li>{@code loggerName} – {@code String}, the name of the person who logged the activity.
 *       A name is free text, so {@code String} is the natural type.</li>
 *   <li>{@code title} – {@code String}, a short free text heading.</li>
 *   <li>{@code description} – {@code String}, a longer free text. It may be empty, since a
 *       title alone can be enough.</li>
 *   <li>{@code registrationDateTime} – {@link LocalDateTime}, the date and time the activity
 *       was registered. {@code LocalDateTime} holds both date and time in one immutable
 *       object, can be compared and sorted, and cannot hold an impossible date such as
 *       30 February, which a {@code String} or a set of {@code int} fields could. The
 *       application runs on a single machine, so no time zone is needed.</li>
 * </ul>
 *
 * <p><b>Mutability</b><br>
 * The logger and the time of registration are facts about when and by whom the activity was
 * logged. They are fixed at creation, and their set-methods are private. The title and the
 * description can be corrected afterwards and have public set-methods. All the data types
 * used are immutable, so the get-methods can hand out the field values directly.
 *
 * <p><b>Invalid data</b><br>
 * Every value passes through a set-method, also in the constructor, so an {@code Activity}
 * can never be in an invalid state. Invalid data is rejected with an unchecked exception that
 * tells what kind of error it is:
 * <ul>
 *   <li>{@link NullPointerException} when a required value is {@code null}</li>
 *   <li>{@link IllegalArgumentException} when a required text is blank</li>
 *   <li>{@link DateTimeException} when the time of registration is in the future</li>
 * </ul>
 * A set-method that throws leaves the activity unchanged.
 */
public class Activity {

  private String loggerName;
  private String title;
  private String description;
  private LocalDateTime registrationDateTime;

  /**
   * Creates an activity.
   *
   * @param loggerName the name of the person logging the activity, not blank
   * @param title a short heading for the activity, not blank
   * @param description a longer text about the activity, may be empty
   * @param registrationDateTime the date and time of registration, not in the future
   * @throws NullPointerException if any of the parameters is {@code null}
   * @throws IllegalArgumentException if {@code loggerName} or {@code title} is blank
   * @throws DateTimeException if {@code registrationDateTime} is in the future
   */
  public Activity(String loggerName, String title, String description,
      LocalDateTime registrationDateTime) {
    setLoggerName(loggerName);
    setTitle(title);
    setDescription(description);
    setRegistrationDateTime(registrationDateTime);
  }

  /**
   * Returns the name of the person who logged the activity.
   *
   * @return the name of the logger
   */
  public String getLoggerName() {
    return loggerName;
  }

  /**
   * Returns the title of the activity.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the description of the activity.
   *
   * @return the description, which may be empty
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the date and time the activity was registered.
   *
   * @return the date and time of registration
   */
  public LocalDateTime getRegistrationDateTime() {
    return registrationDateTime;
  }

  /**
   * Changes the title of the activity. Whitespace around the title is removed.
   *
   * @param title the new title, not blank
   * @throws NullPointerException if {@code title} is {@code null}
   * @throws IllegalArgumentException if {@code title} is blank
   */
  public void setTitle(String title) {
    this.title = requireNonBlankText(title, "title");
  }

  /**
   * Changes the description of the activity. Whitespace around the description is removed.
   *
   * @param description the new description, may be empty
   * @throws NullPointerException if {@code description} is {@code null}
   */
  public void setDescription(String description) {
    Objects.requireNonNull(description, "description must not be null");
    this.description = description.strip();
  }

  private void setLoggerName(String loggerName) {
    this.loggerName = requireNonBlankText(loggerName, "loggerName");
  }

  private void setRegistrationDateTime(LocalDateTime registrationDateTime) {
    Objects.requireNonNull(registrationDateTime, "registrationDateTime must not be null");
    if (registrationDateTime.isAfter(LocalDateTime.now())) {
      throw new DateTimeException(
          "registrationDateTime must not be in the future: " + registrationDateTime);
    }
    this.registrationDateTime = registrationDateTime;
  }

  private static String requireNonBlankText(String text, String fieldName) {
    Objects.requireNonNull(text, fieldName + " must not be null");
    if (text.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return text.strip();
  }
}
