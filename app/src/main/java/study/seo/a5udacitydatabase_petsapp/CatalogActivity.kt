package study.seo.a5udacitydatabase_petsapp

import android.app.LoaderManager
import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import study.seo.a5udacitydatabase_petsapp.data.PetContract.PetEntry


class CatalogActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var cursorAdapter: PetCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        findViewById<FloatingActionButton>(R.id.fab)
            .setOnClickListener {
                startActivity(
                    Intent(
                        this@CatalogActivity,
                        EditorActivity::class.java
                    )
                )
            }

        val listView = findViewById<ListView>(R.id.list)
        listView.emptyView = findViewById(R.id.empty_view)
        cursorAdapter = PetCursorAdapter(this@CatalogActivity, null)
        listView.adapter = cursorAdapter

        listView.setOnItemClickListener { adapterView, view, position, id ->
            Intent(this@CatalogActivity, EditorActivity::class.java).apply {
                data = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id)
                startActivity(this)
            }
        }

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()
                return true
            }
            R.id.action_delete_all_entries -> {
                deletePets()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertPet() {
        with(PetEntry) {
            contentResolver.insert(CONTENT_URI, ContentValues().apply {
                put(PET_NAME, "Tto")
                put(PET_BREED, "Terrier")
                put(PET_GENDER, GENDER_MALE)
                put(PET_WEIGHT, 7)
            })
        }
    }

    private fun deletePets() {
        contentResolver.delete(PetEntry.CONTENT_URI, null, null)
        Log.v("CatalogActivity", "데이터가 삭제 되었습니다!")
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = with(PetEntry) {
            arrayOf(
                _ID,
                PET_NAME,
                PET_BREED,
            )
        }
        return CursorLoader(
            this,
            PetEntry.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        cursorAdapter.swapCursor(data)
    }

    @Deprecated("Deprecated in Java")
    override fun onLoaderReset(loader: Loader<Cursor>?) {
        cursorAdapter.swapCursor(null)
    }

}