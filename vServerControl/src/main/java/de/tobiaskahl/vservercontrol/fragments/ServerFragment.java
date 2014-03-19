package de.tobiaskahl.vservercontrol.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

import de.tobiaskahl.vservercontrol.Direktor;
import de.tobiaskahl.vservercontrol.R;
import de.tobiaskahl.vservercontrol.Session;
import de.tobiaskahl.vservercontrol.exceptions.LeseException;
import de.tobiaskahl.vservercontrol.modelle.Server;

public final class ServerFragment extends Fragment {

    // Modell-Objekte
    private Server server;
    // View-Objekte.
    private View vServerData;
    private View vProgressBar;
    private Button actionstart;
    private Button actionRestart;
    private Button actionStop;
    private Button actionReset;
    private Button actionPoweroff;
    private TextView serverNameText;
    private TextView serverNickText;
    private TextView serverStateText;
    private TextView serverTrafficMonthText;
    private TextView serverTrafficDayText;
    private TextView serverIpsText;

    public ServerFragment() {
        super();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = Session.getServer();
    }

    private final void showDialogAndRunTaskWennOK(String title, String message, final AsyncTask<Server, ?, ?> task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.execute(server);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_server, container, false);
        vServerData = rootView.findViewById(R.id.server_data);
        vProgressBar = rootView.findViewById(R.id.lade_status);
        actionstart = (Button) rootView.findViewById(R.id.action_start);
        actionRestart = (Button) rootView.findViewById(R.id.action_restart);
        actionStop = (Button) rootView.findViewById(R.id.action_stop);
        actionReset = (Button) rootView.findViewById(R.id.action_reset);
        actionPoweroff = (Button) rootView.findViewById(R.id.action_poweroff);
        serverNameText = (TextView) rootView.findViewById(R.id.serverNameText);
        serverNickText = (TextView) rootView.findViewById(R.id.serverNickText);
        serverStateText = (TextView) rootView.findViewById(R.id.serverStateText);
        serverTrafficMonthText = (TextView) rootView.findViewById(R.id.serverTrafficMonthText);
        serverTrafficDayText = (TextView) rootView.findViewById(R.id.serverTrafficDayText);
        serverIpsText = (TextView) rootView.findViewById(R.id.serverIpsText);
        //
        actionstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndRunTaskWennOK("Wirklich?", "Wollen Sie den Server wirklich starten?", new ServerStartTask());
            }
        });
        actionRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndRunTaskWennOK("Wirklich?", "Wollen Sie den Server wirklich neustarten?", new ServerRestartTask());
            }
        });
        actionStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndRunTaskWennOK("Wirklich?", "Wollen Sie den Server wirklich herunterfahren?", new ServerStopTask());
            }
        });
        actionReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndRunTaskWennOK("Wirklich?", "Wollen Sie den Server wirklich resetten?", new ServerResetTask());
            }
        });
        actionPoweroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndRunTaskWennOK("Wirklich?", "Wollen Sie den Server wirklich ausschalten?", new ServerPowerOffTask());
            }
        });
        //
        showProgress(true);
        new LoadServerDataTask().execute(server);
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private final void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vProgressBar.setVisibility(View.VISIBLE);
            vProgressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            vProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            vServerData.setVisibility(View.VISIBLE);
            vServerData.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            vServerData.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            vProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            vServerData.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private final class ServerRestartTask extends AsyncTask<Server, Integer, Exception> {
        @Override
        protected final Exception doInBackground(final Server... servers) {
            try {
                for (Server server : servers) {
                    Direktor.restart(server);
                }
                return null;
            } catch (final LeseException e) {
                return e;
            }
        }

        @Override
        protected final void onPostExecute(final Exception exception) {
            if (exception != null) {
                Toast.makeText(getActivity().getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final class ServerStartTask extends AsyncTask<Server, Integer, Exception> {
        @Override
        protected final Exception doInBackground(final Server... servers) {
            try {
                for (Server server : servers) {
                    Direktor.start(server);
                }
                return null;
            } catch (final LeseException e) {
                return e;
            }
        }

        @Override
        protected final void onPostExecute(final Exception exception) {
            if (exception != null) {
                Toast.makeText(getActivity().getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final class ServerStopTask extends AsyncTask<Server, Integer, Exception> {
        @Override
        protected final Exception doInBackground(final Server... servers) {
            try {
                for (Server server : servers) {
                    Direktor.stop(server);
                }
                return null;
            } catch (final LeseException e) {
                return e;
            }
        }

        @Override
        protected final void onPostExecute(final Exception exception) {
            if (exception != null) {
                Toast.makeText(getActivity().getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final class ServerResetTask extends AsyncTask<Server, Integer, Exception> {
        @Override
        protected final Exception doInBackground(final Server... servers) {
            try {
                for (Server server : servers) {
                    Direktor.reset(server);
                }
                return null;
            } catch (final LeseException e) {
                return e;
            }
        }

        @Override
        protected final void onPostExecute(final Exception exception) {
            if (exception != null) {
                Toast.makeText(getActivity().getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final class ServerPowerOffTask extends AsyncTask<Server, Integer, Exception> {
        @Override
        protected final Exception doInBackground(final Server... servers) {
            try {
                for (Server server : servers) {
                    Direktor.powerOff(server);
                }
                return null;
            } catch (final LeseException e) {
                return e;
            }
        }

        @Override
        protected final void onPostExecute(final Exception exception) {
            if (exception != null) {
                Toast.makeText(getActivity().getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final class LoadServerDataTask extends AsyncTask<Server, Integer, Object> {
        @Override
        protected final Object doInBackground(final Server... server) {
            if (server.length == 0 && server.length > 1) {
                throw new IllegalArgumentException();
            }
            try {
                Direktor.lese(server[0]);
                return server[0];
            } catch (final LeseException e) {
                return e;
            }
        }

        @Override
        protected final void onPostExecute(final Object object) {
            if (object instanceof Server) {
                serverNameText.setText(server.getVservername());
                serverNickText.setText(server.getVservernickname());
                serverStateText.setText(server.isOnline() ? "Online" : "Offline");
                serverTrafficMonthText.setText(server.getTrafficOfMonth());
                serverTrafficDayText.setText(server.getTrafficOfDay());
                final StringBuilder ips = new StringBuilder();
                final Iterator<String> iter = server.getIpAdressen().iterator();
                while (iter.hasNext()) {
                    ips.append(iter.next());
                    if (iter.hasNext()) {
                        ips.append("\n");
                    }
                }
                serverIpsText.setText(ips.toString());
                //
                actionstart.setVisibility(server.isOnline() ? View.GONE : View.VISIBLE);
                actionRestart.setVisibility(server.isOnline() ? View.VISIBLE : View.GONE);
                actionStop.setVisibility(server.isOnline() ? View.VISIBLE : View.GONE);
                actionReset.setVisibility(server.isOnline() ? View.VISIBLE : View.GONE);
                actionPoweroff.setVisibility(server.isOnline() ? View.VISIBLE : View.GONE);
            } else if (object instanceof Exception) {
                Toast.makeText(getActivity().getApplicationContext(), object.toString(), Toast.LENGTH_LONG).show();
            } else {
                throw new IllegalArgumentException();
            }
            showProgress(false);
        }
    }
}