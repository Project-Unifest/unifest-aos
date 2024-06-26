package com.unifest.android.core.designsystem

import com.naver.maps.map.overlay.OverlayImage

enum class MarkerCategory(val value: String) {
    BAR("BAR"),
    FOOD("FOOD"),
    EVENT("EVENT"),
    NORMAL("NORMAL"),
    MEDICAL("MEDICAL"),
    TOILET("TOILET"),
    ;

    companion object {
        fun fromString(value: String): MarkerCategory {
            return entries.find { it.value == value } ?: NORMAL
        }
    }

    fun getMarkerIcon(isSelected: Boolean): OverlayImage {
        if (isSelected) {
            return when (this) {
                BAR -> OverlayImage.fromResource(R.drawable.ic_marker_bar_selected)
                FOOD -> OverlayImage.fromResource(R.drawable.ic_marker_food_selected)
                EVENT -> OverlayImage.fromResource(R.drawable.ic_marker_event_selected)
                NORMAL -> OverlayImage.fromResource(R.drawable.ic_marker_normal_selected)
                MEDICAL -> OverlayImage.fromResource(R.drawable.ic_marker_medical_selected)
                TOILET -> OverlayImage.fromResource(R.drawable.ic_marker_toilet_selected)
            }
        } else {
            return when (this) {
                BAR -> OverlayImage.fromResource(R.drawable.ic_marker_bar)
                FOOD -> OverlayImage.fromResource(R.drawable.ic_marker_food)
                EVENT -> OverlayImage.fromResource(R.drawable.ic_marker_event)
                NORMAL -> OverlayImage.fromResource(R.drawable.ic_marker_normal)
                MEDICAL -> OverlayImage.fromResource(R.drawable.ic_marker_medical)
                TOILET -> OverlayImage.fromResource(R.drawable.ic_marker_toilet)
            }
        }
    }
}
