package edu.ntnu.idi.idatt.ui;

/**
 * Text based user interface of the trip and activity register.
 *
 * <p>Responsible for all interaction with the user: presenting information in the console and
 * reading input from the keyboard. The class holds no business logic of its own. It delegates
 * to the registers, which are created in {@link #init()}.
 */
public class TextUserInterface {

  /**
   * Prepares the application for use by creating the objects the user interface depends on.
   */
  public void init() {
    // The registers are created here once they exist. See the plan in README.md.
  }

  /**
   * Runs the user interface until the user chooses to quit.
   */
  public void start() {
    System.out.println("Trip and activity register");
  }
}
