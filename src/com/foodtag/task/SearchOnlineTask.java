package com.foodtag.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.foodtag.Product;
import com.foodtag.service.ProductService;

public class SearchOnlineTask extends AsyncTask<String, Integer, Product> {

	private final ProductService productService;

	public SearchOnlineTask(ProductService productService) {
		this.productService = productService;
	}

	protected Product doInBackground(String... barcodes) {
		if (barcodes.length > 0) {
			Product product = null;
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(productService.getWsURL() + "/p/"
					+ barcodes[0] + "?region="
					+ System.getProperty("user.region"));
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}

					JSONObject productJson = new JSONObject(builder.toString());
					boolean locked = productJson.getBoolean("locked");
					product = new Product(productJson.getString("barcode"),
							productJson.getString("name"),
							productJson.getString("desc"), locked);
					product.setHalal(productJson.getBoolean("halal"));
					product.setKosher(productJson.getBoolean("kosher"));
					product.setVegetarian(productJson.getBoolean("vegetarian"));
				} else {
					Log.e(getClass().toString(), "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			publishProgress(100);
			return product;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Product result) {
		if (result != null) {
			productService.searchFinished(result);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

}
