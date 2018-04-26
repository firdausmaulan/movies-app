package com.movies.app.movieDetail

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.movies.app.BuildConfig

class PresenterMovieDetail(private val view: ViewMovieDetail) {

    fun getRequestDetail(requestQueue: RequestQueue?, id: Int?) {
        val url = BuildConfig.BASE_URL + "movie/$id?api_key=${BuildConfig.API_KEY}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("id")) {
                        view.onSuccessGetDetail(response)
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

    fun getTrailerVideo(requestQueue: RequestQueue?, id: Int?) {
        val url = BuildConfig.BASE_URL + "movie/$id/videos?api_key=${BuildConfig.API_KEY}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("results")) {
                        view.onSuccessGetTrailerVideo(response)
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