package edu.ntnu.idi.idatt.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.Person;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link PersonRegister}.
 *
 * <p>Each test starts with a new register holding two persons, added out of alphabetical
 * order: Ola Hansen and Kari Nordmann.
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>A new register is empty</li>
 *   <li>{@code findPersonByName} finds the person, ignoring case and whitespace around the
 *       name</li>
 *   <li>{@code findPersonByName} returns an empty result for an unknown name</li>
 *   <li>{@code getPersonsSortedByName} returns all persons alphabetically</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>{@code addPerson} rejects {@code null}</li>
 *   <li>{@code addPerson} rejects a person whose name is already in use, also with different
 *       case, and keeps the original person</li>
 *   <li>{@code findPersonByName} rejects {@code null}</li>
 * </ul>
 */
class PersonRegisterTest {

  private PersonRegister personRegister;
  private Person ola;
  private Person kari;

  @BeforeEach
  void createRegisterWithTwoPersons() {
    personRegister = new PersonRegister();
    ola = new Person("Ola Hansen");
    kari = new Person("Kari Nordmann");
    personRegister.addPerson(ola);
    personRegister.addPerson(kari);
  }

  // ---------- Positive tests ----------

  @Test
  void newRegisterIsEmpty() {
    PersonRegister emptyRegister = new PersonRegister();

    assertTrue(emptyRegister.getPersonsSortedByName().isEmpty());
  }

  @Test
  void findPersonByNameFindsPersonIgnoringCaseAndWhitespace() {
    Person foundPerson = personRegister.findPersonByName("  kari NORDMANN ").orElseThrow();

    assertSame(kari, foundPerson);
  }

  @Test
  void findPersonByNameReturnsEmptyResultForUnknownName() {
    assertTrue(personRegister.findPersonByName("Aisha Khan").isEmpty());
  }

  @Test
  void getPersonsSortedByNameReturnsAllPersonsAlphabetically() {
    assertEquals(List.of(kari, ola), personRegister.getPersonsSortedByName());
  }

  // ---------- Negative tests ----------

  @Test
  void addPersonThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class, () -> personRegister.addPerson(null));
  }

  @Test
  void addPersonThrowsIllegalArgumentExceptionAndKeepsOriginalWhenNameIsInUse() {
    Person anotherKari = new Person("KARI nordmann");

    assertThrows(IllegalArgumentException.class, () -> personRegister.addPerson(anotherKari));

    assertSame(kari, personRegister.findPersonByName("Kari Nordmann").orElseThrow());
    assertEquals(2, personRegister.getPersonsSortedByName().size());
  }

  @Test
  void findPersonByNameThrowsNullPointerExceptionWhenNameIsNull() {
    assertThrows(NullPointerException.class, () -> personRegister.findPersonByName(null));
  }
}
