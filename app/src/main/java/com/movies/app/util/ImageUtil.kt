package com.movies.app.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.movies.app.BuildConfig
import com.movies.app.R

private val requestOption = RequestOptions().centerCrop()
        .placeholder(R.mipmap.ic_launcher_round)
        .error(R.mipmap.ic_launcher_round)

fun Context.loadImage(imageView: ImageView?, path: String?) {
    imageView?.let { view ->
        Glide.with(this).load(BuildConfig.SOURCE_URL + path)
                .apply(requestOption)
                .into(view)
    }
}