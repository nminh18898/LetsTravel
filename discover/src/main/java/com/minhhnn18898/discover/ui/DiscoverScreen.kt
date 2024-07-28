@file:OptIn(ExperimentalFoundationApi::class)

package com.minhhnn18898.discover.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.minhhnn18898.architecture.ui.UiState

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    ExploreArticlesSection(
        articlesContentState = viewModel.articlesContentState,
        modifier = modifier
    )
}


@Composable
fun ExploreArticlesSection(
    articlesContentState: UiState<List<ArticleDisplayInfo>, UiState.UndefinedError>,
    modifier: Modifier
) {

    if(articlesContentState is UiState.Loading) {
        // todo loading view
    } else if(articlesContentState is UiState.Error) {
        // todo error view
    } else if(articlesContentState is UiState.Success) {
        val isEmpty = articlesContentState.data.isEmpty()

        if(isEmpty) {
           // todo empty view
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

    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.CenterVertically,
        pageSize = PageSize.Fill
    ) { page ->
        val pageInfo = articles[page]

        AsyncImage(
            model = pageInfo.thumbUrl,
            contentDescription = ""
        )
    }
}