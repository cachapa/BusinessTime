package net.cachapa.businesstime.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import net.cachapa.businesstime.R;
import net.cachapa.businesstime.adapter.DayAdapter;
import net.cachapa.businesstime.adapter.DividerItemDecoration;
import net.cachapa.businesstime.manager.TimeManager;
import net.cachapa.businesstime.model.WorkDay;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DayListFragment extends Fragment implements TimeManager.OnTimeListener, AdapterView.OnItemClickListener {
    private DayAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_list, container, false);

        mAdapter = new DayAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TimeManager.getInstance(getActivity()).addOnTimeListener(this);
        updateWorkDays();
    }

    @Override
    public void onPause() {
        TimeManager.getInstance(getActivity()).removeOnTimeListener(this);
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WorkDay workDay = mAdapter.getItem(position);
        EventListDialogFragment.create(workDay.getDate()).show(getFragmentManager(), null);
    }

    @Override
    public void onTimeModified() {
        updateWorkDays();
    }

    private void updateWorkDays() {
        List<Long> workDays = TimeManager.getInstance(getActivity()).getWorkDays();
        mAdapter.setWorkDays(workDays);
    }
}
