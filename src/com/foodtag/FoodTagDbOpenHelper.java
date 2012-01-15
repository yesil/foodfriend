package com.foodtag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FoodTagDbOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "FOODTAG";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "PRODUCTS";
	private static final String TABLE_NAME_TAGS = "TAGS";
	private static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ " (BARCODE TEXT PRIMARY KEY ASC, NAME TEXT, INGREDIENTS TEXT, TAGS TEXT)";

	private static final String TABLE_CREATE_TAGS = "CREATE TABLE "
			+ TABLE_NAME_TAGS
			+ " (CODE TEXT PRIMARY KEY ASC, NAME TEXT, STYLE TEXT)";

	public FoodTagDbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
		db.execSQL(TABLE_CREATE_TAGS);
		db.execSQL("INSERT INTO " + TABLE_NAME_TAGS
				+ " VALUES('HALAL','Halal','')");
		db.execSQL("INSERT INTO " + TABLE_NAME_TAGS
				+ " VALUES('KOSHER','Kosher','')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {

	}

	public Product findByBarcode(String barcode) {
		Product product;
		Cursor result = getReadableDatabase().query(TABLE_NAME, null,
				"BARCODE = ?", new String[] { barcode }, null, null, null, "1");
		result.moveToFirst();
		if (result.getCount() == 1) {
			product = new Product(result.getString(0), result.getString(1),
					result.getString(2), result.getString(3).split(","));
			product.setPersisted(true);
		} else {
			product = new Product(barcode, "", "", new String[] {});
		}
		return product;
	}

	public HashMap<String, Tag> getTags() {
		HashMap<String, Tag> tags = new HashMap<String, Tag>();
		Cursor result = getReadableDatabase().query(TABLE_NAME_TAGS, null,
				null, null, null, null, null);
		while (result.moveToNext()) {
			tags.put(
					result.getString(0),
					new Tag(result.getString(0), result.getString(1), result
							.getString(2)));
		}
		return tags;
	}

	public void createNewEntry(Product product) {
		ContentValues values = new ContentValues();
		values.put("BARCODE", product.getBarcode());
		values.put("NAME", product.getName());
		values.put("INGREDIENTS", product.getIngredients());
		values.put("TAGS", join(product.getTags(), ","));
		getWritableDatabase().insert(TABLE_NAME, "", values);
	}

	static String join(Collection<String> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = s.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}
}