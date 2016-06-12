package net.cachapa.businesstime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import net.cachapa.businesstime.R;
import net.cachapa.businesstime.model.TimeEvent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<TimeEvent> mEvents;
    private List<Long> mCheckedItems;

    public EventAdapter() {
        mEvents = new ArrayList<>();
        mCheckedItems = new LinkedList<>();
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
        boolean checked = mCheckedItems.contains(event.getTimestamp());

        holder.setEvent(event, checked);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public TimeEvent getItem(int position) {
        return mEvents.get(position);
    }

    public void setEvents(List<TimeEvent> events) {
        mCheckedItems.clear();
        mEvents.clear();
        mEvents.addAll(events);
        notifyDataSetChanged();
    }

    public List<Long> getCheckedItems() {
        return mCheckedItems;
    }

    @Override
    public long getItemId(int position) {
        return mEvents.get(position).getTimestamp();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mStatusView;
        private TextView mDateView;
        private TextView mStateView;
        private TimeEvent mEvent;
        private CheckBox mCheckBox;

        public ViewHolder(View v) {
            super(v);
            mStatusView = (ImageView) v.findViewById(R.id.status);
            mDateView = (TextView) v.findViewById(R.id.date);
            mStateView = (TextView) v.findViewById(R.id.state);
            mCheckBox = (CheckBox) v.findViewById(R.id.check_box);

            v.setOnClickListener(this);
        }

        public void setEvent(TimeEvent event, boolean checked) {
            int imageRes = event.isAtWork()
                    ? R.drawable.ic_enter_white_36dp
                    : R.drawable.ic_exit_white_36dp;
            String date = DateFormat.getTimeInstance().format(event.getTimestamp());
            String state = event.isAtWork() ? "Enter" : "Leave";

            mStatusView.setImageResource(imageRes);
            mDateView.setText(date);
            mStateView.setText(state);
            mCheckBox.setChecked(checked);
            mEvent = event;
        }

        @Override
        public void onClick(View view) {
            boolean checked = !mCheckBox.isChecked();

            if (checked) {
                mCheckedItems.add(mEvent.getTimestamp());
            } else {
                mCheckedItems.remove(mEvent.getTimestamp());
            }
            mCheckBox.setChecked(!mCheckBox.isChecked());
        }
    }
}
