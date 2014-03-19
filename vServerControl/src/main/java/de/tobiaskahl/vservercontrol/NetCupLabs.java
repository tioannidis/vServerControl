package de.tobiaskahl.vservercontrol;

public final class NetCupLabs extends NetCup {

    private static final String URL = "https://vcptest.netcup.net/WSEndUser";

    public NetCupLabs() {
        super();
    }

    @Override
    protected final String getUrl() {
        return URL;
    }
}