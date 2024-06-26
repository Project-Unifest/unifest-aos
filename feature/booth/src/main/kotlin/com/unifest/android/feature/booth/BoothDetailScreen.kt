package com.unifest.android.feature.booth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unifest.android.core.common.ObserveAsEvents
import com.unifest.android.core.common.extension.clickableSingle
import com.unifest.android.core.common.utils.PhoneNumberVisualTransformation
import com.unifest.android.core.common.utils.formatAsCurrency
import com.unifest.android.core.designsystem.R
import com.unifest.android.core.designsystem.component.LoadingWheel
import com.unifest.android.core.designsystem.component.NetworkErrorDialog
import com.unifest.android.core.designsystem.component.NetworkImage
import com.unifest.android.core.designsystem.component.ServerErrorDialog
import com.unifest.android.core.designsystem.component.TopAppBarNavigationType
import com.unifest.android.core.designsystem.component.UnifestButton
import com.unifest.android.core.designsystem.component.UnifestHorizontalDivider
import com.unifest.android.core.designsystem.component.UnifestOutlinedButton
import com.unifest.android.core.designsystem.component.UnifestSnackBar
import com.unifest.android.core.designsystem.component.UnifestTopAppBar
import com.unifest.android.core.designsystem.theme.BoothCaution
import com.unifest.android.core.designsystem.theme.BoothLocation
import com.unifest.android.core.designsystem.theme.BoothTitle1
import com.unifest.android.core.designsystem.theme.Content2
import com.unifest.android.core.designsystem.theme.Content3
import com.unifest.android.core.designsystem.theme.MainColor
import com.unifest.android.core.designsystem.theme.MenuPrice
import com.unifest.android.core.designsystem.theme.MenuTitle
import com.unifest.android.core.designsystem.theme.Title1
import com.unifest.android.core.designsystem.theme.Title2
import com.unifest.android.core.designsystem.theme.Title3
import com.unifest.android.core.designsystem.theme.Title4
import com.unifest.android.core.designsystem.theme.Title5
import com.unifest.android.core.designsystem.theme.UnifestTheme
import com.unifest.android.core.designsystem.theme.WaitingNumber3
import com.unifest.android.core.designsystem.theme.WaitingTeam
import com.unifest.android.core.model.BoothDetailModel
import com.unifest.android.core.model.MenuModel
import com.unifest.android.core.ui.DevicePreview
import com.unifest.android.feature.booth.viewmodel.BoothUiAction
import com.unifest.android.feature.booth.viewmodel.BoothUiEvent
import com.unifest.android.feature.booth.viewmodel.BoothUiState
import com.unifest.android.feature.booth.viewmodel.BoothViewModel
import com.unifest.android.feature.booth.viewmodel.ErrorType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.thdev.compose.exteions.system.ui.controller.rememberExSystemUiController

private const val SnackBarDuration = 1000L

@Composable
internal fun BoothDetailRoute(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    navigateToBoothLocation: () -> Unit,
    viewModel: BoothViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemUiController = rememberExSystemUiController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }

    DisposableEffect(systemUiController) {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = false,
        )
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = true,
            )
        }
    }

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is BoothUiEvent.NavigateBack -> onBackClick()
            is BoothUiEvent.NavigateToBoothLocation -> navigateToBoothLocation()
            is BoothUiEvent.ShowSnackBar -> {
                scope.launch {
                    val job = launch {
                        snackBarState.showSnackbar(
                            message = event.message.asString(context),
                            duration = SnackbarDuration.Short,
                        )
                    }
                    delay(SnackBarDuration)
                    job.cancel()
                }
            }
        }
    }

    BoothDetailScreen(
        padding = padding,
        uiState = uiState,
        snackBarState = snackBarState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun BoothDetailScreen(
    padding: PaddingValues,
    uiState: BoothUiState,
    snackBarState: SnackbarHostState,
    onAction: (BoothUiAction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BoothDetailContent(
            uiState = uiState,
            onAction = onAction,
            modifier = Modifier.padding(bottom = 116.dp),
        )
        UnifestTopAppBar(
            navigationType = TopAppBarNavigationType.Back,
            navigationIconRes = R.drawable.ic_arrow_back_gray,
            containerColor = Color.Transparent,
            onNavigationClick = { onAction(BoothUiAction.OnBackClick) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(padding),
        )
        BottomBar(
            isBookmarked = uiState.isLiked,
            bookmarkCount = uiState.boothDetailInfo.likes,
            onAction = onAction,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
        SnackbarHost(
            hostState = snackBarState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 112.dp),
            snackbar = {
                UnifestSnackBar(snackBarData = it)
            },
        )

        if (uiState.isMenuImageDialogVisible && uiState.selectedMenu != null) {
            MenuImageDialog(
                onDismissRequest = { onAction(BoothUiAction.OnMenuImageDialogDismiss) },
                menu = uiState.selectedMenu,
            )
        }

        if (uiState.isLoading) {
            LoadingWheel(modifier = Modifier.fillMaxSize())
        }

        if (uiState.isServerErrorDialogVisible) {
            ServerErrorDialog(
                onRetryClick = { onAction(BoothUiAction.OnRetryClick(ErrorType.SERVER)) },
            )
        }

        if (uiState.isNetworkErrorDialogVisible) {
            NetworkErrorDialog(
                onRetryClick = { onAction(BoothUiAction.OnRetryClick(ErrorType.NETWORK)) },
            )
        }
    }
}

@Composable
fun BoothDetailContent(
    uiState: BoothUiState,
    onAction: (BoothUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            BoothImage(uiState.boothDetailInfo.thumbnail)
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }
        item {
            BoothDescription(
                name = uiState.boothDetailInfo.name,
                warning = uiState.boothDetailInfo.warning,
                description = uiState.boothDetailInfo.description,
                location = uiState.boothDetailInfo.location,
                onAction = onAction,
            )
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { UnifestHorizontalDivider() }
        item { Spacer(modifier = Modifier.height(22.dp)) }
        item { MenuText() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        if (uiState.boothDetailInfo.menus.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.booth_empty_menu),
                        modifier = Modifier.padding(top = 76.dp),
                        color = Color(0xFF7E7E7E),
                        style = Content3,
                    )
                }
            }
        } else {
            items(
                items = uiState.boothDetailInfo.menus,
                key = { it.id },
            ) { menu ->
                MenuItem(
                    menu = menu,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    bookmarkCount: Int,
    isBookmarked: Boolean,
    onAction: (BoothUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookMarkColor = if (isBookmarked) MainColor else Color(0xFF4B4B4B)
    Surface(
        modifier = modifier.height(116.dp),
        shadowElevation = 32.dp,
        color = Color.White,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 27.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(if (isBookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark),
                        contentDescription = if (isBookmarked) "북마크됨" else "북마크하기",
                        tint = bookMarkColor,
                        modifier = Modifier.clickableSingle {
                            onAction(BoothUiAction.OnToggleBookmark)
                        },
                    )
                    Text(
                        text = "$bookmarkCount",
                        color = bookMarkColor,
                        style = BoothCaution.copy(fontWeight = FontWeight.Bold),
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
                UnifestButton(
                    onClick = { /* showWaitingDialog = true */ },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 15.dp),
                    enabled = false,
                    containerColor = Color(0xFF777777),
                ) {
                    Text(
                        text = stringResource(id = R.string.booth_waiting_button_invalid),
                        style = Title4,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun BoothImage(
    imgUrl: String,
) {
    NetworkImage(
        imgUrl = imgUrl,
        contentDescription = "Booth Image",
        modifier = Modifier
            .height(260.dp)
            .fillMaxWidth(),
        placeholder = painterResource(id = R.drawable.ic_image_placeholder),
    )
}

@Composable
fun BoothDescription(
    name: String,
    warning: String,
    description: String,
    location: String,
    onAction: (BoothUiAction) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val maxWidth = remember(configuration) {
        val screenWidth = configuration.screenWidthDp.dp - 40.dp
        screenWidth * (2 / 3f)
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .alignBy(LastBaseline),
                style = BoothTitle1,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = warning,
                modifier = Modifier.alignBy(LastBaseline),
                style = BoothCaution,
                color = MainColor,
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp),
            style = Content2.copy(lineHeight = 18.sp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_location_green),
                contentDescription = "location icon",
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = location,
                color = Color(0xFF393939),
                style = BoothLocation,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        UnifestOutlinedButton(
            onClick = { onAction(BoothUiAction.OnCheckLocationClick) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.booth_check_locaiton),
                style = Title5,
            )
        }
    }
}

@Composable
fun MenuText() {
    Text(
        text = stringResource(id = R.string.booth_menu),
        modifier = Modifier.padding(start = 20.dp),
        style = Title2,
    )
}

@Composable
fun MenuItem(
    menu: MenuModel,
    onAction: (BoothUiAction) -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        NetworkImage(
            imgUrl = menu.imgUrl,
            contentDescription = menu.name,
            placeholder = painterResource(id = R.drawable.ic_item_placeholder),
            modifier = Modifier
                .size(86.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    onClick = {
                        if (menu.imgUrl.isNotEmpty()) {
                            onAction(BoothUiAction.OnMenuImageClick(menu))
                        }
                    },
                ),
        )
        Spacer(modifier = Modifier.width(13.dp))
        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
        ) {
            Text(
                text = menu.name,
                style = MenuTitle,
                color = Color(0xFF545454),
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = menu.price.formatAsCurrency(),
                style = MenuPrice,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingDialog(
    boothName: String,
    waitingCount: Int,
    phoneNumber: String,
    onDismissRequest: () -> Unit,
    onWaitingConfirm: (Int, String) -> Unit,
) {
    var currentPhoneNumber by remember { mutableStateOf(phoneNumber) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_location_green),
                    contentDescription = "location icon",
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = boothName, style = Title3)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "현재 내 앞 웨이팅",
                style = BoothLocation,
                color = Color(0xFF545454),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$waitingCount 팀",
                style = WaitingTeam,
            )
            Spacer(modifier = Modifier.height(9.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE0E0E0),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    "인원 수",
                    style = Title5,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(27.dp),
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        "$waitingCount",
                        style = WaitingNumber3,
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(27.dp),
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            BasicTextField(
                value = currentPhoneNumber,
                onValueChange = {
                    if (it.length <= 11) {
                        currentPhoneNumber = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color(0xFFD6D6D6), RoundedCornerShape(5.dp))
                    .background(
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(5.dp),
                    )
                    .padding(11.dp),
                decorationBox = { innerTextField ->
                    if (currentPhoneNumber.isEmpty()) {
                        Text(
                            text = "전화번호를 입력해주세요",
                            color = Color(0xFF848484),
                            style = BoothLocation,
                        )
                    }
                    innerTextField()
                },
                visualTransformation = PhoneNumberVisualTransformation(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            UnifestButton(
                onClick = { onWaitingConfirm(waitingCount, phoneNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text("웨이팅 신청")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingConfirmDialog(
    boothName: String,
    onDismissRequest: () -> Unit,
    // onWaitingConfirm: (Int, String) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_location_green),
                    contentDescription = "location icon",
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = boothName, style = Title3)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "웨이팅 등록 완료!", style = Title1)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "입장 순서가 되면 안내 해드릴게요.",
                style = Content2,
                color = Color(0xFF949494),
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE0E0E0),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("웨이팅 번호", style = Title5)
                    Text("112번", style = WaitingTeam)
                }
                Spacer(modifier = Modifier.width(25.dp))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .background(color = Color(0xFF8E8E8E), shape = CircleShape),
                )
                Spacer(modifier = Modifier.width(25.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("인원수", style = Title5)
                    Text("3명", style = WaitingTeam)
                }
                Spacer(modifier = Modifier.width(25.dp))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .background(color = Color(0xFF8E8E8E), shape = CircleShape),
                )
                Spacer(modifier = Modifier.width(25.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("내 앞 웨이팅", style = Title5)
                    Text("35팀", style = WaitingTeam)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                UnifestOutlinedButton(
                    onClick = { /*TODO*/ },
                    borderColor = Color(0xFFD2D2D2),
                    contentColor = Color.Black,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "웨이팅 취소",
                        style = Title5,
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                UnifestButton(
                    onClick = { /*TODO*/ },
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "순서 확인하기",
                        style = Title5,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@DevicePreview
@Composable
fun BoothScreenPreview() {
    UnifestTheme {
        BoothDetailScreen(
            padding = PaddingValues(),
            uiState = BoothUiState(
                boothDetailInfo = BoothDetailModel(
                    id = 0L,
                    name = "컴공 주점",
                    category = "컴퓨터공학부 전용 부스",
                    description = "저희 주점은 일본 이자카야를 모티브로 만든 컴공인을 위한 주점입니다. 100번째 방문자에게 깜짝 선물 증정 이벤트를 하고 있으니 많은 관심 부탁드려요~!",
                    warning = "",
                    location = "청심대 앞",
                    latitude = 37.54224856023523f,
                    longitude = 127.07605430700158f,
                    menus = listOf(
                        MenuModel(1L, "모둠 사시미", 45000, ""),
                        MenuModel(2L, "모둠 사시미", 45000, ""),
                        MenuModel(3L, "모둠 사시미", 45000, ""),
                        MenuModel(4L, "모둠 사시미", 45000, ""),
                    ),
                ),
            ),
            snackBarState = SnackbarHostState(),
            onAction = {},
        )
    }
}

@DevicePreview
@Composable
fun WaitingDialogPreview() {
    UnifestTheme {
        WaitingDialog(
            boothName = "컴공 주점",
            onDismissRequest = {},
            onWaitingConfirm = { _, _ -> },
            phoneNumber = "",
            waitingCount = 3,
        )
    }
}

@DevicePreview
@Composable
fun WaitingConfirmDialogPreview() {
    UnifestTheme {
        WaitingConfirmDialog(
            boothName = "컴공 주점",
            onDismissRequest = {},
//            onWaitingConfirm = { _, _ -> },
        )
    }
}
