package com.movies.app.movieDetail

import com.google.gson.Gson
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.model.ModelDetailMovie
import com.movies.app.model.ModelTrailer
import com.movies.app.mvp.BaseMvpPresenterImpl
import com.androidnetworking.error.ANError
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.movies.app.BuildConfig
import com.movies.app.model.ModelMovie
import com.movies.app.util.API_URL
import org.json.JSONObject


class PresenterMovieDetail : BaseMvpPresenterImpl<ContractMovieDetail.View>(),
        ContractMovieDetail.Presenter {

    override fun loadMovieDetail(movieId: Int?) {
        AndroidNetworking.get(API_URL.DETAIL)
                .addPathParameter("id", movieId.toString())
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.has("id")) {
                            val movie = Gson().fromJson<ModelDetailMovie>(
                                    response.toString(),
                                    ModelDetailMovie::class.java
                            )
                            mView?.showMovieDetail(movie)
                        } else {
                            mView?.showError(R.string.unable_to_connect)
                        }
                    }

                    override fun onError(error: ANError) {
                        mView?.showError(R.string.unable_to_connect)
                    }
                })
    }

    override fun loadMovieTrailer(movieId: Int?) {
        AndroidNetworking.get(API_URL.TRAILER)
                .addPathParameter("id", movieId.toString())
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.has("results")) {
                            val trailer = Gson().fromJson<ModelTrailer>(
                                    response.toString(), ModelTrailer::class.java
                            )
                            mView?.showMovieTrailer(trailer)
                        } else {
                            mView?.showError(R.string.unable_to_connect)
                        }
                    }

                    override fun onError(error: ANError) {
                        mView?.showError(R.string.unable_to_connect)
                    }
                })
    }

    override fun loadFavourite(movieId: Int?) {
        val mDatabaseManager = DatabaseManager(mView?.getContext())
        mView?.showFavourite(mDatabaseManager.isFavourite(movieId))
    }

    override fun setFavouriteMovie(model: ModelMovie?) {
        val mDatabaseManager = DatabaseManager(mView?.getContext())
        val isFavourite = mDatabaseManager.isFavourite(model?.id)
        if (isFavourite) {
            mDatabaseManager.removeFavourite(model?.id)
        } else {
            mDatabaseManager.addFavourite(model?.id, Gson().toJson(model).toString())
        }
        mView?.showFavourite(!isFavourite)
    }
}