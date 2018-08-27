package com.movies.app.movieList

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.movies.app.BuildConfig
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.model.ModelGenre
import com.movies.app.model.ModelMovie
import com.movies.app.mvp.BaseMvpPresenterImpl
import com.movies.app.util.API_URL
import org.json.JSONObject

class PresenterMovieList : BaseMvpPresenterImpl<ContractMovieList.View>(),
        ContractMovieList.Presenter {

    override fun loadGenre() {
        AndroidNetworking.get(API_URL.GENRES)
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.has("genres")) {
                            val typeToken = object : TypeToken<ArrayList<ModelGenre>>() {}.type
                            val resultList = Gson().fromJson<ArrayList<ModelGenre>>(
                                    response.getJSONArray("genres").toString(), typeToken
                            )
                            mView?.setListGenre(resultList)
                        } else {
                            mView?.showError(R.string.unable_to_connect)
                        }
                    }

                    override fun onError(error: ANError) {
                        mView?.showError(R.string.unable_to_connect)
                    }
                })
    }

    override fun loadPopularMovies(listGenre: ArrayList<ModelGenre>,
                                   listFavourite: ArrayList<Int>,
                                   lastIndex: Int?) {
        AndroidNetworking.get(API_URL.POPULAR_MOVIES)
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .addQueryParameter("page", lastIndex.toString())
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.has("results")) {
                            mView?.showPopularMovies(formatMovieList(
                                    response.getJSONArray("results").toString(), listGenre, listFavourite)
                            )
                        } else if (response.has("errors")) {
                            mView?.showError(response.getJSONArray("errors").getString(0))
                        } else {
                            mView?.showError(R.string.unable_to_connect)
                        }
                    }

                    override fun onError(error: ANError) {
                        mView?.showError(R.string.unable_to_connect)
                    }
                })
    }

    override fun loadTopRatedMovies(listGenre: ArrayList<ModelGenre>,
                                    listFavourite: ArrayList<Int>,
                                    lastIndex: Int?) {
        AndroidNetworking.get(API_URL.TOP_RATED)
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .addQueryParameter("page", lastIndex.toString())
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.has("results")) {
                            mView?.showTopRatedMovies(formatMovieList(
                                    response.getJSONArray("results").toString(), listGenre, listFavourite)
                            )
                        } else if (response.has("errors")) {
                            mView?.showError(response.getJSONArray("errors").getString(0))
                        } else {
                            mView?.showError(R.string.unable_to_connect)
                        }
                    }

                    override fun onError(error: ANError) {
                        mView?.showError(R.string.unable_to_connect)
                    }
                })
    }

    override fun loadFavouriteMovies(listGenre: ArrayList<ModelGenre>,
                                     listFavourite: ArrayList<Int>) {
        val mDatabaseManager = DatabaseManager(mView?.getContext())
        mView?.showFavouriteMovies(formatMovieList(
                mDatabaseManager.getJsonFavourite(), listGenre, listFavourite
        ))
    }

    private fun formatMovieList(response: String,
                                listGenre: ArrayList<ModelGenre>,
                                listFavourite: ArrayList<Int>)
            : ArrayList<ModelMovie> {
        val movieList = ArrayList<ModelMovie>()
        val typeToken = object : TypeToken<List<ModelMovie>>() {}.type
        val resultList = Gson().fromJson<List<ModelMovie>>(response, typeToken)
        for (movie in resultList) {
            // Set Genre
            val genreIdSize = movie.genreIds?.size ?: 0
            var genres = ""
            for (i in 0 until genreIdSize) {
                var genreName = ""
                for (j in 0 until listGenre.size) {
                    if (movie.genreIds?.get(i) == listGenre.get(j).id) {
                        genreName = listGenre.get(j).name.toString()
                    }
                }
                when (i) {
                    0 -> genres = genreName
                    else -> genres = "$genres, $genreName"
                }
            }
            movie.genres = genres
            // Set Favourite
            movie.isFavourite = listFavourite.contains(movie.id)
            // Add movie to list
            movieList.add(movie)
        }
        return movieList
    }

    override fun loadListFavouriteID() {
        val mDatabaseManager = DatabaseManager(mView?.getContext())
        mView?.setListFavouritesID(mDatabaseManager.getListFavouriteID())
    }

    override fun setFavouriteMovie(model: ModelMovie?) {
        val mDatabaseManager = DatabaseManager(mView?.getContext())
        val isFavourite = mDatabaseManager.isFavourite(model?.id)
        if (isFavourite) {
            mDatabaseManager.removeFavourite(model?.id)
        } else {
            mDatabaseManager.addFavourite(model?.id, Gson().toJson(model).toString())
        }
        mView?.setFavouriteIcon(model?.id, !isFavourite)
    }
}