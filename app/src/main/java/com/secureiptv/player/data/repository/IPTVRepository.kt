package com.secureiptv.player.data.repository

import com.secureiptv.player.data.models.*
import com.secureiptv.player.data.network.NetworkModule
import com.secureiptv.player.data.network.XtreamApiService
import com.secureiptv.player.data.security.CredentialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for IPTV data
 * Handles communication with the API and credential management
 */
class IPTVRepository {

    private val credentialManager = CredentialManager.getInstance()
    private val apiService = NetworkModule.createXtreamApiService()
    
    /**
     * Authenticates the user with the Xtream API
     * @return Result containing AuthResponse on success or error message on failure
     */
    suspend fun authenticate(dns: String, username: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            // Save DNS temporarily for authentication
            credentialManager.saveCredentials(dns, username, password)
            
            // Create a new API service with the provided DNS
            val authApiService = NetworkModule.createXtreamApiService()
            
            val response = authApiService.authenticate(username, password)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                
                // Check if authentication was successful
                if (authResponse.userInfo != null && authResponse.error == 0) {
                    // Keep credentials saved
                    return@withContext Result.success(authResponse)
                } else {
                    // Clear credentials on failure
                    credentialManager.clearCredentials()
                    return@withContext Result.failure(Exception(authResponse.message ?: "Authentication failed"))
                }
            } else {
                // Clear credentials on failure
                credentialManager.clearCredentials()
                return@withContext Result.failure(Exception("Authentication failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Clear credentials on exception
            credentialManager.clearCredentials()
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Logs out the user by clearing credentials
     */
    fun logout() {
        credentialManager.clearCredentials()
    }
    
    /**
     * Checks if the user is logged in
     */
    fun isLoggedIn(): Boolean {
        return credentialManager.isLoggedIn()
    }
    
    /**
     * Gets the username and password for API calls
     */
    private fun getCredentials(): Pair<String, String>? {
        val username = credentialManager.getUsername() ?: return null
        val password = credentialManager.getPassword() ?: return null
        return Pair(username, password)
    }
    
    /**
     * Gets live stream categories
     */
    suspend fun getLiveCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getLiveCategories(username, password)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets live streams by category
     */
    suspend fun getLiveStreamsByCategory(categoryId: String): Result<List<LiveStream>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getLiveStreamsByCategory(username, password, categoryId = categoryId)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets all live streams
     */
    suspend fun getAllLiveStreams(): Result<List<LiveStream>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getLiveStreams(username, password)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets VOD categories
     */
    suspend fun getVodCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getVodCategories(username, password)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets VOD streams by category
     */
    suspend fun getVodStreamsByCategory(categoryId: String): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getVodStreamsByCategory(username, password, categoryId = categoryId)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets all VOD streams
     */
    suspend fun getAllVodStreams(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getVodStreams(username, password)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets VOD info
     */
    suspend fun getVodInfo(vodId: Int): Result<Movie> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getVodInfo(username, password, vodId = vodId)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets series categories
     */
    suspend fun getSeriesCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getSeriesCategories(username, password)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets series by category
     */
    suspend fun getSeriesByCategory(categoryId: String): Result<List<Series>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getSeriesByCategory(username, password, categoryId = categoryId)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets all series
     */
    suspend fun getAllSeries(): Result<List<Series>> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getSeries(username, password)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Gets series info
     */
    suspend fun getSeriesInfo(seriesId: Int): Result<SeriesInfo> = withContext(Dispatchers.IO) {
        try {
            val (username, password) = getCredentials() ?: return@withContext Result.failure(Exception("Not logged in"))
            val response = apiService.getSeriesInfo(username, password, seriesId = seriesId)
            return@withContext handleResponse(response)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Builds a live stream URL that goes through the proxy
     */
    fun buildLiveStreamUrl(streamId: Int): String {
        val (username, password) = getCredentials() ?: return ""
        val streamPath = XtreamApiService.getLiveStreamUrl(username, password, streamId)
        return NetworkModule.buildProxyStreamUrl(streamPath)
    }
    
    /**
     * Builds a VOD stream URL that goes through the proxy
     */
    fun buildVodStreamUrl(streamId: Int): String {
        val (username, password) = getCredentials() ?: return ""
        val streamPath = XtreamApiService.getVodStreamUrl(username, password, streamId)
        return NetworkModule.buildProxyStreamUrl(streamPath)
    }
    
    /**
     * Builds a series stream URL that goes through the proxy
     */
    fun buildSeriesStreamUrl(streamId: Int): String {
        val (username, password) = getCredentials() ?: return ""
        val streamPath = XtreamApiService.getSeriesStreamUrl(username, password, streamId)
        return NetworkModule.buildProxyStreamUrl(streamPath)
    }
    
    /**
     * Helper function to handle API responses
     */
    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
        }
    }
}