package com.secureiptv.player.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.secureiptv.player.IPTVApplication

/**
 * Manages secure storage of user credentials using EncryptedSharedPreferences
 */
class CredentialManager(private val context: Context = IPTVApplication.instance) {

    companion object {
        private const val PREFS_FILE_NAME = "secure_iptv_credentials"
        private const val KEY_DNS = "dns_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        @Volatile
        private var INSTANCE: CredentialManager? = null
        
        fun getInstance(context: Context = IPTVApplication.instance): CredentialManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CredentialManager(context).also { INSTANCE = it }
            }
        }
    }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveCredentials(dns: String, username: String, password: String) {
        encryptedSharedPreferences.edit().apply {
            putString(KEY_DNS, dns)
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getDns(): String? {
        return encryptedSharedPreferences.getString(KEY_DNS, null)
    }
    
    fun getUsername(): String? {
        return encryptedSharedPreferences.getString(KEY_USERNAME, null)
    }
    
    fun getPassword(): String? {
        return encryptedSharedPreferences.getString(KEY_PASSWORD, null)
    }
    
    fun isLoggedIn(): Boolean {
        return encryptedSharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun clearCredentials() {
        encryptedSharedPreferences.edit().clear().apply()
    }
}