package de.tobiaskahl.vservercontrol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.tobiaskahl.vservercontrol.R;
import de.tobiaskahl.vservercontrol.Session;
import de.tobiaskahl.vservercontrol.fragments.ServerFragment;
import de.tobiaskahl.vservercontrol.fragments.ServerListeFragment;
import de.tobiaskahl.vservercontrol.modelle.Server;

/**
 * Aktivität mit einer Übersicht über die Account-Daten.
 * Inhalt / Layout variiert je nach Auflösung des Engerätes.
 * <p/>
 * Handy:
 * - Liste aller Inseln in einem {@link de.tobiaskahl.vservercontrol.fragments.ServerListeFragment}.
 * <p/>
 * Tablet:
 * - Links: Liste aller Inseln in einem {@link de.tobiaskahl.vservercontrol.fragments.ServerListeFragment}.
 * - Rechts: Detail-Ansicht der ausgewählten Insel in einem {@link de.tobiaskahl.vservercontrol.fragments.ServerFragment}.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class AccountUebersichtActivity extends FragmentActivity implements ServerListeEmbedder {

    @Override
    public final void serverSelected(final Server server) {
        Session.setServer(server);
        if (isTwoColumnView()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.server_detail_container, new ServerFragment()).commit();
        } else {
            startActivity(new Intent(this, ServerActivity.class));
        }
    }

    /**
     * Prüfmethode ob es sich um eine Darstellung mit Liste + Detail oder nur die Liste handelt.
     *
     * @return true, wenn der Insel-Detail-Container server_detail_container in der View vorhanden ist.
     */
    private final boolean isTwoColumnView() {
        return findViewById(R.id.server_detail_container) != null;
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isTwoColumnView()) {
            setContentView(R.layout.activity_account_uebersicht_and_server);
            final ServerListeFragment fragment = (ServerListeFragment) getSupportFragmentManager().findFragmentById(
                    R.id.server_list);
            fragment.setActivateOnItemClick(true);
            if (Session.getAccount().getServer().size() > 0) {
                serverSelected(Session.getAccount().getServer().get(0));
            }
        } else {
            setContentView(R.layout.activity_account_uebersicht);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.server_liste_container, new ServerListeFragment())
                .commit();
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        if (isTwoColumnView()) {
            getMenuInflater().inflate(R.menu.menu_refresh, menu);
        }
        getMenuInflater().inflate(R.menu.menu_account_ueberblick, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                startLoginScreen();
                return true;
            case R.id.menu_refresh:
                if (isTwoColumnView()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.server_detail_container, new ServerFragment()).commit();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start-Methode für den Login-Screen.
     * Es wird zusätzlich als Parameter mitgegeben, dass die Auto-Login-Parameter der Properties ignoriert werden
     * sollen.
     */
    private final void startLoginScreen() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.ARG_LOGOUT, true);
        startActivity(intent);
        finish();
    }
}