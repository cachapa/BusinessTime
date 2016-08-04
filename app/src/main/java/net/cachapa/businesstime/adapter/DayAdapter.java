package net.cachapa.businesstime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import net.cachapa.businesstime.R;
import net.cachapa.businesstime.manager.TimeManager;
import net.cachapa.businesstime.model.WorkDay;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private List<Long> mWorkDays;
    private TimeManager mTimeManager;
    private StringBuilder mRecycleStringBuilder;
    private AdapterView.OnItemClickListener mListener;

    public DayAdapter(Context context) {
        mWorkDays = new ArrayList<>();
        mTimeManager = TimeManager.getInstance(context);
        mRecycleStringBuilder = new StringBuilder();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_day, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        long date = mWorkDays.get(position);
        WorkDay workDay = mTimeManager.getWorkDay(date);

        String dateStr = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        String eventCountStr = holder.mEventCountView.getResources().getQuantityString(R.plurals.events, workDay.getEventCount(), workDay.getEventCount());
        String workTimeStr = DateUtils.formatElapsedTime(mRecycleStringBuilder, workDay.getWorkTime() / 1000);
        String pauseTimeStr = DateUtils.formatElapsedTime(mRecycleStringBuilder, workDay.getPauseTime() / 1000);
        String enterTimeStr = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(workDay.getEnterTime());
        String leaveTimeStr = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(workDay.getLeaveTime());

        holder.mDateView.setText(dateStr);
        holder.mEventCountView.setText(eventCountStr);
        holder.mWorkTimeView.setText(workTimeStr);
        holder.mPauseTimeView.setText(pauseTimeStr);
        holder.mEnterTimeView.setText(enterTimeStr);
        holder.mLeaveTimeView.setText(leaveTimeStr);
    }

    @Override
    public int getItemCount() {
        return mWorkDays.size();
    }

    public WorkDay getItem(int position) {
        return mTimeManager.getWorkDay(mWorkDays.get(position));
    }

    public void setWorkDays(List<Long> workDays) {
        mWorkDays.clear();
        mWorkDays.addAll(workDays);
        Collections.reverse(mWorkDays);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDateView;
        public TextView mEventCountView;
        public TextView mWorkTimeView;
        public TextView mPauseTimeView;
        public TextView mEnterTimeView;

        public TextView mLeaveTimeView;

        public ViewHolder(View v, final AdapterView.OnItemClickListener listener) {
            super(v);
            mDateView = (TextView) v.findViewById(R.id.date);
            mEventCountView = (TextView) v.findViewById(R.id.event_count);
            mWorkTimeView = (TextView) v.findViewById(R.id.work_time);
            mPauseTimeView = (TextView) v.findViewById(R.id.pause_time);
            mEnterTimeView = (TextView) v.findViewById(R.id.enter_time);
            mLeaveTimeView = (TextView) v.findViewById(R.id.leave_time);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(null, v, position, position);
                    }
                }
            });
        }

    }
}
