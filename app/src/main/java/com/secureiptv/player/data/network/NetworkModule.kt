package com.secureiptv.player.data.network

import com.secureiptv.player.data.security.CredentialManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides network-related dependencies
 */
object NetworkModule {

    private const val TIMEOUT_SECONDS = 30L
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val proxyUrlInterceptor = ProxyUrlInterceptor()
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(proxyUrlInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    
    /**
     * Creates a Retrofit instance for the Xtream API
     * Uses the real DNS from CredentialManager but all requests will be routed through the proxy
     */
    fun createXtreamApiService(): XtreamApiService {
        val credentialManager = CredentialManager.getInstance()
        val baseUrl = credentialManager.getDns() ?: "http://localhost/"
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XtreamApiService::class.java)
    }
    
    /**
     * Builds a stream URL that goes through the proxy
     */
    fun buildProxyStreamUrl(streamPath: String): String {
        val credentialManager = CredentialManager.getInstance()
        val username = credentialManager.getUsername() ?: return ""
        val password = credentialManager.getPassword() ?: return ""
        
        // Get the proxy base URL from Firebase Remote Config
        val proxyBaseUrl = com.secureiptv.player.IPTVApplication.instance.getProxyBaseUrl()
        
        // Construct the full URL with the proxy domain
        return "$proxyBaseUrl/$streamPath"
    }
}