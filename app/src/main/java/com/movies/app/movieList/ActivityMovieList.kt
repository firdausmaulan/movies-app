package com.movies.app.movieList

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.movieList.model.ModelGenre
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONException


class ActivityMovieList : AppCompatActivity(), ViewMovieList {

    private var mPresenter: PresenterMovieList? = null
    private var mRequestQueue: RequestQueue? = null
    private var mVpAdapter: ViewPagerAdapter? = null
    var mListGenre = ArrayList<ModelGenre>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        doAsync {
            mRequestQueue = Volley.newRequestQueue(this@ActivityMovieList)
            mPresenter = PresenterMovieList(this@ActivityMovieList)
            mPresenter?.getGenreName(mRequestQueue)
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        mVpAdapter = ViewPagerAdapter(supportFragmentManager)
        mVpAdapter?.addFragment(FragmentPopular(), getString(R.string.popular))
        mVpAdapter?.addFragment(FragmentTopRated(), getString(R.string.top_rated))
        mVpAdapter?.addFragment(FragmentFavourite(), getString(R.string.favourite))
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = mVpAdapter
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }

    // Request
    override fun onSuccessGetGenre(response: JSONArray?) {
        val size = response?.length() ?: 0
        for (i in 0 until size) {
            try {
                val jsonGenre = response?.getJSONObject(i)
                val genre = ModelGenre()
                genre.id = jsonGenre?.getInt("id")
                genre.name = jsonGenre?.getString("name")
                mListGenre.add(genre)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        setupViewPager(vpMovieList)
        tlMovieList.setupWithViewPager(vpMovieList)
    }

    override fun onRequestPopular(lastIndex: Int?) {
        mPresenter?.getPopularMovies(mRequestQueue, lastIndex)
    }

    override fun onRequestTopRated(lastIndex: Int?) {
        mPresenter?.getTopRatedMovies(mRequestQueue, lastIndex)
    }

    // Response
    override fun onSuccessGetPopular(response: String?) {
        (mVpAdapter?.getItem(0) as FragmentPopular).successGetPopular(response)
    }

    override fun onSuccessGetTopRated(response: String?) {
        (mVpAdapter?.getItem(1) as FragmentTopRated).successGetTopRated(response)
    }

    // Error
    override fun onError(message: String?) {
        Snackbar.make(vpMovieList,
                message.toString(),
                Snackbar.LENGTH_LONG).show()
    }

    override fun onError() {
        Snackbar.make(vpMovieList,
                getString(R.string.unable_to_connect),
                Snackbar.LENGTH_LONG).show()
    }
}