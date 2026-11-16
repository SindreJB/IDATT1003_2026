package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link Person}.
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>The constructor stores the name</li>
 *   <li>The constructor removes whitespace around the name</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>The constructor rejects a {@code null} name</li>
 *   <li>The constructor rejects a blank name</li>
 * </ul>
 */
class PersonTest {

  // ---------- Positive tests ----------

  @Test
  void constructorStoresName() {
    Person person = new Person("Kari Nordmann");

    assertEquals("Kari Nordmann", person.getName());
  }

  @Test
  void constructorRemovesWhitespaceAroundName() {
    Person person = new Person("  Kari Nordmann\t");

    assertEquals("Kari Nordmann", person.getName());
  }

  // ---------- Negative tests ----------

  @Test
  void constructorThrowsNullPointerExceptionWhenNameIsNull() {
    assertThrows(NullPointerException.class, () -> new Person(null));
  }

  @Test
  void constructorThrowsIllegalArgumentExceptionWhenNameIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Person("   "));
  }
}
