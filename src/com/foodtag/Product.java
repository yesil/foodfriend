package com.foodtag;

import java.util.HashSet;
import java.util.Set;

public class Product {

	private final String barcode;

	private String ingredients;

	private String name;

	private boolean persisted;

	private final Set<TagEnum> tags;

	public Product(String barcode, String name, String ingredients,
			String[] tagsAsString) {
		super();
		HashSet<TagEnum> tags = new HashSet<TagEnum>();
		for (String tag : tagsAsString) {
			tags.add(TagEnum.valueOf(tag));
		}
		this.barcode = barcode;
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
	}

	public Product(String barcode, String name, String ingredients,
			Set<TagEnum> tags) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
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
