package com.foodtag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class SearchOnlineTask extends AsyncTask<String, Integer, Product> {

	private final FoodTagDbOpenHelper dbOpenHelper;
	private final FoodTagMainActivity captureActivity;

	public SearchOnlineTask(FoodTagMainActivity captureActivity,
			FoodTagDbOpenHelper dbOpenHelper) {
		this.dbOpenHelper = dbOpenHelper;
		this.captureActivity = captureActivity;
	}

	protected Product doInBackground(String... barcodes) {
		if (barcodes.length > 0) {
			Product product = null;
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://yesil.github.com/yesil/"
					+ barcodes[0] + ".json" + "?region="
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
					Set<String> tagSet = new HashSet<String>();
					JSONArray jsonArray = productJson.getJSONArray("tags");
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							tagSet.add(jsonArray.get(i).toString());
						}
					}
					product = new Product(productJson.getString("barcode"),
							productJson.getString("name"),
							productJson.getString("ingredients"), tagSet);
					dbOpenHelper.createNewEntry(product);
					product.setPersisted(true);

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
			captureActivity.productFound(result);
		} else {
			captureActivity.productNotFound();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

}
