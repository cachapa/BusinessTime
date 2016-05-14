package com.codingbuffalo.businesstime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingbuffalo.businesstime.R;
import com.codingbuffalo.businesstime.manager.TimeManager;
import com.codingbuffalo.businesstime.model.TimeEvent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<TimeEvent> mEvents;

    public EventAdapter() {
        mEvents = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TimeEvent event = getItem(position);

        holder.setEvent(event);
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

    @Override
    public long getItemId(int position) {
        return mEvents.get(position).getTimestamp();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mStatusView;
        private TextView mDateView;
        private TextView mStateView;
        private TimeEvent mEvent;

        public ViewHolder(View v) {
            super(v);
            mStatusView = (ImageView) v.findViewById(R.id.status);
            mDateView = (TextView) v.findViewById(R.id.date);
            mStateView = (TextView) v.findViewById(R.id.state);
            v.findViewById(R.id.delete).setOnClickListener(this);
        }

        public void setEvent(TimeEvent event) {
            int imageRes = event.isAtWork() ? R.drawable.ic_enter_white_36dp : R.drawable.ic_exit_white_36dp;
            String date = DateFormat.getTimeInstance().format(event.getTimestamp());
            String state = event.isAtWork() ? "Enter" : "Leave";

            mStatusView.setImageResource(imageRes);
            mDateView.setText(date);
            mStateView.setText(state);
            mEvent = event;
        }

        @Override
        public void onClick(View view) {
            TimeManager.getInstance(view.getContext()).removeEvent(mEvent.getTimestamp());
        }
    }
}
