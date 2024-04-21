package com.unifest.android.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import com.unifest.android.core.designsystem.ComponentPreview
import com.unifest.android.core.designsystem.R
import com.unifest.android.core.designsystem.theme.Content5
import com.unifest.android.core.designsystem.theme.Title1
import com.unifest.android.core.designsystem.theme.UnifestTheme
import kotlinx.coroutines.launch

enum class TopAppBarNavigationType { None, Back, Search }

@Composable
fun UnifestTopAppBar(
    navigationType: TopAppBarNavigationType,
    modifier: Modifier = Modifier,
    title: String = "",
    titleStyle: TextStyle = Title1,
    @DrawableRes navigationIconRes: Int = R.drawable.ic_arrow_back_dark_gray,
    navigationIconContentDescription: String? = null,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    onNavigationClick: () -> Unit = {},
    onTitleClick: (Boolean) -> Unit = {},
    isOnboardingCompleted: Boolean = false,
    onTooltipClick: (Boolean) -> Unit = {},
    elevation: Dp = 0.dp,
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        val icon: @Composable (Modifier, imageVector: ImageVector) -> Unit =
            { modifier, imageVector ->
                IconButton(
                    onClick = onNavigationClick,
                    modifier = modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = navigationIconContentDescription,
                        tint = Color.Unspecified,
                    )
                }
            }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation, RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                .background(containerColor)
                .then(modifier),
        ) {
            if (navigationType == TopAppBarNavigationType.Back) {
                icon(
                    Modifier.align(Alignment.CenterStart),
                    ImageVector.vectorResource(id = navigationIconRes),
                )
            }
            if (navigationType == TopAppBarNavigationType.Search) {
                if (isOnboardingCompleted) {
                    SchoolSearchTitle(
                        title = title,
                        onTitleClick = onTitleClick,
                    )
                } else {
                    SchoolSearchTitleWithToolTip(
                        title = title,
                        onTitleClick = onTitleClick,
                        completeOnboarding = onTooltipClick,
                    )
                }
            } else {
                Text(
                    text = title,
                    modifier = if (navigationType == TopAppBarNavigationType.Back) {
                        Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 18.dp)
                    } else {
                        Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 20.dp, top = 18.dp, bottom = 18.dp)
                    },
                    style = titleStyle,
                )
            }
        }
    }
}

@Composable
fun SchoolSearchTitle(
    title: String,
    onTitleClick: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 22.dp, top = 10.dp, bottom = 10.dp, end = 9.dp)
            .clickable {
                onTitleClick(true)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = Title1,
        )
        Spacer(modifier = Modifier.width(7.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_below),
            contentDescription = "Search School",
            tint = Color.Unspecified,
        )
    }
}

@Composable
fun SchoolSearchTitleWithToolTip(
    title: String,
    onTitleClick: (Boolean) -> Unit,
    completeOnboarding: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowOrientation(ArrowOrientation.START)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(9)
        setCornerRadius(8f)
        setBackgroundColor(Color(0xFFF5687E))
        setBalloonAnimation(BalloonAnimation.FADE)
        setDismissWhenClicked(true)
        setDismissWhenTouchOutside(false)
        setFocusable(false)
    }

    Balloon(
        builder = builder,
        balloonContent = {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable {
                        completeOnboarding(true)
                    },
                text = stringResource(id = R.string.map_school_search_tool_tip_description),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = Content5,
            )
        },
    ) { balloonWindow ->
        Row(
            modifier = Modifier
                .padding(start = 22.dp, top = 10.dp, bottom = 10.dp, end = 9.dp)
                .clickable {
                    onTitleClick(true)
                    completeOnboarding(true)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = Title1,
            )
            Spacer(modifier = Modifier.width(7.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_below),
                contentDescription = "Search School",
                tint = Color.Unspecified,
            )
            LaunchedEffect(key1 = Unit) {
                scope.launch {
                    balloonWindow.awaitAlignEnd()
                }
            }
        }
    }
}

@ComponentPreview
@Composable
fun UnifestTopAppBarPreview() {
    UnifestTheme {
        UnifestTopAppBar(
            navigationType = TopAppBarNavigationType.None,
            title = "UniFest",
        )
    }
}

@ComponentPreview
@Composable
fun SchoolSearchTitlePreview() {
    UnifestTheme {
        SchoolSearchTitle(
            title = "건국대학교",
            onTitleClick = {},
        )
    }
}

@ComponentPreview
@Composable
fun SchoolSearchTitlePreviewWithTitle() {
    UnifestTheme {
        SchoolSearchTitleWithToolTip(
            title = "건국대학교",
            onTitleClick = {},
            completeOnboarding = {},
        )
    }
}

@ComponentPreview
@Composable
fun UnifestTopAppBarWithBackButtonPreview() {
    UnifestTheme {
        UnifestTopAppBar(
            navigationType = TopAppBarNavigationType.Back,
            navigationIconContentDescription = "Navigation back icon",
        )
    }
}
