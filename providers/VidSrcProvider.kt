
package com.yourname

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class VidSrcProvider : MainAPI() {

    override var name = "VidSrc"
    override var mainUrl = "https://vidsrc.to"
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override val hasMainPage = false

    // Cloudstream TMDB auto-loader
    override suspend fun loadFromTmdb(
        tmdbId: Int,
        title: String?,
        year: Int?,
        isMovie: Boolean
    ): LoadResponse {

        return if (isMovie) {
            MovieLoadResponse(
                name = title ?: "Movie",
                url = "movie|$tmdbId",
                apiName = name,
                type = TvType.Movie,
                dataUrl = "movie|$tmdbId"
            )
        } else {
            TvSeriesLoadResponse(
                name = title ?: "Series",
                url = "tv|$tmdbId",
                apiName = name,
                type = TvType.TvSeries,
                episodes = emptyList()
            )
        }
    }

    override suspend fun loadEpisodeLinks(
        data: String,
        season: Int,
        episode: Int,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {

        val tmdbId = data.removePrefix("tv|")
        val embedUrl = "$mainUrl/embed/tv/$tmdbId/$season/$episode"

        loadExtractor(embedUrl, subtitleCallback, callback)
        return true
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {

        val parts = data.split("|")
        if (parts.size != 2) return false

        val type = parts[0]
        val tmdbId = parts[1]

        val embedUrl = when (type) {
            "movie" -> "$mainUrl/embed/movie/$tmdbId"
            else -> return false
        }

        loadExtractor(embedUrl, subtitleCallback, callback)
        return true
    }

    override suspend fun search(query: String): List<SearchResponse> = emptyList()
    override suspend fun load(url: String): LoadResponse? = null
}
