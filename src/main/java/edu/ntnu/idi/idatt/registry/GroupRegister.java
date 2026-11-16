package edu.ntnu.idi.idatt.registry;

import edu.ntnu.idi.idatt.model.Group;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A register of the groups that activities can be shared with.
 *
 * <p><b>Choice of collection</b><br>
 * The groups are kept in a {@link HashMap} with the name as key, for the same reasons as in
 * {@link PersonRegister}: the user picks a group by name, and the key keeps the names unique.
 * The key is the name in lower case, so names that differ only in case count as the same.
 *
 * <p><b>Encapsulation</b><br>
 * The internal map is never handed out. Lists of groups are new lists.
 */
public class GroupRegister {

  private static final Comparator<Group> BY_NAME =
      Comparator.comparing(Group::getName, String.CASE_INSENSITIVE_ORDER);

  private final Map<String, Group> groupsByLowerCaseName;

  /**
   * Creates an empty register.
   */
  public GroupRegister() {
    this.groupsByLowerCaseName = new HashMap<>();
  }

  /**
   * Adds a group to the register.
   *
   * @param group the group to add, whose name is not already in use
   * @throws NullPointerException if {@code group} is {@code null}
   * @throws IllegalArgumentException if a group with the same name, ignoring case, is already
   *     in the register
   */
  public void addGroup(Group group) {
    Objects.requireNonNull(group, "group must not be null");
    String key = toKey(group.getName());
    if (groupsByLowerCaseName.containsKey(key)) {
      throw new IllegalArgumentException("a group named " + group.getName()
          + " is already in the register");
    }
    groupsByLowerCaseName.put(key, group);
  }

  /**
   * Finds the group with a given name, ignoring case and whitespace around the name.
   *
   * @param name the name to search for
   * @return the group with that name, or an empty {@code Optional} if there is none
   * @throws NullPointerException if {@code name} is {@code null}
   */
  public Optional<Group> findGroupByName(String name) {
    Objects.requireNonNull(name, "name must not be null");
    return Optional.ofNullable(groupsByLowerCaseName.get(toKey(name)));
  }

  /**
   * Returns all groups, sorted alphabetically by name.
   *
   * @return a new, unmodifiable list, empty if the register is empty
   */
  public List<Group> getGroupsSortedByName() {
    return groupsByLowerCaseName.values().stream()
        .sorted(BY_NAME)
        .toList();
  }

  private static String toKey(String name) {
    return name.strip().toLowerCase(Locale.ROOT);
  }
}
