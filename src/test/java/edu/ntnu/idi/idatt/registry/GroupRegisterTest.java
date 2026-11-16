package edu.ntnu.idi.idatt.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.Group;
import edu.ntnu.idi.idatt.model.Person;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link GroupRegister}.
 *
 * <p>Each test starts with a new register holding two groups, added out of alphabetical
 * order: "Hiking club" and "Cycling friends".
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>A new register is empty</li>
 *   <li>{@code findGroupByName} finds the group, ignoring case and whitespace around the
 *       name</li>
 *   <li>{@code findGroupByName} returns an empty result for an unknown name</li>
 *   <li>{@code getGroupsSortedByName} returns all groups alphabetically</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>{@code addGroup} rejects {@code null}</li>
 *   <li>{@code addGroup} rejects a group whose name is already in use, also with different
 *       case, and keeps the original group</li>
 *   <li>{@code findGroupByName} rejects {@code null}</li>
 * </ul>
 */
class GroupRegisterTest {

  private GroupRegister groupRegister;
  private Person kari;
  private Group hikingClub;
  private Group cyclingFriends;

  @BeforeEach
  void createRegisterWithTwoGroups() {
    groupRegister = new GroupRegister();
    kari = new Person("Kari Nordmann");
    hikingClub = new Group("Hiking club", kari);
    cyclingFriends = new Group("Cycling friends", kari);
    groupRegister.addGroup(hikingClub);
    groupRegister.addGroup(cyclingFriends);
  }

  // ---------- Positive tests ----------

  @Test
  void newRegisterIsEmpty() {
    GroupRegister emptyRegister = new GroupRegister();

    assertTrue(emptyRegister.getGroupsSortedByName().isEmpty());
  }

  @Test
  void findGroupByNameFindsGroupIgnoringCaseAndWhitespace() {
    Group foundGroup = groupRegister.findGroupByName(" HIKING club ").orElseThrow();

    assertSame(hikingClub, foundGroup);
  }

  @Test
  void findGroupByNameReturnsEmptyResultForUnknownName() {
    assertTrue(groupRegister.findGroupByName("Ski team").isEmpty());
  }

  @Test
  void getGroupsSortedByNameReturnsAllGroupsAlphabetically() {
    assertEquals(List.of(cyclingFriends, hikingClub), groupRegister.getGroupsSortedByName());
  }

  // ---------- Negative tests ----------

  @Test
  void addGroupThrowsNullPointerExceptionWhenGroupIsNull() {
    assertThrows(NullPointerException.class, () -> groupRegister.addGroup(null));
  }

  @Test
  void addGroupThrowsIllegalArgumentExceptionAndKeepsOriginalWhenNameIsInUse() {
    Group anotherHikingClub = new Group("hiking CLUB", kari);

    assertThrows(IllegalArgumentException.class,
        () -> groupRegister.addGroup(anotherHikingClub));

    assertSame(hikingClub, groupRegister.findGroupByName("Hiking club").orElseThrow());
    assertEquals(2, groupRegister.getGroupsSortedByName().size());
  }

  @Test
  void findGroupByNameThrowsNullPointerExceptionWhenNameIsNull() {
    assertThrows(NullPointerException.class, () -> groupRegister.findGroupByName(null));
  }
}
