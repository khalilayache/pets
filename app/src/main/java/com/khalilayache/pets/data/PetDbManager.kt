package com.khalilayache.pets.data

import android.content.ContentValues
import android.content.Context
import com.khalilayache.pets.Pet

class PetDbManager(context: Context) {

  companion object {
    const val DATABASE_READABLE = "readable"
    const val DATABASE_WRITEABLE = "writeable"
  }

  private val petDbHelper: PetDbHelper = PetDbHelper(context)

  fun insertPet(pet: Pet): Long {
    val petsDb = petDbHelper.writableDatabase
    val values = ContentValues()

    values.put(PetContract.COLUMN_NAME, pet.name)
    values.put(PetContract.COLUMN_BREED, pet.breed)
    values.put(PetContract.COLUMN_GENDER, getGenderId(pet.gender))
    values.put(PetContract.COLUMN_WEIGHT, pet.weight)

    return petsDb.insert(PetContract.TABLE_NAME,
        null,
        values)
  }

  fun insertDummyPet(): Boolean {
    val petsDb = petDbHelper.writableDatabase
    val values = ContentValues()

    values.put(PetContract.COLUMN_NAME, "Toto")
    values.put(PetContract.COLUMN_BREED, "Terrier")
    values.put(PetContract.COLUMN_GENDER, PetContract.GENDER_MALE)
    values.put(PetContract.COLUMN_WEIGHT, 7)

    val result = petsDb.insert(PetContract.TABLE_NAME,
        null,
        values)

    return result != -1L
  }

  fun selectAllFromPets(): ArrayList<Pet> {
    val petList: ArrayList<Pet> = ArrayList()

    val projection = arrayOf(PetContract.COLUMN_ID,
        PetContract.COLUMN_NAME,
        PetContract.COLUMN_BREED,
        PetContract.COLUMN_GENDER,
        PetContract.COLUMN_WEIGHT)

    val petsDb = petDbHelper.readableDatabase

    val cursor = petsDb.query(false,
        PetContract.TABLE_NAME,
        projection,
        null,
        null,
        null,
        null,
        null,
        null)

    while (cursor.moveToNext()) {
      val name = cursor.getString(cursor.getColumnIndex(PetContract.COLUMN_NAME))
      val breed = cursor.getString(cursor.getColumnIndex(PetContract.COLUMN_BREED))
      val genderId = cursor.getInt(cursor.getColumnIndex(PetContract.COLUMN_GENDER))
      val weight = cursor.getInt(cursor.getColumnIndex(PetContract.COLUMN_WEIGHT))

      val gender = getGender(genderId)

      petList.add(Pet(name, breed, gender, weight))
    }

    cursor.close()

    return petList
  }

  private fun getGender(genderId: Int): String {
    return when (genderId) {
      PetContract.GENDER_MALE -> "Male"
      PetContract.GENDER_FEMALE -> "Female"
      else -> "Unknown"
    }
  }

  private fun getGenderId(gender: String): Int {
    return when (gender) {
      "Male" -> PetContract.GENDER_MALE
      "Female" -> PetContract.GENDER_FEMALE
      else -> PetContract.GENDER_UNKNOWN
    }
    }

}
