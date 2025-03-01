package com.minhhnn18898.discover.presentation.discover

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.GetAuthStateUseCase
import com.minhhnn18898.account.test_helper.FakeAccountService
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.test_helper.FakeDateTimeFormatter
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.discover.domain.GetListArticlesDiscoveryUseCase
import com.minhhnn18898.discover.presentation.ui_models.toArticlePreviewDisplayInfo
import com.minhhnn18898.discover.test_helper.FakeDiscoveryRepository
import com.minhhnn18898.discover.test_helper.australiaArticleDisplayInfoTest
import com.minhhnn18898.discover.test_helper.icelandArticleDisplayInfoTest
import com.minhhnn18898.discover.test_helper.vietnamArticleDisplayInfoTest
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getListArticlesDiscoveryUseCase: GetListArticlesDiscoveryUseCase
    private lateinit var fakeDiscoveryRepository: FakeDiscoveryRepository
    private lateinit var fakeAccountService: FakeAccountService
    private lateinit var checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase
    private lateinit var getAuthStateUseCase: GetAuthStateUseCase

    @Mock
    private lateinit var dateTimeFormatter: BaseDateTimeFormatter

    private lateinit var viewModel: DiscoverViewModel

    @Before
    fun setup() {
        fakeDiscoveryRepository = FakeDiscoveryRepository()
        getListArticlesDiscoveryUseCase = GetListArticlesDiscoveryUseCase(fakeDiscoveryRepository)
        fakeAccountService = FakeAccountService()
        checkValidSignedInUserUseCase = CheckValidSignedInUserUseCase(fakeAccountService)
        getAuthStateUseCase = GetAuthStateUseCase(fakeAccountService)
        dateTimeFormatter = FakeDateTimeFormatter()
    }

    private fun setupViewModel() {
        viewModel = DiscoverViewModel(
            getListArticlesDiscoveryUseCase = getListArticlesDiscoveryUseCase,
            baseDateTimeFormatter = dateTimeFormatter,
            checkValidSignedInUserUseCase = checkValidSignedInUserUseCase,
            getAuthStateUseCase = getAuthStateUseCase
        )
    }

    @Test
    fun getVerifiedUserState_signedInUser() {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.verifiedUserState).isTrue()
    }

    @Test
    fun getVerifiedUserState_notSignedInUser() {
        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.verifiedUserState).isFalse()
    }

    @Test
    fun getVerifiedUserState_signedInUser_thenSignOut() {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.verifiedUserState).isTrue()

        // When
        fakeAccountService.signOut()

        // Then
        Truth.assertThat(viewModel.verifiedUserState).isFalse()
    }

    @Test
    fun getArticlesContentState_initStateLoading() = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.articlesContentState).isEqualTo(UiState.Loading)
    }

    @Test
    fun getArticlesContentState_loadArticlesSuccess() = runTest {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.articlesContentState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((viewModel.articlesContentState as UiState.Success).data).isEqualTo(
            mutableListOf(
                vietnamArticleDisplayInfoTest,
                icelandArticleDisplayInfoTest,
                australiaArticleDisplayInfoTest
            ).map {
                it.toArticlePreviewDisplayInfo()
            }
        )
    }

    @Test
    fun getArticlesContentState_loadArticlesError() = runTest {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")
        fakeDiscoveryRepository.forceError = true

        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.articlesContentState).isInstanceOf(UiState.Error::class.java)
    }
}