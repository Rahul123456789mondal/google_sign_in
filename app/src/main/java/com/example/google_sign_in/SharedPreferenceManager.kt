package com.example.google_sign_in

import android.content.Context
import android.content.SharedPreferences

/**
 * Session manager to save and fetch data from SharedPreferences
 */

class SharedPreferenceManager (context: Context) {
    
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun saveDataToSharedPreference(keyName : String, data: String) {
        val editor = prefs.edit()
        editor.putString(keyName, data)
        editor.apply()
    }

    fun fetchData(keyName: String): String? {
        return prefs.getString(keyName, null)
    }

    fun clearPref() {
        val editor = prefs.edit()
        editor.clear().apply()
    }


}