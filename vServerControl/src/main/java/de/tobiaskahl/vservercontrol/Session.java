package de.tobiaskahl.vservercontrol;

import de.tobiaskahl.vservercontrol.modelle.Account;
import de.tobiaskahl.vservercontrol.modelle.Server;

/**
 * Anwendungsweite Session-Daten für die actitivätenübergreifende transiente Speicherung von Sitzungsdaten
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class Session {

    private static Account account = null;
    private static Server server = null;

    /**
     * Clear-Methode für die Session-Daten.
     * Es werden alle gespeicherten Daten geleert.
     */
    public static final void clear() {
        setAccount(null);
        setServer(null);
    }

    /**
     * Getter-Methode für das Account-Objekt der aktuellen Sitzung.
     *
     * @return Aktuell genutzes Account-Objekt
     */
    public static final Account getAccount() {
        return account;
    }

    /**
     * Getter-Methode für das Server-Objekt der aktuellen Sitzung.
     *
     * @return Aktuell genutzes Server-Objekt
     */
    public static final Server getServer() {
        return server;
    }

    /**
     * Setter-Methode für das Account-Objekt der aktuellen Sitzung.
     *
     * @param pAccount Account-Objekt welches ab sofort innerhalb der Sitzung verwendet wird.
     */
    public static final void setAccount(final Account pAccount) {
        account = pAccount;
    }

    /**
     * Setter-Methode für das Server-Objekt der aktuellen Sitzung.
     *
     * @param pServer Server-Objekt welches ab sofort innerhalb der Sitzung verwendet wird.
     */
    public static final void setServer(final Server pServer) {
        server = pServer;
    }

    /**
     * Privater Konstruktor.
     * Session wird nur statisch genutzt.
     */
    private Session() {
        super();
    }
}