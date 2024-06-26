package com.unifest.android.core.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileResponse(
    @SerialName("imgUrl")
    val imgUrl: String,
    @SerialName("imageName")
    val imgName: String,
)
