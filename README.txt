Android sqite database classes from android-m-preview-1 tag now *partially* working on a special version of Android-sqlite-native-driver (JNI interface using Gluegen)

License: Apache 2.0 (using SQLiteNative interface which is UNLICENSE [public domain])

Now tested working OK:
- Very simple CREATE/INSERT sql statements
- (Very) simple Cursor queries
- Simple string bindings

```Java
java.io.File dbfile = new java.io.File(this.getFilesDir(), "t1.db");
String dbpath = dbfile.getAbsolutePath();

com.test.sqlc.SQLiteDatabase d1 = com.test.sqlc.SQLiteDatabase.openOrCreateDatabase(dbpath, null);

// Simple string query test (using com.test.sqlc.SQLiteDatabase.rawQuery() function)
android.database.Cursor c1 = d1.rawQuery("SELECT UPPER('Camel String') as uppertext", new String[0]);
android.util.Log.e("Test", "column count: " + c1.getColumnCount()); // should be 1
if (c1 != null && c1.moveToFirst()) {
  android.util.Log.e("Test", "position: " + c1.getPosition());
  android.util.Log.e("Test", "column 1 name: " + c1.getColumnName(0));
  android.util.Log.e("Test", "column 1 type: " + c1.getType(0));
  android.util.Log.e("Test", "column 1 text [string]: " + c1.getString(0));
}

// populate database using com.test.sqlc.SQLiteDatabase:
d1.execSQL("DROP TABLE IF EXISTS tt");
d1.execSQL("CREATE TABLE tt (t1 TEXT);");
d1.execSQL("INSERT INTO tt VALUES(?),(?);", new String[]{"Hello", "world"});

// Check contents using query through com.test.sqlc.SQLiteDirectCursorDriver:
com.test.sqlc.SQLiteCursorDriver cd1 = new com.test.sqlc.SQLiteDirectCursorDriver(d1, "SELECT * from tt", null, null);
c1 = cd1.query(null, new String[0]);
if (c1 != null && c1.moveToFirst()) {
  android.util.Log.e("Test", "position: " + c1.getPosition());
  android.util.Log.e("Test", "column 1 name: " + c1.getColumnName(0));
  android.util.Log.e("Test", "column 1 type: " + c1.getType(0));
  android.util.Log.e("Test", "column 1 text [string]: " + c1.getString(0));
  while(c1.moveToNext()) {
    android.util.Log.e("Test", "column 1 text [string]: " + c1.getString(0));
  }
}

d1.close();

// Check contents again using io.liteglue.SQLiteNative:
long mydbc = io.liteglue.SQLiteNative.sqlc_db_open(dbpath, io.liteglue.SQLiteNative.SQLC_OPEN_READWRITE);
if (mydbc < 0) throw new RuntimeException("could not open db");

long myst = io.liteglue.SQLiteNative.sqlc_db_prepare_st(mydbc, "SELECT * FROM tt");
if (myst < 0) throw new RuntimeException("could not open db");

long sr = io.liteglue.SQLiteNative.sqlc_st_step(myst);
while (sr == io.liteglue.SQLiteNative.SQLC_RESULT_ROW) {
  int cc = io.liteglue.SQLiteNative.sqlc_st_column_count(myst);
  android.util.Log.e("Test", "column count: " + cc);

  for (int col=0; col < cc; ++col) {
    String colname = io.liteglue.SQLiteNative.sqlc_st_column_name(myst, col);
    android.util.Log.e("Test", "column " + col + " name: " + colname);
    String coltext = io.liteglue.SQLiteNative.sqlc_st_column_text_native(myst, col);
    android.util.Log.e("Test", "column " + col + " text: " + coltext);

    sr = io.liteglue.SQLiteNative.sqlc_st_step(myst);
  }
}

io.liteglue.SQLiteNative.sqlc_st_finish(myst);

io.liteglue.SQLiteNative.sqlc_db_close(mydbc);
```

