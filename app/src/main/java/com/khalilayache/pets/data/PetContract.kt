package com.khalilayache.pets.data

import android.provider.BaseColumns

object PetContract : BaseColumns {

  const val TABLE_NAME = "pets"

  const val COLUMN_ID = BaseColumns._ID
  const val COLUMN_NAME = "name"
  const val COLUMN_GENDER = "gender"
  const val COLUMN_BREED = "breed"
  const val COLUMN_WEIGHT = "weight"


  const val GENDER_UNKNOWN = 0
  const val GENDER_MALE = 1
  const val GENDER_FEMALE = 2

}
