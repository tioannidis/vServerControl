package de.tobiaskahl.vservercontrol.activities;

import de.tobiaskahl.vservercontrol.modelle.Server;

/**
 * Interface für alle Aktivitäten die eine Liste von Inseln einbetten und über Selektionen informiert werden wollen.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public interface ServerListeEmbedder {

    /**
     * Notifier-Methode für die Auswahl eines Server innerhalb der Liste.
     *
     * @param server In der Liste ausgewähltes Server-Objekt
     */
    void serverSelected(Server server);
}