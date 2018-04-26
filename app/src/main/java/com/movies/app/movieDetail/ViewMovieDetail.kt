package com.movies.app.movieDetail

import org.json.JSONObject

interface ViewMovieDetail {

    fun onSuccessGetDetail(response: JSONObject?)

    fun onSuccessGetTrailerVideo(response: JSONObject?)

    fun onError(message: String?)

    fun onError()
}