package com.codingbuffalo.businesstime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingbuffalo.businesstime.model.TimeEvent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
	private List<TimeEvent> mEvents;

	public EventAdapter() {
		mEvents = new ArrayList<>();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		TimeEvent event = getItem(position);

		String date = DateFormat.getTimeInstance().format(event.getTimestamp());
		String state = event.isAtWork() ? "Enter" : "Leave";

		holder.mDateView.setText(date);
		holder.mStateView.setText(state);
	}

	@Override
	public int getItemCount() {
		return mEvents.size();
	}

	public TimeEvent getItem(int position) {
		return mEvents.get(position);
	}

	public void setEvents(List<TimeEvent> events) {
		mEvents.clear();
		mEvents.addAll(events);
		notifyDataSetChanged();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView mDateView;
		public TextView mStateView;

		public ViewHolder(View v) {
			super(v);
			mDateView = (TextView) v.findViewById(android.R.id.text1);
			mStateView = (TextView) v.findViewById(android.R.id.text2);
		}
	}
}
