package com.test;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.liteglue.SQLiteNative;

import java.io.File;

import java.sql.SQLException;

import android.database.Cursor;

public class SQLiteConnectorTest extends Activity
{
  ArrayAdapter<String> resultsAdapter;

  int errorCount = 0;

  /* package */ void logErrorItem(String result) {
    android.util.Log.e("SQLiteGlueTest", result);
    resultsAdapter.add(result);
  }

  /* package */ void checkBooleanResult(String label, boolean actual, boolean expected) {
    if (expected == actual) {
      logResult(label + " - OK");
    } else {
      ++errorCount;
      logErrorItem("FAILED CHECK" + label);
      logErrorItem("expected: " + expected);
      logErrorItem("actual: " + actual);
    }
  }

  /* package */ void checkIntegerResult(String label, int actual, int expected) {
    if (expected == actual) {
      logResult(label + " - OK");
    } else {
      ++errorCount;
      logErrorItem("FAILED CHECK" + label);
      logErrorItem("expected: " + expected);
      logErrorItem("actual: " + actual);
    }
  }

  /* package */ void checkLongResult(String label, long actual, long expected) {
    if (expected == actual) {
      logResult(label + " - OK");
    } else {
      ++errorCount;
      logErrorItem("FAILED CHECK" + label);
      logErrorItem("expected: " + expected);
      logErrorItem("actual: " + actual);
    }
  }

  /* package */ void checkDoubleResult(String label, double actual, double expected) {
    if (expected == actual) {
      logResult(label + " - OK");
    } else {
      ++errorCount;
      logErrorItem("FAILED CHECK" + label);
      logErrorItem("expected: " + expected);
      logErrorItem("actual: " + actual);
    }
  }

  /* package */ void checkStringResult(String label, String actual, String expected) {
    if (expected.equals(actual)) {
      logResult(label + " - OK");
    } else {
      ++errorCount;
      logErrorItem("FAILED CHECK" + label);
      logErrorItem("expected: " + expected);
      logErrorItem("actual: " + actual);
    }
  }

  /* package */ void logResult(String result) {
    android.util.Log.i("SQLiteGlueTest", result);
    resultsAdapter.add(result);
  }

  /* package */ void logV(String module, String result) {
    android.util.Log.v(module, result);
    resultsAdapter.add(result);
  }

  /* package */ void logError(String result) {
    logErrorItem(result);
    ++errorCount;
  }

  /* package */ void logUnexpectedException(String result, java.lang.Exception ex) {
    android.util.Log.e("SQLiteGlueTest", "UNEXPECTED EXCEPTION IN " + result, ex);
    resultsAdapter.add("UNEXPECTED EXCEPTION IN " + result + " : " + ex);
    ++errorCount;
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    resultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
    ListView lv1 = (ListView)findViewById(R.id.results);
    lv1.setAdapter(resultsAdapter);

// Sample test code now working
// ============================

java.io.File dbfile = new java.io.File(this.getFilesDir(), "t1.db");
String dbpath = dbfile.getAbsolutePath();

com.test.sqlc.SQLiteDatabase d1 = com.test.sqlc.SQLiteDatabase.openOrCreateDatabase(dbpath, null);

// Simple string query test (using com.test.sqlc.SQLiteDatabase.rawQuery() function)
android.database.Cursor c1 = d1.rawQuery("SELECT UPPER('Camel String') as uppertext", new String[0]);
logV("Test", "column count: " + c1.getColumnCount()); // should be 1
if (c1 != null && c1.moveToFirst()) {
  logV("Test", "position: " + c1.getPosition());
  logV("Test", "column 1 name: " + c1.getColumnName(0));
  logV("Test", "column 1 type: " + c1.getType(0));
  logV("Test", "column 1 text [string]: " + c1.getString(0));
}

// populate database using com.test.sqlc.SQLiteDatabase:
d1.execSQL("DROP TABLE IF EXISTS tt");
d1.execSQL("CREATE TABLE tt (t1 TEXT);");
d1.execSQL("INSERT INTO tt VALUES(?),(?);", new String[]{"Hello", "world"});

// Check contents using query through com.test.sqlc.SQLiteDirectCursorDriver:
com.test.sqlc.SQLiteCursorDriver cd1 = new com.test.sqlc.SQLiteDirectCursorDriver(d1, "SELECT * from tt", null, null);
c1 = cd1.query(null, new String[0]);
if (c1 != null && c1.moveToFirst()) {
  logV("Test", "position: " + c1.getPosition());
  logV("Test", "column 1 name: " + c1.getColumnName(0));
  logV("Test", "column 1 type: " + c1.getType(0));
  logV("Test", "column 1 text [string]: " + c1.getString(0));
  while(c1.moveToNext()) {
    logV("Test", "column 1 text [string]: " + c1.getString(0));
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
  logV("Test", "column count: " + cc);

  for (int col=0; col < cc; ++col) {
    String colname = io.liteglue.SQLiteNative.sqlc_st_column_name(myst, col);
    logV("Test", "column " + col + " name: " + colname);
    String coltext = io.liteglue.SQLiteNative.sqlc_st_column_text_native(myst, col);
    logV("Test", "column " + col + " text: " + coltext);

    sr = io.liteglue.SQLiteNative.sqlc_st_step(myst);
  }
}

io.liteglue.SQLiteNative.sqlc_st_finish(myst);

io.liteglue.SQLiteNative.sqlc_db_close(mydbc);

  }
}
