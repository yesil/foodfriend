package com.foodfriend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.UrlQuerySanitizer;

public class FoodFriendDbOpenHelper extends SQLiteOpenHelper {

	private static final UrlQuerySanitizer URL_QUERY_SANITIZER = new UrlQuerySanitizer();
	private static final String DATABASE_NAME = "FOODFRIEND";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "PRODUCTS";
	private static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ " (BARCODE TEXT PRIMARY KEY ASC, NAME TEXT, INGREDIENTS TEXT, VEGETARIAN INTEGER, HALAL INTEGER, KOSHER INTEGER);";

	public FoodFriendDbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {

	}

	public Product findByBarcode(String barcode) {
		Product product;
		Cursor result = getReadableDatabase().query(TABLE_NAME, null,
				"BARCODE = ?", new String[] { barcode }, null, null, null, "1");

		if (result.getCount() == 1) {
			result.moveToFirst();
			product = new Product(result.getString(0),
					URL_QUERY_SANITIZER.unescape(result.getString(1)),
					URL_QUERY_SANITIZER.unescape(result.getString(2)),
					result.getInt(3) == 1, result.getInt(4) == 1,
					result.getInt(5) == 1);
			product.setPersisted(true);
		} else {
			product = new Product(barcode, "", "", false, false, false);
		}
		return product;
	}

	public void createNewEntry(Product product) {
		ContentValues values = new ContentValues();
		values.put("BARCODE", product.getBarcode());
		values.put("NAME", DatabaseUtils.sqlEscapeString(product.getName()));
		values.put("INGREDIENTS",
				DatabaseUtils.sqlEscapeString(product.getIngredients()));
		values.put("VEGETARIAN", product.isVegetarian());
		values.put("HALAL", product.isHalal());
		values.put("KOSHER", product.isKosher());
		getWritableDatabase().insert(TABLE_NAME, "", values);
	}
}