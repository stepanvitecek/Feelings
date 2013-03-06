package eu.vstepik.feelings.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import eu.vstepik.feelings.data.Feeling.Feelings;

/**
 * Provides access to a database.
 */
public class Database extends ContentProvider {

	@SuppressWarnings("unused")
	private static final String TAG = "Database";
	public static final String AUTHORITY = "eu.vstepik.feelings.provider";
	public static final String DATABASE_NAME = "feelings.db";
	public static final int DATABASE_VERSION = 1;
	public static final String FEELINGS_TABLE_NAME = "feelings";

	private static final int FEELINGS = 1;
	private static final int FEELING = 2;

	private static final UriMatcher uriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + FEELINGS_TABLE_NAME + " ("
					+ Feelings._ID + " INTEGER PRIMARY KEY," + Feelings.VALUE
					+ " INTEGER," + Feelings.CREATED + " BIGINT" + ", "
					+ Feelings.NOTE + " TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// don't support migration from very old versions
			db.execSQL("DROP TABLE IF EXISTS " + FEELINGS_TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case FEELINGS:
			return db.query(FEELINGS_TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case FEELINGS:
			return Feelings.CONTENT_TYPE;
		case FEELING:
			return Feelings.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId;
		int match = uriMatcher.match(uri);
		switch (match) {
		case FEELINGS:
			rowId = db.insert(FEELINGS_TABLE_NAME, null, values);
			if (rowId > 0) {
				uri = ContentUris.withAppendedId(Feelings.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return uri;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int match = uriMatcher.match(uri);
		int affected = 0;
		switch (match) {
		case FEELING:
			affected = db.update(FEELINGS_TABLE_NAME, values, Feelings._ID
					+ "=" + uri.getPathSegments().get(1), null);
			break;
		case FEELINGS:
			affected = db.update(FEELINGS_TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return affected;
	}

	@Override
	public synchronized int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int match = uriMatcher.match(uri);
		int affected = 0;
		switch (match) {
		case FEELING:
			affected = db.delete(FEELINGS_TABLE_NAME, Feelings._ID + "="
					+ uri.getPathSegments().get(1), null);
			break;
		case FEELINGS:
			affected = db.delete(FEELINGS_TABLE_NAME, where, whereArgs);
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return affected;
	}

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Database.AUTHORITY, "feelings", FEELINGS);
		uriMatcher.addURI(Database.AUTHORITY, "feelings/#", FEELING);
	}
}
