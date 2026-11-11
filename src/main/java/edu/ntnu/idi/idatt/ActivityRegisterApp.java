package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.ui.TextUserInterface;

/**
 * Entry point of the trip and activity register application.
 *
 * <p>The only responsibility of this class is to create the user interface and hand over
 * control to it. All user interaction is handled by {@link TextUserInterface}.
 */
public class ActivityRegisterApp {

  /**
   * Starts the application by initializing and then starting the text based user interface.
   *
   * @param args command line arguments, which are not used by the application
   */
  public static void main(String[] args) {
    TextUserInterface textUserInterface = new TextUserInterface();
    textUserInterface.init();
    textUserInterface.start();
  }
}
