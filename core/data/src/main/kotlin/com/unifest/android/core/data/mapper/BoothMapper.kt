package com.unifest.android.core.data.mapper

import com.unifest.android.core.model.AllBoothsModel
import com.unifest.android.core.model.BoothDetailModel
import com.unifest.android.core.model.MenuModel
import com.unifest.android.core.network.response.AllBooths
import com.unifest.android.core.network.response.BoothDetail
import com.unifest.android.core.network.response.Menu

internal fun BoothDetail.toDetailModel(): BoothDetailModel {
    return BoothDetailModel(
        id = id,
        name = name,
        category = category,
        description = description,
        thumbnail = thumbnail,
        warning = warning,
        location = location,
        latitude = latitude,
        longitude = longitude,
        menus = menus.map { it.toModel() },
        likes = likes,
        isLiked = false,
    )
}

internal fun Menu.toModel(): MenuModel {
    return MenuModel(
        id = id,
        name = name,
        price = price,
        imgUrl = imgUrl,
    )
}

internal fun BoothDetail.toAllModel(): AllBoothsModel {
    return AllBoothsModel(
        id = id,
        name = name,
        category = category,
        description = description,
        thumbnail = thumbnail,
        location = location,
        latitude = latitude,
        longitude = longitude,
    )
}

internal fun AllBooths.toModel(): AllBoothsModel {
    return AllBoothsModel(
        id = id,
        name = name,
        category = category,
        description = description,
        thumbnail = thumbnail,
        location = location,
        latitude = latitude,
        longitude = longitude,
    )
}
