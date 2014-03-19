package de.tobiaskahl.vservercontrol.exceptions;

/**
 * Anwendungsweite Exception welche bei einem Datenzugriff.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class LeseException extends Exception {

    private static final long serialVersionUID = 20131006L;

    public LeseException(final String message) {
        super(message);
    }

    public LeseException(final Throwable t) {
        super(t);
    }
}