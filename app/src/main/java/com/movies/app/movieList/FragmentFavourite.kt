package com.movies.app.movieList

import android.app.Activity
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
import android.support.v4.content.ContextCompat
import com.movies.app.util.Constants


class FragmentFavourite : Fragment() {

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
        return view
    }

    private fun setSwipeRefreshLayout() {
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

    private fun setAction() {
        mView.srlMovieList?.setOnRefreshListener { onRefresh() }

        // handle event onItemClick and onClickFavourite
        mAdapterMovies.setOnItemClickListener(object : AdapterMovies.ClickListener {
            override fun onItemClick(position: Int, v: View, model: ModelMovie?) {
                val intent = Intent(activity, ActivityMovieDetail::class.java)
                val bundle = Bundle()
                bundle.putSerializable(Constants.TAG_MODEL, model)
                intent.putExtras(bundle)
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
    }

    private fun onRefresh() {
        mView.srlMovieList?.isRefreshing = true
        mMovieList.clear()
        mViewMovieList?.onSwipeToRefreshFavourite()
    }

    fun showFavouriteMovies(listMovie: ArrayList<ModelMovie>) {
        mMovieList.addAll(listMovie)
        mView.srlMovieList?.isRefreshing = false
        mAdapterMovies.notifyDataSetChanged()
        mAllowedToRequest = true
        mLastIndex++
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            onRefresh()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_DETAIL) {
                val movieId = data?.extras?.getInt(Constants.TAG_MOVIE_ID)
                val isFavourite = data?.extras?.getBoolean(Constants.TAG_IS_FAVOURITE)
                mViewMovieList?.setFavouriteIcon(movieId, isFavourite)
            }
        }
    }
}