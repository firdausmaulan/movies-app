package com.movies.app.movieDetail

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.movies.app.BuildConfig
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.movieDetail.model.ModelDetailMovie
import com.movies.app.movieDetail.model.ModelTrailer
import kotlinx.android.synthetic.main.activity_movie_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject


class ActivityMovieDetail : AppCompatActivity(), ViewMovieDetail {

    private var mPresenter: PresenterMovieDetail? = null
    private var mRequestQueue: RequestQueue? = null
    private var mListFavourite = ArrayList<Int>()
    private var mDatabaseManager: DatabaseManager? = null
    private var mMovieId : Int? = null

    private val requestOption = RequestOptions().centerCrop()
            .placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        doAsync {
            mDatabaseManager = DatabaseManager(this@ActivityMovieDetail)
            mDatabaseManager?.getListFavouriteId()?.let { mListFavourite = it }
            mRequestQueue = Volley.newRequestQueue(this@ActivityMovieDetail)
            mPresenter = PresenterMovieDetail(this@ActivityMovieDetail)
            uiThread {
                intent?.extras?.let {
                    mMovieId = it.getInt("id")
                    mPresenter?.getRequestDetail(mRequestQueue, mMovieId)
                    mPresenter?.getTrailerVideo(mRequestQueue, mMovieId)
                }
            }
        }
    }

    override fun onSuccessGetDetail(response: JSONObject?) {
        val movie = Gson().fromJson<ModelDetailMovie>(
                response.toString(),
                ModelDetailMovie::class.java
        )
        Glide.with(this).load(BuildConfig.SOURCE_URL + movie.backdropPath)
                .apply(requestOption)
                .into(ivBackdrop)
        Glide.with(this).load(BuildConfig.SOURCE_URL + movie.posterPath)
                .apply(requestOption)
                .into(ivPoster)
        tvTitle.text = movie.title
        if (mListFavourite.contains(mMovieId)) ivFavourite.setImageResource(R.drawable.ic_favorite)
        tvRating.text = "${movie.voteAverage}/10"
        tvVotes.text = "${movie.voteCount} ${getString(R.string.votes)}"
        tvDate.text = movie.releaseDate
        tvLanguage.text = movie.originalLanguage
        tvOverview.text = movie.overview
    }

    override fun onSuccessGetTrailerVideo(response: JSONObject?) {
        doAsync {
            val listTrailer = Gson().fromJson<ModelTrailer>(
                    response.toString(), ModelTrailer::class.java
            )
            uiThread {
                lytTrailer.visibility = View.VISIBLE
                wvTrailer.webViewClient = WebViewClient()
                wvTrailer.settings.javaScriptEnabled = true
                wvTrailer.loadUrl(BuildConfig.VIDEO_URL + listTrailer.results?.get(0)?.key)
            }
        }
    }

    // Error
    override fun onError(message: String?) {
        Snackbar.make(lytParent,
                message.toString(),
                Snackbar.LENGTH_LONG).show()
    }

    override fun onError() {
        Snackbar.make(lytParent,
                getString(R.string.unable_to_connect),
                Snackbar.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}