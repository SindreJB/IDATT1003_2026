package edu.ntnu.idi.idatt.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A single trip or activity logged by one person, and possibly shared with others.
 *
 * <p>The class is an entity class: it holds the data of one activity and guarantees that the
 * data is valid, but it knows nothing about how activities are stored or shown to the user.
 *
 * <p><b>Fields and data types</b>
 * <ul>
 *   <li>{@code logger} – {@link Person}, the person who logged the activity. A reference to
 *       the {@code Person} object rather than a copy of the name, so that all activities by
 *       the same person point to one and the same object, and searching by logger is a simple
 *       comparison instead of matching texts that may be spelled differently.</li>
 *   <li>{@code title} – {@code String}, a short free text heading.</li>
 *   <li>{@code description} – {@code String}, a longer free text. It may be empty, since a
 *       title alone can be enough.</li>
 *   <li>{@code registrationDateTime} – {@link LocalDateTime}, the date and time the activity
 *       was registered. {@code LocalDateTime} holds both date and time in one immutable
 *       object, can be compared and sorted, and cannot hold an impossible date such as
 *       30 February, which a {@code String} or a set of {@code int} fields could. The
 *       application runs on a single machine, so no time zone is needed.</li>
 *   <li>{@code sharedWithPersons} and {@code sharedWithGroups} – {@link LinkedHashSet}s of the
 *       persons and groups the activity is shared with. Sets, because sharing twice with the
 *       same recipient has no meaning; linked, so recipients are listed in the order they were
 *       added.</li>
 * </ul>
 *
 * <p><b>Private or shared</b><br>
 * An activity is shared exactly when it has at least one recipient, and private otherwise.
 * There is no separate flag, because a flag could say "shared" while nobody is a recipient,
 * or "private" while recipients remain. A new activity is private. Sharing it with a group
 * means sharing it with the members the group has at any time, so a person who joins the group
 * later can also see the activity.
 *
 * <p><b>Mutability</b><br>
 * The logger and the time of registration are facts about when and by whom the activity was
 * logged. They are fixed at creation, and their set-methods are private. The title, the
 * description and the recipients can change afterwards. The internal sets of recipients are
 * never handed out.
 *
 * <p><b>Invalid data</b><br>
 * Every value passes through a set-method or a guard condition, also in the constructor, so an
 * {@code Activity} can never be in an invalid state. Invalid data is rejected with an
 * unchecked exception that tells what kind of error it is:
 * <ul>
 *   <li>{@link NullPointerException} when a required value is {@code null}</li>
 *   <li>{@link IllegalArgumentException} when a required text is blank, when the activity is
 *       shared with its own logger, or when it is shared twice with the same recipient</li>
 *   <li>{@link DateTimeException} when the time of registration is in the future</li>
 * </ul>
 * A method that throws leaves the activity unchanged.
 */
public class Activity {

  private Person logger;
  private String title;
  private String description;
  private LocalDateTime registrationDateTime;
  private final Set<Person> sharedWithPersons;
  private final Set<Group> sharedWithGroups;

  /**
   * Creates a private activity.
   *
   * @param logger the person logging the activity
   * @param title a short heading for the activity, not blank
   * @param description a longer text about the activity, may be empty
   * @param registrationDateTime the date and time of registration, not in the future
   * @throws NullPointerException if any of the parameters is {@code null}
   * @throws IllegalArgumentException if {@code title} is blank
   * @throws DateTimeException if {@code registrationDateTime} is in the future
   */
  public Activity(Person logger, String title, String description,
      LocalDateTime registrationDateTime) {
    setLogger(logger);
    setTitle(title);
    setDescription(description);
    setRegistrationDateTime(registrationDateTime);
    this.sharedWithPersons = new LinkedHashSet<>();
    this.sharedWithGroups = new LinkedHashSet<>();
  }

  /**
   * Returns the person who logged the activity.
   *
   * @return the logger
   */
  public Person getLogger() {
    return logger;
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
   * Returns the persons the activity is shared with directly, not through a group.
   *
   * @return a new, unmodifiable list in the order the persons were added, possibly empty
   */
  public List<Person> getSharedWithPersons() {
    return List.copyOf(sharedWithPersons);
  }

  /**
   * Returns the groups the activity is shared with.
   *
   * @return a new, unmodifiable list in the order the groups were added, possibly empty
   */
  public List<Group> getSharedWithGroups() {
    return List.copyOf(sharedWithGroups);
  }

  /**
   * Tells whether the activity is shared with anyone.
   *
   * @return {@code true} if the activity has at least one recipient, {@code false} if it is
   *     private
   */
  public boolean isShared() {
    return !sharedWithPersons.isEmpty() || !sharedWithGroups.isEmpty();
  }

  /**
   * Tells whether a person can see the activity because it is shared with them, either
   * directly or through a group they are a member of. The logger is not counted as a
   * recipient.
   *
   * @param person the person to check
   * @return {@code true} if the activity is shared with {@code person}, otherwise
   *     {@code false}
   * @throws NullPointerException if {@code person} is {@code null}
   */
  public boolean isSharedWith(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    if (person == logger) {
      return false;
    }
    return sharedWithPersons.contains(person)
        || sharedWithGroups.stream().anyMatch(group -> group.isMember(person));
  }

  /**
   * Changes the title of the activity. Whitespace around the title is removed.
   *
   * @param title the new title, not blank
   * @throws NullPointerException if {@code title} is {@code null}
   * @throws IllegalArgumentException if {@code title} is blank
   */
  public void setTitle(String title) {
    Objects.requireNonNull(title, "title must not be null");
    if (title.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    this.title = title.strip();
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

  /**
   * Shares the activity with a single person. The activity becomes shared.
   *
   * @param person the person to share with, not the logger and not already a recipient
   * @throws NullPointerException if {@code person} is {@code null}
   * @throws IllegalArgumentException if {@code person} is the logger, or the activity is
   *     already shared directly with {@code person}
   */
  public void shareWithPerson(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    if (person == logger) {
      throw new IllegalArgumentException("an activity cannot be shared with its own logger");
    }
    if (!sharedWithPersons.add(person)) {
      throw new IllegalArgumentException("the activity is already shared with "
          + person.getName());
    }
  }

  /**
   * Shares the activity with all members of a group. The activity becomes shared.
   *
   * @param group the group to share with, not already a recipient
   * @throws NullPointerException if {@code group} is {@code null}
   * @throws IllegalArgumentException if the activity is already shared with {@code group}
   */
  public void shareWithGroup(Group group) {
    Objects.requireNonNull(group, "group must not be null");
    if (!sharedWithGroups.add(group)) {
      throw new IllegalArgumentException("the activity is already shared with "
          + group.getName());
    }
  }

  /**
   * Makes the activity private by removing all persons and groups it is shared with.
   */
  public void markAsPrivate() {
    sharedWithPersons.clear();
    sharedWithGroups.clear();
  }

  private void setLogger(Person logger) {
    this.logger = Objects.requireNonNull(logger, "logger must not be null");
  }

  private void setRegistrationDateTime(LocalDateTime registrationDateTime) {
    Objects.requireNonNull(registrationDateTime, "registrationDateTime must not be null");
    if (registrationDateTime.isAfter(LocalDateTime.now())) {
      throw new DateTimeException(
          "registrationDateTime must not be in the future: " + registrationDateTime);
    }
    this.registrationDateTime = registrationDateTime;
  }
}
