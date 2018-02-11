package com.khalilayache.pets

import android.app.LoaderManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.khalilayache.pets.data.PetContract
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_BREED
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_ID
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_NAME
import com.khalilayache.pets.data.PetContract.PetEntry.PET_CONTENT_URI
import kotlinx.android.synthetic.main.activity_catalog.empty_view
import kotlinx.android.synthetic.main.activity_catalog.fab
import kotlinx.android.synthetic.main.activity_catalog.pets_list

class CatalogActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

  private val PET_LOADER = 1000

  private lateinit var petCursorAdapter: PetCursorAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_catalog)

    initListeners()
    initListView()
    initLoader()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_catalog, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_insert_dummy_data -> {
        insertDummyPet()
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

  private fun initLoader() {
    loaderManager.initLoader<Cursor>(PET_LOADER, null, this)
  }

  private fun initListView() {
    petCursorAdapter = PetCursorAdapter(this@CatalogActivity, null)

    pets_list.adapter = petCursorAdapter

    pets_list.emptyView = empty_view

    pets_list.setOnItemClickListener { parent, view, position, id ->

      startActivity( EditorActivity.createIntentWithUri(
          this@CatalogActivity,
          ContentUris.withAppendedId(
              PET_CONTENT_URI,
              id
          )))
    }
  }

  override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
    val projection = arrayOf(COLUMN_ID,
        COLUMN_NAME,
        COLUMN_BREED)

    return CursorLoader(this,
        PET_CONTENT_URI,
        projection,
        null,
        null,
        null)
  }

  override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
    petCursorAdapter.swapCursor(data)
  }

  override fun onLoaderReset(loader: Loader<Cursor>?) {
    petCursorAdapter.swapCursor(null)
  }

}
