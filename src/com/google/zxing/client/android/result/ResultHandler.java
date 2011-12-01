/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.Button;

import com.foodriend.Product;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

/**
 * A base class for the Android-specific barcode handlers. These allow the app
 * to polymorphically suggest the appropriate actions for each data type.
 * 
 * This class also contains a bunch of utility methods to take common actions
 * like opening a URL. They could easily be moved into a helper object, but it
 * can't be static because the Activity instance is needed to launch an intent.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class ResultHandler {

	private static final int[] buttons = { R.string.button_product_vegetarian,
			R.string.button_product_halal, R.string.button_product_kosher };
	private static final DateFormat DATE_FORMAT;
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss", Locale.ENGLISH);
	public static final int MAX_BUTTON_COUNT = 3;

	static {
		DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		// For dates without a time, for purposes of interacting with Android,
		// the resulting timestamp
		// needs to be midnight of that day in GMT. See:
		// http://code.google.com/p/android/issues/detail?id=8330
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	private final Activity activity;

	private Product product;
	private final ParsedResult result;

	public ResultHandler(Activity activity, ParsedResult result, Product product) {
		this.result = result;
		this.activity = activity;
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}

	Activity getActivity() {
		return activity;
	}

	public int getButtonCount() {
		return buttons.length - 1;
	}

	public int getButtonText(int index) {
		return buttons[index];
	}

	/**
	 * Create a possibly styled string for the contents of the current barcode.
	 * 
	 * @return The text to be displayed.
	 */
	public CharSequence getDisplayContents() {
		String contents = result.getDisplayResult();
		return contents.replace("\r", "");
	}

	public int getDisplayTitle() {
		return R.string.result_product;
	}

	public ParsedResult getResult() {
		return result;
	}

	/**
	 * A convenience method to get the parsed type. Should not be overridden.
	 * 
	 * @return The parsed type, e.g. URI or ISBN
	 */
	public final ParsedResultType getType() {
		return result.getType();
	}

	public void handleAfterView(Button button, int index) {
		switch (index) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		default:
			break;
		}
		if (product != null) {
			if (index == 0 && product.isVegetarian()) {
				button.setBackgroundColor(Color.GREEN);
			}
			if (index == 1 && product.isHalal()) {
				button.setBackgroundColor(Color.GREEN);
			}
			if (index == 2 && product.isKosher()) {
				button.setBackgroundColor(Color.GREEN);
			}
		}
	}

	public boolean isPersisted() {
		return product.isPersisted();
	}
}
