package com.movies.app.util

import com.movies.app.BuildConfig

object API_URL {

    val GENRES = BuildConfig.BASE_URL + "genre/movie/list"
    val POPULAR_MOVIES = BuildConfig.BASE_URL + "movie/popular"
    val TOP_RATED = BuildConfig.BASE_URL + "movie/top_rated"
    val DETAIL = BuildConfig.BASE_URL + "movie/{id}"
    val TRAILER = BuildConfig.BASE_URL + "movie/{id}/videos"
}