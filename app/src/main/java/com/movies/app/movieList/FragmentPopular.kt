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
import com.movies.app.R
import com.movies.app.movieDetail.ActivityMovieDetail
import com.movies.app.model.ModelMovie
import kotlinx.android.synthetic.main.fragment_movie_list.view.*
import android.support.v7.widget.RecyclerView
import com.movies.app.util.RecyclerUtil

class FragmentPopular : Fragment() {

    private var mMovieList: ArrayList<ModelMovie> = ArrayList()
    private var mAdapterMovies: AdapterMovies? = null
    private var mLastIndex = 1
    private var mAllowedToRequest = true

    private var mViewMovieList: ContractMovieList.View? = null
    private var mView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewMovieList = (context as ContractMovieList.View)
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
        setRecycleView()
        view.srlMovieList?.setOnRefreshListener { onRefresh() }
        onRefresh()
        return view
    }

    private fun onRefresh() {
        mView?.srlMovieList?.isRefreshing = true
        mLastIndex = 1
        mMovieList.clear()
        mViewMovieList?.onFragmentPopularLoad(mLastIndex)
    }

    private fun setRecycleView() {
        val glm = GridLayoutManager(activity, 2)
        mView?.rvMovieList?.layoutManager = glm
        mAdapterMovies = AdapterMovies(activity, mMovieList)
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

            override fun onFavouriteClick(position: Int, v: View, model: ModelMovie?) {
                mViewMovieList?.onClickFavourite(model)
            }
        })
        // read last recycle view item for load more
        mView?.rvMovieList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (RecyclerUtil.isLoadMore(dy, glm, mAllowedToRequest)) {
                    loadMore()
                }
            }
        })
    }

    private fun loadMore() {
        mView?.srlMovieList?.isRefreshing = true
        mAllowedToRequest = false
        mViewMovieList?.onFragmentPopularLoad(mLastIndex)
    }

    fun showPopularMovies(listMovie: ArrayList<ModelMovie>) {
        mMovieList.addAll(listMovie)
        mView?.srlMovieList?.isRefreshing = false
        mAdapterMovies?.notifyDataSetChanged()
        mAllowedToRequest = true
        mLastIndex++
    }

    fun setFavouriteIcon(movieId: Int?, isFavourite: Boolean?) {
        for (i in 0 until mMovieList.size) {
            if (mMovieList[i].id == movieId) {
                mMovieList[i].isFavourite = isFavourite
                mAdapterMovies?.notifyItemChanged(i)
                break
            }
        }
    }
}