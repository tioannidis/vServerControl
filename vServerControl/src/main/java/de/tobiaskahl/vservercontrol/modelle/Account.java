package de.tobiaskahl.vservercontrol.modelle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.tobiaskahl.vservercontrol.Hoster;

public class Account implements Serializable {

    private static final long serialVersionUID = 20131006L;
    // Login
    private final String user;
    private final String passwort;
    private final Hoster hoster;
    private final List<Server> server = new ArrayList<Server>();

    public Account(final String pUser, final String pPasswort, final Hoster pHoster) {
        super();
        user = pUser;
        passwort = pPasswort;
        hoster = pHoster;
    }

    public final String getPasswort() {
        return passwort;
    }

    public final List<Server> getServer() {
        return server;
    }

    public final String getUser() {
        return user;
    }

    public final Hoster getHoster() {
        return hoster;
    }

    @Override
    public final String toString() {
        return user;
    }
}