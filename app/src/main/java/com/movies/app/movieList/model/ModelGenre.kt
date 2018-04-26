package com.movies.app.movieList.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelGenre {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}