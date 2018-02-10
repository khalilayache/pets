package com.khalilayache.pets

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.khalilayache.pets.data.PetContract
import com.khalilayache.pets.data.PetDbManager
import kotlinx.android.synthetic.main.activity_catalog.fab
import kotlinx.android.synthetic.main.activity_catalog.textPetInfo

class CatalogActivity : AppCompatActivity() {


  private val petsDbManager by lazy { PetDbManager(this@CatalogActivity) }

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
        petsDbManager.insertDummyPet()
        displayAllDatabaseInfos()
        return true
      }
      R.id.action_delete_all_entries ->
        return true
    }
    return super.onOptionsItemSelected(item)
  }

  private fun initListeners() {
    fab.setOnClickListener {
      startActivity(EditorActivity.createIntent(this@CatalogActivity))
    }
  }

  private fun displayAllDatabaseInfos() {
    displayDatabaseInfos(petsDbManager.selectAllFromPets())
  }


  private fun displayDatabaseInfos(pets: ArrayList<Pet>) {

    textPetInfo.text = "The pets table contains " + pets.size + " pets.\n\n"

    for (pet: Pet in pets) {
      textPetInfo.append(PetContract.COLUMN_NAME.toUpperCase() + "\t-\t" + pet.name + "\n" +
          PetContract.COLUMN_BREED.toUpperCase() + "\t-\t" + pet.breed + "\n" +
          PetContract.COLUMN_GENDER.toUpperCase() + "\t-\t" + pet.gender + "\n" +
          PetContract.COLUMN_WEIGHT.toUpperCase() + "\t-\t"+pet.weight + "\n\n")
    }

  }
}
