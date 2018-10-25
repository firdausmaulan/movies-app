package com.movies.app

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.movies.app.model.ModelGenre
import com.movies.app.movieList.ContractMovieList
import com.movies.app.movieList.PresenterMovieList

import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*


class MockitoTest {

    @Mock
    internal var view: ContractMovieList.View? = null
    @Mock
    internal val callGenre = AndroidNetworking
            .get(BuildConfig.BASE_URL + "genre/movie/list")
            .addQueryParameter("api_key", BuildConfig.API_KEY)

    private var presenter: PresenterMovieList? = null

    companion object {
        private val testResponse = JSONObject()
    }

    @Captor
    private val loadCallbackArgumentCaptor: ArgumentCaptor<JSONObjectRequestListener>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PresenterMovieList()
    }

    @Test
    fun loadGenre() {
        presenter?.loadGenre()
        verify(callGenre)?.build()?.getAsJSONObject(loadCallbackArgumentCaptor?.capture())
        loadCallbackArgumentCaptor?.value?.onResponse(testResponse)
        val typeToken = object : TypeToken<ArrayList<ModelGenre>>() {}.type
        val resultList = Gson().fromJson<ArrayList<ModelGenre>>(
                testResponse.getJSONArray("genres").toString(), typeToken
        )
        verify(view)?.setListGenre(resultList)
    }
}
