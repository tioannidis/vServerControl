package de.tobiaskahl.vservercontrol;

import java.util.List;

import de.tobiaskahl.vservercontrol.exceptions.IncorrectUserDataException;
import de.tobiaskahl.vservercontrol.exceptions.LeseException;


public interface HosterAPI {
    List<String> getVServerIPs(String username, String password,
                               String vserverName) throws LeseException;

    String getVServerLoad(String username, String password, String vserverName)
            throws LeseException;

    String getVServerNickname(String username, String password, String vserverName)
            throws LeseException;

    String getVServerProcesses(String username, String password,
                               String vserverName) throws LeseException;

    String getVServerState(String username, String password, String vserverName)
            throws LeseException;

    String getVServerTrafficOfDay(String username, String password,
                                  String vserverName, int year, int month, int day) throws LeseException;

    List<String> getVServers(String username, String password)
            throws IncorrectUserDataException, LeseException;

    String getVServerTrafficOfMonth(String username, String password,
                                    String vserverName, int year, int month) throws LeseException;

    String getVServerUptime(String username, String password, String vserverName)
            throws LeseException;

    boolean restart(String username, String password, String verserName) throws LeseException;

    boolean start(String username, String password, String verserName) throws LeseException;

    boolean stop(String username, String password, String verserName) throws LeseException;

    boolean reset(String username, String password, String verserName) throws LeseException;

    boolean powerOff(String username, String password, String verserName) throws LeseException;
}
