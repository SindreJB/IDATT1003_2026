package edu.ntnu.idi.idatt.ui;

import java.io.EOFException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Reads and checks input typed by the user in the console.
 *
 * <p>Every method keeps asking until the input is valid, so the caller always gets a usable
 * value and never has to handle a parse error. Wrong input is answered with a short message
 * telling the user what was expected.
 *
 * <p>If the input ends, for example because the user presses Ctrl+D (Ctrl+Z on Windows), an
 * {@link UncheckedIOException} is thrown, so the user interface can close in an orderly way.
 *
 * <p>Written with help from Claude Code (AI), see the AI log in the README.
 */
public class InputReader {

  /** Format of dates, both when read and when printed. Strict, so 30.02.2026 is rejected. */
  static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

  /** Format of date and time, both when read and when printed. */
  static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

  private final Scanner scanner;

  /**
   * Creates a reader of the given input.
   *
   * @param scanner the source of the user's input
   * @throws NullPointerException if {@code scanner} is {@code null}
   */
  public InputReader(Scanner scanner) {
    this.scanner = Objects.requireNonNull(scanner, "scanner must not be null");
  }

  /**
   * Reads a line of text, which may be empty.
   *
   * @param prompt the text shown before the input
   * @return the text without whitespace around it
   * @throws UncheckedIOException if there is no more input
   */
  public String readText(String prompt) {
    System.out.print(prompt);
    if (!scanner.hasNextLine()) {
      throw new UncheckedIOException(new EOFException("no more input"));
    }
    return scanner.nextLine().strip();
  }

  /**
   * Reads a line of text that is not blank.
   *
   * @param prompt the text shown before the input
   * @return the text without whitespace around it, never empty
   * @throws UncheckedIOException if there is no more input
   */
  public String readNonBlankText(String prompt) {
    String text = readText(prompt);
    while (text.isEmpty()) {
      System.out.println("The input cannot be empty.");
      text = readText(prompt);
    }
    return text;
  }

  /**
   * Reads a whole number within a range.
   *
   * @param prompt the text shown before the input
   * @param min the smallest allowed number
   * @param max the largest allowed number
   * @return a number from {@code min} to {@code max}, both included
   * @throws UncheckedIOException if there is no more input
   */
  public int readIntInRange(String prompt, int min, int max) {
    Optional<Integer> number = parseIntInRange(readText(prompt), min, max);
    while (number.isEmpty()) {
      System.out.println("Please type a whole number from " + min + " to " + max + ".");
      number = parseIntInRange(readText(prompt), min, max);
    }
    return number.get();
  }

  /**
   * Reads a yes or no answer. Accepts y, yes, n and no, ignoring case.
   *
   * @param prompt the text shown before the input
   * @return {@code true} for yes, {@code false} for no
   * @throws UncheckedIOException if there is no more input
   */
  public boolean readYesNo(String prompt) {
    String answer = readText(prompt).toLowerCase(Locale.ROOT);
    while (!List.of("y", "yes", "n", "no").contains(answer)) {
      System.out.println("Please answer y or n.");
      answer = readText(prompt).toLowerCase(Locale.ROOT);
    }
    return answer.startsWith("y");
  }

  /**
   * Reads a date in the format dd.MM.yyyy.
   *
   * @param prompt the text shown before the input
   * @return the date
   * @throws UncheckedIOException if there is no more input
   */
  public LocalDate readDate(String prompt) {
    Optional<LocalDate> date = parseDate(readText(prompt));
    while (date.isEmpty()) {
      System.out.println("Please type a valid date as dd.mm.yyyy, for example 24.12.2026.");
      date = parseDate(readText(prompt));
    }
    return date.get();
  }

  /**
   * Reads a date and time in the format dd.MM.yyyy HH:mm that is not in the future. Empty
   * input means the current date and time.
   *
   * @param prompt the text shown before the input
   * @return the date and time, not in the future
   * @throws UncheckedIOException if there is no more input
   */
  public LocalDateTime readPastDateTimeOrNow(String prompt) {
    Optional<LocalDateTime> dateTime = parsePastDateTimeOrNow(readText(prompt));
    while (dateTime.isEmpty()) {
      System.out.println("Please type a date and time that is not in the future, as"
          + " dd.mm.yyyy hh:mm, for example 03.10.2026 14:30. Leave it empty for now.");
      dateTime = parsePastDateTimeOrNow(readText(prompt));
    }
    return dateTime.get();
  }

  /**
   * Lets the user choose one of several options by number. The options are numbered from 1,
   * and 0 means none of them.
   *
   * @param <T> the type of the options
   * @param heading the text shown above the numbered options
   * @param options the options to choose from
   * @param describeOption gives the text shown for each option
   * @param labelOfZero what choosing 0 means, for example "Cancel" or "Done"
   * @return the chosen option, or an empty {@code Optional} if the user chose 0 or there was
   *     nothing to choose from
   * @throws UncheckedIOException if there is no more input
   */
  public <T> Optional<T> chooseFromList(String heading, List<T> options,
      Function<T, String> describeOption, String labelOfZero) {
    if (options.isEmpty()) {
      System.out.println("There is nothing to choose from.");
      return Optional.empty();
    }
    System.out.println(heading);
    for (int number = 1; number <= options.size(); number++) {
      System.out.printf("%3d. %s%n", number, describeOption.apply(options.get(number - 1)));
    }
    System.out.printf("%3d. %s%n", 0, labelOfZero);
    int choice = readIntInRange("Choose a number: ", 0, options.size());
    if (choice == 0) {
      return Optional.empty();
    }
    return Optional.of(options.get(choice - 1));
  }

  private static Optional<Integer> parseIntInRange(String text, int min, int max) {
    try {
      int number = Integer.parseInt(text);
      if (number < min || number > max) {
        return Optional.empty();
      }
      return Optional.of(number);
    } catch (NumberFormatException notANumber) {
      return Optional.empty();
    }
  }

  private static Optional<LocalDate> parseDate(String text) {
    try {
      return Optional.of(LocalDate.parse(text, DATE_FORMATTER));
    } catch (DateTimeParseException notADate) {
      return Optional.empty();
    }
  }

  private static Optional<LocalDateTime> parsePastDateTimeOrNow(String text) {
    if (text.isEmpty()) {
      return Optional.of(LocalDateTime.now().withSecond(0).withNano(0));
    }
    try {
      LocalDateTime dateTime = LocalDateTime.parse(text, DATE_TIME_FORMATTER);
      if (dateTime.isAfter(LocalDateTime.now())) {
        return Optional.empty();
      }
      return Optional.of(dateTime);
    } catch (DateTimeParseException notADateTime) {
      return Optional.empty();
    }
  }
}
