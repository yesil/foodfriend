package com.foodfriend;

public class Product {

	private String barcode;

	private boolean vegetarian;

	private boolean halal;

	private String ingredients;

	private boolean kosher;
	private String name;

	private boolean persisted;

	public Product(String barcode, String name, String ingredients,
			boolean vegetarian, boolean halal, boolean kosher) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.ingredients = ingredients;
		this.vegetarian = vegetarian;
		this.halal = halal;
		this.kosher = kosher;
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

	public boolean isVegetarian() {
		return vegetarian;
	}

	public boolean isHalal() {
		return halal;
	}

	public boolean isKosher() {
		return kosher;
	}

	public boolean isPersisted() {
		return persisted;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setVegetarian(boolean vegetarian) {
		this.vegetarian =vegetarian;
	}

	public void setHalal(boolean halal) {
		this.halal = halal;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public void setKosher(boolean kosher) {
		this.kosher = kosher;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

}
