package com.foodtag.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.foodtag.R;

public class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {

	private final ImageView barcodeImageView;
	private String wsURL;

	public LoadImageTask(String wsURL, ImageView barcodeImageView) {
		this.barcodeImageView = barcodeImageView;
		this.wsURL = wsURL;
	}

	@Override
	protected Bitmap doInBackground(String... barcodes) {
		Bitmap bm = null;
		if (barcodes.length > 0) {
			BitmapFactory.Options bmOptions;
			bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;
			Log.i("HTTP", "loading image for barcode " + barcodes[0]);
			bm = LoadImage(wsURL + "/pic/" + barcodes[0], bmOptions);
		}
		return bm;
	}

	private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			if (in != null) {
				bitmap = BitmapFactory.decodeStream(in, null, options);
				in.close();
			}
		} catch (IOException e1) {
			Log.e("HTTP", "product image loading is failed", e1);
		}
		return bitmap;
	}

	private InputStream OpenHttpConnection(String strURL) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			Log.e("HTTP", "product image loading is failed", ex);
		}
		return inputStream;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			barcodeImageView.setImageBitmap(result);
		}
	}

}
