package com.khalilayache.pets

import android.app.AlertDialog
import android.app.LoaderManager
import android.content.ContentValues
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.khalilayache.pets.data.PetContract
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_BREED
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_GENDER
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_NAME
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_WEIGHT
import com.khalilayache.pets.data.PetContract.PetEntry.PET_CONTENT_URI
import kotlinx.android.synthetic.main.activity_editor.editBreed
import kotlinx.android.synthetic.main.activity_editor.editName
import kotlinx.android.synthetic.main.activity_editor.editWeight
import kotlinx.android.synthetic.main.activity_editor.spinnerGender

class EditorActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

  companion object {
    fun createIntent(context: Context) = Intent(context, EditorActivity::class.java)

    fun createIntentWithUri(context: Context, uri: Uri): Intent {
      val intent = Intent(context, EditorActivity::class.java)
      intent.data = uri
      return intent
    }
  }

  private val EDITOR_LOADER = 1001

  private var CURRENT_URI: Uri? = null

  private var mPetHasChanged = false

  private val mTouchListener = View.OnTouchListener { view, motionEvent ->
    mPetHasChanged = true
    false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_editor)


    initActivity()
    initSpinner()
    initToolbar()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_editor, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_save -> {
        savePet()
        return true
      }
      R.id.action_delete -> {
        deletePet()
        return true
      }
      android.R.id.home -> {
        if (!mPetHasChanged) {
          NavUtils.navigateUpFromSameTask(this@EditorActivity)
          return true
        }

        val discardButtonClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
          NavUtils.navigateUpFromSameTask(this@EditorActivity)
        }

        showUnsavedChangesDialog(discardButtonClickListener)
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {


    return CursorLoader(this,
        CURRENT_URI,
        null,
        null,
        null,
        null)
  }

  override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
    if (cursor == null || cursor.count < 1) {
      return
    }

    if (cursor.moveToFirst()) {
      editBreed.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BREED)))
      editName.setText(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)))
      editWeight.setText(cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT)).toString())
      spinnerGender.setSelection(cursor.getInt(cursor.getColumnIndex(COLUMN_GENDER)))
    }
  }

  override fun onLoaderReset(loader: android.content.Loader<Cursor>?) {
    editBreed.setText("")
    editName.setText("")
    editWeight.setText("")
    spinnerGender.setSelection(0)
  }

  override fun onBackPressed() {
    if (!mPetHasChanged) {
      super.onBackPressed()
      return
    }

    val discardButtonClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
      finish()
    }

    showUnsavedChangesDialog(discardButtonClickListener)
  }

  override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
    super.onPrepareOptionsMenu(menu)
    if (CURRENT_URI == null) {
      val menuItem = menu?.findItem(R.id.action_delete)
      menuItem?.isVisible = false
    }
    return true
  }

  private fun initLoader() {
    loaderManager.initLoader<Cursor>(EDITOR_LOADER, null, this)
  }

  private fun savePet() {
    if (editBreed.text.toString().isEmpty() or
        editName.text.toString().isEmpty()) {
      Toast.makeText(this@EditorActivity, "Invalid data! There are blank/empty fields", Toast.LENGTH_SHORT).show()
      return
    }

    val name = editName.text.toString().trim()
    val breed = editBreed.text.toString().trim()
    val gender = spinnerGender.selectedItem.toString()
    var weight = 0

    if (!editWeight.text.toString().isEmpty()) {
      weight = editBreed.text.toString().trim().toInt()
    }

    if (CURRENT_URI == null) {
      contentResolver.insert(PET_CONTENT_URI, getContentValues(Pet(name, breed, gender, weight)))
    } else {
      contentResolver.update(CURRENT_URI, getContentValues(Pet(name, breed, gender, weight)), null, null)
    }

    finish()
  }

  private fun showDeleteConfirmationDialog() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.delete_dialog_msg)
    builder.setPositiveButton(R.string.delete, { dialog, id ->
      deletePet()
    })
    builder.setNegativeButton(R.string.cancel, { dialog, id ->
      dialog?.dismiss()
    })

    val alertDialog = builder.create()
    alertDialog.show()
  }

  private fun deletePet() {
   contentResolver.delete(CURRENT_URI, null, null)
    finish()
  }

  private fun getContentValues(pet: Pet): ContentValues {
    val values = ContentValues()

    values.put(PetContract.PetEntry.COLUMN_NAME, pet.name)
    values.put(PetContract.PetEntry.COLUMN_BREED, pet.breed)
    values.put(PetContract.PetEntry.COLUMN_GENDER, getGenderId(pet.gender))
    values.put(PetContract.PetEntry.COLUMN_WEIGHT, pet.weight)

    return values
  }

  private fun getGenderId(gender: String): Int {
    return when (gender) {
      "Male" -> PetContract.PetEntry.GENDER_MALE
      "Female" -> PetContract.PetEntry.GENDER_FEMALE
      else -> PetContract.PetEntry.GENDER_UNKNOWN
    }
  }

  private fun initSpinner() {
    val genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
        R.array.array_gender_options, android.R.layout.simple_spinner_item)

    genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

    spinnerGender!!.adapter = genderSpinnerAdapter
  }

  private fun initToolbar() {
    if (CURRENT_URI != null) {
      setTitle(R.string.edit_pet)

      initLoader()
    } else {
      setTitle(R.string.add_pet)
      invalidateOptionsMenu()
    }
  }

  private fun initActivity() {
    CURRENT_URI = intent.data
    editWeight.setOnTouchListener(mTouchListener)
    editName.setOnTouchListener(mTouchListener)
    editBreed.setOnTouchListener(mTouchListener)
    spinnerGender.setOnTouchListener(mTouchListener)
  }

  private fun showUnsavedChangesDialog(
      discardButtonClickListener: DialogInterface.OnClickListener) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.unsaved_changes_dialog_msg)
    builder.setPositiveButton(R.string.discard, discardButtonClickListener)
    builder.setNegativeButton(R.string.keep_editing, { dialog, id ->
      dialog?.dismiss()
    })

    val alertDialog = builder.create()
    alertDialog.show()
  }
}
