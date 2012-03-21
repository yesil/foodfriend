package com.foodtag.service;

import java.io.File;
import java.io.Serializable;

import android.widget.ImageView;

import com.foodtag.FoodTagMainActivity;
import com.foodtag.Product;
import com.foodtag.TagEnum;
import com.foodtag.task.LoadImageTask;
import com.foodtag.task.SaveTask;
import com.foodtag.task.SearchOnlineTask;

public class ProductService implements Serializable {

	private static final long serialVersionUID = 2311832181163174393L;

	private final String wsURL;

	private final FoodTagMainActivity captureActivity;

	private boolean searching;

	private boolean loadingImage;

	private boolean saving;

	public ProductService(FoodTagMainActivity captureActivity, String wsURL) {
		this.captureActivity = captureActivity;
		this.wsURL = wsURL;
	}

	public boolean addRemoveTag(Product product, TagEnum tag) {
		boolean added = true;
		if (product.getTags().contains(tag)) {
			product.getTags().remove(tag);
			added = false;
		} else {
			product.getTags().add(tag);
		}
		save(product);
		return added;
	}

	public String getWsURL() {
		return wsURL;
	}

	public void loadImage(ImageView barcodeImageView, String barcode) {
		loadingImage = true;
		new LoadImageTask(this, wsURL, barcodeImageView).execute(barcode);
		showHideProgress();
	}

	public void save(Product product) {
		saving = true;
		new SaveTask(this, wsURL, product, null).execute((Void) null);
		showHideProgress();
	}

	public void save(Product product, File tempFile) {
		saving = true;
		new SaveTask(this, wsURL, product, tempFile).execute((Void) null);
		showHideProgress();
	}

	public void searchOnline(String barcode) {
		searching = true;
		new SearchOnlineTask(this).execute(new String[] { barcode });
		showHideProgress();
	}

	public void loadingImageFinished() {
		loadingImage = false;
		showHideProgress();
	}

	public void savingFinished() {
		saving = false;
		showHideProgress();
	}

	public void searchFinished(Product product) {
		searching = false;
		showHideProgress();
		captureActivity.productFound(product);
	}

	private void showHideProgress() {
		captureActivity.showHideProgress(searching || loadingImage || saving);
	}

}
