package com.movies.app.movieDetail

import com.movies.app.model.ModelDetailMovie
import com.movies.app.model.ModelMovie
import com.movies.app.model.ModelTrailer
import com.movies.app.mvp.BaseMvpPresenter
import com.movies.app.mvp.BaseMvpView

object ContractMovieDetail {

    interface View : BaseMvpView {
        fun showMovieDetail(movie: ModelDetailMovie?)

        fun showMovieTrailer(trailer: ModelTrailer?)

        fun showFavourite(isFavourite: Boolean)
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadFavourite(movieId: Int?)

        fun loadMovieDetail(movieId: Int?)

        fun loadMovieTrailer(movieId: Int?)

        fun setFavouriteMovie(model: ModelMovie?)
    }
}