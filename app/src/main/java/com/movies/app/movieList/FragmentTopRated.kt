package com.movies.app.movieList

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.movieDetail.ActivityMovieDetail
import com.movies.app.movieList.model.ModelMovie
import kotlinx.android.synthetic.main.fragment_movie_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class FragmentTopRated : Fragment() {

    private var mMovieList: ArrayList<ModelMovie> = ArrayList()
    private var mFavourite: ArrayList<Int> = ArrayList()
    private var mAdapterMovies: AdapterMovies? = null
    private val mListId = LinkedHashSet<String>()
    private var mLastIndex = 1
    private var mAllowedToRequest = true
    private var mListFavourite = ArrayList<Int>()
    private var mDatabaseManager : DatabaseManager? = null

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
        doAsync {
            mDatabaseManager = DatabaseManager(activity)
            mDatabaseManager?.getListFavouriteId()?.let { mListFavourite = it }
            uiThread {
                setRecycleView()
                onRefresh()
                view.srlMovieList?.setOnRefreshListener { onRefresh() }
            }
        }
        return view
    }

    private fun onRefresh() {
        doAsync {
            mDatabaseManager = DatabaseManager(activity)
            mDatabaseManager?.getListFavouriteId()?.let { mListFavourite = it }
            uiThread {
                setRecycleView()
                mView?.srlMovieList?.isRefreshing = true
                mLastIndex = 1
                mMovieList.clear()
                mListId.clear()
                mViewMovieList?.onRequestTopRated(mLastIndex)
            }
        }
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
        // read last recycle view item for load more
        mView?.rvMovieList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = glm.childCount
                    val totalItemCount = glm.itemCount
                    val pastVisiblesItems = glm.findFirstVisibleItemPosition()
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount && mAllowedToRequest) {
                        if (totalItemCount > 0) {
                            mView?.srlMovieList?.isRefreshing = true
                            mAllowedToRequest = false
                            mViewMovieList?.onRequestTopRated(mLastIndex)
                        }
                    }
                }
            }
        })
    }

    fun successGetTopRated(response: String?) {
        doAsync {
            val typeToken = object : TypeToken<List<ModelMovie>>() {}.type
            val resultList = Gson().fromJson<List<ModelMovie>>(response, typeToken)
            val listGenreSize = (activity as ActivityMovieList).mListGenre.size
            for (movie in resultList) {
                if (mListId.add(movie.id.toString())) {
                    val genreIdSize = movie.genreIds?.size ?: 0
                    var genres = ""
                    for (i in 0 until genreIdSize) {
                        var genreName = ""
                        for (j in 0 until listGenreSize) {
                            if (movie.genreIds?.get(i) ==
                                    (activity as ActivityMovieList).mListGenre.get(j).id) {
                                genreName = (activity as ActivityMovieList)
                                        .mListGenre.get(j).name.toString()
                            }
                        }
                        when (i) {
                            0 -> genres = genreName
                            else -> genres = "$genres, $genreName"
                        }
                    }
                    movie.genres = genres
                    mMovieList.add(movie)
                }
            }
            mLastIndex++
            uiThread {
                mView?.srlMovieList?.isRefreshing = false
                mAdapterMovies?.notifyDataSetChanged()
                mAllowedToRequest = true
            }
        }
    }

}