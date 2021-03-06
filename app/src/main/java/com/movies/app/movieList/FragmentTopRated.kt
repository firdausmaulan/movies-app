package com.movies.app.movieList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.movies.app.R
import com.movies.app.movieDetail.ActivityMovieDetail
import com.movies.app.model.ModelMovie
import com.movies.app.util.Constants
import com.movies.app.util.RecyclerUtil
import kotlinx.android.synthetic.main.fragment_movie_list.view.*

class FragmentTopRated : Fragment() {

    private var mMovieList: ArrayList<ModelMovie> = ArrayList()
    private lateinit var mAdapterMovies: AdapterMovies
    private var mLastIndex = 1
    private var mAllowedToRequest = true

    private var mViewMovieList: ContractMovieList.View? = null
    private lateinit var mView: View

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
        setSwipeRefreshLayout()
        setAction()
        onRefresh()
        return view
    }

    private fun setSwipeRefreshLayout(){
        activity?.let { ContextCompat.getColor(it, R.color.colorAccent) }?.let {
            mView.srlMovieList?.setColorSchemeColors(it)
        }
    }

    private fun setRecycleView() {
        val glm = GridLayoutManager(activity, 2)
        mView.rvMovieList?.layoutManager = glm
        mAdapterMovies = AdapterMovies(activity, mMovieList)
        mView.rvMovieList?.adapter = mAdapterMovies
    }

    private fun setAction(){
        mView.srlMovieList?.setOnRefreshListener { onRefresh() }

        // handle event onItemClick and onClickFavourite
        mAdapterMovies.setOnItemClickListener(object : AdapterMovies.ClickListener {
            override fun onItemClick(position: Int, v: View, model: ModelMovie?) {
                val intent = Intent(activity, ActivityMovieDetail::class.java)
                intent.putExtra(Constants.TAG_MODEL, model)
                if (Build.VERSION.SDK_INT >= 21) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity as ActivityMovieList, v, ViewCompat.getTransitionName(v))
                    startActivityForResult(intent, Constants.REQUEST_CODE_DETAIL, options.toBundle())
                } else {
                    startActivityForResult(intent, Constants.REQUEST_CODE_DETAIL)
                }
            }

            override fun onFavouriteClick(position: Int, v: View, model: ModelMovie?) {
                mViewMovieList?.onClickFavourite(model)
            }
        })
        // read last recycle view item for load more
        mView.rvMovieList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView?.layoutManager as GridLayoutManager
                if (RecyclerUtil.isLoadMore(dy, layoutManager, mAllowedToRequest)) {
                    loadMore()
                }
            }
        })
    }

    private fun onRefresh() {
        mView.srlMovieList?.isRefreshing = true
        mLastIndex = 1
        mMovieList.clear()
        mViewMovieList?.onSwipeToRefreshTopRated()
    }

    private fun loadMore() {
        mView.srlMovieList?.isRefreshing = true
        mAllowedToRequest = false
        mViewMovieList?.onScrollToLastIndexTopRated(mLastIndex)
    }

    fun showTopRatedMovies(listMovie: ArrayList<ModelMovie>) {
        mMovieList.addAll(listMovie)
        mLastIndex++
        mView.srlMovieList?.isRefreshing = false
        mAdapterMovies.notifyDataSetChanged()
        mAllowedToRequest = true
    }

    fun setFavouriteIcon(movieId: Int?, isFavourite: Boolean?) {
        for (i in 0 until mMovieList.size) {
            if (mMovieList[i].id == movieId) {
                mMovieList[i].isFavourite = isFavourite
                mAdapterMovies.notifyItemChanged(i)
                break
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.REQUEST_CODE_DETAIL){
                val movieId = data?.extras?.getInt(Constants.TAG_MOVIE_ID)
                val isFavourite = data?.extras?.getBoolean(Constants.TAG_IS_FAVOURITE)
                mViewMovieList?.setFavouriteIcon(movieId, isFavourite)
            }
        }
    }
}