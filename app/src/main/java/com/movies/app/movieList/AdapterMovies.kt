package com.movies.app.movieList

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.movies.app.BuildConfig
import com.movies.app.R
import com.movies.app.database.DatabaseManager
import com.movies.app.movieList.model.ModelMovie
import kotlinx.android.synthetic.main.adapter_movie_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class AdapterMovies(val context: Context?,
                    private val mMovieList: ArrayList<ModelMovie>,
                    favourites: ArrayList<Int>)
    : RecyclerView.Adapter<AdapterMovies.ViewHolder>() {

    private var clickListener: ClickListener? = null
    private var mDatabaseManager: DatabaseManager? = null
    private var mFavourite = favourites
    private val requestOption = RequestOptions().centerCrop()
            .placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mDatabaseManager = DatabaseManager(context)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_movie_list, parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        context?.let {
            holder.ivMovie?.let { imageView ->
                Glide.with(it).load(BuildConfig.SOURCE_URL + mMovieList[position].posterPath)
                        .apply(requestOption)
                        .into(imageView)
            }
        }
        holder.tvTitle?.text = mMovieList[position].title
        holder.tvTitle?.isSelected = true
        holder.tvGenre?.text = mMovieList[position].genres.toString()
        if (mFavourite.contains(mMovieList[position].id)) {
            holder.ivFavourite?.setImageResource(R.drawable.ic_favorite)
        } else {
            holder.ivFavourite?.setImageResource(R.drawable.ic_unfavorite)
        }
        holder.ivFavourite?.setOnClickListener {
            doAsync {
                if (mFavourite.contains(mMovieList[position].id)) {
                    mDatabaseManager?.removeFavourite(mMovieList[position].id)
                } else {
                    mDatabaseManager?.addFavourite(
                            mMovieList[position].id,
                            Gson().toJson(mMovieList[position]).toString()
                    )
                }
                mDatabaseManager?.getListFavouriteId()?.let { mFavourite = it }
                uiThread {
                    notifyItemChanged(position)
                }
            }
        }
        holder.lytParent?.setOnClickListener {
            holder.ivMovie?.let { imageView ->
                clickListener?.onItemClick(position, imageView, mMovieList[position].id)
            }
        }
    }

    override fun getItemCount(): Int = mMovieList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMovie: AppCompatImageView? = itemView.ivMovie
        val tvTitle: AppCompatTextView? = itemView.tvTitle
        val ivFavourite: AppCompatImageView? = itemView.ivFavourite
        val tvGenre: AppCompatTextView? = itemView.tvGenre
        val lytParent: CardView? = itemView.lytParent
    }

    fun setOnItemClickListener(mClickListener: ClickListener) {
        clickListener = mClickListener
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View, id: Int?)
    }
}