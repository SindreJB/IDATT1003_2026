package edu.ntnu.idi.idatt.registry;

import edu.ntnu.idi.idatt.model.Person;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A register of the persons who can log activities, so that a logger can be chosen among
 * earlier loggers instead of being typed again.
 *
 * <p><b>Choice of collection</b><br>
 * The persons are kept in a {@link HashMap} with the name as key. The user picks a logger by
 * name, so lookup by name is the main operation, and a map does it directly. The key also
 * makes the names unique: two persons whose names differ only in upper and lower case would
 * be impossible to tell apart in the user interface, so the key is the name in lower case.
 *
 * <p><b>Encapsulation</b><br>
 * The internal map is never handed out. Lists of persons are new lists.
 */
public class PersonRegister {

  private static final Comparator<Person> BY_NAME =
      Comparator.comparing(Person::getName, String.CASE_INSENSITIVE_ORDER);

  private final Map<String, Person> personsByLowerCaseName;

  /**
   * Creates an empty register.
   */
  public PersonRegister() {
    this.personsByLowerCaseName = new HashMap<>();
  }

  /**
   * Adds a person to the register.
   *
   * @param person the person to add, whose name is not already in use
   * @throws NullPointerException if {@code person} is {@code null}
   * @throws IllegalArgumentException if a person with the same name, ignoring case, is
   *     already in the register
   */
  public void addPerson(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    String key = toKey(person.getName());
    if (personsByLowerCaseName.containsKey(key)) {
      throw new IllegalArgumentException("a person named " + person.getName()
          + " is already in the register");
    }
    personsByLowerCaseName.put(key, person);
  }

  /**
   * Finds the person with a given name, ignoring case and whitespace around the name.
   *
   * @param name the name to search for
   * @return the person with that name, or an empty {@code Optional} if there is none
   * @throws NullPointerException if {@code name} is {@code null}
   */
  public Optional<Person> findPersonByName(String name) {
    Objects.requireNonNull(name, "name must not be null");
    return Optional.ofNullable(personsByLowerCaseName.get(toKey(name)));
  }

  /**
   * Returns all persons, sorted alphabetically by name.
   *
   * @return a new, unmodifiable list, empty if the register is empty
   */
  public List<Person> getPersonsSortedByName() {
    return personsByLowerCaseName.values().stream()
        .sorted(BY_NAME)
        .toList();
  }

  private static String toKey(String name) {
    return name.strip().toLowerCase(Locale.ROOT);
  }
}
