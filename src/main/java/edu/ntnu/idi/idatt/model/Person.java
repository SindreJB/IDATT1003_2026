package edu.ntnu.idi.idatt.model;

import java.util.Objects;

/**
 * A person who can log activities, be a member of groups and have activities shared with them.
 *
 * <p><b>Fields and data types</b>
 * <ul>
 *   <li>{@code name} – {@code String}, the full name of the person. A name is free text, so
 *       {@code String} is the natural type.</li>
 * </ul>
 *
 * <p><b>Mutability</b><br>
 * The name is fixed at creation. The person register finds persons by their name, so a name
 * that could change afterwards would let the register get out of step with its contents.
 * {@code Person} objects are therefore immutable.
 *
 * <p><b>Identity</b><br>
 * {@code equals} and {@code hashCode} are not overridden, so two {@code Person} objects are
 * equal only if they are the same object. Two real people can share a name; telling them apart
 * is the job of the person register, which refuses a second person with the same name.
 *
 * <p><b>Invalid data</b><br>
 * The name is checked by its set-method, which the constructor uses:
 * <ul>
 *   <li>{@link NullPointerException} when the name is {@code null}</li>
 *   <li>{@link IllegalArgumentException} when the name is blank</li>
 * </ul>
 */
public class Person {

  private String name;

  /**
   * Creates a person. Whitespace around the name is removed.
   *
   * @param name the full name of the person, not blank
   * @throws NullPointerException if {@code name} is {@code null}
   * @throws IllegalArgumentException if {@code name} is blank
   */
  public Person(String name) {
    setName(name);
  }

  /**
   * Returns the full name of the person.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  private void setName(String name) {
    Objects.requireNonNull(name, "name must not be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    this.name = name.strip();
  }
}
