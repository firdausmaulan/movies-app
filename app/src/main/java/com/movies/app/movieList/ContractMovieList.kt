package com.movies.app.movieList

import com.movies.app.model.ModelGenre
import com.movies.app.model.ModelMovie
import com.movies.app.mvp.BaseMvpPresenter
import com.movies.app.mvp.BaseMvpView

interface ContractMovieList {

    interface View : BaseMvpView {
        fun setListGenre(listGenre: ArrayList<ModelGenre>)

        fun setListFavouritesID(listFavouriteID: ArrayList<Int>)

        fun onSwipeToRefreshPopular()

        fun onSwipeToRefreshTopRated()

        fun onSwipeToRefreshFavourite()

        fun onScrollToLastIndexPopular(lastIndex: Int?)

        fun onScrollToLastIndexTopRated(lastIndex: Int?)

        fun showPopularMovies(listMovie: ArrayList<ModelMovie>)

        fun showTopRatedMovies(listMovie: ArrayList<ModelMovie>)

        fun showFavouriteMovies(listMovie: ArrayList<ModelMovie>)

        fun onClickFavourite(model: ModelMovie?)

        fun setFavouriteIcon(movieId: Int?, isFavourite: Boolean?)
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadGenre()

        fun loadListFavouriteID()

        fun loadPopularMovies(listGenre: ArrayList<ModelGenre>,
                              listFavourite: ArrayList<Int>,
                              lastIndex: Int?)

        fun loadTopRatedMovies(listGenre: ArrayList<ModelGenre>,
                               listFavourite: ArrayList<Int>,
                               lastIndex: Int?)

        fun loadFavouriteMovies(listGenre: ArrayList<ModelGenre>,
                                listFavourite: ArrayList<Int>)

        fun setFavouriteMovie(model: ModelMovie?)
    }
}