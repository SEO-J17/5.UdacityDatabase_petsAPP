package study.seo.a5udacitydatabase_petsapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(
    context: Context?
) : SQLiteOpenHelper(context, "shelter.db", null, 1) {
    companion object {
        const val DATABASE_NAME = "shelter.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            with(PetContract.PetEntry) {
                "CREATE TABLE " + TABLE_NAME +
                        "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PET_NAME + " TEXT NOT NULL, " +
                        PET_BREED + " TEXT, " +
                        PET_GENDER + " INTEGER NOT NULL, " +
                        PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);"
            })
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}