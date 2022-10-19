package study.seo.a5udacitydatabase_petsapp

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import study.seo.a5udacitydatabase_petsapp.data.PetContract

class PetCursorAdapter(context: Context?, c: Cursor?) : CursorAdapter(context, c) {
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
    }

    @SuppressLint("Range")
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        view?.findViewById<TextView>(R.id.name)?.text =
            cursor?.getString(cursor.getColumnIndex(PetContract.PetEntry.PET_NAME))

        view?.findViewById<TextView>(R.id.summary)?.text =
            cursor?.getString(cursor.getColumnIndex(PetContract.PetEntry.PET_BREED))

    }

}