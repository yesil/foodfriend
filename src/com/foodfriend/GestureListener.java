package com.foodfriend;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

	private CaptureActivity activity;

	public GestureListener(CaptureActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		activity.showKeyboard();
		return true;
	}
}