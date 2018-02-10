package com.khalilayache.pets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.khalilayache.pets.data.PetDbManager
import kotlinx.android.synthetic.main.activity_editor.editBreed
import kotlinx.android.synthetic.main.activity_editor.editName
import kotlinx.android.synthetic.main.activity_editor.editWeight
import kotlinx.android.synthetic.main.activity_editor.spinnerGender

class EditorActivity : AppCompatActivity() {

  companion object {
    fun createIntent(context: Context) = Intent(context, EditorActivity::class.java)
  }

  private val petDbManager by lazy { PetDbManager(this@EditorActivity) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_editor)

    initSpinner()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_editor, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_save -> {
        saveNewPet()
        finish()
        return true
      }
      R.id.action_delete ->
        return true
      android.R.id.home -> {
        NavUtils.navigateUpFromSameTask(this)
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun saveNewPet() {
    val name = editName.text.toString().trim()
    val breed = editBreed.text.toString().trim()
    val weight = editWeight.text.toString().trim().toInt()
    val gender = spinnerGender.selectedItem.toString()

    val savedId = petDbManager.insertPet(Pet(name, breed, gender, weight))
    if (savedId != -1L) {
      Toast.makeText(this@EditorActivity, "Pet saved with id: $savedId", Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(this@EditorActivity, "Error with saving pet", Toast.LENGTH_SHORT).show()
    }
  }

  private fun initSpinner() {
    val genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
        R.array.array_gender_options, android.R.layout.simple_spinner_item)

    genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

    spinnerGender!!.adapter = genderSpinnerAdapter

    /*spinnerGender!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selection = parent.getItemAtPosition(position) as String
        if (!selection.isEmpty()) {
          when (selection) {
            "Male" -> PetContract.GENDER_MALE // Male
            "Female" -> PetContract.GENDER_FEMALE // Female
            else -> PetContract.GENDER_UNKNOWN // Unknown
          }
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {
      }
    }*/
  }

}
