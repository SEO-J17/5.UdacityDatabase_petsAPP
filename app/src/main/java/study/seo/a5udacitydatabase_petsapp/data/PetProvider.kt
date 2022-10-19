package study.seo.a5udacitydatabase_petsapp.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import java.lang.IllegalArgumentException

class PetProvider : ContentProvider() {
    private lateinit var dbHelper: DbHelper

    companion object {
        const val PETS = 100
        const val PET_ID = 101
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    override fun onCreate(): Boolean {
        uriMatcher.addURI(PetContract.CONTENT_AUTHOR, PetContract.PATH_PETS, PETS);
        uriMatcher.addURI(PetContract.CONTENT_AUTHOR, PetContract.PATH_PETS + "/#", PET_ID);
        dbHelper = DbHelper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val database = dbHelper.readableDatabase
        val cursor: Cursor
        when (uriMatcher.match(uri)) {
            PETS -> {
                cursor = database.query(
                    PetContract.PetEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            PET_ID -> {
                cursor = database.query(
                    PetContract.PetEntry.TABLE_NAME,
                    projection,
                    PetContract.PetEntry._ID + "=?",
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unkown URI$uri")
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return if (uriMatcher.match(uri) == PETS) {
            insertPet(uri, values)
        } else {
            throw IllegalArgumentException("INSERT ERROR $uri")
        }
    }

    private fun insertPet(uri: Uri, values: ContentValues?): Uri? {
        val id = dbHelper.readableDatabase
            .insert(PetContract.PetEntry.TABLE_NAME, null, values)
        return if (!id.equals(-1)) {
            ContentUris.withAppendedId(uri, id)
        } else {
            Log.e("Provider", "Fail insert $uri")
            null
        }
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}


