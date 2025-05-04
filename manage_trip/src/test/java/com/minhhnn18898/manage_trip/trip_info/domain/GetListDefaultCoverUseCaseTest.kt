package com.minhhnn18898.manage_trip.trip_info.domain

import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.trip_info.data.repo.DefaultCoverElement
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetListDefaultCoverUseCaseTest {


    private lateinit var getListDefaultCoverUseCase: GetListDefaultCoverUseCase

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        getListDefaultCoverUseCase = GetListDefaultCoverUseCase(fakeTripInfoRepository)
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun getListDefaultCover() {
        // When
        val result = getListDefaultCoverUseCase.execute()

        // Then
        Truth.assertThat(result).isEqualTo(listOf(
                DefaultCoverElement.COVER_DEFAULT_THEME_SPRING,
                DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER,
                DefaultCoverElement.COVER_DEFAULT_THEME_AUTUMN,
                DefaultCoverElement.COVER_DEFAULT_THEME_WINTER,
                DefaultCoverElement.COVER_DEFAULT_THEME_BEACH,
                DefaultCoverElement.COVER_DEFAULT_THEME_MOUNTAIN,
                DefaultCoverElement.COVER_DEFAULT_THEME_AURORA,
                DefaultCoverElement.COVER_DEFAULT_THEME_VIETNAM,
                DefaultCoverElement.COVER_DEFAULT_THEME_CHINA,
                DefaultCoverElement.COVER_DEFAULT_THEME_SEA_DIVING
            )
        )
    }
}