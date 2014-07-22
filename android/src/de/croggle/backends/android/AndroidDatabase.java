package de.croggle.backends.android;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.croggle.backends.sqlite.ContentValues;
import de.croggle.backends.sqlite.Cursor;
import de.croggle.backends.sqlite.Database;

public class AndroidDatabase implements Database {

	final SQLiteDatabase db;

	public AndroidDatabase(SQLiteDatabase db) {
		this.db = db;
	}

	@Override
	public void execSQL(String sql) {
		db.execSQL(sql);
	}

	@Override
	public boolean isReadOnly() {
		return db.isReadOnly();
	}

	@Override
	public Cursor rawQuery(String sql, String[] args) {
		return new AndroidCursor(db.rawQuery(sql, args));
	}

	@Override
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		return db.update(table, ((AndroidContentValues) values).content,
				whereClause, whereArgs);
	}

	@Override
	public int delete(String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}

	@Override
	public long insert(String table, String nullColumnHack, ContentValues values) {
		return db.insert(table, nullColumnHack,
				((AndroidContentValues) values).content);
	}

	@Override
	public long queryForLong(String selection, String[] args) {
		SQLiteStatement prog = db.compileStatement(selection);
		try {
			if (args != null) {
				// prog.bindAllArgsAsStrings(args);
				for (int i = 0; i < args.length; i++) {
					prog.bindString(i + 1, args[i]);
				}
			}
			return prog.simpleQueryForLong();
		} finally {
			prog.close();
		}
	}

}
