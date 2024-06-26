package com.unifest.android.feature.festival.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.unifest.android.core.model.FestivalModel

sealed interface FestivalUiAction {
    data object OnAddLikedFestivalClick : FestivalUiAction
    data object OnDismiss : FestivalUiAction
    data class OnSearchTextUpdated(val searchText: TextFieldValue) : FestivalUiAction
    data object OnSearchTextCleared : FestivalUiAction
    data object OnTooltipClick : FestivalUiAction
    data class OnEnableSearchMode(val flag: Boolean) : FestivalUiAction
    data object OnEnableEditMode : FestivalUiAction
    data class OnLikedFestivalSelected(val festival: FestivalModel) : FestivalUiAction
    data class OnAddClick(val festival: FestivalModel) : FestivalUiAction
    data class OnDeleteIconClick(val deleteSelectedFestival: FestivalModel) : FestivalUiAction
    data class OnDeleteDialogButtonClick(val buttonType: ButtonType) : FestivalUiAction
}

enum class ButtonType {
    CONFIRM,
    CANCEL,
}
