package com.movies.app

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.movies.app.model.ModelGenre
import com.movies.app.movieList.ContractMovieList
import com.movies.app.movieList.PresenterMovieList
import com.movies.app.util.ApiURL
import com.movies.app.util.AppLog
import org.json.JSONObject

import org.junit.Before
import org.junit.Test
import org.mockito.*


class SampleTest {

    @Mock
    internal var view: ContractMovieList.View? = null

    private var presenter: PresenterMovieList? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PresenterMovieList()
    }

    @Test
    fun loadGenre() {
        AndroidNetworking.get(ApiURL.GENRES)
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.has("genres")) {
                            val typeToken = object : TypeToken<ArrayList<ModelGenre>>() {}.type
                            val resultList = Gson().fromJson<ArrayList<ModelGenre>>(
                                    response.getJSONArray("genres").toString(), typeToken
                            )
                            Mockito.verify(view)?.setListGenre(resultList)
                        } else {
                            Mockito.verify(view)?.showError(R.string.unable_to_connect)
                        }
                    }

                    override fun onError(error: ANError) {
                        Mockito.verify(view)?.showError(R.string.unable_to_connect)
                    }
                })
    }

    @Test
    fun loadPopularMovies() {
        val lastIndex = "1"
        System.out.print("start test")
        AndroidNetworking.get(ApiURL.POPULAR_MOVIES)
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .addQueryParameter("page", lastIndex)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        System.out.print(response.toString())
                        if (response.has("results")) {
                            assert(true)
                        } else if (response.has("errors")) {
                            assert(false)
                        } else {
                            assert(false)
                        }
                    }

                    override fun onError(error: ANError) {
                        assert(false)
                    }
                })
    }
}
