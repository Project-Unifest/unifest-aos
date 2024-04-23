package com.unifest.android.feature.menu.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifest.android.core.common.ButtonType
import com.unifest.android.core.common.FestivalUiAction
import com.unifest.android.core.common.UiText
import com.unifest.android.core.data.repository.FestivalRepository
import com.unifest.android.core.data.repository.LikedBoothRepository
import com.unifest.android.core.designsystem.R
import com.unifest.android.core.model.BoothDetailModel
import com.unifest.android.core.model.FestivalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val likedBoothRepository: LikedBoothRepository,
    private val festivalRepository: FestivalRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<MenuUiEvent>()
    val uiEvent: Flow<MenuUiEvent> = _uiEvent.receiveAsFlow()

    init {
        observeLikedFestivals()
        observeLikedBooth()
    }

    fun onMenuUiAction(action: MenuUiAction) {
        when (action) {
            is MenuUiAction.OnAddClick -> setFestivalSearchBottomSheetVisible(true)
            is MenuUiAction.OnShowMoreClick -> navigateToLikedBooth()
            is MenuUiAction.OnContactClick -> navigateToContact()
            is MenuUiAction.OnLikedBoothItemClick -> navigateToBoothDetail(action.boothId)
            is MenuUiAction.OnToggleBookmark -> deleteLikedBooth(action.booth)
        }
    }

    fun onFestivalUiAction(action: FestivalUiAction) {
        when (action) {
            is FestivalUiAction.OnDismiss -> setFestivalSearchBottomSheetVisible(false)
            is FestivalUiAction.OnSearchTextUpdated -> updateSearchText(action.text)
            is FestivalUiAction.OnSearchTextCleared -> clearSearchText()
            is FestivalUiAction.OnEnableSearchMode -> setEnableSearchMode(action.flag)
            is FestivalUiAction.OnEnableEditMode -> setEnableEditMode()
            is FestivalUiAction.OnAddClick -> addLikeFestival(action.festival)
            is FestivalUiAction.OnDeleteIconClick -> setLikedFestivalDeleteDialogVisible(true)
            is FestivalUiAction.OnDialogButtonClick -> {
                when (action.type) {
                    ButtonType.CONFIRM -> {
                        setLikedFestivalDeleteDialogVisible(false)
                        action.festival?.let { deleteLikedFestival(it) }
                    }
                    ButtonType.CANCEL -> setLikedFestivalDeleteDialogVisible(false)
                }
            }
        }
    }

    private fun observeLikedFestivals() {
        viewModelScope.launch {
            festivalRepository.getLikedFestivals().collect { likedFestivalList ->
                _uiState.update {
                    it.copy(
                        likedFestivals = likedFestivalList.toMutableList(),
                    )
                }
            }
        }
    }

    private fun observeLikedBooth() {
        viewModelScope.launch {
            likedBoothRepository.getLikedBoothList().collect { likedBoothList ->
                _uiState.update {
                    it.copy(
                        likedBoothList = likedBoothList.toImmutableList(),
                    )
                }
            }
        }
    }

    private fun navigateToLikedBooth() {
        viewModelScope.launch {
            _uiEvent.send(MenuUiEvent.NavigateToLikedBooth)
        }
    }

    private fun navigateToContact() {
        viewModelScope.launch {
            _uiEvent.send(MenuUiEvent.NavigateToContact)
        }
    }

    private fun addLikeFestival(festival: FestivalModel) {
        viewModelScope.launch {
            festivalRepository.insertLikedFestivalAtSearch(festival)
        }
    }

    private fun navigateToBoothDetail(boothId: Long) {
        viewModelScope.launch {
            _uiEvent.send(MenuUiEvent.NavigateToBoothDetail(boothId))
        }
    }

    private fun deleteLikedBooth(booth: BoothDetailModel) {
        viewModelScope.launch {
            updateLikedBooth(booth)
            delay(500)
            likedBoothRepository.deleteLikedBooth(booth)
            _uiEvent.send(MenuUiEvent.ShowSnackBar(UiText.StringResource(R.string.booth_bookmark_removed_message)))
        }
    }

    private suspend fun updateLikedBooth(booth: BoothDetailModel) {
        likedBoothRepository.updateLikedBooth(booth.copy(isLiked = false))
    }

    private fun updateSearchText(text: TextFieldValue) {
        _uiState.update {
            it.copy(festivalSearchText = text)
        }
    }

    private fun clearSearchText() {
        _uiState.update {
            it.copy(festivalSearchText = TextFieldValue())
        }
    }

    private fun setFestivalSearchBottomSheetVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isFestivalSearchBottomSheetVisible = flag)
        }
    }

    private fun setEnableSearchMode(flag: Boolean) {
        _uiState.update {
            it.copy(isSearchMode = flag)
        }
    }

    private fun setEnableEditMode() {
        _uiState.update {
            it.copy(isEditMode = !_uiState.value.isEditMode)
        }
    }

    private fun setLikedFestivalDeleteDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isLikedFestivalDeleteDialogVisible = flag)
        }
    }

    private fun deleteLikedFestival(festival: FestivalModel) {
        viewModelScope.launch {
            festivalRepository.deleteLikedFestival(festival)
        }
    }

    //    init {
//        _uiState.update { currentState ->
//            currentState.copy(
//                likedFestivals = mutableListOf(
//                    Festival("https://picsum.photos/36", "서울대학교", "설대축제", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "연세대학교", "연대축제", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "고려대학교", "고대축제", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "건국대학교", "녹색지대", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "성균관대학교", "성대축제", "05.06-05.08"),
//                ),
//                festivalSearchResults = persistentListOf(
//                    Festival("https://picsum.photos/36", "서울대학교", "설대축제", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "연세대학교", "연대축제", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "고려대학교", "고대축제", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "건국대학교", "녹색지대", "05.06-05.08"),
//                    Festival("https://picsum.photos/36", "성균관대학교", "성대축제", "05.06-05.08"),
//                ),
//                // 임시 데이터
//                festivals = persistentListOf(
//                    Festival("school_image_url_1", "서울대학교", "설대축제", "05.06-05.08"),
//                    Festival("school_image_url_2", "연세대학교", "연대축제", "05.06-05.08"),
//                    Festival("school_image_url_3", "고려대학교", "고대축제", "05.06-05.08"),
//                    Festival("school_image_url_4", "건국대학교", "녹색지대", "05.06-05.08"),
//                    Festival("school_image_url_5", "성균관대", "성대축제", "05.06-05.08"),
//                ),
//                likedBoothList = persistentListOf(
//                    BoothDetailEntity(
//                        id = 1,
//                        name = "부스1",
//                        category = "카페",
//                        description = "부스1 설명",
//                        warning = "부스1 주의사항",
//                        location = "부스1 위치",
//                        latitude = 37.5665f,
//                        longitude = 126.9780f,
//                        menus = listOf(),
//                    ),
//                    BoothDetailEntity(
//                        id = 2,
//                        name = "부스2",
//                        category = "카페",
//                        description = "부스2 설명",
//                        warning = "부스2 주의사항",
//                        location = "부스2 위치",
//                        latitude = 37.5665f,
//                        longitude = 126.9780f,
//                        menus = listOf(),
//                    ),
//                    BoothDetailEntity(
//                        id = 3,
//                        name = "부스3",
//                        category = "카페",
//                        description = "부스3 설명",
//                        warning = "부스3 주의사항",
//                        location = "부스3 위치",
//                        latitude = 37.5665f,
//                        longitude = 126.9780f,
//                        menus = listOf(),
//                    ),
//                ),
//            )
//        }
//    }
}
