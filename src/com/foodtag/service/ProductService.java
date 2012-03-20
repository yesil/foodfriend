package com.foodtag.service;

import java.io.File;
import java.io.Serializable;

import android.widget.ImageView;

import com.foodtag.FoodTagDbOpenHelper;
import com.foodtag.FoodTagMainActivity;
import com.foodtag.Product;
import com.foodtag.TagEnum;
import com.foodtag.task.LoadImageTask;
import com.foodtag.task.SaveTask;
import com.foodtag.task.SearchOnlineTask;

public class ProductService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2311832181163174393L;

	private final FoodTagDbOpenHelper dbOpenHelper;

	private final String wsURL;

	public ProductService(FoodTagDbOpenHelper dbOpenHelper, String wsURL) {
		this.wsURL = wsURL;
		this.dbOpenHelper = dbOpenHelper;
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

	public Product findByBarcode(String barcode) {
		return dbOpenHelper.findByBarcode(barcode);
	}

	public String getWsURL() {
		return wsURL;
	}

	public void loadImage(ImageView barcodeImageView, String barcode) {
		new LoadImageTask(wsURL, barcodeImageView).execute(barcode);

	}

	public void save(Product product) {
		dbOpenHelper.remove(product);
		new SaveTask(wsURL, product, null).execute((Void) null);
	}

	public void save(Product product, File tempFile) {
		new SaveTask(wsURL, product, tempFile).execute((Void) null);
	}

	public void searchOnline(FoodTagMainActivity activity, String barcode) {
		new SearchOnlineTask(activity, this).execute(new String[] { barcode });
	}
}
