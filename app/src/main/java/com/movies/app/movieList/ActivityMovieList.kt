package com.movies.app.movieList

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.movies.app.R
import com.movies.app.model.ModelGenre
import com.movies.app.model.ModelMovie
import com.movies.app.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_movie_list.*


class ActivityMovieList : BaseMvpActivity<ContractMovieList.View,
        ContractMovieList.Presenter>(), ContractMovieList.View {

    override var mPresenter: ContractMovieList.Presenter = PresenterMovieList()
    private var mVpAdapter: ViewPagerAdapter? = null
    private var mListGenre = ArrayList<ModelGenre>()
    private var mListFavouriteID = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        mPresenter.loadGenre()
        mPresenter.loadListFavouriteID()
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

    override fun onSwipeToRefreshPopular() {
        mPresenter.loadPopularMovies(mListGenre, mListFavouriteID, 1)
    }

    override fun onScrollToLastIndexPopular(lastIndex: Int?) {
        mPresenter.loadPopularMovies(mListGenre, mListFavouriteID, lastIndex)
    }

    override fun onSwipeToRefreshTopRated() {
        mPresenter.loadTopRatedMovies(mListGenre, mListFavouriteID, 1)
    }

    override fun onScrollToLastIndexTopRated(lastIndex: Int?) {
        mPresenter.loadTopRatedMovies(mListGenre, mListFavouriteID, lastIndex)
    }

    override fun onSwipeToRefreshFavourite() {
        mPresenter.loadFavouriteMovies(mListGenre, mListFavouriteID)
    }

    override fun onClickFavourite(model: ModelMovie?) {
        mPresenter.setFavouriteMovie(model)
    }

    override fun setFavouriteIcon(movieId: Int?, isFavourite: Boolean?) {
        mPresenter.loadListFavouriteID()
        (mVpAdapter?.getItem(0) as FragmentPopular).setFavouriteIcon(movieId, isFavourite)
        (mVpAdapter?.getItem(1) as FragmentTopRated).setFavouriteIcon(movieId, isFavourite)
        (mVpAdapter?.getItem(2) as FragmentFavourite).setFavouriteIcon(movieId, isFavourite)
    }

    override fun setListGenre(listGenre: ArrayList<ModelGenre>) {
        mListGenre = listGenre
        setupViewPager(vpMovieList)
        tlMovieList.setupWithViewPager(vpMovieList)
    }

    override fun setListFavouritesID(listFavouriteID: ArrayList<Int>) {
        mListFavouriteID = listFavouriteID
    }

    override fun showPopularMovies(listMovie: ArrayList<ModelMovie>) {
        (mVpAdapter?.getItem(0) as FragmentPopular).showPopularMovies(listMovie)
    }


    override fun showTopRatedMovies(listMovie: ArrayList<ModelMovie>) {
        (mVpAdapter?.getItem(1) as FragmentTopRated).showTopRatedMovies(listMovie)
    }

    override fun showFavouriteMovies(listMovie: ArrayList<ModelMovie>) {
        (mVpAdapter?.getItem(2) as FragmentFavourite).showFavouriteMovies(listMovie)
    }

    // Error
    override fun showError(error: String?) {
        Snackbar.make(vpMovieList,
                error.toString(),
                Snackbar.LENGTH_LONG).show()
    }
}