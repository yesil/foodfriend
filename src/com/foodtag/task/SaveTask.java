package com.foodtag.task;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.foodtag.Product;
import com.foodtag.service.ProductService;

public class SaveTask extends AsyncTask<Void, Integer, Boolean> {

	private final Product product;
	private final File file;
	private String wsURL;
	private final ProductService productService;

	public SaveTask(ProductService productService, String wsURL,
			Product product, File file) {
		this.productService = productService;
		this.product = product;
		this.file = file;
		this.wsURL = wsURL;
	}

	@Override
	protected Boolean doInBackground(Void... args) {
		BitmapFactory.Options bmOptions;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		Log.i("HTTP", "Uploading product");
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 60000);
			HttpConnectionParams.setSoTimeout(params, 60000);
			HttpPost post = new HttpPost(wsURL + "/ws");
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			if (file != null) {
				FileBody cbFile = new FileBody(file, "image/jpeg");
				mpEntity.addPart("picture", cbFile);
			}
			mpEntity.addPart("barcode", new StringBody(product.getBarcode()));
			mpEntity.addPart("name", new StringBody(product.getName()));
			mpEntity.addPart("desc",
					new StringBody(product.getDescription()));
			mpEntity.addPart("region",
					new StringBody(System.getProperty("user.region")));
			mpEntity.addPart("halal",
					new StringBody(String.valueOf(product.isHalal())));
			mpEntity.addPart("kosher",
					new StringBody(String.valueOf(product.isKosher())));
			mpEntity.addPart("vegetarian",
					new StringBody(String.valueOf(product.isVegetarian())));
			post.setEntity(mpEntity);
			httpclient.execute(post);
			if (file != null) {
				file.delete();
			}
		} catch (IOException e) {
			Log.e("HTTP", "Erreur while uploading product");
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		productService.savingFinished();
	}
}
