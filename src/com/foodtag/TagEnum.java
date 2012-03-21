package com.foodtag;

public enum TagEnum {
	TAG, HALAL, KOSHER, VEGETARIAN;

	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
