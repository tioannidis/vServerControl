package de.tobiaskahl.vservercontrol.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import de.tobiaskahl.vservercontrol.Direktor;
import de.tobiaskahl.vservercontrol.Hoster;
import de.tobiaskahl.vservercontrol.R;
import de.tobiaskahl.vservercontrol.Session;
import de.tobiaskahl.vservercontrol.exceptions.IncorrectUserDataException;
import de.tobiaskahl.vservercontrol.exceptions.LeseException;

/**
 * Aktivität für den Log-In an der Anwendung.
 * <p/>
 * Der zuletzt eingeloggte Benutzername wird in den Properties der Anwendung gespeichert.
 * Sollte der Benutzer sich für den Autologin entscheiden, so wird zusätzlich auch das Passwort gespeichert und beim
 * nächsten Start dieser Aktivität die Anmeldung automatisch durchgeführt.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public final class LoginActivity extends Activity {

    /**
     * Key für die Übergabe ob es sich bei dem Start dieser Aktivität um einen Logout-Prozess handelt und die
     * automatische Anmeldung daher nicht durchgeführt werden soll.
     */
    public static final String ARG_LOGOUT = "logout";
    private static final String LOGGER_TAG = "LoginActivity";
    // Keys für das Speichern der Anmeldedaten in den Anwendungs-Properties
    private static final String PREF_ARG_HOSTER = "hoster";
    private static final String PREF_ARG_PASSWORD = "password";
    private static final String PREF_ARG_SILENT = "silentLogin";
    private static final String PREF_ARG_USERNAME = "username";
    /**
     * Thread-Referenz für den Login-Task. Ermöglicht das senden eines Cancel-Requests an den Thread.
     */
    private UserLoginTask mAuthTask = null;
    // Modell-Objekte
    private String mUsername = "";
    private String mPassword = "";
    private boolean mSilentLogin = false;
    private Hoster mHoster = Hoster.NETCUP;
    // View-Objekte.
    private CheckBox vAutologin;
    private View vLoginForm;
    private View vLoginStatus;
    private TextView vLoginStatusMessage;
    private EditText vPassword;
    private EditText vUsername;

    /**
     * Anmelde-Methode welche versucht mit den eingegebenen Benutzerdaten eine Anmeldung durchzuführen.
     * Wenn bereits ein Anmeldeprozess läuft, dann wird nichts getan.
     * <p/>
     * ggf. auftretende Fehler werden dem Anwender per Error an den Eingabe-Feldern visualisiert.
     */
    public final void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Fehler! Netzwerk nicht verfügbar.", Toast.LENGTH_LONG).show();
            return;
        }

        // Alte Error-Angaben zurücksetzen
        vUsername.setError(null);
        vPassword.setError(null);

        // Aktuelle GUI-Felder in die Modell-Attribute mappen
        mUsername = vUsername.getText().toString();
        mPassword = vPassword.getText().toString();
        mSilentLogin = vAutologin.isChecked();

        // Eingaben auf Plausibilität prüfen
        boolean plausibel = true;
        if (TextUtils.isEmpty(mUsername)) {
            vUsername.setError(getString(R.string.error_field_required));
            vUsername.requestFocus();
            plausibel = false;
        }
        if (TextUtils.isEmpty(mPassword)) {
            vPassword.setError(getString(R.string.error_field_required));
            if (plausibel) {
                vPassword.requestFocus();
            }
            plausibel = false;
        }

        // Anmeldeprozess durchführen
        if (plausibel) {
            vLoginStatusMessage.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Anmelde-Methode welche durch das Lesen der Account-Daten prüft, ob die Benutzerdaten korrekt sind.
     * Bei erfolgreicher Anmeldung wird das Account-Objekt in der Session gespeichert.
     *
     * @return true, wenn die Anmeldung durchgeführt und der Account in der Session gespeichert werden konnte.
     * @throws de.tobiaskahl.vservercontrol.exceptions.LeseException
     */
    private final boolean fuehreAnmeldungDurch() throws LeseException {
        try {
            Session.setAccount(Direktor.leseAccount(mUsername, mPassword, Hoster.NETCUP));
        } catch (final IncorrectUserDataException e) {
            Log.i(LOGGER_TAG, "Anmeldung Fehlgeschlagen (IncorrectUserData): username=\"" + mUsername + "\"");
            return false;
        }
        return true;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.anbieterNetcup:
                if (checked)
                    mHoster = Hoster.NETCUP;
                break;
            case R.id.anbieterNetcupLabs:
                if (checked)
                    mHoster = Hoster.NETCUP_LABS;
                break;
        }
    }

    private final boolean isNetworkAvailable() {
        final ConnectivityManager localConnectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService("connectivity");
        NetworkInfo[] arrayOfNetworkInfo = null;
        if (localConnectivityManager != null) {
            arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
        }
        if (arrayOfNetworkInfo != null) {
            for (final NetworkInfo element : arrayOfNetworkInfo) {
                if (element.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Einstellungen laden
        final SharedPreferences settings = getPreferences(MODE_PRIVATE);
        mUsername = settings.getString(PREF_ARG_USERNAME, "");
        try {
            mHoster = Hoster.valueOf(settings.getString(PREF_ARG_HOSTER, Hoster.NETCUP.name()));
        } catch (IllegalArgumentException e) {
            mHoster = Hoster.NETCUP;
        }
        RadioButton btnHoster = null;
        switch (mHoster) {
            case NETCUP:
                btnHoster = (RadioButton) findViewById(R.id.anbieterNetcup);
                break;
            case NETCUP_LABS:
                btnHoster = (RadioButton) findViewById(R.id.anbieterNetcupLabs);
                break;
        }
        if (btnHoster != null) {
            btnHoster.setChecked(true);
        }
        if (!getIntent().getBooleanExtra(ARG_LOGOUT, false)) {
            mPassword = settings.getString(PREF_ARG_PASSWORD, "");
            mSilentLogin = settings.getBoolean(PREF_ARG_SILENT, false);
        }

        // View vorbelegen
        vUsername = (EditText) findViewById(R.id.username);
        vUsername.setText(mUsername);
        vPassword = (EditText) findViewById(R.id.password);
        vPassword.setText(mPassword);
        vPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int id, final KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        vAutologin = (CheckBox) findViewById(R.id.autologin);
        vAutologin.setChecked(mSilentLogin);
        vLoginForm = findViewById(R.id.login_form);
        vLoginStatus = findViewById(R.id.login_status);
        vLoginStatusMessage = (TextView) findViewById(R.id.login_status_message);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected final void onDestroy() {
        super.onDestroy();

        // ggf. noch laufende Anmeldung abbrechen
        if (mAuthTask != null && !mAuthTask.isCancelled()) {
            mAuthTask.cancel(true);
        }
    }

    @Override
    protected final void onStart() {
        super.onStart();

        // Focus auf die jeweils fehlende Angaben legen
        if (mUsername.isEmpty()) {
            vUsername.requestFocus();
        } else if (mPassword.isEmpty()) {
            vPassword.requestFocus();
        } else if (mSilentLogin) {
            // Wenn alle Angaben vollständig sind und das Flag silentLogin gesetzt ist, dann Anmeldung durchführen
            attemptLogin();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Einstellungen speichern
        final SharedPreferences settings = getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREF_ARG_SILENT, mSilentLogin);
        editor.putString(PREF_ARG_USERNAME, mUsername);
        editor.putString(PREF_ARG_PASSWORD, mSilentLogin ? mPassword : "");
        editor.putString(PREF_ARG_HOSTER, mHoster.name());
        editor.commit();
    }

    /**
     * Aufräum-Methode die nach dem Abbrechen des Anmeldeprozessen aufgerufen werden sollte.
     * <p/>
     * Diese Methode leer die Daten im Cache, löscht die Referenz auf den Anmelde-Task und stellt die GUI wieder für
     * Eingaben bereit.
     */
    private final void postAnmeldungAbgebrochen() {
        mAuthTask = null;
        Session.clear();
        showProgress(false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private final void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vLoginStatus.setVisibility(View.VISIBLE);
            vLoginStatus.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            vLoginStatus.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            vLoginForm.setVisibility(View.VISIBLE);
            vLoginForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            vLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            vLoginStatus.setVisibility(show ? View.VISIBLE : View.GONE);
            vLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private final void verarbeiteAnmeldeErgebnis(final boolean success, final Exception exeption) {
        mAuthTask = null;
        if (success) {
            startActivity(new Intent(LoginActivity.this, AccountUebersichtActivity.class));
            finish();
        } else {
            showProgress(false);
            if (exeption != null) {
                vPassword.setError(exeption.toString());
            } else {
                vPassword.setError(getString(R.string.error_incorrect_password));
            }
            vPassword.requestFocus();
        }
    }

    /**
     * Asynchroner Login-Task für die Anmeldung des Benutzer in einem separaten Thread.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private LeseException exeption = null;

        @Override
        protected final Boolean doInBackground(final Void... params) {
            try {
                return fuehreAnmeldungDurch();
            } catch (final LeseException e) {
                exeption = e;
                return Boolean.FALSE;
            }
        }

        @Override
        protected final void onCancelled() {
            postAnmeldungAbgebrochen();
        }

        @Override
        protected final void onPostExecute(final Boolean success) {
            verarbeiteAnmeldeErgebnis(success, exeption);
        }
    }
}