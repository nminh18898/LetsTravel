package com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo

import com.minhhnn18898.manage_trip.trip_info.data.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.test_utils.MainDispatcherRule
import org.junit.Rule
import org.junit.Test

class AddEditTripViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository
    private lateinit var fakeCoverDefaultResourceProvider: FakeCoverDefaultResourceProvider

    private lateinit var viewModel: AddEditTripViewModel

    @Test
    fun getUiState() {
    }

    @Test
    fun onTripTitleUpdated() {
    }

    @Test
    fun onCoverSelected() {
    }

    @Test
    fun onSaveClick() {
    }

    @Test
    fun onNewPhotoPicked() {
    }

    @Test
    fun onDeleteClick() {
    }

    @Test
    fun onDeleteConfirm() {
    }

    @Test
    fun onDeleteDismiss() {
    }
}