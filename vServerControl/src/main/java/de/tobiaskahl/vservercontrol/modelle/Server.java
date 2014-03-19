package de.tobiaskahl.vservercontrol.modelle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Server implements Serializable {

    private static final long serialVersionUID = 20131006L;

    private final List<String> ipAdressen = new ArrayList<String>();
    private boolean online = false;
    private String trafficOfDay = "";
    private String trafficOfMonth = "";
    private final String vservername;

    private String vservernickname = "";

    public Server(final String pVservername) {
        super();
        vservername = pVservername;
    }

    public Server(final String pVservername, final String pVservernickname) {
        this(pVservername);
        vservernickname = pVservernickname;
    }

    public final List<String> getIpAdressen() {
        return ipAdressen;
    }

    public final String getTrafficOfDay() {
        return trafficOfDay;
    }

    public final String getTrafficOfMonth() {
        return trafficOfMonth;
    }

    public final String getVservername() {
        return vservername;
    }

    public final String getVservernickname() {
        return vservernickname;
    }

    public final boolean isOnline() {
        return online;
    }

    public final void setOnline(final boolean pOnline) {
        online = pOnline;
    }

    public final void setTrafficOfDay(final String pTrafficOfDay) {
        trafficOfDay = pTrafficOfDay;
    }

    public final void setTrafficOfMonth(final String pTrafficOfMonth) {
        trafficOfMonth = pTrafficOfMonth;
    }

    public final void setVservernickname(final String pVservernickname) {
        vservernickname = pVservernickname;
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder(getVservername());
        if (!getVservernickname().isEmpty()) {
            builder.append(" [");
            builder.append(getVservernickname());
            builder.append("]");
        }
        return builder.toString();
    }
}