package com.foodtag;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Product tags
 * 
 * @author ilyas Stéphane Türkben
 * 
 */
public class Tag {

	private final String code;

	private final String name;

	private final String style;

	public Tag(JSONObject jsonObject) throws JSONException {
		code = jsonObject.getString("code");
		name = jsonObject.getString("name");
		style = jsonObject.getString("style");
	}

	public Tag(String code, String name, String style) {
		super();
		this.code = code;
		this.name = name;
		this.style = style;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getStyle() {
		return style;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
}
