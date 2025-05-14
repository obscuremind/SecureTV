package com.secureiptv.player.data.network

import com.secureiptv.player.IPTVApplication
import com.secureiptv.player.data.security.CredentialManager
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp Interceptor that replaces the real DNS with the proxy URL
 * This ensures that all network traffic appears to be going to the proxy domain
 */
class ProxyUrlInterceptor : Interceptor {

    private val credentialManager = CredentialManager.getInstance()
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip if not logged in
        if (!credentialManager.isLoggedIn()) {
            return chain.proceed(originalRequest)
        }
        
        val realDns = credentialManager.getDns() ?: return chain.proceed(originalRequest)
        val proxyBaseUrl = IPTVApplication.instance.getProxyBaseUrl()
        
        // Create a new request with the proxy URL
        val newRequest = rewriteRequestWithProxy(originalRequest, realDns, proxyBaseUrl)
        
        // Add the real host as a header for the proxy to use
        val requestWithRealHost = newRequest.newBuilder()
            .header("Host", realDns.toHttpUrl().host)
            .header("X-Real-IP", "127.0.0.1")
            .header("X-Forwarded-For", "127.0.0.1")
            .build()
        
        return chain.proceed(requestWithRealHost)
    }
    
    private fun rewriteRequestWithProxy(
        originalRequest: Request,
        realDns: String,
        proxyBaseUrl: String
    ): Request {
        val originalUrl = originalRequest.url
        val realDnsUrl = realDns.toHttpUrl()
        val proxyUrl = proxyBaseUrl.toHttpUrl()
        
        // Build a new URL that uses the proxy domain but keeps the path and query from the original
        val newUrl = originalUrl.newBuilder()
            .scheme(proxyUrl.scheme)
            .host(proxyUrl.host)
            .port(proxyUrl.port)
            .build()
        
        return originalRequest.newBuilder()
            .url(newUrl)
            .build()
    }
}