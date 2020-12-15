package com.magic.smarttravel.data

import android.content.Context
import android.content.SharedPreferences.*
import android.content.SharedPreferences

/**
 * Created by Marta Turchyniak on 12/9/20.
 */
class SharedPrefs(context: Context) {

    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SMART_TRAVEL, Context.MODE_PRIVATE)
    var editor: Editor = sharedPreferences.edit()

    fun putGroupId(id: String) {
        editor.putString(GROUP_ID, id)
        editor.commit()
    }

    fun getGroupId(): String {
        return sharedPreferences.getString(GROUP_ID, "") ?: ""
    }

    companion object {
        private const val SMART_TRAVEL = "smart_travel"
        private const val GROUP_ID = "groupId"
    }
}