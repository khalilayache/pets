package com.khalilayache.pets.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PetDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  companion object {
    const val DATABASE_NAME = "pets.db"
    const val DATABASE_VERSION = 1
  }

  val CREATE_TABLE_SQL = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME + " ( " +
      PetContract.PetEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      PetContract.PetEntry.COLUMN_NAME + " TEXT NOT NULL, " +
      PetContract.PetEntry.COLUMN_BREED + " TEXT, " +
      PetContract.PetEntry.COLUMN_GENDER + " INTEGER NOT NULL, " +
      PetContract.PetEntry.COLUMN_WEIGHT + " INTEGER NOT NULL DEFAULT 0);"

  override fun onCreate(db: SQLiteDatabase?) {
    db?.execSQL(CREATE_TABLE_SQL)
  }

  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

  }


}
