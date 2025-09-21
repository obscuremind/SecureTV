package com.secureiptv.player.data.models

/**
 * Represents the possible playback sources for a stream.
 * [httpUrl] is always populated and points to the proxy HTTP endpoint.
 * [torrentUrl] is optional and contains a magnet link or torrent URL when available.
 */
data class StreamSource(
    val httpUrl: String,
    val torrentUrl: String?
)
