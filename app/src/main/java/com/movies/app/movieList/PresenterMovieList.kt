package com.movies.app.movieList

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.movies.app.BuildConfig

class PresenterMovieList(private val view: ViewMovieList) {

    fun getGenreName(requestQueue: RequestQueue?) {
        val url = BuildConfig.BASE_URL + "genre/movie/list?api_key=${BuildConfig.API_KEY}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("genres")){
                        view.onSuccessGetGenre(response.getJSONArray("genres"))
                    } else {
                        view.onError()
                    }
                },
                Response.ErrorListener { error ->
                    view.onError()
                }
        )
        requestQueue?.add(request)
    }

    fun getPopularMovies(requestQueue: RequestQueue?, lastIndex: Int?) {
        val url = BuildConfig.BASE_URL + "movie/popular?api_key=${BuildConfig.API_KEY}" +
                "&page=$lastIndex"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("results")){
                        view.onSuccessGetPopular(response.getJSONArray("results").toString())
                    } else if (response.has("errors")) {
                        view.onError(response.getJSONArray("errors").getString(0))
                    } else {
                        view.onError()
                    }
                },
                Response.ErrorListener { error ->
                    view.onError()
                }
        )
        requestQueue?.add(request)
    }

    fun getTopRatedMovies(requestQueue: RequestQueue?, lastIndex: Int?) {
        val url = BuildConfig.BASE_URL + "movie/top_rated?api_key=${BuildConfig.API_KEY}" +
                "&page=$lastIndex"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("results")){
                        view.onSuccessGetTopRated(response.getJSONArray("results").toString())
                    } else if (response.has("errors")) {
                        view.onError(response.getJSONArray("errors").getString(0))
                    } else {
                        view.onError()
                    }
                },
                Response.ErrorListener { error ->
                    view.onError()
                }
        )
        requestQueue?.add(request)
    }
}