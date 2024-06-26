package com.unifest.android.core.data.mapper

import com.unifest.android.core.database.entity.LikedFestivalEntity
import com.unifest.android.core.database.entity.StarInfoEntity
import com.unifest.android.core.model.FestivalModel
import com.unifest.android.core.model.FestivalTodayModel
import com.unifest.android.core.model.StarInfoModel

internal fun LikedFestivalEntity.toModel(): FestivalModel {
    return FestivalModel(
        festivalId = festivalId,
        schoolId = schoolId,
        thumbnail = thumbnail,
        schoolName = schoolName,
        region = region ?: "",
        festivalName = festivalName,
        beginDate = beginDate,
        endDate = endDate,
        latitude = latitude ?: 0.0F,
        longitude = longitude ?: 0.0F,
    )
}

internal fun FestivalTodayModel.toEntity(): LikedFestivalEntity {
    return LikedFestivalEntity(
        festivalId = festivalId,
        schoolName = schoolName,
        festivalName = festivalName,
        beginDate = beginDate,
        endDate = endDate,
        starInfo = starInfo.map { it.toEntity() },
        schoolId = schoolId,
        thumbnail = thumbnail,
    )
}

internal fun StarInfoModel.toEntity(): StarInfoEntity {
    return StarInfoEntity(
        starId = starId,
        name = name,
        imgUrl = imgUrl,
    )
}

internal fun FestivalModel.toEntity(): LikedFestivalEntity {
    return LikedFestivalEntity(
        festivalId = festivalId,
        schoolName = schoolName,
        festivalName = festivalName,
        schoolId = schoolId,
        thumbnail = thumbnail,
        beginDate = beginDate,
        endDate = endDate,
        latitude = latitude,
        longitude = longitude,
    )
}
