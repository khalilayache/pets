package com.khalilayache.pets

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.khalilayache.pets.data.PetContract
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_BREED
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_GENDER
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_ID
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_NAME
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_WEIGHT
import com.khalilayache.pets.data.PetContract.PetEntry.PET_CONTENT_URI
import kotlinx.android.synthetic.main.activity_catalog.fab
import kotlinx.android.synthetic.main.activity_catalog.textPetInfo

class CatalogActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_catalog)

    initListeners()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_catalog, menu)
    return true
  }

  override fun onStart() {
    super.onStart()
    displayAllDatabaseInfos()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_insert_dummy_data -> {
        insertDummyPet()
        displayAllDatabaseInfos()
        return true
      }
      R.id.action_delete_all_entries ->
        return true
    }
    return super.onOptionsItemSelected(item)
  }

  private fun insertDummyPet() {

    val values = ContentValues()

    values.put(PetContract.PetEntry.COLUMN_NAME, "Toto")
    values.put(PetContract.PetEntry.COLUMN_BREED, "Terrier")
    values.put(PetContract.PetEntry.COLUMN_GENDER, PetContract.PetEntry.GENDER_MALE)
    values.put(PetContract.PetEntry.COLUMN_WEIGHT, 7)

    contentResolver.insert(PET_CONTENT_URI, values)

  }

  private fun initListeners() {
    fab.setOnClickListener {
      startActivity(EditorActivity.createIntent(this@CatalogActivity))
    }
  }

  private fun displayAllDatabaseInfos() {

    val projection = arrayOf(COLUMN_ID,
        COLUMN_NAME,
        COLUMN_BREED,
        COLUMN_GENDER,
        COLUMN_WEIGHT)

    val cursor = contentResolver.query(PET_CONTENT_URI,
        projection,
        null,
        null,
        null)

    val petList: ArrayList<Pet> = ArrayList()

    while (cursor.moveToNext()) {
      val name = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_NAME))
      val breed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_BREED))
      val genderId = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_GENDER))
      val weight = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_WEIGHT))

      val gender = getGender(genderId)

      petList.add(Pet(name, breed, gender, weight))
    }

    cursor.close()
    displayDatabaseInfos(petList)
  }

  private fun getGender(genderId: Int): String {
    return when (genderId) {
      PetContract.PetEntry.GENDER_MALE -> "Male"
      PetContract.PetEntry.GENDER_FEMALE -> "Female"
      else -> "Unknown"
    }
  }

  private fun displayDatabaseInfos(pets: ArrayList<Pet>) {

    textPetInfo.text = "The pets table contains ${pets.size} pets.\n\n"

    for (pet: Pet in pets) {
      textPetInfo.append(PetContract.PetEntry.COLUMN_NAME.toUpperCase() + "\t-\t" + pet.name + "\n" +
          PetContract.PetEntry.COLUMN_BREED.toUpperCase() + "\t-\t" + pet.breed + "\n" +
          PetContract.PetEntry.COLUMN_GENDER.toUpperCase() + "\t-\t" + pet.gender + "\n" +
          PetContract.PetEntry.COLUMN_WEIGHT.toUpperCase() + "\t-\t" + pet.weight + "\n\n")
    }

  }
}
