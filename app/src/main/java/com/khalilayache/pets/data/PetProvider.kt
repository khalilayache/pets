package com.khalilayache.pets.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_ID
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_NAME
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_WEIGHT
import com.khalilayache.pets.data.PetContract.PetEntry.CONTENT_ITEM_TYPE
import com.khalilayache.pets.data.PetContract.PetEntry.CONTENT_LIST_TYPE
import com.khalilayache.pets.data.PetContract.PetEntry.TABLE_NAME

class PetProvider : ContentProvider() {

  private var petDbHelper: PetDbHelper? = null
  private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

  override fun onCreate(): Boolean {
    petDbHelper = PetDbHelper(context)
    return false
  }

  override fun insert(uri: Uri?, values: ContentValues?): Uri {
    val match = uriMatcher.match(uri)

    return when (match) {
      PETS -> insertPet(uri, values)
      else -> Uri.EMPTY
    }
  }

  override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
    val db = petDbHelper?.readableDatabase

    lateinit var cursor: Cursor

    val match = uriMatcher.match(uri)

    when (match) {

      PETS -> {
        db?.let {
          cursor = it.query(TABLE_NAME,
              projection,
              selection,
              selectionArgs,
              null,
              null,
              sortOrder)
        }

      }
      PET_ID -> {
        val select = COLUMN_ID + "=?"
        val selectArgs = arrayOf(ContentUris.parseId(uri).toString())

        db?.let {
          cursor = it.query(TABLE_NAME,
              projection,
              select,
              selectArgs,
              null,
              null,
              sortOrder)
        }

      }
      else -> {

      }


    }

    return cursor
  }

  override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
    val match = uriMatcher.match(uri)

    return when (match) {
      PETS -> updatePet(uri, values, selection, selectionArgs)
      PET_ID -> {
        val select = "$COLUMN_ID=?"
        val selectArgs = arrayOf(ContentUris.parseId(uri).toString())
        updatePet(uri, values, select, selectArgs)
      }
      else -> -1
    }

  }

  override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
    val db = petDbHelper?.writableDatabase

    db?.let {
      val match = uriMatcher.match(uri)
      when (match) {
        PETS ->
          return db.delete(TABLE_NAME, selection, selectionArgs)
        PET_ID -> {
          val select = "$COLUMN_ID=?"
          val selectArgs = arrayOf(ContentUris.parseId(uri).toString())
          return db.delete(TABLE_NAME, select, selectArgs)
        }
        else -> throw IllegalArgumentException("Deletion is not supported for " + uri)
      }
    }
    return -1
  }

  override fun getType(uri: Uri?): String {
    val match = uriMatcher.match(uri)

    return when (match) {
      PETS -> CONTENT_LIST_TYPE
      PET_ID -> CONTENT_ITEM_TYPE
      else -> throw IllegalStateException("Unknown URI $uri with match $match")
    }
  }

  private fun updatePet(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {

    var name = ""
    var weight = 0

    values?.let {
      if (it.containsKey(COLUMN_NAME)) {
        name = it.getAsString(COLUMN_NAME)
      }
      if (values.containsKey(COLUMN_WEIGHT)) {
        weight = values.getAsInteger(COLUMN_WEIGHT)
      }
    }

    if (name.isEmpty()) throw IllegalArgumentException("Pet requires valid name")

    if (weight < 0) throw IllegalArgumentException("Pet requires valid weight")


    if (values?.size() == 0) {
      return 0
    }

    val db = petDbHelper?.writableDatabase

    db?.let {

      return db.update(TABLE_NAME, values, selection, selectionArgs)
    }

    return -1
  }

  private fun insertPet(uri: Uri?, values: ContentValues?): Uri {

    var name = ""
    var weight = 0

    val db = petDbHelper?.writableDatabase

    values?.let {
      name = it.getAsString(COLUMN_NAME)
      weight = it.getAsInteger(COLUMN_WEIGHT)
    }

    if (name.isEmpty()) throw IllegalArgumentException("Pet requires valid name")

    if (weight < 0) throw IllegalArgumentException("Pet requires valid weight")


    var id: Long = -1L
    db?.let {
      id = it.insert(TABLE_NAME, null, values)
    }

    if (id == -1L) return Uri.EMPTY

    return ContentUris.withAppendedId(uri, id)
  }

  companion object {
    const val PETS = 100
    const val PET_ID = 101
  }

  init {
    uriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS)
    uriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID)
  }

}
