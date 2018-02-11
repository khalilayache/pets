package com.khalilayache.pets

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_BREED
import com.khalilayache.pets.data.PetContract.PetEntry.COLUMN_NAME

class PetCursorAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {


  override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
    return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

  }

  override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
    val petName = view?.findViewById<TextView>(R.id.pet_name)
    val petBreed = view?.findViewById<TextView>(R.id.pet_breed)

    petBreed?.text = cursor?.getString(cursor.getColumnIndexOrThrow(COLUMN_BREED))
    petName?.text = cursor?.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
  }
}
