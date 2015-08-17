Android sqite database classes now working on a special version of Android-sqlite-native-driver (JNI interface using Gluegen)

License: Apache 2.0 (using SQLiteNative interface which is UNLICENSE [public domain])

Very simple CREATE/INSERT sql working

```Java
java.io.File dbfile = new java.io.File(this.getFilesDir(), "t1.db");
String dbpath = dbfile.getAbsolutePath();

// populate database using com.test.sqlc.SQLiteDatabase:
com.test.sqlc.SQLiteDatabase d1 = com.test.sqlc.SQLiteDatabase.openOrCreateDatabase(dbpath, null);
d1.execSQL("DROP TABLE IF EXISTS tt");
d1.execSQL("CREATE TABLE tt (t1 TEXT);");
d1.execSQL("INSERT INTO tt VALUES('hello');");
d1.close();

// Check contents using io.liteglue.SQLiteNative (for now):
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

