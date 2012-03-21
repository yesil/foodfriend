package com.foodtag.task;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.foodtag.service.ProductService;

public class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {

	private final ImageView barcodeImageView;
	private String wsURL;
	private final ProductService productService;

	public LoadImageTask(ProductService productService, String wsURL,
			ImageView barcodeImageView) {
		this.productService = productService;
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
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(URL);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			entity.writeTo(bout);
			bitmap = BitmapFactory.decodeByteArray(bout.toByteArray(), 0,
					bout.size(), options);
		} catch (Exception ex) {
			Log.e("HTTP", "product image loading is failed", ex);
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			productService.loadingImageFinished();
			barcodeImageView.setImageBitmap(result);
		}
	}

}
