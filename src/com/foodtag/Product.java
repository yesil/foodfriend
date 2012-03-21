package com.foodtag;

import java.util.Set;

public class Product {

	private final String barcode;

	private String ingredients;

	private String name;

	private final boolean locked;

	public boolean isLocked() {
		return locked;
	}

	private final Set<TagEnum> tags;

	public Product(String barcode, String name, String ingredients,
			Set<TagEnum> tags, boolean locked) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
		this.locked = locked;
	}

	public void addTag(TagEnum productTag) {
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

	public Set<TagEnum> getTags() {
		return tags;
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
}
