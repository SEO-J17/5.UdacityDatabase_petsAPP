package study.seo.a5udacitydatabase_petsapp

import android.annotation.SuppressLint
import android.app.LoaderManager
import android.content.ContentValues
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import study.seo.a5udacitydatabase_petsapp.data.PetContract.PetEntry


class EditorActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor?> {
    private var currentPetUri: Uri? = null
    private lateinit var nameEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var genderSpinner: Spinner
    private var gender = PetEntry.GENDER_UNKNOWN
    private var petHasChanged = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        currentPetUri = intent.data

        if (currentPetUri == null) {
            title = getString(R.string.editor_activity_title_new_pet)
            invalidateOptionsMenu()
        } else {
            title = "Edit Pet"
            loaderManager.initLoader(EXISTING_PET_LOADER, null, this)
        }


        nameEditText = findViewById(R.id.edit_pet_name)
        breedEditText = findViewById(R.id.edit_pet_breed)
        weightEditText = findViewById(R.id.edit_pet_weight)
        genderSpinner = findViewById(R.id.spinner_gender)


        with(
            OnTouchListener { view, motionEvent ->
                petHasChanged = true
                false
            }) {
            nameEditText.setOnTouchListener(this)
            breedEditText.setOnTouchListener(this)
            weightEditText.setOnTouchListener(this)
            genderSpinner.setOnTouchListener(this)
        }

        setupSpinner()
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.array_gender_options,
            android.R.layout.simple_spinner_item
        )

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        genderSpinner.adapter = genderSpinnerAdapter

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent.getItemAtPosition(position).toString().apply {
                    if (isEmpty()) {
                        gender = if (this == getString(R.string.gender_male)) {
                            PetEntry.GENDER_MALE
                        } else if (this == getString(R.string.gender_female)) {
                            PetEntry.GENDER_FEMALE
                        } else {
                            PetEntry.GENDER_UNKNOWN
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                gender = PetEntry.GENDER_UNKNOWN
            }
        }
    }

    private fun savePet() {
        val nameString = nameEditText.text.toString().trim()
        val breedString = breedEditText.text.toString().trim()
        val weightString = weightEditText.text.toString().trim()

        if (currentPetUri == null &&
            nameString.isEmpty() && breedString.isEmpty() &&
            weightString.isEmpty() && gender == PetEntry.GENDER_UNKNOWN
        ) {
            return
        }

        val values = ContentValues().apply {
            put(PetEntry.PET_NAME, nameString)
            put(PetEntry.PET_BREED, breedString)
            put(PetEntry.PET_GENDER, gender)
            var weight = 0
            if (!TextUtils.isEmpty(weightString)) {
                weight = weightString.toInt()
            }
            put(PetEntry.PET_WEIGHT, weight)
        }

        currentPetUri?.let {
            val rowsAffected = contentResolver.update(it, values, null, null)
            if (rowsAffected == 0) {
                Toast.makeText(
                    this, "펫 업데이트 실패",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "펫 업데이트 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } ?: run {
            val newUri: Uri? = contentResolver.insert(PetEntry.CONTENT_URI, values)
            if (newUri == null) {
                Toast.makeText(
                    this, getString(R.string.editor_insert_pet_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, getString(R.string.editor_insert_pet_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        currentPetUri ?: run {
            val menuItem: MenuItem = menu.findItem(R.id.action_delete)
            menuItem.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                savePet()
                finish()
                return true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                return true
            }
            android.R.id.home -> {
                if (!petHasChanged) {
                    NavUtils.navigateUpFromSameTask(this@EditorActivity)
                    return true
                }
                showUnsavedChangesDialog(DialogInterface.OnClickListener { dialogInterface, i ->
                    NavUtils.navigateUpFromSameTask(this@EditorActivity)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!petHasChanged) {
            super.onBackPressed()
            return
        }
        showUnsavedChangesDialog { dialogInterface, i ->
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor?> {
        val projection = arrayOf(
            PetEntry._ID,
            PetEntry.PET_NAME,
            PetEntry.PET_BREED,
            PetEntry.PET_GENDER,
            PetEntry.PET_WEIGHT
        )

        return CursorLoader(
            this,
            currentPetUri,
            projection,
            null,
            null,
            null
        )
    }

    @SuppressLint("Range")
    @Deprecated("Deprecated in Java")
    override fun onLoadFinished(loader: Loader<Cursor?>?, cursor: Cursor?) {
        if (cursor == null || cursor.count < 1) {
            return
        }

        if (cursor.moveToFirst()) {
            nameEditText.setText(cursor.getString(cursor.getColumnIndex(PetEntry.PET_NAME)))
            breedEditText.setText(cursor.getString(cursor.getColumnIndex(PetEntry.PET_BREED)))
            weightEditText.setText(
                cursor.getInt(cursor.getColumnIndex(PetEntry.PET_WEIGHT)).toString()
            )
            when (cursor.getInt(cursor.getColumnIndex(PetEntry.PET_GENDER))) {
                PetEntry.GENDER_MALE -> genderSpinner.setSelection(1)
                PetEntry.GENDER_FEMALE -> genderSpinner.setSelection(2)
                else -> genderSpinner.setSelection(0)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onLoaderReset(loader: Loader<Cursor?>?) {
        nameEditText.setText("")
        breedEditText.setText("")
        weightEditText.setText("")
        genderSpinner.setSelection(0)
    }

    private fun showUnsavedChangesDialog(
        discardButtonClickListener: DialogInterface.OnClickListener
    ) {
        android.app.AlertDialog.Builder(this).apply {
            setMessage(R.string.unsaved_changes_dialog_msg)
            setPositiveButton(R.string.discard, discardButtonClickListener)
            setNegativeButton(
                R.string.keep_editing
            ) { dialog, id ->
                dialog?.dismiss()
            }
            create().show()
        }

    }

    private fun showDeleteConfirmationDialog() {
        android.app.AlertDialog.Builder(this).apply {
            setMessage(R.string.delete_dialog_msg)
            setPositiveButton(
                R.string.delete
            ) { dialog, id ->
                deletePet()
            }
            setNegativeButton(
                R.string.cancel
            ) { dialog, id ->
                dialog?.dismiss()
            }
            create().show()
        }
    }

    private fun deletePet() {
        currentPetUri?.let {
            if (contentResolver.delete(it, null, null) == 0) {
                Toast.makeText(
                    this, "펫을 삭제하는데 실패했습니다!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "펫 삭제 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        finish()
    }

    companion object {
        private const val EXISTING_PET_LOADER = 0
    }
}