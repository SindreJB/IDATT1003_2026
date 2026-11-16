package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link Group}.
 *
 * <p>Each test starts with a new group, "Hiking club", whose only member is Kari. Ola and
 * Aisha are not members.
 *
 * <p><b>Positive tests</b>
 * <ul>
 *   <li>The constructor stores the name and the first member</li>
 *   <li>The constructor removes whitespace around the name</li>
 *   <li>{@code addMember} adds the person after the existing members</li>
 *   <li>{@code removeMember} removes only the given member</li>
 *   <li>{@code isMember} is {@code false} for a person who is not a member</li>
 * </ul>
 *
 * <p><b>Negative tests</b>
 * <ul>
 *   <li>The constructor rejects a {@code null} name, a blank name and a {@code null} first
 *       member</li>
 *   <li>{@code addMember} rejects {@code null} and a person who is already a member</li>
 *   <li>{@code removeMember} rejects {@code null}, a person who is not a member, and the last
 *       member</li>
 *   <li>{@code isMember} rejects {@code null}</li>
 *   <li>A returned list of members cannot be used to change the group</li>
 * </ul>
 */
class GroupTest {

  private Person kari;
  private Person ola;
  private Person aisha;
  private Group hikingClub;

  @BeforeEach
  void createGroupWithKariAsOnlyMember() {
    kari = new Person("Kari Nordmann");
    ola = new Person("Ola Hansen");
    aisha = new Person("Aisha Khan");
    hikingClub = new Group("Hiking club", kari);
  }

  // ---------- Positive tests ----------

  @Test
  void constructorStoresNameAndFirstMember() {
    assertEquals("Hiking club", hikingClub.getName());
    assertEquals(List.of(kari), hikingClub.getMembers());
    assertTrue(hikingClub.isMember(kari));
  }

  @Test
  void constructorRemovesWhitespaceAroundName() {
    Group group = new Group("  Hiking club ", kari);

    assertEquals("Hiking club", group.getName());
  }

  @Test
  void addMemberAddsPersonAfterExistingMembers() {
    hikingClub.addMember(ola);
    hikingClub.addMember(aisha);

    assertEquals(List.of(kari, ola, aisha), hikingClub.getMembers());
  }

  @Test
  void removeMemberRemovesOnlyTheGivenMember() {
    hikingClub.addMember(ola);
    hikingClub.addMember(aisha);

    hikingClub.removeMember(ola);

    assertEquals(List.of(kari, aisha), hikingClub.getMembers());
    assertFalse(hikingClub.isMember(ola));
  }

  @Test
  void isMemberIsFalseForPersonWhoIsNotMember() {
    assertFalse(hikingClub.isMember(ola));
  }

  // ---------- Negative tests ----------

  @Test
  void constructorThrowsNullPointerExceptionWhenNameIsNull() {
    assertThrows(NullPointerException.class, () -> new Group(null, kari));
  }

  @Test
  void constructorThrowsIllegalArgumentExceptionWhenNameIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Group(" ", kari));
  }

  @Test
  void constructorThrowsNullPointerExceptionWhenFirstMemberIsNull() {
    assertThrows(NullPointerException.class, () -> new Group("Hiking club", null));
  }

  @Test
  void addMemberThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class, () -> hikingClub.addMember(null));
  }

  @Test
  void addMemberThrowsIllegalArgumentExceptionWhenPersonIsAlreadyMember() {
    assertThrows(IllegalArgumentException.class, () -> hikingClub.addMember(kari));

    assertEquals(List.of(kari), hikingClub.getMembers());
  }

  @Test
  void removeMemberThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class, () -> hikingClub.removeMember(null));
  }

  @Test
  void removeMemberThrowsNoSuchElementExceptionWhenPersonIsNotMember() {
    hikingClub.addMember(ola);

    assertThrows(NoSuchElementException.class, () -> hikingClub.removeMember(aisha));

    assertEquals(List.of(kari, ola), hikingClub.getMembers());
  }

  @Test
  void removeMemberThrowsIllegalStateExceptionAndKeepsMemberWhenMemberIsTheLast() {
    assertThrows(IllegalStateException.class, () -> hikingClub.removeMember(kari));

    assertEquals(List.of(kari), hikingClub.getMembers());
  }

  @Test
  void isMemberThrowsNullPointerExceptionWhenPersonIsNull() {
    assertThrows(NullPointerException.class, () -> hikingClub.isMember(null));
  }

  @Test
  void returnedListOfMembersCannotChangeGroup() {
    List<Person> members = hikingClub.getMembers();

    assertThrows(UnsupportedOperationException.class, () -> members.add(ola));

    assertFalse(hikingClub.isMember(ola));
  }
}
