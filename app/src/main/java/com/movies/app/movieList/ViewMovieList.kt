package com.movies.app.movieList

import org.json.JSONArray

interface ViewMovieList {
    fun onRequestPopular(lastIndex: Int?)

    fun onRequestTopRated(lastIndex: Int?)

    fun onSuccessGetGenre(response: JSONArray?)

    fun onSuccessGetPopular(response: String?)

    fun onSuccessGetTopRated(response: String?)

    fun onError(message: String?)

    fun onError()
}