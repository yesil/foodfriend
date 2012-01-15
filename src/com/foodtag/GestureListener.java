package com.foodtag;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

	private FoodTagMainActivity activity;

	public GestureListener(FoodTagMainActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		activity.showKeyboard();
		return true;
	}
}