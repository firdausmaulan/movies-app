package com.movies.app.movieDetail

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import com.movies.app.BuildConfig
import com.movies.app.R
import com.movies.app.model.ModelDetailMovie
import com.movies.app.model.ModelTrailer
import com.movies.app.mvp.BaseMvpActivity
import com.movies.app.util.loadImage
import kotlinx.android.synthetic.main.activity_movie_detail.*
import android.support.design.widget.AppBarLayout


class ActivityMovieDetail : BaseMvpActivity<ContractMovieDetail.View,
        ContractMovieDetail.Presenter>(), ContractMovieDetail.View {

    override var mPresenter: ContractMovieDetail.Presenter = PresenterMovieDetail()
    private var mMovieId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent?.extras?.let { mMovieId = it.getInt("id") }
        mPresenter.loadFavourite(mMovieId)
        mPresenter.loadMovieDetail(mMovieId)
        mPresenter.loadMovieTrailer(mMovieId)
    }

    override fun showFavourite(isFavourite: Boolean) {
        if (isFavourite) ivFavourite.setImageResource(R.drawable.ic_favorite)
    }

    override fun showMovieDetail(movie: ModelDetailMovie?) {
        loadImage(ivBackdrop, movie?.backdropPath)
        loadImage(ivPoster, movie?.posterPath)
        tvTitle.text = movie?.title
        tvRating.text = "${movie?.voteAverage}/10"
        tvVotes.text = "${movie?.voteCount} ${getString(R.string.votes)}"
        tvDate.text = movie?.releaseDate
        tvLanguage.text = movie?.originalLanguage
        tvOverview.text = movie?.overview
        setToolbarTitle(movie?.title)
    }

    private fun setToolbarTitle(title: String?) {
        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsToolbar.title = title
                } else if (isShow) {
                    collapsToolbar.title = ""
                }
            }
        })
    }

    override fun showMovieTrailer(trailer: ModelTrailer?) {
        lytTrailer.visibility = View.VISIBLE
        ivPlayTrailer.setOnClickListener {
            ivPlayTrailer.visibility = View.GONE
            wvTrailer.webViewClient = WebViewClient()
            wvTrailer.settings.javaScriptEnabled = true
            wvTrailer.loadUrl(BuildConfig.VIDEO_URL + trailer?.results?.get(0)?.key)
        }
    }

    // Error
    override fun showError(error: String?) {
        Snackbar.make(lytParent,
                error.toString(),
                Snackbar.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}