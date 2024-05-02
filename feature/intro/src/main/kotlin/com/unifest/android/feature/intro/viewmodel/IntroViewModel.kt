package com.unifest.android.feature.intro.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifest.android.core.common.ErrorHandlerActions
import com.unifest.android.core.common.handleException
import com.unifest.android.core.data.repository.FestivalRepository
import com.unifest.android.core.data.repository.LikedFestivalRepository
import com.unifest.android.core.data.repository.OnboardingRepository
import com.unifest.android.core.model.FestivalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val festivalRepository: FestivalRepository,
    private val likedFestivalRepository: LikedFestivalRepository,
) : ViewModel(), ErrorHandlerActions {
    private val _uiState = MutableStateFlow(IntroUiState())
    val uiState: StateFlow<IntroUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<IntroUiEvent>()
    val uiEvent: Flow<IntroUiEvent> = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            getAllFestivals()
            if (onboardingRepository.checkIntroCompletion()) {
                _uiEvent.send(IntroUiEvent.NavigateToMain)
            } else {
//                _uiState.update {
//                    it.copy(
//                        festivals = persistentListOf(
//                            FestivalModel(
//                                1,
//                                1,
//                                "https://picsum.photos/36",
//                                "서울대학교",
//                                "설대축제",
//                                "2024-04-21",
//                                "2024-04-23",
//                                126.957f,
//                                37.460f,
//                            ),
//                            FestivalModel(
//                                2,
//                                2,
//                                "https://picsum.photos/36",
//                                "연세대학교",
//                                "연대축제",
//                                "2024-04-21",
//                                "2024-04-23",
//                                126.957f,
//                                37.460f,
//                            ),
//                            FestivalModel(
//                                3,
//                                3,
//                                "https://picsum.photos/36",
//                                "고려대학교",
//                                "고대축제",
//                                "2024-04-21",
//                                "2024-04-23",
//                                126.957f,
//                                37.460f,
//                            ),
//                            FestivalModel(
//                                4,
//                                4,
//                                "https://picsum.photos/36",
//                                "성균관대학교",
//                                "성대축제",
//                                "2024-04-21",
//                                "2024-04-23",
//                                126.957f,
//                                37.460f,
//                            ),
//                            FestivalModel(
//                                5,
//                                5,
//                                "https://picsum.photos/36",
//                                "건국대학교",
//                                "건대축제",
//                                "2024-04-21",
//                                "2024-04-23",
//                                126.957f,
//                                37.460f,
//                            ),
//                        ),
//                    )
//                }
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun onAction(action: IntroUiAction) {
        when (action) {
            is IntroUiAction.OnSearchTextUpdated -> updateSearchText(action.searchText)
            is IntroUiAction.OnSearchTextCleared -> clearSearchText()
            is IntroUiAction.OnSearch -> searchSchool(action.searchText.text)
            is IntroUiAction.OnRegionTapClicked -> searchRegion(action.region)
            is IntroUiAction.OnClearSelectionClick -> clearSelectedFestivals()
            is IntroUiAction.OnFestivalSelected -> addSelectedFestival(action.festival)
            is IntroUiAction.OnFestivalDeselected -> removeSelectedFestivals(action.festival)
            is IntroUiAction.OnAddCompleteClick -> addLikedFestivals()
            is IntroUiAction.OnRetryClick -> refresh(action.error)
        }
    }

    private fun updateSearchText(text: TextFieldValue) {
        _uiState.update {
            it.copy(searchText = text)
        }
    }

    private fun clearSearchText() {
        _uiState.update {
            it.copy(searchText = TextFieldValue())
        }
    }

    private fun getAllFestivals() {
        viewModelScope.launch {
            festivalRepository.getAllFestivals()
                .onSuccess { festivals ->
                    _uiState.update {
                        it.copy(festivals = festivals.toImmutableList())
                    }
                }
                .onFailure { exception ->
                    handleException(exception, this@IntroViewModel)
                }
        }
    }

    private fun searchSchool(searchText: String) {
        if (searchText.isEmpty()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSearchLoading = true)
            }
            festivalRepository.searchSchool(searchText)
                .onSuccess { festivals ->
                    _uiState.update {
                        it.copy(
                            festivals = festivals.toImmutableList(),
                        )
                    }
                }.onFailure { exception ->
                    handleException(exception, this@IntroViewModel)
                }
            _uiState.update {
                it.copy(isSearchLoading = false)
            }
        }
    }

    private fun searchRegion(region: String) {
        viewModelScope.launch {
//            _uiState.update {
//                it.copy(isSearchLoading = true)
//            }
            if (region == "전체") {
                getAllFestivals()
            } else {
                _uiState.update {
                    it.copy(
                        selectedRegion = region,
                    )
                }
                festivalRepository.searchRegion(region)
                    .onSuccess { festivals ->
                        _uiState.update {
                            it.copy(festivals = festivals.toImmutableList())
                        }
                    }.onFailure { exception ->
                        handleException(exception, this@IntroViewModel)
                    }
            }
//            _uiState.update {
//                it.copy(isSearchLoading = false)
//            }
        }
    }

    private fun clearSelectedFestivals() {
        _uiState.update {
            it.copy(selectedFestivals = persistentListOf())
        }
    }

    private fun addSelectedFestival(festival: FestivalModel) {
        _uiState.update {
            it.copy(
                selectedFestivals = it.selectedFestivals.add(festival),
            )
        }
    }

    private fun removeSelectedFestivals(festival: FestivalModel) {
        _uiState.update {
            it.copy(
                selectedFestivals = it.selectedFestivals.remove(festival),
            )
        }
    }

    private fun addLikedFestivals() {
        viewModelScope.launch {
            _uiState.value.selectedFestivals.forEach { festival ->
                likedFestivalRepository.insertLikedFestivalAtSearch(festival)
            }
            onboardingRepository.completeIntro(true)
            _uiEvent.send(IntroUiEvent.NavigateToMain)
        }
    }

    override fun setServerErrorDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isServerErrorDialogVisible = flag)
        }
    }

    override fun setNetworkErrorDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isNetworkErrorDialogVisible = flag)
        }
    }

    private fun refresh(error: ErrorType) {
        when (error) {
            ErrorType.NETWORK -> setNetworkErrorDialogVisible(false)
            ErrorType.SERVER -> setServerErrorDialogVisible(false)
        }
    }
}
