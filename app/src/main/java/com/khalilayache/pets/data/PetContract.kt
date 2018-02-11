package com.khalilayache.pets.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

object PetContract {

  val CONTENT_AUTHORITY = "com.khalilayache.pets"
  val PATH_PETS = "pets"
  val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

  object PetEntry : BaseColumns {

    val TABLE_NAME = "pets"

    val COLUMN_ID = BaseColumns._ID
    val COLUMN_NAME = "name"
    val COLUMN_GENDER = "gender"
    val COLUMN_BREED = "breed"
    val COLUMN_WEIGHT = "weight"

    val GENDER_UNKNOWN = 0
    val GENDER_MALE = 1
    val GENDER_FEMALE = 2

    val PET_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)

    val CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS

    val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS

  }

}
