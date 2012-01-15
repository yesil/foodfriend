package com.foodtag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class TagButton extends Button {

	private Tag productTag;

	public Tag getProductTag() {
		return productTag;
	}

	public TagButton(Context context, AttributeSet attributeSet) {
		this(null, context);
	}

	public TagButton(final Tag productTag, Context context) {
		super(context);
		this.productTag = productTag;
		this.setFocusable(false);
		setDrawingCacheEnabled(true);
		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.tag_button_blue));
		this.setText(productTag.getName());
		this.setTextSize(18f);
	}
}
