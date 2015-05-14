package com.codingbuffalo.businesstime.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.codingbuffalo.businesstime.R;
import com.codingbuffalo.businesstime.adapter.DayAdapter;
import com.codingbuffalo.businesstime.adapter.DividerItemDecoration;
import com.codingbuffalo.businesstime.manager.TimeManager;
import com.codingbuffalo.businesstime.model.WorkDay;

import java.util.List;

public class DayListFragment extends Fragment implements TimeManager.OnTimeListener, AdapterView.OnItemClickListener {
	private DayAdapter   mAdapter;

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
