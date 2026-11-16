package edu.ntnu.idi.idatt.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * A named group of persons, for example a hiking club, that activities can be shared with.
 *
 * <p><b>Fields and data types</b>
 * <ul>
 *   <li>{@code name} – {@code String}, a free text name for the group.</li>
 *   <li>{@code members} – a {@link LinkedHashSet} of {@link Person}. A set, because a person
 *       is either a member or not, and can never be a member twice. The linked variant keeps
 *       the members in the order they joined, so printed member lists are predictable.</li>
 * </ul>
 *
 * <p><b>Mutability</b><br>
 * The name is fixed at creation, since the group register finds groups by name. The members
 * can change: persons can be added and removed. The internal set is never handed out.
 *
 * <p><b>Invalid data</b><br>
 * A group always has at least one member, so the constructor requires a first member and the
 * last member cannot be removed. Invalid data is rejected with:
 * <ul>
 *   <li>{@link NullPointerException} when a required value is {@code null}</li>
 *   <li>{@link IllegalArgumentException} when the name is blank, or a person who is already a
 *       member is added</li>
 *   <li>{@link NoSuchElementException} when a person who is not a member is removed</li>
 *   <li>{@link IllegalStateException} when the last member is removed</li>
 * </ul>
 * A method that throws leaves the group unchanged.
 */
public class Group {

  private String name;
  private final Set<Person> members;

  /**
   * Creates a group with one member. Whitespace around the name is removed.
   *
   * @param name the name of the group, not blank
   * @param firstMember the first member of the group
   * @throws NullPointerException if {@code name} or {@code firstMember} is {@code null}
   * @throws IllegalArgumentException if {@code name} is blank
   */
  public Group(String name, Person firstMember) {
    setName(name);
    this.members = new LinkedHashSet<>();
    addMember(firstMember);
  }

  /**
   * Returns the name of the group.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the members of the group, in the order they joined.
   *
   * @return a new, unmodifiable list with at least one member
   */
  public List<Person> getMembers() {
    return List.copyOf(members);
  }

  /**
   * Tells whether a person is a member of the group.
   *
   * @param person the person to check
   * @return {@code true} if {@code person} is a member, otherwise {@code false}
   * @throws NullPointerException if {@code person} is {@code null}
   */
  public boolean isMember(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    return members.contains(person);
  }

  /**
   * Adds a person to the group.
   *
   * @param person the person to add, not already a member
   * @throws NullPointerException if {@code person} is {@code null}
   * @throws IllegalArgumentException if {@code person} is already a member
   */
  public void addMember(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    if (!members.add(person)) {
      throw new IllegalArgumentException(person.getName() + " is already a member of " + name);
    }
  }

  /**
   * Removes a person from the group.
   *
   * @param person the member to remove
   * @throws NullPointerException if {@code person} is {@code null}
   * @throws NoSuchElementException if {@code person} is not a member
   * @throws IllegalStateException if {@code person} is the only member left
   */
  public void removeMember(Person person) {
    Objects.requireNonNull(person, "person must not be null");
    if (!members.contains(person)) {
      throw new NoSuchElementException(person.getName() + " is not a member of " + name);
    }
    if (members.size() == 1) {
      throw new IllegalStateException("the last member of " + name + " cannot be removed");
    }
    members.remove(person);
  }

  private void setName(String name) {
    Objects.requireNonNull(name, "name must not be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    this.name = name.strip();
  }
}
