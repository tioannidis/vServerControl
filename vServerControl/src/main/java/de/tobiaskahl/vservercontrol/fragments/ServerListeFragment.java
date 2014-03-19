package de.tobiaskahl.vservercontrol.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.tobiaskahl.vservercontrol.Session;
import de.tobiaskahl.vservercontrol.activities.ServerListeEmbedder;
import de.tobiaskahl.vservercontrol.modelle.Server;

public final class ServerListeFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private List<Server> liste = null;
    private ServerListeEmbedder listEmbedder = null;
    private int mActivatedPosition = ListView.INVALID_POSITION;

    public ServerListeFragment() {
        super();
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ServerListeEmbedder)) {
            throw new IllegalStateException(
                    "Das ServerListeFragment darf nur in einem ServerListeEmbedder verwendet werden!");
        }
        listEmbedder = (ServerListeEmbedder) activity;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.getAccount() != null) {
            liste = Session.getAccount().getServer();
        } else {
            liste = new ArrayList<Server>();
        }
        setListAdapter(new ArrayAdapter<Server>(getActivity(), android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, liste));
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        listEmbedder = null;
    }

    @Override
    public final void onListItemClick(final ListView listView, final View view, final int position, final long id) {
        super.onListItemClick(listView, view, position, id);
        if (listEmbedder != null) {
            listEmbedder.serverSelected(liste.get(position));
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != AdapterView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    private final void setActivatedPosition(final int position) {
        if (position == AdapterView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public final void setActivateOnItemClick(final boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView()
                .setChoiceMode(activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
    }
}