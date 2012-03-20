package com.foodtag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class TagButton extends ToggleButton {

	private TagEnum productTag;

	public TagButton(Context context, AttributeSet attributeSet) {
		this(null, context);
	}

	public TagButton(TagEnum productTag, Context context) {
		super(context);
		this.productTag = productTag;
		this.setFocusable(false);
		setDrawingCacheEnabled(true);
		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.tag_button_blue));
		this.setText(productTag.getLabel());
		this.setTextSize(18f);
	}

	public TagEnum getProductTag() {
		return productTag;
	}

	public void setProductTag(String TagEnum) {
		this.productTag = productTag;
	}
}
