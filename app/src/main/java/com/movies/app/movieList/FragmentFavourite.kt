package com.movies.app.movieList

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.movieDetail.ActivityMovieDetail
import com.movies.app.movieList.model.ModelMovie
import kotlinx.android.synthetic.main.fragment_movie_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class FragmentFavourite : Fragment() {

    private var mMovieList: ArrayList<ModelMovie> = ArrayList()
    private var mListFavourite = ArrayList<Int>()
    private var mAdapterMovies: AdapterMovies? = null
    private var mDatabaseManager: DatabaseManager? = null

    private var mViewMovieList: ViewMovieList? = null
    private var mView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewMovieList = (context as ViewMovieList)
    }

    override fun onDetach() {
        super.onDetach()
        mViewMovieList = null
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        mView = view
        mDatabaseManager = DatabaseManager(activity)
        setRecycleView()
        onRefresh()
        view.srlMovieList?.setOnRefreshListener { onRefresh() }
        return view
    }

    private fun onRefresh() {
        mView?.srlMovieList?.isRefreshing = true
        mMovieList.clear()
        mListFavourite.clear()
        onLoadFavourite()
    }

    private fun setRecycleView() {
        val glm = GridLayoutManager(activity, 2)
        mView?.rvMovieList?.layoutManager = glm
        mAdapterMovies = AdapterMovies(activity, mMovieList, mListFavourite)
        mView?.rvMovieList?.adapter = mAdapterMovies
        // handle event onItemClick and onClickFavourite
        mAdapterMovies?.setOnItemClickListener(object : AdapterMovies.ClickListener {
            override fun onItemClick(position: Int, v: View, id: Int?) {
                val intent = Intent(activity, ActivityMovieDetail::class.java)
                intent.putExtra("id", id)
                if (Build.VERSION.SDK_INT >= 21) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity as ActivityMovieList, v, ViewCompat.getTransitionName(v))
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(intent)
                }
            }
        })
    }

    private fun onLoadFavourite() {
        doAsync {
            mDatabaseManager?.getListFavouriteId()?.let { mListFavourite = it }
            val listJsonData = mDatabaseManager?.getListFavourite()
            val size = listJsonData?.size ?: 0
            val listGenreSize = (activity as ActivityMovieList).mListGenre.size
            for (i in 0 until size) {
                val movie = Gson().fromJson<ModelMovie>(listJsonData?.get(i), ModelMovie::class.java)
                val genreIdSize = movie.genreIds?.size ?: 0
                var genres = ""
                for (j in 0 until genreIdSize) {
                    var genreName = ""
                    for (k in 0 until listGenreSize) {
                        if (movie.genreIds?.get(j) ==
                                (activity as ActivityMovieList).mListGenre.get(k).id) {
                            genreName = (activity as ActivityMovieList)
                                    .mListGenre.get(k).name.toString()
                        }
                    }
                    when (j) {
                        0 -> genres = genreName
                        else -> genres = "$genres, $genreName"
                    }
                }
                movie.genres = genres
                mMovieList.add(movie)
            }
            uiThread {
                setRecycleView()
                mView?.srlMovieList?.isRefreshing = false
                mAdapterMovies?.notifyDataSetChanged()
            }
        }
    }

}