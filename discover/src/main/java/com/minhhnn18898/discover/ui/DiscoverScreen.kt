@file:OptIn(ExperimentalFoundationApi::class)

package com.minhhnn18898.discover.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.discover.R
import com.minhhnn18898.ui_components.base_components.DefaultCtaButton
import com.minhhnn18898.ui_components.theme.typography
import kotlinx.coroutines.launch
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun DiscoverScreen(
    onNavigateToSignInScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    if(viewModel.verifiedUserState) {
        ExploreArticlesSection(
            articlesContentState = viewModel.articlesContentState,
            modifier = modifier
        )
    }
    else {
        RequireSignInPromptForm(
            onClickSignIn = onNavigateToSignInScreen,
            modifier = modifier
        )
    }
}

private val promptUserSignInPhotoThumb = listOf(
    R.drawable.explore_section_promt_photo_1,
    R.drawable.explore_section_promt_photo_2,
    R.drawable.explore_section_promt_photo_3
)

@Composable
private fun RequireSignInPromptForm(
    onClickSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = R.string.prompt_user_log_in_to_explore_articles),
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        RequireSignInPromptSuggestThumb(modifier)

        Spacer(modifier = Modifier.height(8.dp))

        DefaultCtaButton(
            text = stringResource(id = CommonStringRes.sign_in),
            iconRes = R.drawable.login_24,
            onClick = onClickSignIn,
            modifier = modifier
        )
    }
}

@Composable
private fun RequireSignInPromptSuggestThumb(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-32).dp)
        ) {
            promptUserSignInPhotoThumb.forEach {
                RequireSignInPromptSuggestThumbElement(it)
            }
        }
    }
}

@Composable
private fun RequireSignInPromptSuggestThumbElement(
    @DrawableRes drawable: Int
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .border(2.dp, MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            .clip(CircleShape)
    )
}

@Composable
fun ExploreArticlesSection(
    articlesContentState: UiState<List<ArticleDisplayInfo>, UiState.UndefinedError>,
    modifier: Modifier
) {

    if (articlesContentState is UiState.Loading) {
        DiscoverySectionLoadingView()
    } else if (articlesContentState is UiState.Error) {
        DiscoverySectionEmptyView()
    } else if (articlesContentState is UiState.Success) {
        val isEmpty = articlesContentState.data.isEmpty()

        if (isEmpty) {
            DiscoverySectionEmptyView()
        } else {
            ArticlesPager(
                articles = articlesContentState.data,
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ArticlesPager(
    articles: List<ArticleDisplayInfo>,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = {
        articles.size
    })

    val selectedIndex: MutableState<Int> = remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedIndex.value = page
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically,
            pageSize = PageSize.Fill,
            modifier = modifier
                .height(436.dp)
        ) { page ->
            val pageInfo = articles[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    AsyncImage(
                        model = pageInfo.thumbUrl,
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = CommonDrawableRes.image_placeholder),
                        error = painterResource(id = CommonDrawableRes.empty_image_bg)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pageInfo.lastEdited,
                    style = typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pageInfo.title,
                    style = typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pageInfo.content,
                    style = typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        onClick = {

                        }
                    ) {
                        Text(stringResource(id = CommonStringRes.read_more))
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        PageIndicator(
            position = selectedIndex.value,
            total = articles.size,
            onClickNextItem = {
                val nextIndex = kotlin.math.min(selectedIndex.value + 1, articles.size - 1)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page = nextIndex)
                }
            },
            onClickPrevItem = {
                val  prevIndex = kotlin.math.max(selectedIndex.value - 1, 0)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page = prevIndex)
                }
            }
        )
    }
}

@Composable
fun PageIndicator(
    position: Int,
    total: Int,
    onClickPrevItem: () -> Unit,
    onClickNextItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roundCornerBgShape = RoundedCornerShape(50f)

    val backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
    val contentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = roundCornerBgShape
                )
                .padding(4.dp)
        ) {

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onClickPrevItem.invoke()
                    },
                painter = painterResource(R.drawable.chevron_backward_24),
                contentDescription = "",
                tint = contentColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${position + 1} ${stringResource(id = CommonStringRes.of)} $total",
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onClickNextItem.invoke()
                    },
                painter = painterResource(R.drawable.chevron_forward_24),
                contentDescription = "",
                tint = contentColor
            )
        }
    }
}

@Composable
private fun DiscoverySectionEmptyView(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = stringResource(id = R.string.empty_view_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.empty_view_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }

}

@Composable
private fun DiscoverySectionLoadingView() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        ArticleThumbSkeleton(alpha = alpha)
        Spacer(modifier = Modifier.height(8.dp))
        ArticleEditedDateSkeleton(alpha)
        Spacer(modifier = Modifier.height(24.dp))
        ArticleTitleSkeleton(alpha = alpha)
        Spacer(modifier = Modifier.height(16.dp))
        ArticleContentSkeleton(alpha = alpha)
    }
}

@Composable
private fun ArticleThumbSkeleton(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(color = Color.LightGray.copy(alpha = alpha))
    )
}

@Composable
private fun ArticleEditedDateSkeleton(alpha: Float) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(16.dp)
            .background(color = Color.LightGray.copy(alpha = alpha))
    )
}

@Composable
private fun ArticleTitleSkeleton(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(color = Color.LightGray.copy(alpha = alpha))
    )
}

@Composable
private fun ArticleContentSkeleton(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(color = Color.LightGray.copy(alpha = alpha))
    )
}