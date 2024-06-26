package com.unifest.android.core.model

data class LikedBoothModel(
    val id: Long = 0L,
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val location: String = "",
    val latitude: Float = 0F,
    val longitude: Float = 0F,
    val warning: String = "",
    val isLiked: Boolean = true,
)
