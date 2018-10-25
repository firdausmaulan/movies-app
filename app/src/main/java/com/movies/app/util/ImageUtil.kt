package com.movies.app.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.movies.app.App
import com.movies.app.BuildConfig
import com.movies.app.R

object ImageUtil {
    private val requestOption = RequestOptions().centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)

    fun loadImage(imageView: ImageView?, path: String?) {
        imageView?.let { view ->
            Glide.with(App.getContext())
                    .load(BuildConfig.SOURCE_URL + path)
                    .apply(requestOption)
                    .into(view)
        }
    }
}