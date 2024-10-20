package com.minhhnn18898.discover.presentation.article_detail

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.discover.presentation.ui_models.ArticleDisplayInfo
import com.minhhnn18898.ui_components.R
import com.minhhnn18898.ui_components.base_components.DefaultErrorView
import com.minhhnn18898.ui_components.base_components.ErrorTextView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun ArticleDetailScreen(
    onClickPhoto: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailScreenViewModel = hiltViewModel()
) {

    if(viewModel.verifiedUserState) {
        ArticlesDetailView(
            articlesContentState = viewModel.articlesContentState,
            articlesBottomControllerUiState = viewModel.articleBottomNavigationUiState,
            onClickPrevious = viewModel::onClickPreviousItem,
            onClickNext = viewModel::onClickNextItem,
            onClickPhoto = onClickPhoto,
            modifier = modifier
        )
    }
    else {
        ErrorTextView(
            error = stringResource(id = com.minhhnn18898.core.R.string.can_not_load_info),
            modifier = modifier
        )
    }
}

@Composable
fun ArticlesDetailView(
    articlesContentState: UiState<ArticleDisplayInfo>,
    articlesBottomControllerUiState: ArticleDetailBottomNavigationUiState,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
    onClickPhoto: (String) -> Unit,
    modifier: Modifier
) {
    when (articlesContentState) {
        is UiState.Success -> {
            val articleDisplayInfo = articlesContentState.data

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ArticleHeaderThumbDisplay(
                    thumbUrl = articleDisplayInfo.thumbUrl,
                    title = articleDisplayInfo.title,
                    lastEdited = articleDisplayInfo.lastEdited
                )

                ArticleTagDisplay(tags = articleDisplayInfo.tag)

                Text(
                    text = articleDisplayInfo.content,
                    style = typography.bodyMedium.copy(
                        lineBreak = LineBreak.Paragraph
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if(articleDisplayInfo.originalSrc.isNotBlankOrEmpty()) {
                    Text(
                        text = "(${stringResource(id = CommonStringRes.source)}: ${articleDisplayInfo.originalSrc})",
                        style = typography.bodySmall.copy(
                            lineBreak = LineBreak.Simple
                        ),
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if(articleDisplayInfo.photoUrls.isNotEmpty()) {
                    PhotoCarousel(
                        urls = articleDisplayInfo.photoUrls,
                        onClickPhoto = onClickPhoto
                    )
                }

                NextAndPreviousControlBar(
                    previous = articlesBottomControllerUiState.previous,
                    next = articlesBottomControllerUiState.next,
                    onClickPrevious = onClickPrevious,
                    onClickNext = onClickNext
                )
            }
        }

        is UiState.Loading -> {
            ArticleDetailLoadingView()
        }

        is UiState.Error -> {
            DefaultErrorView(modifier = modifier)
        }
    }
}

@Composable
private fun ArticleHeaderThumbDisplay(
    thumbUrl: String,
    title: String,
    lastEdited: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = thumbUrl,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.image_placeholder),
            error = painterResource(id = R.drawable.empty_image_bg)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.DarkGray.copy(alpha = 0.6f))
        )

        Text(
            text = title,
            style = typography.headlineMedium.copy(
                shadow = Shadow(
                    color = Color.DarkGray,
                    offset = Offset(8f, 8f),
                    blurRadius = 8f
                )
            ),
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = lastEdited,
                style = typography.labelLarge.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                ),
                color = Color(0xFFD7DDD9),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }
    }
}

@Composable
private fun ArticleTagDisplay(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        if(tags.isNotEmpty()) {
            Row {
                tags.forEach { tag ->
                    if(tag.isNotBlankOrEmpty()) {
                        TagDisplay(tag = tag)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TagDisplay(
    tag: String
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(percent = 25)
            )
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
    ) {
        Text(
            text = tag,
            style = typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoCarousel(
    urls: List<String>,
    onClickPhoto: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalUncontainedCarousel(
            state = rememberCarouselState {
                urls.count()
            },
            itemWidth = 250.dp,
            itemSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = 12.dp,
                    bottom = 12.dp
                )
        ) { index ->

            val value = urls[index]

            AsyncImage(
                model = value,
                contentDescription = "",
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth()
                    .maskClip(shape = MaterialTheme.shapes.large)
                    .clickable {
                        onClickPhoto(value)
                    },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.image_placeholder),
                error = painterResource(id = R.drawable.empty_image_bg)
            )
        }
    }
}

@Composable
private fun ArticleDetailLoadingView() {
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

    Column {
        ArticleThumbSkeleton(alpha = alpha)
        Spacer(modifier = Modifier.height(16.dp))
        ArticleTagSkeleton(alpha)
        Spacer(modifier = Modifier.height(16.dp))
        ArticleContentSkeleton(alpha)
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
private fun ArticleTagSkeleton(alpha: Float) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        for(i in 0..2) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(20.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = alpha),
                        shape = RoundedCornerShape(percent = 25)
                    )
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun ArticleContentSkeleton(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(color = Color.LightGray.copy(alpha = alpha))
    )
}

@Composable
private fun NextAndPreviousControlBar(
    previous: String,
    next: String,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {

        if(previous.isNotBlankOrEmpty()) {
            ControlButtonPrev(
                title = previous,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onClickPrevious()
                    }
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.width(24.dp))
        
        if(next.isNotBlankOrEmpty()) {
            ControlButtonNext(
                title = next,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onClickNext()
                    }
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ControlButtonPrev(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = com.minhhnn18898.discover.R.drawable.line_start_arrow_notch_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.width(8.dp))

        TwoLinesText(
            text = title,
            contentAlignment = Alignment.CenterStart,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun ControlButtonNext(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TwoLinesText(
            text = title,
            contentAlignment = Alignment.CenterEnd,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(id = com.minhhnn18898.discover.R.drawable.line_end_arrow_notch_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun TwoLinesText(
    text: String,
    contentAlignment: Alignment,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = contentAlignment, 
        modifier = modifier
    ) {
        // Empty text for vertical alignment purposes.
        Text(text = "\n")

        Text(
            text = text,
            overflow = Ellipsis,
            maxLines = 2,
            style = typography.labelMedium.copy(
                lineBreak = LineBreak.Heading
            ),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = textAlign
        )
    }
}