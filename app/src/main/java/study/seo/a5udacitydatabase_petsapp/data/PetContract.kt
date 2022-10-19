package study.seo.a5udacitydatabase_petsapp.data

import android.net.Uri
import android.provider.BaseColumns

//테이블 정보를 갖는 클래스.
object PetContract {
    const val CONTENT_AUTHOR = "study.seo.a5.udacitydatabase_petsapp"
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHOR")
    const val PATH_PETS = "pets"

    object PetEntry : BaseColumns {
        val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
        const val TABLE_NAME = "pets"

        const val _ID = BaseColumns._ID     //기본키설정
        const val PET_NAME = "name"
        const val PET_BREED = "breed"
        const val PET_GENDER = "gender"
        const val PET_WEIGHT = "weight"

        const val GENDER_UNKNOWN = 0
        const val GENDER_MALE = 1
        const val GENDER_FEMALE = 2

    }
}