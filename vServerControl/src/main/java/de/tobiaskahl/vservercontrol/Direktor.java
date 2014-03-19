package de.tobiaskahl.vservercontrol;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tobiaskahl.vservercontrol.exceptions.IncorrectUserDataException;
import de.tobiaskahl.vservercontrol.exceptions.LeseException;
import de.tobiaskahl.vservercontrol.modelle.Account;
import de.tobiaskahl.vservercontrol.modelle.Server;

/**
 * Anwendungsweiter Direktor für die deligierung von Datenzugriffen.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class Direktor {

    private static final String LOGGER_TAG = "Direktor";
    private static final String NAMESPACE = "http://enduser.service.web.vcp.netcup.de/";
    private static final String URL = "https://www.vservercontrolpanel.de/WSEndUser";
    private static final Map<Class<? extends NetCup>, NetCup> apiCache = new HashMap<Class<? extends NetCup>, NetCup>();

    /**
     * Privater Konstruktor.
     * Direktor wird nur statisch genutzt.
     */
    private Direktor() {
        super();
    }

    public static final void lese(final Server server) throws LeseException {
        HosterAPI api = getApi(Session.getAccount().getHoster());
        final String username = Session.getAccount().getUser();
        final String password = Session.getAccount().getPasswort();
        final String vserverName = server.getVservername();
        //
        final GregorianCalendar calendar = new GregorianCalendar();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        //
        final String vServerState = api.getVServerState(username, password, vserverName);
        if (vServerState.equalsIgnoreCase("online")) {
            server.setOnline(true);
        } else if (vServerState.equalsIgnoreCase("offline")) {
            server.setOnline(false);
        } else {
            throw new IllegalStateException();
        }
        server.getIpAdressen().clear();
        server.getIpAdressen().addAll(api.getVServerIPs(username, password, vserverName));
        server.setTrafficOfMonth(api.getVServerTrafficOfMonth(username, password, vserverName, year, month));
        server.setTrafficOfDay(api.getVServerTrafficOfDay(username, password, vserverName, year, month, day));
        /*
         *** Nicht erlaubt für vServer ***
        String vServerLoad = api.getVServerLoad(username, password, vserverName);
        String vServerUptime = api.getVServerUptime(username, password, vserverName);
        String vServerProcesses = api.getVServerProcesses(username, password, vserverName);
        List<FilterObject> firewallRules = api.getFirewallRules(username, password, vserverName);
        */
    }

    private static final HosterAPI getApi(Hoster hoster) throws LeseException {
        NetCup api = apiCache.get(hoster.getApiClass());
        if (api == null) {
            try {
                api = hoster.getApiClass().newInstance();
            } catch (InstantiationException e) {
                new LeseException(e);
            } catch (IllegalAccessException e) {
                new LeseException(e);
            }
            apiCache.put(hoster.getApiClass(), api);
        }
        return api;
    }

    /**
     * Liest die Account-Daten, so wie die Liste der Server über den Webservice ein.
     *
     * @param username Benutzername des Accounts
     * @param password Passwort des Accounts
     * @return Account Objekt inklusive der Serverliste
     * @throws de.tobiaskahl.vservercontrol.exceptions.IncorrectUserDataException
     * @throws de.tobiaskahl.vservercontrol.exceptions.LeseException
     */
    public static final Account leseAccount(final String username, final String password, Hoster hoster)
            throws IncorrectUserDataException, LeseException {
        try {
            Log.i(LOGGER_TAG, "leseAccount(username=\"" + username + "\", password=\"" + password + "\"");
            //
            HosterAPI api = getApi(hoster);
            List<String> liste = api.getVServers(username, password);
            if (liste.isEmpty()) {
                throw new LeseException("Unbekannter Fehler");
            }
            //
            final Account account = new Account(username, password, hoster);
            for (final String vserverName : liste) {
                Server server = new Server(vserverName);
                server.setVservernickname(api.getVServerNickname(username, password, vserverName));
                account.getServer().add(server);
            }
            return account;
        } catch (final IncorrectUserDataException e) {
            throw e;
        } catch (final LeseException e) {
            throw e;
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    public static final boolean restart(Server server) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "restart(server=\"" + server + "\"");
            //
            HosterAPI api = getApi(Session.getAccount().getHoster());
            String username = Session.getAccount().getUser();
            String password = Session.getAccount().getPasswort();
            String vServerName = server.getVservername();
            return api.restart(username, password, vServerName);
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    public static final boolean start(Server server) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "start(server=\"" + server + "\"");
            //
            HosterAPI api = getApi(Session.getAccount().getHoster());
            String username = Session.getAccount().getUser();
            String password = Session.getAccount().getPasswort();
            String vServerName = server.getVservername();
            return api.start(username, password, vServerName);
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    public static final boolean stop(Server server) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "stop(server=\"" + server + "\"");
            //
            HosterAPI api = getApi(Session.getAccount().getHoster());
            String username = Session.getAccount().getUser();
            String password = Session.getAccount().getPasswort();
            String vServerName = server.getVservername();
            return api.stop(username, password, vServerName);
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    public static final boolean reset(Server server) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "reset(server=\"" + server + "\"");
            //
            HosterAPI api = getApi(Session.getAccount().getHoster());
            String username = Session.getAccount().getUser();
            String password = Session.getAccount().getPasswort();
            String vServerName = server.getVservername();
            return api.reset(username, password, vServerName);
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    public static final boolean powerOff(Server server) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "powerOff(server=\"" + server + "\"");
            //
            HosterAPI api = getApi(Session.getAccount().getHoster());
            String username = Session.getAccount().getUser();
            String password = Session.getAccount().getPasswort();
            String vServerName = server.getVservername();
            return api.powerOff(username, password, vServerName);
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }
}