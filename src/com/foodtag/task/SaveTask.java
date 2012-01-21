package com.foodtag.task;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
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

public class SaveTask extends AsyncTask<Void, Integer, Boolean> {

	private final Product product;
	private final File file;
	private String wsURL;

	public SaveTask(String wsURL, File file, Product product) {
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
			FileBody cbFile = new FileBody(file, "image/jpeg");
			mpEntity.addPart("barcode", new StringBody(product.getBarcode()));
			mpEntity.addPart("picture", cbFile);
			mpEntity.addPart("region",
					new StringBody(System.getProperty("user.region")));
			mpEntity.addPart("tags",
					new StringBody(StringUtils.join(product.getTags(), ",")));
			post.setEntity(mpEntity);
			httpclient.execute(post);
			file.delete();
		} catch (IOException e) {
			Log.e("HTTP", "Erreur while uploading product");
			return false;
		}
		return true;
	}
}
