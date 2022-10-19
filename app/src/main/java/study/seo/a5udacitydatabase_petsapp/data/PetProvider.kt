package study.seo.a5udacitydatabase_petsapp.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import study.seo.a5udacitydatabase_petsapp.data.PetContract.PetEntry
import study.seo.a5udacitydatabase_petsapp.data.PetContract.PetEntry.isValidGender


class PetProvider : ContentProvider() {
    private lateinit var dbHelper: DbHelper

    companion object {
        const val PETS = 100
        const val PET_ID = 101
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        uriMatcher.addURI(PetContract.CONTENT_AUTHOR, PetContract.PATH_PETS, PETS);
        uriMatcher.addURI(PetContract.CONTENT_AUTHOR, PetContract.PATH_PETS + "/#", PET_ID);
    }

    override fun onCreate(): Boolean {
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
                    PetEntry.TABLE_NAME,
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
                    PetEntry.TABLE_NAME,
                    projection,
                    PetEntry._ID + "=?",
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unkown URI$uri")
        }
        //uri가 변경되면 커서에게 알려줌 업데이트 해야한다고.
        cursor.setNotificationUri(context?.contentResolver, uri)

        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            PETS -> PetEntry.CONTENT_LIST_TYPE
            PET_ID -> PetEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return if (uriMatcher.match(uri) == PETS) {
            insertPet(uri, values)
        } else {
            throw IllegalArgumentException("INSERT ERROR $uri")
        }
    }

    private fun insertPet(uri: Uri, values: ContentValues?): Uri? {
        val id = dbHelper.writableDatabase
            .insert(PetEntry.TABLE_NAME, null, values)
        return if (!id.equals(-1)) {
            //모든 리스너에게 데이터 변경이 되었다고 알린다.URI가.
            context?.contentResolver?.notifyChange(uri, null)
            ContentUris.withAppendedId(uri, id)
        } else {
            Log.e("Provider", "Fail insert $uri")
            null
        }
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return with(dbHelper.writableDatabase) {
            when (uriMatcher.match(uri)) {
                PETS -> delete(PetEntry.TABLE_NAME, selection, selectionArgs)
                PET_ID -> {
                    delete(
                        PetEntry.TABLE_NAME,
                        PetEntry._ID + "=?",
                        arrayOf(ContentUris.parseId(uri).toString())
                    )
                }
                else -> throw IllegalArgumentException("DB에서 삭제를 실패했습니다. $uri")
            }
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            PETS -> updatePet(uri, values, selection, selectionArgs)
            PET_ID -> {
                updatePet(
                    uri,
                    values,
                    PetEntry._ID + "=?",
                    arrayOf(ContentUris.parseId(uri).toString())
                )
            }
            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }

    private fun updatePet(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        values?.let {
            with(it) {
                if (containsKey(PetEntry.PET_NAME)) {
                    getAsString(PetEntry.PET_NAME)
                        ?: throw IllegalArgumentException("Pet requires a name")
                }

                if (containsKey(PetEntry.PET_GENDER)) {
                    val gender = getAsInteger(PetEntry.PET_GENDER)
                    require(!(gender == null || !isValidGender(gender))) { "Pet requires valid gender" }
                }

                if (containsKey(PetEntry.PET_WEIGHT)) {
                    val weight = getAsInteger(PetEntry.PET_WEIGHT)
                    require(!(weight != null && weight < 0)) { "Pet requires valid weight" }
                }

                if (size() == 0) {
                    return 0
                }
            }
        }
        val rowsUpdated =
            dbHelper.writableDatabase
                .update(PetEntry.TABLE_NAME, values, selection, selectionArgs)

        if (rowsUpdated != 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }

        return rowsUpdated
    }
}


