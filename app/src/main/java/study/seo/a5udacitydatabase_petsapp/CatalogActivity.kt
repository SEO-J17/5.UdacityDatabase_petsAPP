/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package study.seo.a5udacitydatabase_petsapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import study.seo.a5udacitydatabase_petsapp.data.PetContract

class CatalogActivity : AppCompatActivity() {
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
        showDatabaseInfo()
    }

    override fun onStart() {
        super.onStart()
    }

    @SuppressLint("Recycle", "Range", "SetTextI18n")
    private fun showDatabaseInfo() {
        val displayView = findViewById<TextView>(R.id.text_view_pet)
        val projection = with(PetContract.PetEntry) {
            arrayOf(
                _ID,
                PET_NAME,
                PET_BREED,
                PET_GENDER,
                PET_WEIGHT
            )
        }
        contentResolver.query(
            PetContract.PetEntry.CONTENT_URI,
            projection,
            null,
            null,
            null,
        ).use { cursor ->
            displayView.text = "table contains ${cursor?.count}"
            displayView.append(with(PetContract.PetEntry) {
                "$_ID - $PET_NAME - $PET_BREED - $PET_GENDER - $PET_WEIGHT"
            })
            with(PetContract.PetEntry) {
                while (cursor?.moveToNext() == true) {
                    displayView.append(
                        "\n ${cursor.getInt(cursor.getColumnIndex(_ID))} " +
                                "${cursor.getString(cursor.getColumnIndex(PET_NAME))} " +
                                "${cursor.getString(cursor.getColumnIndex(PET_BREED))} " +
                                "${cursor.getInt(cursor.getColumnIndex(PET_GENDER))} " +
                                "${cursor.getInt(cursor.getColumnIndex(PET_WEIGHT))} "
                    )
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()
                showDatabaseInfo()
                return true
            }
            R.id.action_delete_all_entries ->                 // Do nothing for now
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertPet() {
        with(PetContract.PetEntry) {
            contentResolver.insert(CONTENT_URI, ContentValues().apply {
                put(PET_NAME, "Tto")
                put(PET_BREED, "Terrier")
                put(PET_GENDER, GENDER_MALE)
                put(PET_WEIGHT, 7)
            })
        }
    }
}