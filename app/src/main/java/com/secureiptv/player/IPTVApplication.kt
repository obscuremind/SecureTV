package com.secureiptv.player

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class IPTVApplication : Application() {

    companion object {
        const val PROXY_BASE_URL_KEY = "proxy_base_url"
        const val DEFAULT_PROXY_URL = "http://firebaseremoteconfig.googleapis.com"
        
        lateinit var instance: IPTVApplication
            private set
    }

    lateinit var remoteConfig: FirebaseRemoteConfig
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Setup Remote Config
        setupRemoteConfig()
    }

    private fun setupRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
        
        // Set default values
        val defaults = mapOf(
            PROXY_BASE_URL_KEY to DEFAULT_PROXY_URL
        )
        remoteConfig.setDefaultsAsync(defaults)
        
        // Configure fetch settings
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Fetch remote config
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Remote config fetched successfully
            }
        }
    }

    fun getProxyBaseUrl(): String {
        return remoteConfig.getString(PROXY_BASE_URL_KEY)
    }
}