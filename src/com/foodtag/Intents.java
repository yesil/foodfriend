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

package com.foodtag;

/**
 * This class provides the constants to use when sending an Intent to Barcode
 * Scanner. These strings are effectively API and cannot be changed.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
	private Intents() {
	}

	public static final class Scan {
		/**
		 * Send this intent to open the Barcodes app in scanning mode, find a
		 * barcode, and return the results.
		 */
		public static final String ACTION = "com.google.zxing.client.android.SCAN";

		/**
		 * By default, sending this will decode all barcodes that we understand.
		 * However it may be useful to limit scanning to certain formats. Use
		 * {@link android.content.Intent#putExtra(String, String)} with one of
		 * the values below.
		 * 
		 * Setting this is effectively shorthand for setting explicit formats
		 * with {@link #FORMATS}. It is overridden by that setting.
		 */
		public static final String MODE = "SCAN_MODE";

		/**
		 * Decode only UPC and EAN barcodes. This is the right choice for
		 * shopping apps which get prices, reviews, etc. for products.
		 */
		public static final String PRODUCT_MODE = "PRODUCT_MODE";

		/**
		 * Decode only 1D barcodes.
		 */
		public static final String ONE_D_MODE = "ONE_D_MODE";

		/**
		 * Comma-separated list of formats to scan for. The values must match
		 * the names of {@link com.google.zxing.BarcodeFormat}s, e.g.
		 * {@link com.google.zxing.BarcodeFormat#EAN_13}. Example:
		 * "EAN_13,EAN_8,QR_CODE"
		 * 
		 * This overrides {@link #MODE}.
		 */
		public static final String FORMATS = "SCAN_FORMATS";

		/**
		 * @see com.google.zxing.DecodeHintType#CHARACTER_SET
		 */
		public static final String CHARACTER_SET = "CHARACTER_SET";

		/**
		 * Optional parameters to specify the width and height of the scanning
		 * rectangle in pixels. The app will try to honor these, but will clamp
		 * them to the size of the preview frame. You should specify both or
		 * neither, and pass the size as an int.
		 */
		public static final String WIDTH = "SCAN_WIDTH";
		public static final String HEIGHT = "SCAN_HEIGHT";

		/**
		 * If a barcode is found, Barcodes returns RESULT_OK to
		 * {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
		 * of the app which requested the scan via
		 * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}
		 * The barcodes contents can be retrieved with
		 * {@link android.content.Intent#getStringExtra(String)}. If the user
		 * presses Back, the result code will be RESULT_CANCELED.
		 */
		public static final String RESULT = "SCAN_RESULT";

		/**
		 * Call intent.getStringExtra(RESULT_FORMAT) to determine which barcode
		 * format was found. See Contents.Format for possible values.
		 */
		public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

		/**
		 * Call intent.getByteArrayExtra(RESULT_BYTES) to get a {@code byte[]}
		 * of raw bytes in the barcode, if available.
		 */
		public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";

		/**
		 * Setting this to false will not save scanned codes in the history.
		 */
		public static final String SAVE_HISTORY = "SAVE_HISTORY";

		private Scan() {
		}
	}

	public static final class WifiConnect {
		/**
		 * Internal intent used to trigger connection to a wi-fi network.
		 */
		public static final String ACTION = "com.google.zxing.client.android.WIFI_CONNECT";

		/**
		 * The network to connect to, all the configuration provided here.
		 */
		public static final String SSID = "SSID";

		/**
		 * The network to connect to, all the configuration provided here.
		 */
		public static final String TYPE = "TYPE";

		/**
		 * The network to connect to, all the configuration provided here.
		 */
		public static final String PASSWORD = "PASSWORD";

		private WifiConnect() {
		}
	}

	public static final class Share {
		/**
		 * Give the user a choice of items to encode as a barcode, then render
		 * it as a QR Code and display onscreen for a friend to scan with their
		 * phone.
		 */
		public static final String ACTION = "com.google.zxing.client.android.SHARE";

		private Share() {
		}
	}
}
