package com.foodtag;

public class Product {

	private final String barcode;

	private String description;

	private boolean halal;

	private boolean kosher;

	private final boolean locked;
	private String name;
	private boolean vegetarian;

	public Product(String barcode, String name, String description,
			boolean locked) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.description = description;
		this.locked = locked;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public boolean isHalal() {
		return halal;
	}

	public boolean isKosher() {
		return kosher;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHalal(boolean halal) {
		this.halal = halal;
	}

	public void setKosher(boolean kosher) {
		this.kosher = kosher;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVegetarian(boolean vegetarian) {
		this.vegetarian = vegetarian;
	}
}
