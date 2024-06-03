package kr.nbc.momo.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.nbc.momo.domain.model.UserEntity
import javax.inject.Inject

class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>){

    private object PreferencesKeys{
        val USER_ID = stringPreferencesKey("user_id")
        //필요한 데이터 추가
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID]
    }

    suspend fun saveUserInfo(user: UserEntity){
        dataStore.edit {
            it[PreferencesKeys.USER_ID] = user.userId
        }
    }
}