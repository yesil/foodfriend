package com.foodtag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Product {

	private final String barcode;

	private String ingredients;

	private String name;

	private boolean persisted;

	private final Set<String> tags;

	public Product(String barcode, String name, String ingredients,
			String[] tagsAsString) {
		super();
		HashSet<String> tags = new HashSet<String>();
		tags.addAll(Arrays.asList(tagsAsString));
		this.barcode = barcode;
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
	}

	public Product(String barcode, String name, String ingredients,
			Set<String> tags) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
	}

	public void addTag(String productTag) {
		tags.add(productTag);

	}

	public String getBarcode() {
		return barcode;
	}

	public String getIngredients() {
		return ingredients;
	}

	public String getName() {
		return name;
	}

	public Set<String> getTags() {
		return tags;
	}

	public boolean isPersisted() {
		return persisted;
	}

	public void removeTag(String productTag) {
		tags.remove(productTag);
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}
}
