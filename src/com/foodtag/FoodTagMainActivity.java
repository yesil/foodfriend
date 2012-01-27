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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.apmem.tools.layouts.FlowLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodtag.camera.CameraManager;
import com.foodtag.task.LoadImageTask;
import com.foodtag.task.SaveTask;
import com.foodtag.task.SearchOnlineTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author Ilyas Türkben yesilturk@gmail.com
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class FoodTagMainActivity extends Activity implements
		SurfaceHolder.Callback, OnCheckedChangeListener, OnClickListener,
		OnTouchListener {

	private static final String TAG = FoodTagMainActivity.class.getSimpleName();

	private static final int SETTINGS_ID = Menu.FIRST + 2;

	private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

	private static final int CAMERA_PIC_REQUEST = 1986;

	private CameraManager cameraManager;
	private FoodTagActivityHandler handler;
	private ViewfinderView viewfinderView;
	private View resultView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private FoodTagDbOpenHelper dbOpenHelper;
	private GestureDetector gestureDetector;
	private AlertDialog dialog;
	private EditText inputBarcode;
	private ViewGroup foundView;
	private ViewGroup notFoundView;

	private Product product;

	private FlowLayout tagContainer;

	private FlowLayout tagContainerNotAdded;

	private FlowLayout tagContainerAdded;

	private String wsURL;

	private File tempFile;

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// new
		// ContextWrapper(this).deleteDatabase(FoodTagDbOpenHelper.DATABASE_NAME);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);
		window.getDecorView().setOnTouchListener(this);
		gestureDetector = new GestureDetector(new GestureListener(this));

		resultView = findViewById(R.id.result_view);

		handler = null;
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		dbOpenHelper = new FoodTagDbOpenHelper(this);
		inputBarcode = new EditText(this);
		inputBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);

		tagContainer = (FlowLayout) findViewById(R.id.tag_container);
		foundView = (ViewGroup) findViewById(R.id.found_view);
		notFoundView = (ViewGroup) findViewById(R.id.not_found_view);
		tagContainerAdded = (FlowLayout) findViewById(R.id.tag_container_added);
		tagContainerNotAdded = (FlowLayout) findViewById(R.id.tag_container_not_added);

		foundView.setVisibility(View.GONE);
		notFoundView.setVisibility(View.GONE);
		wsURL = getString(R.string.ws_url);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		Intent intent = getIntent();
		String action = intent == null ? null : intent.getAction();
		if (intent != null && action != null) {
			if (action.equals(Intents.Scan.ACTION)) {
				// Scan the formats the intent requested, and return the result
				// to the calling activity.
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
				if (intent.hasExtra(Intents.Scan.WIDTH)
						&& intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}
			} else {
				// Scan all formats and handle the results ourselves (launched
				// from Home).
				decodeFormats = null;
			}
			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
		} else {
			decodeFormats = null;
			characterSet = null;
		}

		beepManager.updatePrefs();
		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			resetStatusView();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS
				|| keyCode == KeyEvent.KEYCODE_CAMERA) {
			// Handle these events so they don't launch the Camera app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SETTINGS_ID, 0, R.string.menu_settings).setIcon(
				android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		switch (item.getItemId()) {
		case SETTINGS_ID:
			intent.setClassName(this, PreferencesActivity.class.getName());
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		product = dbOpenHelper.findByBarcode(rawResult.getText());

		if (barcode == null) {
			// This is from history -- no saved barcode
			handleDecodeInternally(rawResult.getText(), null, true);
		} else {
			beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, rawResult);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			if (prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
				Toast.makeText(this, R.string.msg_bulk_mode_scanned,
						Toast.LENGTH_SHORT).show();
				// Wait a moment or else it will scan the same barcode
				// continuously about 3 times
				if (handler != null) {
					handler.sendEmptyMessageDelayed(R.id.restart_preview,
							BULK_MODE_SCAN_DELAY_MS);
				}
				resetStatusView();
			} else {
				handleDecodeInternally(rawResult.getText(), barcode, true);
			}
		}
	}

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of
	 * the barcode.
	 * 
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_image_border));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2,
					barcode.getHeight() - 2);
			canvas.drawRect(border, paint);

			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(String barcode, Bitmap barcodeImage,
			boolean searchOnline) {
		if (searchOnline) {
			new SearchOnlineTask(wsURL, this, dbOpenHelper)
					.execute(new String[] { product.getBarcode() });
		}

		viewfinderView.setVisibility(View.GONE);
		resultView.setVisibility(View.VISIBLE);
		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		if (barcodeImage != null) {
			barcodeImageView.setImageBitmap(barcodeImage);
		} else if (!product.isPersisted()) {
			barcodeImageView.setVisibility(View.INVISIBLE);
			barcodeImageView.setImageBitmap(null);
		}
		if (product.isPersisted()) {
			barcodeImageView.setVisibility(View.VISIBLE);
			if (!searchOnline) {
				new LoadImageTask(wsURL, barcodeImageView).execute(barcode);
			}
		}

		TextView txtName = (TextView) findViewById(R.id.text_name);
		txtName.setText(product.getName());

		TextView txtDescription = (TextView) findViewById(R.id.text_description);
		txtDescription.setText(product.getIngredients());

		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				PreferencesActivity.KEY_SUPPLEMENTAL, true)) {
		}

		foundView.setVisibility(product.isPersisted() ? View.VISIBLE
				: View.GONE);
		notFoundView.setVisibility(product.isPersisted() ? View.GONE
				: View.VISIBLE);

		if (product.isPersisted()) {
			tagContainer.removeAllViews();
			HashMap<String, Tag> tags = dbOpenHelper.getTags();
			for (String tagAsString : product.getTags()) {
				if (tagAsString != null && !"".equals(tagAsString)) {
					TagButton b = new TagButton(tags.get(tagAsString), this);
					tagContainer.addView(b);
				}
			}
		} else {
			tagContainerAdded.removeAllViews();
			tagContainerNotAdded.removeAllViews();
			HashMap<String, Tag> tags = dbOpenHelper.getTags();
			for (String tagAsString : tags.keySet()) {
				TagButton b = new TagButton(tags.get(tagAsString), this);
				b.setOnClickListener(this);
				tagContainerNotAdded.addView(b);
			}

			Button newEntryButton = (Button) findViewById(R.id.btn_save_entry);
			newEntryButton.setOnClickListener(this);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			Thread.sleep(1000L);
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new FoodTagActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		} catch (InterruptedException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	private void resetStatusView() {
		resultView.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.VISIBLE);
		if (handler != null) {
			handler.restartPreviewAndDecode();
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		if (TagButton.class.equals(v.getClass())) {
			TagButton b = (TagButton) v;
			if (v.getParent() == tagContainerAdded) {
				tagContainerAdded.removeView(b);
				tagContainerNotAdded.addView(b);
				product.removeTag(b.getProductTag().getCode());
			} else {
				tagContainerNotAdded.removeView(b);
				tagContainerAdded.addView(b);
				product.addTag(b.getProductTag().getCode());
			}

		} else {
			switch (v.getId()) {
			default:
				// dbOpenHelper.createNewEntry(product);
				// resetStatusView();
				takePhoto();
			}
		}
	}

	public void showKeyboard() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.enter_barcode)
					.setMessage("")
					.setView(inputBarcode)
					.setPositiveButton(R.string.button_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String barcode = inputBarcode.getText()
											.toString();
									product = dbOpenHelper
											.findByBarcode(barcode);
									handleDecodeInternally(barcode, null, true);
								}
							})
					.setNegativeButton(R.string.button_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									resetStatusView();
								}
							}).show();
		}
		inputBarcode.setText("");
		dialog.show();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return false;
	}

	public void productFound(Product result) {
		this.product = result;
		handleDecodeInternally(product.getBarcode(), null, false);
	}

	private void takePhoto() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			tempFile = File.createTempFile("foodtag", "jpg").getAbsoluteFile();
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(tempFile));
		} catch (IOException e) {
			resetStatusView();
		}
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			new SaveTask(wsURL, tempFile, product).execute(null);
		}
		resetStatusView();
	}
}
