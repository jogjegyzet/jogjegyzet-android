package com.danielgergely.jogjegyzet.persistence.update

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.danielgergely.jogjegyzet.domain.UpdateMessage
import javax.inject.Inject
import javax.inject.Singleton

private const val SHARED_PREF_NAME = "update"

private const val KEY_TYPE = "type"
private const val KEY_MESSAGE = "msg"
private const val KEY_MAX_VERSION = "version"

private const val VALUE_OPTIONAL = "opt"
private const val VALUE_MUST = "must"
private const val VALUE_NONE = "none"

@Singleton
class UpdateMessageStorage @Inject constructor(private val context: Context) {
    fun storeMessage(message: UpdateMessage) {
        val sp = sharedPrefs()
        val editor = sp.edit()

        when (message) {
            is UpdateMessage.None -> {
                editor.putString(KEY_TYPE, VALUE_NONE)
            }
            is UpdateMessage.OptionalUpdate -> {
                editor.putString(KEY_TYPE, VALUE_OPTIONAL)
                editor.putString(KEY_MESSAGE, message.message)
                editor.putInt(KEY_MAX_VERSION, message.maxVersion)
            }
            is UpdateMessage.MustUpdate -> {
                editor.putString(KEY_TYPE, VALUE_MUST)
                editor.putString(KEY_MESSAGE, message.message)
                editor.putInt(KEY_MAX_VERSION, message.maxVersion)
            }
        }

        editor.apply()
    }

    fun getMessage(): UpdateMessage? {
        val sp = sharedPrefs() ?: return null

        val type = sp.getString(KEY_TYPE, null) ?: return null
        if (type == VALUE_NONE) return UpdateMessage.None

        val message = sp.getString(KEY_MESSAGE, null) ?: return null
        val maxVersion = sp.getInt(KEY_MAX_VERSION, -1)

        if (maxVersion < 0) return null

        return when(type) {
            VALUE_MUST -> UpdateMessage.MustUpdate(message, maxVersion)
            VALUE_OPTIONAL -> UpdateMessage.OptionalUpdate(message, maxVersion)
            else -> null
        }
    }

    private fun sharedPrefs() = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
}