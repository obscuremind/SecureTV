package com.secureiptv.player.data.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("user_info") val userInfo: UserInfo?,
    @SerializedName("server_info") val serverInfo: ServerInfo?,
    val error: Int = 0,
    val message: String? = null
)

data class UserInfo(
    val username: String,
    val password: String,
    val status: String,
    @SerializedName("exp_date") val expDate: String,
    @SerializedName("is_trial") val isTrial: String,
    @SerializedName("active_cons") val activeCons: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("max_connections") val maxConnections: String,
    val allowed_output_formats: List<String>
)

data class ServerInfo(
    val url: String,
    @SerializedName("port") val port: String,
    @SerializedName("https_port") val httpsPort: String,
    val server_protocol: String,
    val rtmp_port: String,
    val timezone: String,
    val timestamp_now: Long,
    val time_now: String
)

data class LiveStreamsResponse(
    val categories: List<Category>? = null,
    val streams: List<LiveStream>? = null
)

data class VodResponse(
    val categories: List<Category>? = null,
    @SerializedName("movies") val movies: List<Movie>? = null
)

data class SeriesResponse(
    val categories: List<Category>? = null,
    val series: List<Series>? = null
)

data class Category(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int = 0
)

data class LiveStream(
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    val name: String,
    @SerializedName("epg_channel_id") val epgChannelId: String?,
    @SerializedName("added") val added: String?,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("tv_archive") val tvArchive: Int,
    @SerializedName("direct_source") val directSource: String?,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: Int
)

data class Movie(
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    val name: String,
    @SerializedName("added") val added: String?,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("container_extension") val containerExtension: String,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("direct_source") val directSource: String?,
    val plot: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("releasedate") val releaseDate: String?,
    @SerializedName("year") val year: String?,
    @SerializedName("backdrop_path") val backdropPath: List<String>?,
    @SerializedName("youtube_trailer") val youtubeTrailer: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("actors") val actors: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("cover") val cover: String?
)

data class Series(
    @SerializedName("series_id") val seriesId: Int,
    val name: String,
    @SerializedName("cover") val cover: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("last_modified") val lastModified: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("rating_5based") val rating5based: Double?,
    @SerializedName("backdrop_path") val backdropPath: List<String>?,
    @SerializedName("youtube_trailer") val youtubeTrailer: String?,
    @SerializedName("episode_run_time") val episodeRunTime: String?,
    @SerializedName("category_id") val categoryId: String
)

data class SeriesInfo(
    val seasons: Map<String, List<Episode>>,
    val info: Series
)

data class Episode(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_num") val episodeNum: Int,
    @SerializedName("title") val title: String,
    @SerializedName("container_extension") val containerExtension: String,
    @SerializedName("info") val info: EpisodeInfo?,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("added") val added: String?,
    @SerializedName("season") val season: Int,
    @SerializedName("direct_source") val directSource: String?
)

data class EpisodeInfo(
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("releasedate") val releaseDate: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("duration_secs") val durationSecs: Int?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("movie_image") val movieImage: String?,
    @SerializedName("bitrate") val bitrate: Int?,
    @SerializedName("rating") val rating: String?
)