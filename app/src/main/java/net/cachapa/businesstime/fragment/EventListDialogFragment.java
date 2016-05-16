package net.cachapa.businesstime.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import net.cachapa.businesstime.R;
import net.cachapa.businesstime.adapter.DividerItemDecoration;
import net.cachapa.businesstime.adapter.EventAdapter;
import net.cachapa.businesstime.manager.TimeManager;
import net.cachapa.businesstime.model.TimeEvent;

import java.text.DateFormat;
import java.util.List;

public class EventListDialogFragment extends DialogFragment implements TimeManager.OnTimeListener {
    private static final String ARG_DATE = "date";

    private long mDate;
    private EventAdapter mAdapter;
    private Toolbar mToolbar;

    /**
     * @deprecated Use {@link #create(long)} instead
     */
    @Deprecated
    public EventListDialogFragment() {
        super();
    }

    public static EventListDialogFragment create(long date) {
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date);

        @SuppressWarnings("deprecation")
        EventListDialogFragment fragment = new EventListDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_event_list, null);

        mDate = getArguments().getLong(ARG_DATE);
        mAdapter = new EventAdapter();

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(DateFormat.getDateInstance(DateFormat.FULL).format(mDate));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Close", null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        TimeManager.getInstance(getActivity()).addOnTimeListener(this);
        updateEvents();
    }

    @Override
    public void onPause() {
        TimeManager.getInstance(getActivity()).removeOnTimeListener(this);
        super.onPause();
    }

    @Override
    public void onTimeModified() {
        updateEvents();
    }

    private void updateEvents() {
        List<TimeEvent> events = TimeManager.getInstance(getActivity()).getEventsForDay(mDate);
        mToolbar.setSubtitle(events.size() + (events.size() == 1 ? " event" : " events"));
        mAdapter.setEvents(events);
    }
}
