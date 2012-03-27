package com.foodtag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class TagButton extends ToggleButton {

	public TagButton(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.setFocusable(false);
		setDrawingCacheEnabled(true);
		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.tag_button_blue));
		this.setTextSize(18f);
	}
}
