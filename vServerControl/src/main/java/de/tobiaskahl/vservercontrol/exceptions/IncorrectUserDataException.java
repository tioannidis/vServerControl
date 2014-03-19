package de.tobiaskahl.vservercontrol.exceptions;

/**
 * Anwendungsweite Exception welche bei einem Datenzugriff auf eine ungültige Angabe von Benutzername ODER Passwort
 * hindeutet.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class IncorrectUserDataException extends Exception {

    private static final long serialVersionUID = 20131006L;

    public IncorrectUserDataException(final String username) {
        super("Die Benutzerdaten für den Account " + username + " sind nicht korrekt.");
    }
}