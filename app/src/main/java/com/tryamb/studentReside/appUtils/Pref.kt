package com.tryamb.studentReside.appUtils

import android.content.Context
import android.content.SharedPreferences


class Pref(context: Context) {
    private val context: Context
    private val PREF_FILE: String
    private val mPrefEditor: SharedPreferences.Editor
    private val pref: SharedPreferences

    init {
        this.context = context
        PREF_FILE = context.packageName
        pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        mPrefEditor = pref.edit()
    }

    fun putString(key: String?, value: String?) {
        mPrefEditor.putString(key, value)
        mPrefEditor.apply()
    }
    fun getString(key: String?): String? {
        return pref.getString(key, "")
    }

    fun clearAll() {
        mPrefEditor.clear()
        mPrefEditor.apply()
    }
}
