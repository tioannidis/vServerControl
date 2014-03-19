package de.tobiaskahl.vservercontrol;

public enum Hoster {
    NETCUP(NetCup.class),
    NETCUP_LABS(NetCupLabs.class);

    private final Class<? extends NetCup> api;

    Hoster(Class<? extends NetCup> pApi) {
        api = pApi;
    }

    public final Class<? extends NetCup> getApiClass() {
        return api;
    }
}