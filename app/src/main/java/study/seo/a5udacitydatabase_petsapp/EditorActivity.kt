package study.seo.a5udacitydatabase_petsapp

import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import study.seo.a5udacitydatabase_petsapp.data.PetContract

class EditorActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var genderSpinner: Spinner
    private var gender = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        nameEditText = findViewById(R.id.edit_pet_name)
        breedEditText = findViewById(R.id.edit_pet_breed)
        weightEditText = findViewById(R.id.edit_pet_weight)
        genderSpinner = findViewById(R.id.spinner_gender)
        setupSpinner()
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.array_gender_options, android.R.layout.simple_spinner_item
        )

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        genderSpinner.adapter = genderSpinnerAdapter

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selection = parent.getItemAtPosition(position).toString()
                if (!TextUtils.isEmpty(selection)) {
                    gender = when (selection) {
                        getString(R.string.gender_male) -> {
                            PetContract.PetEntry.GENDER_MALE
                        }
                        getString(R.string.gender_female) -> {
                            PetContract.PetEntry.GENDER_FEMALE
                        }
                        else -> {
                            PetContract.PetEntry.GENDER_UNKNOWN
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                gender = 0 // Unknown
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                insertPet()
                return true
            }
            R.id.action_delete ->                 // Do nothing for now
                return true
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertPet() {
        ContentValues().also { values ->
            with(PetContract.PetEntry) {
                values.put(PET_NAME, nameEditText.text.toString().trim())
                values.put(PET_BREED, breedEditText.text.toString().trim())
                values.put(PET_GENDER, gender)
                values.put(PET_WEIGHT, weightEditText.text.toString().toIntOrNull())
                if (contentResolver.insert(CONTENT_URI, values) == null) {
                    Toast.makeText(this@EditorActivity, "저장이 실패했습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditorActivity, "저장 성공!!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}