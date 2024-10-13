package com.minhhnn18898.discover.test_helper

import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.presentation.ui_models.ArticleDisplayInfo
import java.util.Date

val vietnamArticleTest = Article(
    id = "1",
    title = "Vietnam",
    content = "From the stunning views of Sam Mountain and Ha Long Bay to the man-made artistry of the sacred temples and pagodas to the rice terraces and beaches, Vietnam is stunning",
    thumbUrl = "https://letstravel.net/thumb_vietnam",
    photoUrls = mutableListOf(
        "https://letstravel.net/vietnam/photo_1",
        "https://letstravel.net/vietnam/photo_2",
        "https://letstravel.net/vietnam/photo_3"
    ),
    lastEdited = Date(1728781470),
    originalSrc = "https://www.nomadicmatt.com/travel-guides/vietnam-travel-tips/",
    tag = mutableListOf(
        "vietnam",
        "culture"
    )
)

val icelandArticleTest = Article(
    id = "2",
    title = "How to plan the perfect trip to the land of fire and ice",
    content = "Iceland has become one of the top adventure travel destinations in the world. Though, in many respects, it still feels like a well-guarded secret.",
    thumbUrl = "https://letstravel.net/thumb_iceland",
    photoUrls = mutableListOf(
        "https://letstravel.net/iceland/photo_1",
        "https://letstravel.net/iceland/photo_2",
        "https://letstravel.net/iceland/photo_3"
    ),
    lastEdited = Date(1728781475),
    originalSrc = "https://www.travelandleisure.com/travel-guide/iceland",
    tag = mutableListOf(
        "iceland",
        "aurora borealis"
    )
)

val australiaArticleTest = Article(
    id = "2",
    title = "Best day trips from Sydney",
    content = "Australia is home to some of the most diverse wildlife and landscapes in the world, so don’t limit yourself—rent a car and hit the road.",
    thumbUrl = "https://letstravel.net/thumb_australia",
    photoUrls = mutableListOf(
        "https://letstravel.net/australia/photo_1",
        "https://letstravel.net/australia/photo_2",
        "https://letstravel.net/australia/photo_3"
    ),
    lastEdited = Date(1728781923),
    originalSrc = "https://www.theblondeabroad.com/best-day-trips-from-sydney/",
    tag = mutableListOf()
)

val vietnamArticleDisplayInfoTest = ArticleDisplayInfo(
    id = "1",
    title = "Vietnam",
    content = "From the stunning views of Sam Mountain and Ha Long Bay to the man-made artistry of the sacred temples and pagodas to the rice terraces and beaches, Vietnam is stunning",
    thumbUrl = "https://letstravel.net/thumb_vietnam",
    photoUrls = mutableListOf(
        "https://letstravel.net/vietnam/photo_1",
        "https://letstravel.net/vietnam/photo_2",
        "https://letstravel.net/vietnam/photo_3"
    ),
    lastEdited = "1728781470",
    originalSrc = "https://www.nomadicmatt.com/travel-guides/vietnam-travel-tips/",
    tag = mutableListOf(
        "vietnam",
        "culture"
    )
)

val icelandArticleDisplayInfoTest = ArticleDisplayInfo(
    id = "2",
    title = "How to plan the perfect trip to the land of fire and ice",
    content = "Iceland has become one of the top adventure travel destinations in the world. Though, in many respects, it still feels like a well-guarded secret.",
    thumbUrl = "https://letstravel.net/thumb_iceland",
    photoUrls = mutableListOf(
        "https://letstravel.net/iceland/photo_1",
        "https://letstravel.net/iceland/photo_2",
        "https://letstravel.net/iceland/photo_3"
    ),
    lastEdited = "1728781475",
    originalSrc = "https://www.travelandleisure.com/travel-guide/iceland",
    tag = mutableListOf(
        "iceland",
        "aurora borealis"
    )
)

val australiaArticleDisplayInfoTest = ArticleDisplayInfo(
    id = "2",
    title = "Best day trips from Sydney",
    content = "Australia is home to some of the most diverse wildlife and landscapes in the world, so don’t limit yourself—rent a car and hit the road.",
    thumbUrl = "https://letstravel.net/thumb_australia",
    photoUrls = mutableListOf(
        "https://letstravel.net/australia/photo_1",
        "https://letstravel.net/australia/photo_2",
        "https://letstravel.net/australia/photo_3"
    ),
    lastEdited = "1728781923",
    originalSrc = "https://www.theblondeabroad.com/best-day-trips-from-sydney/",
    tag = mutableListOf()
)