package de.tobiaskahl.vservercontrol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import de.tobiaskahl.vservercontrol.R;
import de.tobiaskahl.vservercontrol.fragments.ServerFragment;

/**
 * Aktivität für die Detail-Auskunft einer Insel.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class ServerActivity extends FragmentActivity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.server_detail_container, new ServerFragment()).commit();
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        getMenuInflater().inflate(R.menu.menu_account_ueberblick, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, AccountUebersichtActivity.class));
                return true;
            case R.id.menu_logout:
                startLoginScreen();
                return true;
            case R.id.menu_refresh:
                getSupportFragmentManager().beginTransaction().replace(R.id.server_detail_container, new ServerFragment()).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}