package com.codingbuffalo.businesstime.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingbuffalo.businesstime.R;
import com.codingbuffalo.businesstime.manager.TimeManager;

public class ClockFragment extends Fragment implements TimeManager.OnTimeListener {
	private Handler       mTickHandler;
	private StringBuilder mRecycleStringBuilder;

	private TextView mTimeCounterView;
	private TextView mWorkStatusView;
	private TextView mBalanceView;

	public ClockFragment() {
		mTickHandler = new Handler();
		mRecycleStringBuilder = new StringBuilder();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_clock, container, false);

		mTimeCounterView = (TextView) view.findViewById(R.id.time_counter);
		mWorkStatusView = (TextView) view.findViewById(R.id.connection_status);
		mBalanceView = (TextView) view.findViewById(R.id.balance);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		TimeManager.getInstance(getActivity()).addOnTimeListener(this);
		updateViews();
	}

	@Override
	public void onPause() {
		mTickHandler.removeCallbacks(mTicker);
		TimeManager.getInstance(getActivity()).removeOnTimeListener(this);
		super.onPause();
	}

	@Override
	public void onTimeModified() {
		updateViews();
	}

	private void updateViews() {
		TimeManager tm = TimeManager.getInstance(getActivity());

		long elapsedSeconds = tm.getWorkTimeToday() / 1000;
		String elapsedTime = DateUtils.formatElapsedTime(mRecycleStringBuilder, elapsedSeconds);

		long balanceSeconds = tm.getTimeBalance() / 1000;
		String balanceTime;
		if (balanceSeconds < 0) {
			balanceTime = "-" + DateUtils.formatElapsedTime(mRecycleStringBuilder, -balanceSeconds);
		} else {
			balanceTime = DateUtils.formatElapsedTime(mRecycleStringBuilder, balanceSeconds);
		}

		mTimeCounterView.setText(elapsedTime);
		mWorkStatusView.setText(tm.isCacheAtWork() ? "At work" : "Left work");
		mBalanceView.setText(balanceTime);

		// Schedule an update after 1 second
		if (tm.isCacheAtWork()) {
			mTickHandler.postDelayed(mTicker, 1000);
		}
	}

	private Runnable mTicker = new Runnable() {
		@Override
		public void run() {
			updateViews();
		}
	};
}
